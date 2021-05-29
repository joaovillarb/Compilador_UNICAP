package projeto_compilador.parser;

import java.util.LinkedList;
import java.util.List;

public class Simbolo {

    List<Variavel> variaveis = new LinkedList<>();

    public List<Variavel> getVariaveis() {
//        return Collections.unmodifiableList(simbolos);
        return variaveis;
    }

    public void setVariaveis(List<Variavel> variaveis) {
        this.variaveis = variaveis;
    }

    public void adicionar(Variavel variavel) {
        this.variaveis.add(variavel);
    }
}
