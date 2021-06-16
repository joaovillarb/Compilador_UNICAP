package projeto_compilador.parser;

import projeto_compilador.Token;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Atribuicoes {

    List<Variavel> atribuicao = new ArrayList<>();
    List<Token> operador = new ArrayList<>();

    public List<Variavel> getAtribuicao() {
        return atribuicao;
    }

    public void adicionar(Variavel variavel) {
        this.atribuicao.add(variavel);
    }
    public void adicionar(Token token) {
        this.operador.add(token);
    }

    public List<Token> getOperador() {
        return operador;
    }

    public int getSize(){
        return atribuicao.size();
    }

}
