package projeto_compilador.parser;

import projeto_compilador.Token;
import projeto_compilador.TypeToken;

public class Variavel {

    private final Token token;
    private final TypeToken tipo;
    private Token pai;
    private int escopo;

    public Variavel(Token token, TypeToken tipo, int escopo) {
        this.token = token;
        this.tipo = tipo;
        this.escopo = escopo;
    }

    public Token getPai() {
        return pai;
    }

    public void setPai(Token token) {
        this.pai = token;
    }

    public TypeToken getTipo() {
        return tipo;
    }

    public Token getToken() {
        return token;
    }

    public int getEscopo() {
        return escopo;
    }

    @Override
    public String toString() {
        return "Variavel{" +
                token.getLexema() +
                ", tipo=" + tipo +
                ", pai=" + pai +
                ", escopo=" + escopo +
                '}';
    }
}
