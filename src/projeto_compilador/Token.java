package projeto_compilador;

public class Token {

    private String lexema;
    private TypeToken type;
    private int line;
    private int column;
    private String codIter;

    public String getCodIter() {
        return codIter;
    }

    public void setCodIter(String codIter) {
        this.codIter = codIter;
    }

    public Token(TypeToken type, int line, int column) {
        this.type = type;
        this.lexema = type.getNome();
        this.line = line;
        this.column = column;
    }

    public Token(TypeToken type, String lexema, int line, int column) {
        this.type = type;
        this.lexema = lexema;
        this.line = line;
        this.column = column;
    }

    public TypeToken getType() {
        return this.type;
    }

    public String getLexema() {
        return lexema;
    }

    public int getColumn() {
        return this.column;
    }

    public int getLine() {
        return this.line;
    }

    public TypeToken getTypePalavraReservada(Token token) {
        if (token.getType() == TypeToken.PR_FLOAT) {
            return TypeToken.DECIMAL;
        } else if (token.getType() == TypeToken.PR_INT) {
            return TypeToken.INTEIRO;
        } else if (token.getType() == TypeToken.PR_CHAR) {
            return TypeToken.CARACTER;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Token [Type=" + type.getClasse() + ", text=" + lexema + ", column=" + column + ", line=" + line + "]";
    }

}