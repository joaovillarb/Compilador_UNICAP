package projeto_compilador.exceptions;

public class ErrorParserException extends RuntimeException {
  public ErrorParserException(int line, int column, String msg) {
    super("ERRO na linha " + line + ", coluna " + column + ", ultimo token lido t: " + msg);
  }
}