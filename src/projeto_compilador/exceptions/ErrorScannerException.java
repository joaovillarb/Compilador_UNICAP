package projeto_compilador.exceptions;

public class ErrorScannerException extends RuntimeException {

  private final String COR_VERDE = "\033[0m";
  private final String COR_VERDE2 = "\033[1;32m";
  private final String COR_VERMELHA = "\033[91m";

  public ErrorScannerException(String pos, char caractere, String descricao) {
    super(("ERRO: " + pos + "\nULTIMO caractere LIDO: " + caractere + " " + descricao));
  }
}
