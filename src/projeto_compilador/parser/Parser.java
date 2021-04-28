package projeto_compilador.parser;

import projeto_compilador.scanner.Scanner;
import projeto_compilador.ClasseTokens;
import projeto_compilador.Token;
import projeto_compilador.exceptions.ErrorSyntaxException;

public class Parser {

	private Scanner scanner;
	private Token token;

	public Parser(Scanner scanner) throws Exception {
		this.scanner = scanner;
	}

	public void init() throws Exception {
		execute();
	}

	private void getNextToken() {
		token = scanner.getNextToken();
		System.out.println(token);
	}

	private void execute() {
		this.getNextToken();
		if (token.getClasse() != ClasseTokens.PR_INT.getClasse()) {
			throw new ErrorSyntaxException("Palavra reservada int Expected");
		}

		this.getNextToken();
		if (token.getClasse() != ClasseTokens.PR_MAIN.getClasse()) {
			throw new ErrorSyntaxException("Palavra reservada main Expected");
		}

		this.getNextToken();
		if (token.getClasse() != ClasseTokens.ABRE_PARENTESES.getClasse()) {
			throw new ErrorSyntaxException("Abre parenteses Expected");
		}

		this.getNextToken();
		if (token.getClasse() != ClasseTokens.FECHA_PARENTESES.getClasse()) {
			throw new ErrorSyntaxException("Fecha parenteses Expected");
		}

		block();

	}

	private void block() {
		this.getNextToken();
		if (token.getClasse() != ClasseTokens.ABRE_BLOCO.getClasse()) {
			throw new ErrorSyntaxException("Abre chaves Expected");
		}

		while (token != null && isPrimaryType()) {
			// lerDeclVariavel();
		}

		this.getNextToken();
		if (token.getClasse() != ClasseTokens.FECHA_BLOCO.getClasse()) {
			throw new ErrorSyntaxException("Fecha chaves Expected");
		}
	}

	private boolean isPrimaryType() {
		return token.getClasse() == ClasseTokens.PR_CHAR.getClasse()
				|| token.getClasse() == ClasseTokens.PR_FLOAT.getClasse()
				|| token.getClasse() == ClasseTokens.PR_INT.getClasse();
	}

}
