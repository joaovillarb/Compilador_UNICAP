package projeto_compilador.parser;

import projeto_compilador.TypeToken;

public class Variavel {

    private final String nome;
    private final TypeToken tipo;

    public Variavel(String nome, TypeToken tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    public TypeToken getTipo() {
        return tipo;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "Variavel{" +
                "nome='" + nome + '\'' +
                ", tipo=" + tipo +
                '}';
    }
}
