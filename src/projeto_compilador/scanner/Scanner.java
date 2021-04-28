package projeto_compilador.scanner;

import java.util.regex.*;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;

import projeto_compilador.TypeToken;
import projeto_compilador.exceptions.ErrorScannerException;
import projeto_compilador.Token;

public class Scanner {

    boolean start = true;

    private File arquivo;
    private FileReader reader;

    private String lexema;
    private char caractere = ' ';
    private int ascii;

    private int line;
    private int column;

    private Pattern especiaisChar = Pattern.compile("[@#$¨?~&]");

    public Scanner(String font) {
        this.lexema = new String();

        this.column = 1;
        this.line = 1;

        this.arquivo = new File(font);

        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException error) {
                error.getMessage();
            }
        }

        try {
            this.reader = new FileReader(this.arquivo);
        } catch (FileNotFoundException error) {
            error.getMessage();
        }

    }

    private void incrementLexema() {
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
            error.getMessage();
        }

        // System.out.println("ascii - " + this.ascii);
        this.caractere = (char) this.ascii;

        Matcher matcher = especiaisChar.matcher(this.caractere + "");
        if (matcher.find()) {
            String descricao = "\nCARACTERES ESPECIAIS SÃO INVALIDOS\n\n";
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
        if (this.caractere == '/') {
            do {
                this.getNextChar();
            } while (this.caractere != '\n');
            return true;
        }
        return false;
    }

    private Token getToken() {
        this.lexema = "";

        while (Character.isWhitespace(this.caractere)) {
            this.getNextChar();
        }

        if (this.isChar(this.caractere)) {
            do {
                incrementLexemaAndGetNextChar();
            } while (this.isNumOrChar(this.caractere));

            Token tokeN = this.palavraReservada();

            if (tokeN != null) {
                return tokeN;
            }
            return new Token(TypeToken.IDENTIFICADOR, this.lexema, this.line, this.column);
        } else

        if (this.isNum(this.caractere) || this.caractere == '.') {

            while (this.isNum(this.caractere)) {
                incrementLexemaAndGetNextChar();
            }
            if (this.caractere == '.') {
                incrementLexemaAndGetNextChar();
                if (!this.isNum(this.caractere)) {
                    String descricao = "\nCARACTERE INVALIDO, AQUI DEVERIA TER UM NUMERO\n\n";
                    throw new ErrorScannerException(getPointer(), this.caractere, descricao);
                }
                while (this.isNum(this.caractere)) {
                    incrementLexemaAndGetNextChar();
                }
                if (this.caractere == '.' || this.isChar(this.caractere)) {
                    String descricao = "\nCARACTERE INVALIDO, AQUI DEVERIA TER UM NUMERO\n\n";
                    throw new ErrorScannerException(getPointer(), this.caractere, descricao);
                }
                return new Token(TypeToken.DECIMAL, this.lexema, this.line, this.column);
            }
            return new Token(TypeToken.INTEIRO, this.lexema, this.line, this.column);
        } else

            switch (this.caractere) {
            case '/':
                return this.isComment() ? new Token(TypeToken.COMENTARIO, this.line, this.column)
                        : new Token(TypeToken.DIVISAO, this.line, this.column);
            case '!':
                incrementLexemaAndGetNextChar();
                if (this.caractere == '=') {
                    incrementLexemaAndGetNextChar();
                    return new Token(TypeToken.DIFERENCA, this.line, this.column);
                } else {
                    String descricao = "\nDEVE-SE USAR !=, PARA VERIFICAR UMA DIFERENCA\n\n";
                    throw new ErrorScannerException(getPointer(), caractere, descricao);
                }
            case '=':
                incrementLexemaAndGetNextChar();
                if (this.caractere == '=') {
                    incrementLexemaAndGetNextChar();

                    return new Token(TypeToken.IGUALDADE, this.line, this.column);
                } else {
                    return new Token(TypeToken.ATRIBUICAO, this.line, this.column);
                }
            case '-':
                incrementLexemaAndGetNextChar();
                return new Token(TypeToken.SUBTRACAO, this.line, this.column);
            case '+':
                incrementLexemaAndGetNextChar();
                return new Token(TypeToken.SOMA, this.line, this.column);
            case '*':
                incrementLexemaAndGetNextChar();
                return new Token(TypeToken.MULTIPLICAO, this.line, this.column);
            case '<':
                incrementLexemaAndGetNextChar();
                if (this.caractere == '=') {
                    incrementLexemaAndGetNextChar();
                    return new Token(TypeToken.MENOR_IGUAL, this.line, this.column);
                } else {
                    return new Token(TypeToken.MENOR_QUE, this.line, this.column);
                }
            case '>':
                incrementLexemaAndGetNextChar();
                if (this.caractere == '=') {
                    incrementLexemaAndGetNextChar();
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
                    incrementLexemaAndGetNextChar();

                    if (this.caractere == '\'') {
                        this.getNextChar();
                        return new Token(TypeToken.CARACTER, "'" + this.lexema + "'", this.line, this.column);
                    } else {
                        // this.erro.Errorlog(6, getPointer(), this.caractere);
                        String descricao = "\nUM CHAR PARA SER FORMADO, PRECISA ESTAR ENTRE ASPAS SIMPLES\n\n";
                        throw new ErrorScannerException(getPointer(), this.caractere, descricao);
                    }
                } else {
                    // this.erro.Errorlog(5, getPointer(), this.caractere);
                    String descricao = "\nCARACTERES ESPECIAIS SÃO INVALIDOS, (APENAS LETRAS E NUMEROS PODEM SER UM CHAR)\n\n";
                    throw new ErrorScannerException(getPointer(), this.caractere, descricao);
                }
            }
        return new Token(TypeToken.ENDFILE, this.line, this.column);
    }

    private void incrementLexemaAndGetNextChar() {
        this.incrementLexema();
        this.getNextChar();
    }

    public boolean endFile() {
        return (this.ascii == -1);
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