package projeto_compilador;

public class Token {

    private String lexema;
    private TypeToken type;
    private int line;
    private int column;

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

    @Override
    public String toString() {
        return "Token [Type=" + type.getClasse() + ", text=" + lexema + ", column=" + column + ", line=" + line + "]";
    }

}