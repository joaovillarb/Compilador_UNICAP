package projeto_compilador;

public class Token {

    private String lexema;
    private int classe;
    private int line;
    private int column;

    public int getClasse() {
        return this.classe;
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

    public Token(int classe, String lexema, int line, int column) {
        this.classe = classe;
        this.lexema = lexema;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Token [Type=" + classe + ", text=" + lexema + ", column=" + column + ", line=" + line + "]";
    }

}