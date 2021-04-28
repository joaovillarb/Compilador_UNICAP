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
			System.out.println("int");
			throw new ErrorSyntaxException("Palavra reservada int Expected");
		}

		this.getNextToken();
		if (token.getClasse() != ClasseTokens.PR_MAIN.getClasse()) {
			System.out.println("main");
			throw new ErrorSyntaxException("Palavra reservada main Expected");
		}

		this.getNextToken();
		if (token.getClasse() != ClasseTokens.ABRE_PARENTESES.getClasse()) {
			System.out.println("Abre parenteses");
			throw new ErrorSyntaxException("Palavra reservada abre parenteses Expected");
		}

		this.getNextToken();
		if (token.getClasse() != ClasseTokens.FECHA_PARENTESES.getClasse()) {
			System.out.println("Fecha parenteses");
			throw new ErrorSyntaxException("Palavra reservada fecha parenteses Expected");
		}

		block();

	}

	private void block() {
		this.getNextToken();
		if (token.getClasse() != ClasseTokens.ABRE_BLOCO.getClasse()) {
			System.out.println("Abre chaves");
			throw new ErrorSyntaxException("Palavra reservada abre chaves Expected");
		}

		this.getNextToken();
		if (token.getClasse() != ClasseTokens.FECHA_BLOCO.getClasse()) {
			System.out.println("Fecha chaves");
			throw new ErrorSyntaxException("Palavra reservada fecha chaves Expected");
		}
	}

}
