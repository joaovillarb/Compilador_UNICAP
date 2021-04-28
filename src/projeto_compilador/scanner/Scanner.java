package projeto_compilador.scanner;

import java.util.regex.*;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;

import projeto_compilador.ClasseTokens;
import projeto_compilador.Token;

public class Scanner {

    boolean start = true;

    private File arquivo;
    private FileReader reader;

    private ErroScanner erro;

    private String lexema;
    private char caractere = ' ';
    private int ascii;

    private int line;
    private int column;

    private Matcher matcher;
    private Pattern especiaisChar = Pattern.compile("[@#$¨?~&]");

    public Scanner(String font) {
        this.lexema = new String();
        this.erro = new ErroScanner();

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

        this.matcher = especiaisChar.matcher(this.caractere + "");
        if (this.matcher.find()) {
            this.erro.Errorlog(7, getPointer(), this.caractere);
        }

    }

    private boolean isComment() {
        this.getNextChar();
        if (this.caractere == '*') {
            while (true) {
                this.getNextChar();
                if (this.ascii == -1) {
                    this.erro.Errorlog(4, getPointer(), this.caractere);
                    return false;
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
            return new Token(ClasseTokens.IDENTIFICADOR.getClasse(), this.lexema, this.line, this.column);
        } else

        if (this.isNum(this.caractere) || this.caractere == '.') {

            while (this.isNum(this.caractere)) {
                incrementLexemaAndGetNextChar();
            }
            if (this.caractere == '.') {
                incrementLexemaAndGetNextChar();
                if (!this.isNum(this.caractere)) {
                    this.erro.Errorlog(1, getPointer(), this.caractere);
                }
                while (this.isNum(this.caractere)) {
                    incrementLexemaAndGetNextChar();
                }
                if (this.caractere == '.' || this.isChar(this.caractere)) {
                    this.erro.Errorlog(1, getPointer(), this.caractere);
                }
                return new Token(ClasseTokens.DECIMAL.getClasse(), this.lexema, this.line, this.column);
            }
            return new Token(ClasseTokens.INTEIRO.getClasse(), this.lexema, this.line, this.column);
        } else

            switch (this.caractere) {
            case '/':
                return this.isComment()
                        ? new Token(ClasseTokens.COMENTARIO.getClasse(), ClasseTokens.COMENTARIO.getNome(), this.line,
                                this.column)
                        : new Token(ClasseTokens.DIVISAO.getClasse(), ClasseTokens.DIVISAO.getNome(), this.line,
                                this.column);
            case '!':
                incrementLexemaAndGetNextChar();
                if (this.caractere == '=') {
                    incrementLexemaAndGetNextChar();
                    return new Token(ClasseTokens.DIFERENCA.getClasse(), ClasseTokens.DIFERENCA.getNome(), this.line,
                            this.column);
                } else {
                    this.erro.Errorlog(2, getPointer(), this.caractere);

                }
                break;
            case '=':
                incrementLexemaAndGetNextChar();
                if (this.caractere == '=') {
                    incrementLexemaAndGetNextChar();

                    return new Token(ClasseTokens.IGUALDADE.getClasse(), ClasseTokens.IGUALDADE.getNome(), this.line,
                            this.column);
                } else {
                    return new Token(ClasseTokens.ATRIBUICAO.getClasse(), ClasseTokens.ATRIBUICAO.getNome(), this.line,
                            this.column);
                }
            case '-':
                incrementLexemaAndGetNextChar();
                return new Token(ClasseTokens.SUBTRACAO.getClasse(), ClasseTokens.SUBTRACAO.getNome(), this.line,
                        this.column);
            case '+':
                incrementLexemaAndGetNextChar();
                return new Token(ClasseTokens.SOMA.getClasse(), ClasseTokens.SOMA.getNome(), this.line, this.column);
            case '*':
                incrementLexemaAndGetNextChar();
                return new Token(ClasseTokens.MULTIPLICAO.getClasse(), ClasseTokens.MULTIPLICAO.getNome(), this.line,
                        this.column);
            case '<':
                incrementLexemaAndGetNextChar();
                if (this.caractere == '=') {
                    incrementLexemaAndGetNextChar();
                    return new Token(ClasseTokens.MENOR_IGUAL.getClasse(), ClasseTokens.MENOR_IGUAL.getNome(),
                            this.line, this.column);
                } else {
                    return new Token(ClasseTokens.MENOR_QUE.getClasse(), ClasseTokens.MENOR_QUE.getNome(), this.line,
                            this.column);
                }
            case '>':
                incrementLexemaAndGetNextChar();
                if (this.caractere == '=') {
                    incrementLexemaAndGetNextChar();
                    return new Token(ClasseTokens.MAIOR_IGUAL.getClasse(), ClasseTokens.MAIOR_IGUAL.getNome(),
                            this.line, this.column);
                } else {
                    return new Token(ClasseTokens.MAIOR_QUE.getClasse(), ClasseTokens.MAIOR_QUE.getNome(), this.line,
                            this.column);
                }
            case ';':
                this.getNextChar();
                return new Token(ClasseTokens.PONTO_VIRGULA.getClasse(), ClasseTokens.PONTO_VIRGULA.getNome(),
                        this.line, this.column);
            case ',':
                this.getNextChar();
                return new Token(ClasseTokens.VIRGULA.getClasse(), ClasseTokens.VIRGULA.getNome(), this.line,
                        this.column);
            case ')':
                this.getNextChar();
                return new Token(ClasseTokens.FECHA_PARENTESES.getClasse(), ClasseTokens.FECHA_PARENTESES.getNome(),
                        this.line, this.column);
            case '(':
                this.getNextChar();
                return new Token(ClasseTokens.ABRE_PARENTESES.getClasse(), ClasseTokens.ABRE_PARENTESES.getNome(),
                        this.line, this.column);
            case '}':
                this.getNextChar();
                return new Token(ClasseTokens.FECHA_BLOCO.getClasse(), ClasseTokens.FECHA_BLOCO.getNome(), this.line,
                        this.column);
            case '{':
                this.getNextChar();
                return new Token(ClasseTokens.ABRE_BLOCO.getClasse(), ClasseTokens.ABRE_BLOCO.getNome(), this.line,
                        this.column);
            case ']':
                this.getNextChar();
                return new Token(ClasseTokens.FECHA_COLCHETE.getClasse(), ClasseTokens.FECHA_COLCHETE.getNome(),
                        this.line, this.column);
            case '[':
                this.getNextChar();
                return new Token(ClasseTokens.ABRE_COLCHETE.getClasse(), ClasseTokens.ABRE_COLCHETE.getNome(),
                        this.line, this.column);
            case '\'':
                this.getNextChar();
                if (this.isNumOrChar(this.caractere)) {
                    incrementLexemaAndGetNextChar();

                    if (this.caractere == '\'') {
                        this.getNextChar();
                        return new Token(ClasseTokens.CARACTER.getClasse(), "'" + this.lexema + "'", this.line,
                                this.column);
                    } else {
                        this.erro.Errorlog(6, getPointer(), this.caractere);
                    }
                } else {
                    this.erro.Errorlog(5, getPointer(), this.caractere);
                }
                break;
            }
        this.erro.mostrarTodosOsErrors();
        return new Token(ClasseTokens.ENDFILE.getClasse(), ClasseTokens.ENDFILE.getNome(), this.line, this.column);
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
            return new Token(ClasseTokens.PR_FLOAT.getClasse(), ClasseTokens.PR_FLOAT.getNome(), this.line,
                    this.column);
        case "int":
            return new Token(ClasseTokens.PR_INT.getClasse(), ClasseTokens.PR_INT.getNome(), this.line, this.column);
        case "main":
            return new Token(ClasseTokens.PR_MAIN.getClasse(), ClasseTokens.PR_MAIN.getNome(), this.line, this.column);
        case "char":
            return new Token(ClasseTokens.PR_CHAR.getClasse(), ClasseTokens.PR_CHAR.getNome(), this.line, this.column);
        case "if":
            return new Token(ClasseTokens.PR_IF.getClasse(), ClasseTokens.PR_IF.getNome(), this.line, this.column);
        case "else":
            return new Token(ClasseTokens.PR_ELSE.getClasse(), ClasseTokens.PR_ELSE.getNome(), this.line, this.column);
        case "while":
            return new Token(ClasseTokens.PR_WHILE.getClasse(), ClasseTokens.PR_WHILE.getNome(), this.line,
                    this.column);
        case "do":
            return new Token(ClasseTokens.PR_DO.getClasse(), ClasseTokens.PR_DO.getNome(), this.line, this.column);
        case "for":
            return new Token(ClasseTokens.PR_FOR.getClasse(), ClasseTokens.PR_FOR.getNome(), this.line, this.column);
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