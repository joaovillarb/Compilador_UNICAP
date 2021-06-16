package projeto_compilador.scanner;

import projeto_compilador.Token;
import projeto_compilador.TypeToken;
import projeto_compilador.exceptions.ErrorScannerException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class Scanner {

    private final Pattern especiaisChar = Pattern.compile("[@#$¨?~&]");
    boolean start = true;
    private FileReader reader;
    private String lexema;
    private char caractere = ' ';
    private int ascii;
    private int line;
    private int column;

    public Scanner(String font) {
        this.lexema = "";

        this.column = 1;
        this.line = 1;

        var arquivo = new File(font);

        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException error) {
                System.err.println(error.getMessage());
            }
        }

        try {
            this.reader = new FileReader(arquivo);
        } catch (FileNotFoundException error) {
            String errorMessage = error.getMessage();
            System.out.println(errorMessage);
        }

    }

    private void acumularLexema() {
        this.lexema += this.caractere;
    }

    private String getPointer() {
        return "linha - " + this.line + " coluna - " + this.column;
    }

    private void getNextChar() {

        movePointer(this.caractere);

        try {
            this.ascii = this.reader.read();
        } catch (IOException error) {
            System.out.println(error.getMessage());
        }

        this.caractere = (char) this.ascii;

        var matcher = especiaisChar.matcher(this.caractere + "");
        if (matcher.find()) {
            var descricao = "\nCARACTERES ESPECIAIS SÃO INVALIDOS\n\n";
            throw new ErrorScannerException(getPointer(), this.caractere, descricao);
        }

    }

    private boolean isComment() {
        this.getNextChar();
        if (this.caractere == '*') {
            while (true) {
                this.getNextChar();
                if (this.ascii == -1) {
                    String descricao = "EOF"
                            + "\nANTES DE TERMINAR O ARQUIVO O COMENTARIO MULTILINHA DEVE SER FECHADO\n\n";
                    throw new ErrorScannerException(getPointer(), this.caractere, descricao);
                }
                if (this.caractere == '*') {
                    this.getNextChar();
                    if (this.caractere == '/') {
                        this.getNextChar();
                        return true;
                    }
                }
            }
        }
        if (this.caractere != '/') {
            return false;
        }
        do {
            this.getNextChar();
        } while (this.caractere != '\n');
        return true;
    }

    private Token getToken() {
        this.lexema = "";

        pularEspacosEmBranco();

        if (this.isChar(this.caractere)) {
            do {
                acumularLexemaEPegarProximoChar();
            } while (this.isNumOrChar(this.caractere));

            var token = this.palavraReservada();

            return Objects.requireNonNullElseGet(token, () -> new Token(TypeToken.IDENTIFICADOR, this.lexema, this.line, this.column));
        }

        if (this.isNum(this.caractere) || this.caractere == '.') {

            while (this.isNum(this.caractere)) {
                acumularLexemaEPegarProximoChar();
            }

            if (this.caractere == '.') {
                acumularLexemaEPegarProximoChar();
                if (!this.isNum(this.caractere)) {
                    var descricao = "\nCARACTERE INVALIDO, AQUI DEVERIA TER UM NUMERO\n\n";
                    throw new ErrorScannerException(getPointer(), this.caractere, descricao);
                }
                while (this.isNum(this.caractere)) {
                    acumularLexemaEPegarProximoChar();
                }
                if (this.caractere != '.' && !this.isChar(this.caractere)) {
                    return new Token(TypeToken.DECIMAL, this.lexema, this.line, this.column);
                }
                var descricao = "\nCARACTERE INVALIDO, AQUI DEVERIA TER UM NUMERO\n\n";
                throw new ErrorScannerException(getPointer(), this.caractere, descricao);
            }
            return new Token(TypeToken.INTEIRO, this.lexema, this.line, this.column);
        } else {
            switch (this.caractere) {
                case '/':
                    return this.isComment() ? new Token(TypeToken.COMENTARIO, this.line, this.column)
                            : new Token(TypeToken.DIVISAO, this.line, this.column);
                case '!':
                    acumularLexemaEPegarProximoChar();
                    if (this.caractere == '=') {
                        acumularLexemaEPegarProximoChar();
                        return new Token(TypeToken.DIFERENCA, this.line, this.column);
                    } else {
                        var descricao = "\nDEVE-SE USAR !=, PARA VERIFICAR UMA DIFERENCA\n\n";
                        throw new ErrorScannerException(getPointer(), caractere, descricao);
                    }
                case '=':
                    acumularLexemaEPegarProximoChar();
                    if (this.caractere == '=') {
                        acumularLexemaEPegarProximoChar();

                        return new Token(TypeToken.IGUALDADE, this.line, this.column);
                    } else {
                        return new Token(TypeToken.ATRIBUICAO, this.line, this.column);
                    }
                case '-':
                    acumularLexemaEPegarProximoChar();
                    return new Token(TypeToken.SUBTRACAO, this.line, this.column);
                case '+':
                    acumularLexemaEPegarProximoChar();
                    return new Token(TypeToken.SOMA, this.line, this.column);
                case '*':
                    acumularLexemaEPegarProximoChar();
                    return new Token(TypeToken.MULTIPLICAO, this.line, this.column);
                case '<':
                    acumularLexemaEPegarProximoChar();
                    if (this.caractere == '=') {
                        acumularLexemaEPegarProximoChar();
                        return new Token(TypeToken.MENOR_IGUAL, this.line, this.column);
                    } else {
                        return new Token(TypeToken.MENOR_QUE, this.line, this.column);
                    }
                case '>':
                    acumularLexemaEPegarProximoChar();
                    if (this.caractere == '=') {
                        acumularLexemaEPegarProximoChar();
                        return new Token(TypeToken.MAIOR_IGUAL, this.line, this.column);
                    } else {
                        return new Token(TypeToken.MAIOR_QUE, this.line, this.column);
                    }
                case ';':
                    this.getNextChar();
                    return new Token(TypeToken.PONTO_VIRGULA, this.line, this.column);
                case ',':
                    this.getNextChar();
                    return new Token(TypeToken.VIRGULA, this.line, this.column);
                case ')':
                    this.getNextChar();
                    return new Token(TypeToken.FECHA_PARENTESES, this.line, this.column);
                case '(':
                    this.getNextChar();
                    return new Token(TypeToken.ABRE_PARENTESES, this.line, this.column);
                case '}':
                    this.getNextChar();
                    return new Token(TypeToken.FECHA_BLOCO, this.line, this.column);
                case '{':
                    this.getNextChar();
                    return new Token(TypeToken.ABRE_BLOCO, this.line, this.column);
                case ']':
                    this.getNextChar();
                    return new Token(TypeToken.FECHA_COLCHETE, this.line, this.column);
                case '[':
                    this.getNextChar();
                    return new Token(TypeToken.ABRE_COLCHETE, this.line, this.column);
                case '\'':
                    this.getNextChar();
                    if (this.isNumOrChar(this.caractere)) {
                        acumularLexemaEPegarProximoChar();

                        if (this.caractere == '\'') {
                            this.getNextChar();
                            return new Token(TypeToken.CARACTER, "'" + this.lexema + "'", this.line, this.column);
                        }
                        var descricao = "\nUM CHAR PARA SER FORMADO, PRECISA ESTAR ENTRE ASPAS SIMPLES\n\n";
                        throw new ErrorScannerException(getPointer(), this.caractere, descricao);
                    } else {
                        var descricao = "\nCARACTERES ESPECIAIS SÃO INVALIDOS, (APENAS LETRAS E NUMEROS PODEM SER UM CHAR)\n\n";
                        throw new ErrorScannerException(getPointer(), this.caractere, descricao);
                    }
            }
        }
        return new Token(TypeToken.ENDFILE, this.line, this.column);
    }

    private void pularEspacosEmBranco() {
        while (Character.isWhitespace(this.caractere)) {
            this.getNextChar();
        }
    }

    private void acumularLexemaEPegarProximoChar() {
        this.acumularLexema();
        this.getNextChar();
    }

    private boolean isChar(char c) {
        return Character.isLetter(c);
    }

    private boolean isNum(char c) {
        return Character.isDigit(c);
    }

    private boolean isNumOrChar(char c) {
        return Character.isLetterOrDigit(c);
    }

    private Token palavraReservada() {
        switch (this.lexema) {
            case "float":
                return new Token(TypeToken.PR_FLOAT, this.line, this.column);
            case "int":
                return new Token(TypeToken.PR_INT, this.line, this.column);
            case "main":
                return new Token(TypeToken.PR_MAIN, this.line, this.column);
            case "char":
                return new Token(TypeToken.PR_CHAR, this.line, this.column);
            case "if":
                return new Token(TypeToken.PR_IF, this.line, this.column);
            case "else":
                return new Token(TypeToken.PR_ELSE, this.line, this.column);
            case "while":
                return new Token(TypeToken.PR_WHILE, this.line, this.column);
            case "do":
                return new Token(TypeToken.PR_DO, this.line, this.column);
            case "for":
                return new Token(TypeToken.PR_FOR, this.line, this.column);
            default:
                return null;
        }
    }

    private void movePointer(char caracter) {
        if (caracter == '\t') {
            this.column = this.column + 4;
        } else if (caracter == '\n') {
            this.column = 1;
            this.moveLine();
        } else {
            this.moveColumn();
        }
    }

    private void moveLine() {
        this.line = this.line + 1;
    }

    private void moveColumn() {
        this.column = this.column + 1;
    }

    public Token getNextToken() {
        if (this.start) {
            this.getNextChar();
            this.start = false;
        }
        return this.getToken();
    }
}