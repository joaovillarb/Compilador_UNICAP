package projeto_compilador.parser;

import projeto_compilador.scanner.Scanner;

import projeto_compilador.TypeToken;
import projeto_compilador.Token;
import projeto_compilador.exceptions.ErrorSyntaxException;

public class ParserOtimizado {

  private Scanner scanner;
  static Token token;
  private int doWhileContador;
  private int ifContador;
  private int whileContador;
  int tipo;
  String lex;

  public ParserOtimizado(Scanner scanner) {
    this.scanner = scanner;
  }

  public void init() {
    execute();
  }

  private void getNextToken() {
    do {
      token = scanner.getNextToken();
    } while (token.getType() == TypeToken.COMENTARIO);
    System.out.println(token);
  }

  private void execute() {
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
    block();

    if (token.getType() != TypeToken.ENDFILE) {
      String msg = "Fim de arquivo esperado";
      throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    }

  }

  private void block() {
    if (token.getType() != TypeToken.ABRE_BLOCO) {
      String msg = "Abre chaves esperado";
      throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    }

    this.getNextToken();
    recursiva();
    recursiva2();

    if (token.getType() != TypeToken.FECHA_BLOCO) {
      String msg = "Fecha chaves esperado";
      throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    }

    this.getNextToken();

  }

  private void recursiva() {
    if (!this.isPrimaryType()) {
    } else {
      this.declareVariable();
      recursiva();
    }
  }

  private void recursiva2() {
    if (!this.isCommand()) {
    } else {
      this.command();
      recursiva2();
    }
  }

  private void recursiva3() {
    if (token.getType() != TypeToken.VIRGULA) {
    } else {
      this.getNextToken();
      if (token.getType() != TypeToken.IDENTIFICADOR) {
        String msg = "Identificador esperado";
        throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
      }
      this.getNextToken();
      recursiva3();
    }
  }

  private void declareVariable() {

    this.getNextToken();
    if (token.getType() != TypeToken.IDENTIFICADOR) {
      String msg = "Identificador esperado";
      throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    }

    this.getNextToken();
    recursiva3();

    if (token.getType() != TypeToken.PONTO_VIRGULA) {
      String msg = "Ponto e virgula esperado";
      throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    }
    this.getNextToken();

  }

  private void command() {
    if (this.isBasicCommand()) {
      System.out.println("basic command");
      this.basicCommand();
      // throw new ErrorSyntaxException("Ponto e virgula esperado");
    } else if (this.isIteration()) {
      System.out.println("iteration");
      this.iteracao();
    } else if (this.isCondition()) {
      System.out.println("condition");
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
    // if (token.getType() == TypeToken.PR_IF) {

    this.getNextToken();
    if (token.getType() != TypeToken.ABRE_PARENTESES) {
      String msg = "Abre parenteses esperado";
      throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    }

    T();
    El();

    // this.getNextToken();
    // this.expRelacional();

    if (token.getType() != TypeToken.FECHA_PARENTESES) {
      String msg = "Fecha parenteses esperado";
      throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    }

    this.ifContador++;
    this.getNextToken();
    this.command();

    if (token.getType() == TypeToken.PR_ELSE) {
      this.getNextToken();
      this.command();
    }

    // }
    // else {
    // String msg = "Deveria ter um IF aqui";
    // throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    // }
  }

  public void comandoDoWhile() {
    this.doWhileContador++;
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
    // this.getNextToken();
    T();
    if (this.primeiroFator()) {
      El();
      // expRelacional();
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
    this.getNextToken();// proximo
  }

  public void comandoWhile() {
    this.getNextToken();
    if (token.getType() != TypeToken.ABRE_PARENTESES) {
      String msg = "Abre parenteses esperado";
      throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    }
    // this.getNextToken();
    // this.expRelacional();

    T();
    El();

    if (token.getType() != TypeToken.FECHA_PARENTESES) {
      String msg = "Fecha parenteses esperado";
      throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    }
    this.whileContador++;
    this.getNextToken();
    this.command();
  }

  private void basicCommand() {
    if (token.getType() == TypeToken.IDENTIFICADOR) {
      this.attribution();

    } else if (token.getType() == TypeToken.ABRE_BLOCO) {
      this.block();
    }
  }

  private void attribution() {
    this.getNextToken();
    if (token.getType() == TypeToken.ATRIBUICAO) {
      T();
      Al();
      if (token.getType() != TypeToken.PONTO_VIRGULA) {
        String msg = "Ponto e virgula esperado";
        throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
      }
      this.getNextToken();
    }

  }

  private void Al() {
    this.getNextToken();
    if (token.getType() == TypeToken.PONTO_VIRGULA) {
    } else {
      OP();
      T();
      Al();
    }
  }

  public void El() {
    this.getNextToken();
    if (token != null) {
      if (token.getType() != TypeToken.FECHA_PARENTESES) {
        OP();
        T();
        El();
      }
    }
  }

  public void T() {
    this.getNextToken();
    if (!this.primeiroFator()) {
      String msg = "Esperado um IDENTIFICADOR, INTEIRO, FLOAT ou CARACTER";
      throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    }
  }

  public void OP() {
    if (!isRelationalOperator() && !this.isAritExpression() && !this.isTermExpression()) {
      String msg = "Esperado um OPERADOR";
      throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
    }
  }

  private boolean isPrimaryType() {
    return token.getType() == TypeToken.PR_CHAR || token.getType() == TypeToken.PR_FLOAT
        || token.getType() == TypeToken.PR_INT;
  }

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

  public boolean primeiroFator() {
    return token.getType() == TypeToken.ABRE_PARENTESES || token.getType() == TypeToken.IDENTIFICADOR
        || token.getType() == TypeToken.INTEIRO || token.getType() == TypeToken.DECIMAL
        || token.getType() == TypeToken.CARACTER;
  }

}
