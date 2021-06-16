package projeto_compilador.parser;

import projeto_compilador.Token;
import projeto_compilador.TypeToken;
import projeto_compilador.exceptions.ErrorSyntaxException;
import projeto_compilador.scanner.Scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Parser {

    private final Scanner scanner;
    private final Simbolo simbolo;
    private final List<Variavel> variaveisDeclaradas;
    private int escopo;
    private int contador;
    private int ifContador;
    private int whileContador;
    private int doWhileContador;
    private Token token;
    private List<Token> listaAtrib;
    private static final String ABRE_PARENTESES_ESPERADO = "Abre parenteses esperado";
    private static final String FECHA_PARENTESES_ESPERADO = "Fecha parenteses esperado";
    private static final String PONTO_E_VIRGULA_ESPERADO = "Ponto e virgula esperado";

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.simbolo = new Simbolo();
        this.variaveisDeclaradas = new ArrayList<>();
        this.listaAtrib = new ArrayList<>();
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
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        this.simbolo.getVariaveis().remove(0);
        //System.out.println(simbolo.getVariaveis());
    }

    private void inicializar() {
        this.getNextToken();
        if (token.getType() != TypeToken.PR_INT) {
            var msg = "Palavra reservada int esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.PR_MAIN) {
            var msg = "Palavra reservada main esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.ABRE_PARENTESES) {
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), ABRE_PARENTESES_ESPERADO);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.FECHA_PARENTESES) {
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), FECHA_PARENTESES_ESPERADO);
        }

        this.getNextToken();
    }

    private void bloco() {
        if (token.getType() != TypeToken.ABRE_BLOCO) {
            var msg = "Abre chaves esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.escopo++;
        this.simbolo.adicionar(new Variavel(this.token, TypeToken.ABRE_BLOCO, this.escopo));
       // System.out.println(simbolo.getVariaveis());

        this.getNextToken();
        enquantoTemTipoPrimario();
        enquantoTemComando();

        if (token.getType() != TypeToken.FECHA_BLOCO) {
            var msg = "Fecha chaves esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
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
        if (!this.isComando())return;
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
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), PONTO_E_VIRGULA_ESPERADO);
        }
        this.getNextToken();
    }

    private void getIdentificador(Token auxToken) {
        this.getNextToken();
        if (token.getType() != TypeToken.IDENTIFICADOR) {
            var msg = "Identificador esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
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
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
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
                throw new ErrorSyntaxException(token.getLine(), token.getColumn(), ABRE_PARENTESES_ESPERADO);
            }

            getProximoToken();
            El();
            System.out.println("if " + "T" + contador + " == 0 goto" + " label_else_" + cont);
            this.ifContador++;

            if (token.getType() != TypeToken.FECHA_PARENTESES) {
                throw new ErrorSyntaxException(token.getLine(), token.getColumn(), FECHA_PARENTESES_ESPERADO);
            }

            this.getNextToken();
            this.comando();
            System.out.println("goto label_fim_if_" + cont);
            System.out.println("label_else_" + cont + ":");

            if (token.getType() == TypeToken.PR_ELSE) {
                this.getProximoToken();
                this.comando();
            }
            System.out.println("label_fim_if_" + cont + ":");
        } else {
            var msg = "Deveria ter um IF aqui";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
    }

    public void comandoDoWhile() {
        int cont = doWhileContador;
        System.out.println("label_doWhile_inicio_" + cont);
        this.getNextToken();
        if (!this.isComando()) {
            var msg = "Deveria ter um comando aqui";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.comando();

        if (token.getType() != TypeToken.PR_WHILE) {
            var msg = "Palavra reservada while esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.ABRE_PARENTESES) {
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), ABRE_PARENTESES_ESPERADO);
        }
        getProximoToken();
        if (this.isPrimeiroFator()) {
            El();
        } else {
            var msg = "Expressão relacional esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        if (token.getType() != TypeToken.FECHA_PARENTESES) {
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), FECHA_PARENTESES_ESPERADO);
        }
        this.getNextToken();
        if (token.getType() != TypeToken.PONTO_VIRGULA) {
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), PONTO_E_VIRGULA_ESPERADO);
        }
        this.getNextToken();
        System.out.println("if T" + contador + " != 0 goto label_doWhile_inicio_" + cont);
    }

    public void comandoWhile() {
        int cont = whileContador;
        System.out.println("label_while_inicio_" + cont + ":");
        this.getNextToken();
        if (token.getType() != TypeToken.ABRE_PARENTESES) {
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), ABRE_PARENTESES_ESPERADO);
        }

        getProximoToken();
        El();
        System.out.println("if T" + contador + " == 0 goto" + " label_while_fim_" + cont);
        this.whileContador++;

        if (token.getType() != TypeToken.FECHA_PARENTESES) {
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), FECHA_PARENTESES_ESPERADO);
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
            getProximoToken();
            Variavel calcularPai = calcularPai(ladoEsquerdo);
            verificarVariavel(calcularPai);
            atribuicaoLogica(calcularPai);
//            for (int i=0;i < listaAtrib.size();i+=3) {
////                ladoEsquerdo.setCodIter("T"+contador);
//                System.out.println("T"+ contador +" = " + listaAtrib.get(i).getLexema()+" "+listaAtrib.get(i+1).getLexema()+" "+listaAtrib.get(i+2).getLexema());
//                contador++;
//
//                if(i == 12){
//                    break;
//                }
//            }
            if (token.getType() != TypeToken.PONTO_VIRGULA) {
                throw new ErrorSyntaxException(token.getLine(), token.getColumn(), PONTO_E_VIRGULA_ESPERADO);
            }
            this.getNextToken();
        }
    }

    private void verificarVariavel(Variavel calcularPai) {
        if (calcularPai == null) {
            var msg = "Variavel não declarada";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        if (!verificarTipo(calcularPai)) {
            var msg = "Tipagem incorreta";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
    }

    private Variavel getLastSimbolo() {
        return this.simbolo.getVariaveis().get(this.simbolo.getVariaveis().size() - 1);
    }

    //    metodo para descobrir quem declarou essa variavel (quem eh o pai)
    private Variavel calcularPai(Token ladoEsquerdo) {
        Stream<Variavel> variavelStream = variaveisDeclaradas.stream().filter(f -> f.getToken().getLexema().equals(ladoEsquerdo.getLexema()));
        return variavelStream.reduce((first, second) -> second).orElse(null);
    }

    private void atribuicaoLogica(Variavel ultimoPaiDoTipo) {
        Token ladoEsquerdo = this.token;
        this.getNextToken();

        if (token.getType() == TypeToken.PONTO_VIRGULA) return;

        Token operador = identificarOperador();
        getProximoToken();

        if (operador.getType() == TypeToken.DIVISAO
                && this.token.getType() == TypeToken.INTEIRO
                && ladoEsquerdo.getType() == TypeToken.INTEIRO
                && ultimoPaiDoTipo.getTipo() == TypeToken.INTEIRO) {
            var msg = "Dividindo-se dois inteiros o tipo esperado é FLOAT";
            throw new ErrorSyntaxException(ultimoPaiDoTipo.getToken().getLine(), ultimoPaiDoTipo.getToken().getColumn(), msg);
        }

        verificarVariavel(ultimoPaiDoTipo);

        atribuicaoLogica(ultimoPaiDoTipo);

    }

    private boolean verificarTipo(Variavel ultimoPaiDoTipo) {
        var ultimaVariavelAdicionada = getLastSimbolo();
        if (ultimaVariavelAdicionada.getTipo() == TypeToken.IDENTIFICADOR)
            ultimaVariavelAdicionada = calcularPai(ultimaVariavelAdicionada.getToken());
        return (ultimaVariavelAdicionada.getTipo() == ultimoPaiDoTipo.getTipo()) || ((ultimaVariavelAdicionada.getTipo() == TypeToken.INTEIRO) && (ultimoPaiDoTipo.getTipo() == TypeToken.DECIMAL));
    }

    private void El() {
            Token ladoEsq = this.token;
        this.getNextToken();
        if (token != null && token.getType() != TypeToken.FECHA_PARENTESES) {
            Token operador = identificarOperador();
            getProximoToken();
            System.out.println(this.newTemp() + " = " + ladoEsq.getLexema() +  operador.getLexema() + this.token.getLexema());
            El();
        }
    }

    public void getProximoToken() {
        this.getNextToken();
        if (!this.isPrimeiroFator()) {
            var msg = "Esperado um IDENTIFICADOR, INTEIRO, FLOAT ou CARACTER";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        var variavel = new Variavel(this.token, token.getType(), this.escopo);
        this.simbolo.adicionar(variavel);
    }

    public Token identificarOperador() {
        if (!isOperadorRelacional() && !this.isExpressaoArit() && !this.isExpressaoTermo()) {
            var msg = "Esperado um OPERADOR";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        return this.token;
    }

        private String newTemp() {this.contador++; return "T" + this.contador;}

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

    private boolean isExpressaoArit() {
        return token.getType() == TypeToken.SOMA || token.getType() == TypeToken.SUBTRACAO;
    }

    private boolean isExpressaoTermo() {
        return token.getType() == TypeToken.MULTIPLICAO || token.getType() == TypeToken.DIVISAO;
    }

    private boolean isOperadorRelacional() {
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