package projeto_compilador.parser;

import java.util.LinkedList;
import java.util.List;

public class Simbolo {

    List<Variavel> variaveis = new LinkedList<>();

    public List<Variavel> getVariaveis() {
        return variaveis;
    }

    public void adicionar(Variavel variavel) {
        this.variaveis.add(variavel);
    }

}
