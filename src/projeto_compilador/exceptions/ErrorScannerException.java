package projeto_compilador.exceptions;

public class ErrorScannerException extends RuntimeException {

  public ErrorScannerException(String pos, char caractere, String descricao) {
    super(("ERRO: " + pos + "\nULTIMO caractere LIDO: " + caractere + " " + descricao));
  }

}
