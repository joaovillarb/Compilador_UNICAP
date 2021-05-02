package projeto_compilador.exceptions;

public class ErrorSyntaxException extends RuntimeException {
  public ErrorSyntaxException(int line, int column, String msg) {
    super("ERRO na linha " + line + ", coluna " + column + ", ultimo token lido t: " + msg);
  }
}