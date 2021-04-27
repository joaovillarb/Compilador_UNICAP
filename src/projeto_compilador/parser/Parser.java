package projeto_compilador.parser;

import projeto_compilador.scanner.Scanner;

import projeto_compilador.Token;

public class Parser {

	private Scanner scanner;
	private Token token;

	public Parser(Scanner scanner) throws Exception {
		this.scanner = scanner;
	}

	public void init() throws Exception {

		do {
			token = scanner.getNextToken();
			System.out.println("tokenID: " + token.getClasse() + " | " + "lexema: " + token.getLexema() + "| "
					+ "Linha: " + token.getLine() + "| " + "coluna: " + token.getColumn());
		} while (!scanner.endFile());

	}

}
