package projeto_compilador.exceptions;

public class ErrorSyntaxException extends RuntimeException {
  public ErrorSyntaxException(String msg) {
    super(msg);
  }
}
