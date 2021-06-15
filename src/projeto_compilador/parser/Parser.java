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

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.simbolo = new Simbolo();
        this.variaveisDeclaradas = new ArrayList<>();
        this.listaAtrib = new ArrayList<>();
        this.escopo = 0;
    }

    public void init() {
        execute();
    }

    private void getNextToken() {
        do {
            token = scanner.getNextToken();
        } while (token.getType() == TypeToken.COMENTARIO);
        //System.out.println(token);
    }

    private void execute() {
        initial();


        block();
        if (token.getType() != TypeToken.ENDFILE) {
            String msg = "Fim de arquivo esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.simbolo.getVariaveis().remove(0);
        //System.out.println(simbolo.getVariaveis());


    }

    private void initial() {
        this.getNextToken();
        if (token.getType() != TypeToken.PR_INT) {
            String msg = "Palavra reservada int esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.PR_MAIN) {
            String msg = "Palavra reservada main esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.ABRE_PARENTESES) {
            String msg = "Abre parenteses esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.FECHA_PARENTESES) {
            String msg = "Fecha parenteses esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.getNextToken();
    }

    private void block() {
        if (token.getType() != TypeToken.ABRE_BLOCO) {
            String msg = "Abre chaves esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.escopo++;
        this.simbolo.adicionar(new Variavel(this.token, TypeToken.ABRE_BLOCO, this.escopo));
       // System.out.println(simbolo.getVariaveis());

        this.getNextToken();
        hasPrimaryType();
        hasCommand();

        if (token.getType() != TypeToken.FECHA_BLOCO) {
            String msg = "Fecha chaves esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        this.getNextToken();
        this.escopo--;
    }

    private void hasPrimaryType() {
        if (this.isPrimaryType()) {
            this.declareVariable();
            hasPrimaryType();
        }
    }

    private void hasCommand() {
        if (this.isCommand()) {
            this.command();
            hasCommand();
        }
    }

    private void hasComma(Token auxToken) {
        if (token.getType() == TypeToken.VIRGULA) {
            this.getNextToken();
            if (token.getType() != TypeToken.IDENTIFICADOR) {
                String msg = "Identificador esperado";
                throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
            }

            calcularDeclaracaoVariavel(auxToken);

            this.getNextToken();
            hasComma(auxToken);
        }
    }

    private void declareVariable() {
        Token auxToken = this.token;

        this.getNextToken();
        if (token.getType() != TypeToken.IDENTIFICADOR) {
            String msg = "Identificador esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        calcularDeclaracaoVariavel(auxToken);

        this.getNextToken();
        hasComma(auxToken);

        if (token.getType() != TypeToken.PONTO_VIRGULA) {
            String msg = "Ponto e virgula esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        this.getNextToken();
    }

    private void calcularDeclaracaoVariavel(Token auxToken) {
        Variavel variavel = new Variavel(this.token, token.getTypePalavraReservada(auxToken), this.escopo);

        Optional<Variavel> any = variaveisDeclaradas.stream().filter(
                f -> f.getEscopo() == this.escopo && f.getToken().getLexema().equals(variavel.getToken().getLexema())
        ).findAny();

        if (any.isPresent()) {
            String msg = "Lexema já declarado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        variaveisDeclaradas.add(variavel);
        //System.out.println(variaveisDeclaradas);
    }

    private void command() {
        if (this.isBasicCommand()) {
            this.basicCommand();
        } else if (this.isIteration()) {
            this.iteracao();
        } else if (this.isCondition()) {
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
                String msg = "Abre parenteses esperado";
                throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
            }

            T();
            El();
            System.out.println("if " + "T" + contador + " == 0 goto" + " label_else_" + cont);
            this.ifContador++;

            if (token.getType() != TypeToken.FECHA_PARENTESES) {
                String msg = "Fecha parenteses esperado";
                throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
            }

            this.getNextToken();
            this.command();
            System.out.println("goto label_fim_if_" + cont);
            System.out.println("label_else_" + cont + ":");

            if (token.getType() == TypeToken.PR_ELSE) {
                this.getNextToken();
                this.command();
            }
            System.out.println("label_fim_if_" + cont + ":");
        } else {
            String msg = "Deveria ter um IF aqui";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
    }

    public void comandoDoWhile() {
        int cont = doWhileContador;
        System.out.println("label_doWhile_inicio_" + cont);
        this.getNextToken();
        if (!this.isCommand()) {
            String msg = "Deveria ter um comando aqui";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.command();

        if (token.getType() != TypeToken.PR_WHILE) {
            String msg = "Palavra reservada while esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        this.getNextToken();
        if (token.getType() != TypeToken.ABRE_PARENTESES) {
            String msg = "Abre parenteses esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        T();
        if (this.isPrimaryFactor()) {
            El();
        } else {
            String msg = "Expressão relacional esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        if (token.getType() != TypeToken.FECHA_PARENTESES) {
            String msg = "Fecha parenteses esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        this.getNextToken();
        if (token.getType() != TypeToken.PONTO_VIRGULA) {
            String msg = "Ponto e virgula esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        this.getNextToken();
        System.out.println("if T" + contador + " != 0 goto label_doWhile_inicio_" + cont);
    }

    public void comandoWhile() {
        int cont = whileContador;
        System.out.println("label_while_inicio_" + cont + ":");
        this.getNextToken();
        if (token.getType() != TypeToken.ABRE_PARENTESES) {
            String msg = "Abre parenteses esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        T();

        El();
        System.out.println("if T" + contador + " == 0 goto" + " label_while_fim_" + cont);
        this.whileContador++;

        if (token.getType() != TypeToken.FECHA_PARENTESES) {
            String msg = "Fecha parenteses esperado";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        this.getNextToken();
        this.command();
        System.out.println("goto label_while_inicio_" + cont);
        System.out.println("label_while_fim_" + cont + ":");
    }

    private void basicCommand() {
        if (token.getType() == TypeToken.IDENTIFICADOR) {
            this.attribution();

        } else if (token.getType() == TypeToken.ABRE_BLOCO) {
            this.block();
        }
    }

    private void attribution() {
        Token ladoEsquerdo = this.token;

        this.getNextToken();
        if (token.getType() == TypeToken.ATRIBUICAO) {
            T();
            Variavel calcularPai = calcularPai(ladoEsquerdo);
            verificarVariavel(calcularPai);
            Al(calcularPai);
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
                String msg = "Ponto e virgula esperado";
                throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
            }
            this.getNextToken();
        }

    }

    private void verificarVariavel(Variavel calcularPai) {
        if (calcularPai == null) {
            String msg = "Variavel não declarada";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }

        if (!verificarTipo(calcularPai)) {
            String msg = "Tipagem incorreta";
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

    private void Al(Variavel ultimoPaiDoTipo) {
        Token ladoEsq = this.token;
        listaAtrib.add(ladoEsq);
        Token operador;
        this.getNextToken();
        if (token.getType() != TypeToken.PONTO_VIRGULA) {
            OP();
            operador = this.token;
            listaAtrib.add(operador);
            T();
            if(operador.getType() == TypeToken.DIVISAO){
                if(this.token.getType() == TypeToken.INTEIRO && ladoEsq.getType() == TypeToken.INTEIRO && ultimoPaiDoTipo.getTipo() == TypeToken.INTEIRO){
                    String msg = "Dividindo-se dois inteiros o tipo esperado é FLOAT";
                    throw new ErrorSyntaxException(ultimoPaiDoTipo.getToken().getLine(), ultimoPaiDoTipo.getToken().getColumn(), msg);
                }
            }
            listaAtrib.add(this.token);

            verificarVariavel(ultimoPaiDoTipo);

            Al(ultimoPaiDoTipo);
        }

    }

    private boolean verificarTipo(Variavel ultimoPaiDoTipo) {
        Variavel ultimaVariavelAdicionada = getLastSimbolo();
        if (ultimaVariavelAdicionada.getTipo() == TypeToken.IDENTIFICADOR)
            ultimaVariavelAdicionada = calcularPai(ultimaVariavelAdicionada.getToken());
//        if(ultimaVariavelAdicionada.getTipo() == TypeToken.INTEIRO && ultimoPaiDoTipo.getTipo() == TypeToken.DECIMAL){
//            System.out.println(this.newTemp() + " = (float)" + ultimaVariavelAdicionada.getToken().getLexema());
//        }
        return ultimaVariavelAdicionada.getTipo() == ultimoPaiDoTipo.getTipo() || (ultimaVariavelAdicionada.getTipo() == TypeToken.INTEIRO && ultimoPaiDoTipo.getTipo() == TypeToken.DECIMAL);
    }

    public void El() {
        Token ladoEsq = this.token;
        this.getNextToken();
        if (token != null) {
            if (token.getType() != TypeToken.FECHA_PARENTESES) {
                OP();
                Token operador = this.token;
                T();
                System.out.println(this.newTemp() + " = " + ladoEsq.getLexema() +  operador.getLexema() + this.token.getLexema());
                El();
            }
        }
    }

    public void T() {
        this.getNextToken();
        if (!this.isPrimaryFactor()) {
            String msg = "Esperado um IDENTIFICADOR, INTEIRO, FLOAT ou CARACTER";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
        Variavel variavel = new Variavel(this.token, token.getType(), this.escopo);
        this.simbolo.adicionar(variavel);
    }

    public void OP() {
        if (!isRelationalOperator() && !this.isAritExpression() && !this.isTermExpression()) {
            String msg = "Esperado um OPERADOR";
            throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
        }
    }

    private String newTemp() {this.contador++; return "T" + this.contador;}

    private boolean isCommand() {
        return this.isIteration() || this.isBasicCommand() || this.isCondition();
    }

    private boolean isCondition() {
        return token.getType() == TypeToken.PR_IF;
    }

    private boolean isIteration() {
        return token.getType() == TypeToken.PR_WHILE || token.getType() == TypeToken.PR_DO;
    }

    private boolean isBasicCommand() {
        return token.getType() == TypeToken.IDENTIFICADOR || token.getType() == TypeToken.ABRE_BLOCO;
    }

    private boolean isAritExpression() {
        return token.getType() == TypeToken.SOMA || token.getType() == TypeToken.SUBTRACAO;
    }

    private boolean isTermExpression() {
        return token.getType() == TypeToken.MULTIPLICAO || token.getType() == TypeToken.DIVISAO;
    }

    private boolean isRelationalOperator() {
        return token.getType() == TypeToken.IGUALDADE || token.getType() == TypeToken.DIFERENCA
                || token.getType() == TypeToken.MAIOR_IGUAL || token.getType() == TypeToken.MENOR_IGUAL
                || token.getType() == TypeToken.MENOR_QUE || token.getType() == TypeToken.MAIOR_QUE;
    }

    public boolean isPrimaryFactor() {
        return token.getType() == TypeToken.ABRE_PARENTESES || token.getType() == TypeToken.IDENTIFICADOR
                || token.getType() == TypeToken.INTEIRO || token.getType() == TypeToken.DECIMAL
                || token.getType() == TypeToken.CARACTER;
    }

    private boolean isPrimaryType() {
        return token.getType() == TypeToken.PR_CHAR || token.getType() == TypeToken.PR_FLOAT
                || token.getType() == TypeToken.PR_INT;
    }

}