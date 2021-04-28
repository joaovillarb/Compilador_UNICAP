package projeto_compilador;

public class Token {

    private String lexema;
    private TypeToken classe;
    private int line;
    private int column;

    public TypeToken getClasse() {
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

    public Token(TypeToken classe, int line, int column) {
        this.classe = classe;
        this.lexema = classe.getNome();
        this.line = line;
        this.column = column;
    }

    public Token(TypeToken classe, String lexema, int line, int column) {
        this.classe = classe;
        this.lexema = lexema;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Token [Type=" + classe.getClasse() + ", text=" + lexema + ", column=" + column + ", line=" + line + "]";
    }

}