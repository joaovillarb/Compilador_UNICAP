package projeto_compilador.parser;

import projeto_compilador.scanner.Scanner;
import projeto_compilador.ClasseTokens;
import projeto_compilador.Token;

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

		if (token.getClasse() == ClasseTokens.PR_INT.getClasse()) {
			System.out.println("int");
		}

		this.getNextToken();
		if (token.getClasse() == ClasseTokens.PR_MAIN.getClasse()) {
			System.out.println("main");
		}

		this.getNextToken();
		if (token.getClasse() == ClasseTokens.ABRE_PARENTESES.getClasse()) {
			System.out.println("Abre parenteses");
		}

		this.getNextToken();
		if (token.getClasse() == ClasseTokens.FECHA_PARENTESES.getClasse()) {
			System.out.println("Fecha parenteses");
		}

	}

}
