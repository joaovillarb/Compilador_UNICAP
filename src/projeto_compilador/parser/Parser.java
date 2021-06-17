package projeto_compilador.parser;

import projeto_compilador.Token;
import projeto_compilador.TypeToken;
import projeto_compilador.exceptions.ErrorParserException;
import projeto_compilador.scanner.Scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Parser {

    private static final String ABRE_PARENTESES_ESPERADO = "Abre parenteses esperado";
    private static final String FECHA_PARENTESES_ESPERADO = "Fecha parenteses esperado";
    private static final String PONTO_E_VIRGULA_ESPERADO = "Ponto e virgula esperado";

    private final Scanner scanner;
    private final Simbolo simbolo;
    private final List<Variavel> variaveisDeclaradas;
    private final List<Token> listaOperador;
    private int escopo;
    private int contador;
    private int ifContador;
    private int whileContador;
    private int doWhileContador;
    private Token token;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.simbolo = new Simbolo();
        this.variaveisDeclaradas = new ArrayList<>();
        this.listaOperador = new ArrayList<>();
        this.escopo = 0;
    }

    public void init() {
        executar();
    }

    private void getNextToken() {
        do {
            token = scanner.getNextToken();
        } while (token.getType() == TypeToken.COMENTARIO);
        //System.out.println(token);
    }

    private void executar() {
        inicializar();
        bloco();
        if (token.getType() != TypeToken.ENDFILE) {
            var msg = "Fim de arquivo esperado";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }
        this.simbolo.getVariaveis().remove(0);
        //System.out.println(simbolo.getVariaveis());
    }

    private void inicializar() {
        this.getNextToken();
        if (token.getType() != TypeToken.PR_INT) {
            var msg = "Palavra reservada int esperado";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.PR_MAIN) {
            var msg = "Palavra reservada main esperado";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.ABRE_PARENTESES) {
            throw new ErrorParserException(token.getLine(), token.getColumn(), ABRE_PARENTESES_ESPERADO);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.FECHA_PARENTESES) {
            throw new ErrorParserException(token.getLine(), token.getColumn(), FECHA_PARENTESES_ESPERADO);
        }

        this.getNextToken();
    }

    private void bloco() {
        if (token.getType() != TypeToken.ABRE_BLOCO) {
            var msg = "Abre chaves esperado";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }

        this.escopo++;
        this.simbolo.adicionar(new Variavel(this.token, TypeToken.ABRE_BLOCO, this.escopo));
        // System.out.println(simbolo.getVariaveis());

        this.getNextToken();
        enquantoTemTipoPrimario();
        enquantoTemComando();

        if (token.getType() != TypeToken.FECHA_BLOCO) {
            var msg = "Fecha chaves esperado";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }
        this.getNextToken();
        this.escopo--;
    }

    private void enquantoTemTipoPrimario() {
        if (!this.isTipoPrimario()) return;
        this.declararVariavel();
        enquantoTemTipoPrimario();
    }

    private void enquantoTemComando() {
        if (!this.isComando()) return;
        this.comando();
        enquantoTemComando();
    }

    private void temVirgula(Token auxToken) {
        if (token.getType() == TypeToken.VIRGULA) getIdentificador(auxToken);
    }

    private void declararVariavel() {
        var auxToken = this.token;

        getIdentificador(auxToken);
        if (token.getType() != TypeToken.PONTO_VIRGULA) {
            throw new ErrorParserException(token.getLine(), token.getColumn(), PONTO_E_VIRGULA_ESPERADO);
        }
        this.getNextToken();
    }

    private void getIdentificador(Token auxToken) {
        this.getNextToken();
        if (token.getType() != TypeToken.IDENTIFICADOR) {
            var msg = "Identificador esperado";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }

        calcularDeclaracaoVariavel(auxToken);

        this.getNextToken();
        temVirgula(auxToken);
    }

    private void calcularDeclaracaoVariavel(Token auxToken) {
        var variavel = new Variavel(this.token, token.getTypePalavraReservada(auxToken), this.escopo);

        Optional<Variavel> any = variaveisDeclaradas.stream().filter(
                f -> f.getEscopo() == this.escopo && f.getToken().getLexema().equals(variavel.getToken().getLexema())
        ).findAny();

        if (any.isPresent()) {
            var msg = "Lexema já declarado";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }

        variaveisDeclaradas.add(variavel);
        //System.out.println(variaveisDeclaradas);
    }

    private void comando() {
        if (this.isIdentificadorOuAbreBloco()) {
            this.basicCommand();
        } else if (this.isIteracao()) {
            this.iteracao();
        } else if (this.isCondicao()) {
            this.condicional();
        }
    }

    public void iteracao() {
        if (token.getType() == TypeToken.PR_WHILE) {
            this.comandoWhile();
        } else if (token.getType() == TypeToken.PR_DO) {
            this.comandoDoWhile();
        }
    }

    public void condicional() {
        int cont = ifContador;
        if (token.getType() == TypeToken.PR_IF) {

            this.getNextToken();
            if (token.getType() != TypeToken.ABRE_PARENTESES) {
                throw new ErrorParserException(token.getLine(), token.getColumn(), ABRE_PARENTESES_ESPERADO);
            }

            T();
            El();
            System.out.println("if " + "T" + contador + " == 0 goto" + " label_else_" + cont);
            this.ifContador++;

            if (token.getType() != TypeToken.FECHA_PARENTESES) {
                throw new ErrorParserException(token.getLine(), token.getColumn(), FECHA_PARENTESES_ESPERADO);
            }

            this.getNextToken();
            this.comando();
            System.out.println("goto label_fim_if_" + cont);
            System.out.println("label_else_" + cont + ":");

            if (token.getType() == TypeToken.PR_ELSE) {
                this.T();
                this.comando();
            }
            System.out.println("label_fim_if_" + cont + ":");
        } else {
            var msg = "Deveria ter um IF aqui";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }
    }

    public void comandoDoWhile() {
        int cont = doWhileContador;
        System.out.println("label_doWhile_inicio_" + cont);
        this.getNextToken();
        if (!this.isComando()) {
            var msg = "Deveria ter um comando aqui";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }

        this.comando();

        if (token.getType() != TypeToken.PR_WHILE) {
            var msg = "Palavra reservada while esperado";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.ABRE_PARENTESES) {
            throw new ErrorParserException(token.getLine(), token.getColumn(), ABRE_PARENTESES_ESPERADO);
        }
        T();
        if (this.isPrimeiroFator()) {
            El();
            this.doWhileContador++;
        } else {
            var msg = "Expressão relacional esperado";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }
        if (token.getType() != TypeToken.FECHA_PARENTESES) {
            throw new ErrorParserException(token.getLine(), token.getColumn(), FECHA_PARENTESES_ESPERADO);
        }
        this.getNextToken();
        if (token.getType() != TypeToken.PONTO_VIRGULA) {
            throw new ErrorParserException(token.getLine(), token.getColumn(), PONTO_E_VIRGULA_ESPERADO);
        }
        this.getNextToken();
        System.out.println("if T" + contador + " != 0 goto label_doWhile_inicio_" + cont);
    }

    public void comandoWhile() {
        int cont = whileContador;
        System.out.println("label_while_inicio_" + cont + ":");
        this.getNextToken();
        if (token.getType() != TypeToken.ABRE_PARENTESES) {
            throw new ErrorParserException(token.getLine(), token.getColumn(), ABRE_PARENTESES_ESPERADO);
        }

        T();
        El();
        System.out.println("if T" + contador + " == 0 goto" + " label_while_fim_" + cont);
        this.whileContador++;

        if (token.getType() != TypeToken.FECHA_PARENTESES) {
            throw new ErrorParserException(token.getLine(), token.getColumn(), FECHA_PARENTESES_ESPERADO);
        }
        this.getNextToken();
        this.comando();
        System.out.println("goto label_while_inicio_" + cont);
        System.out.println("label_while_fim_" + cont + ":");
    }

    private void basicCommand() {
        if (token.getType() == TypeToken.IDENTIFICADOR) {
            this.atribuicao();

        } else if (token.getType() == TypeToken.ABRE_BLOCO) {
            this.bloco();
        }
    }

    private void atribuicao() {
        Token ladoEsquerdo = this.token;

        this.getNextToken();
        if (token.getType() == TypeToken.ATRIBUICAO) {

            T();
            Variavel pai = descobrirPai(ladoEsquerdo);
            verificarVariavel(pai);

            var ultimaVariavelAdicionada = getLastSimbolo();
            listaOperador.add(this.token);
            atribuicaoLogica(pai);

            gerarCodigoIntermediarioAtribuicao(ladoEsquerdo, pai, ultimaVariavelAdicionada);

            if (token.getType() != TypeToken.PONTO_VIRGULA) {
                throw new ErrorParserException(token.getLine(), token.getColumn(), PONTO_E_VIRGULA_ESPERADO);
            }
            this.getNextToken();
        }
    }

    private void gerarCodigoIntermediarioAtribuicao(Token ladoEsquerdo, Variavel calcularPai, Variavel ultimaVariavelAdicionada) {
        if (listaOperador.size() == 1) {
            if (calcularPai.getTipo() == TypeToken.DECIMAL) {
                System.out.println(this.newTemp() + " = (float)" + ultimaVariavelAdicionada.getToken().getLexema());
                ladoEsquerdo.setCodIter("T" + contador);
                System.out.println(calcularPai.getToken().getLexema() + " = " + ladoEsquerdo.getCodIter());
            } else {
                System.out.println(calcularPai.getToken().getLexema() + " = " + ultimaVariavelAdicionada.getToken().getLexema());
            }
        } else {
            while (!listaOperador.isEmpty()) {
                int abre = 0;
                int fecha = 0;
                Token op;
                int i = 0;
                while (listaOperador.stream().anyMatch(f -> f.getType() == TypeToken.ABRE_PARENTESES)) {
                    if (listaOperador.get(i).getType() == TypeToken.ABRE_PARENTESES) {
                        abre = i;
                    }
                    if (listaOperador.get(i).getType() == TypeToken.FECHA_PARENTESES) {
                        fecha = i;

                        var vetToken = new ArrayList<Token>();
                        for (int j = abre + 1; j < fecha; j++) {
                            vetToken.add(listaOperador.get(j));
                        }
                        ladoEsquerdo = geradorDeCodigo(ladoEsquerdo, vetToken);
                        for (int j = fecha; j > abre; j--) {
                            listaOperador.remove(j);
                        }
                        i = -1;
                        listaOperador.set(abre, ladoEsquerdo);
                    }
                    i++;
                }
                ArrayList<Token> novo = new ArrayList<>(listaOperador);
                ladoEsquerdo = geradorDeCodigo(ladoEsquerdo, novo);
                listaOperador.clear();
            }

            System.out.println(ladoEsquerdo.getLexema() + " = " + ladoEsquerdo.getCodIter());
            ladoEsquerdo.setCodIter("T" + contador);
        }
    }

    private Token geradorDeCodigo(Token ladoEsquerdo, ArrayList<Token> novo) {
        int k = 0;
        while (novo.stream().anyMatch(this::isExpressaoTermo)) {
            Token k1 = getK(ladoEsquerdo, novo, k, isExpressaoTermo(novo.get(k)));
            if (k1 != null) {
                ladoEsquerdo = k1;
                k--;
            } else {
                k++;
            }
        }

        k = 0;
        while (novo.stream().anyMatch(this::isExpressaoArit)) {
            Token k1 = getK(ladoEsquerdo, novo, k, isExpressaoArit(novo.get(k)));
            if (k1 != null) {
                ladoEsquerdo = k1;
                k--;
            } else {
                k++;
            }
        }
        return ladoEsquerdo;
    }

    private Token getK(Token ladoEsquerdo, ArrayList<Token> vetToken, int k, boolean expressaoArit) {

        if (expressaoArit) {
            var antes = vetToken.get(k - 1);
            var depois = vetToken.get(k + 1);

            if (antes == ladoEsquerdo)
                System.out.println(this.newTemp() + " = " + antes.getCodIter() + " " + vetToken.get(k).getLexema() + " " + depois.getLexema());
            else if (depois == ladoEsquerdo)
                System.out.println(this.newTemp() + " = " + antes.getLexema() + " " + vetToken.get(k).getLexema() + " " + depois.getCodIter());
            else
                System.out.println(this.newTemp() + " = " + antes.getLexema() + " " + vetToken.get(k).getLexema() + " " + depois.getLexema());

            ladoEsquerdo.setCodIter("T" + contador);

            vetToken.set(k, ladoEsquerdo);
            vetToken.remove(k + 1);
            vetToken.remove(k - 1);
            return ladoEsquerdo;
        } else {
            return null;
        }

    }

    private void verificarVariavel(Variavel calcularPai) {
        if (calcularPai == null) {
            var msg = "Variavel não declarada";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }

        if (!verificarTipo(calcularPai)) {
            var msg = "Tipagem incorreta";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }
    }

    private Variavel getLastSimbolo() {
        return this.simbolo.getVariaveis().get(this.simbolo.getVariaveis().size() - 1);
    }

    //    metodo para descobrir quem declarou essa variavel (quem eh o pai)
    private Variavel descobrirPai(Token ladoEsquerdo) {
        Stream<Variavel> variavelStream = variaveisDeclaradas.stream().filter(f -> f.getToken().getLexema().equals(ladoEsquerdo.getLexema()));
        return variavelStream.reduce((first, second) -> second).orElse(null);
    }

    private void atribuicaoLogica(Variavel ultimoPaiDoTipo) {
        Token ladoEsquerdo = this.token;
        this.getNextToken();

        if (token.getType() == TypeToken.PONTO_VIRGULA) return;

        Token operador = identificarOperador();
        listaOperador.add(operador);
        T();

        verificarTipoDivisao(ultimoPaiDoTipo, ladoEsquerdo, operador);

        verificarVariavel(ultimoPaiDoTipo);
        listaOperador.add(this.token);
        atribuicaoLogica(ultimoPaiDoTipo);
    }

    private void verificarTipoDivisao(Variavel ultimoPaiDoTipo, Token ladoEsquerdo, Token operador) {
        if (operador.getType() == TypeToken.DIVISAO
                && this.token.getType() == TypeToken.INTEIRO
                && ladoEsquerdo.getType() == TypeToken.INTEIRO
                && ultimoPaiDoTipo.getTipo() == TypeToken.INTEIRO) {
            var msg = "Dividindo-se dois inteiros o tipo esperado é FLOAT";
            throw new ErrorParserException(ultimoPaiDoTipo.getToken().getLine(), ultimoPaiDoTipo.getToken().getColumn(), msg);
        }
    }

    private boolean verificarTipo(Variavel ultimoPaiDoTipo) {
        var ultimaVariavelAdicionada = getLastSimbolo();
        if (ultimaVariavelAdicionada.getTipo() == TypeToken.IDENTIFICADOR)
            ultimaVariavelAdicionada = descobrirPai(ultimaVariavelAdicionada.getToken());
        if (ultimaVariavelAdicionada.getTipo() == TypeToken.ABRE_PARENTESES || ultimaVariavelAdicionada.getTipo() == TypeToken.FECHA_PARENTESES)
            return true;
        return (ultimaVariavelAdicionada.getTipo() == ultimoPaiDoTipo.getTipo()) || ((ultimaVariavelAdicionada.getTipo() == TypeToken.INTEIRO) && (ultimoPaiDoTipo.getTipo() == TypeToken.DECIMAL));
    }

    private void El() {
        Token ladoEsq = this.token;
        this.getNextToken();
        if (token != null && token.getType() != TypeToken.FECHA_PARENTESES) {
            Token operador = identificarOperador();
            T();
            System.out.println(this.newTemp() + " = " + ladoEsq.getLexema() + operador.getLexema() + this.token.getLexema());
            El();
        }
    }

    public void T() {
        this.getNextToken();
        if (!this.isPrimeiroFator()) {
            var msg = "Esperado um IDENTIFICADOR, INTEIRO, FLOAT ou CARACTER";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }
        var variavel = new Variavel(this.token, token.getType(), this.escopo);
        this.simbolo.adicionar(variavel);
    }


    public void T2() {
        this.getNextToken();
        if (!this.isPrimeiroFator()) {
            var msg = "Esperado um IDENTIFICADOR, INTEIRO, FLOAT ou CARACTER";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }
        var variavel = new Variavel(this.token, token.getType(), this.escopo);
        this.simbolo.adicionar(variavel);
    }

    public Token identificarOperador() {
        if (!isOperadorRelacional(this.token) && !this.isExpressaoArit(this.token) && !this.isExpressaoTermo(this.token)) {
            var msg = "Esperado um OPERADOR";
            throw new ErrorParserException(token.getLine(), token.getColumn(), msg);
        }
        return this.token;
    }

    private String newTemp() {
        this.contador++;
        return "T" + this.contador;
    }

    private boolean isComando() {
        return this.isIteracao() || this.isIdentificadorOuAbreBloco() || this.isCondicao();
    }

    private boolean isCondicao() {
        return token.getType() == TypeToken.PR_IF;
    }

    private boolean isIteracao() {
        return token.getType() == TypeToken.PR_WHILE || token.getType() == TypeToken.PR_DO;
    }

    private boolean isIdentificadorOuAbreBloco() {
        return token.getType() == TypeToken.IDENTIFICADOR || token.getType() == TypeToken.ABRE_BLOCO;
    }

    private boolean isExpressaoArit(Token token) {
        return token.getType() == TypeToken.SOMA || token.getType() == TypeToken.SUBTRACAO;
    }

    private boolean isExpressaoTermo(Token token) {
        return token.getType() == TypeToken.MULTIPLICAO || token.getType() == TypeToken.DIVISAO;
    }

    private boolean isOperadorRelacional(Token token) {
        return token.getType() == TypeToken.IGUALDADE || token.getType() == TypeToken.DIFERENCA
                || token.getType() == TypeToken.MAIOR_IGUAL || token.getType() == TypeToken.MENOR_IGUAL
                || token.getType() == TypeToken.MENOR_QUE || token.getType() == TypeToken.MAIOR_QUE;
    }

    public boolean isPrimeiroFator() {
        return token.getType() == TypeToken.ABRE_PARENTESES || token.getType() == TypeToken.IDENTIFICADOR
                || token.getType() == TypeToken.INTEIRO || token.getType() == TypeToken.DECIMAL
                || token.getType() == TypeToken.CARACTER;
    }

    private boolean isTipoPrimario() {
        return token.getType() == TypeToken.PR_CHAR || token.getType() == TypeToken.PR_FLOAT
                || token.getType() == TypeToken.PR_INT;
    }

}