package projeto_compilador.parser;

import projeto_compilador.scanner.Scanner;

import projeto_compilador.TypeToken;
import projeto_compilador.Token;
import projeto_compilador.exceptions.ErrorSyntaxException;

public class Parser {

	private Scanner scanner;
	private Token token;

	public Parser(Scanner scanner) {
		this.scanner = scanner;
	}

	public void init() {
		execute();
	}

	private void getNextToken() {
		token = scanner.getNextToken();
		System.out.println(token);
	}

	private void execute() {
		this.getNextToken();
		if (token.getClasse() != TypeToken.PR_INT) {
			throw new ErrorSyntaxException("Palavra reservada int esperado");
		}

		this.getNextToken();
		if (token.getClasse() != TypeToken.PR_MAIN) {
			throw new ErrorSyntaxException("Palavra reservada main esperado");
		}

		this.getNextToken();
		if (token.getClasse() != TypeToken.ABRE_PARENTESES) {
			throw new ErrorSyntaxException("Abre parenteses esperado");
		}

		this.getNextToken();
		if (token.getClasse() != TypeToken.FECHA_PARENTESES) {
			throw new ErrorSyntaxException("Fecha parenteses esperado");
		}

		block();

	}

	private void block() {
		this.getNextToken();
		if (token.getClasse() != TypeToken.ABRE_BLOCO) {
			throw new ErrorSyntaxException("Abre chaves esperado");
		}

		this.getNextToken();
		while (this.isPrimaryType()) {
			this.declareVariable();
			this.getNextToken();
		}

		while (this.isCommand()) {
			System.out.println("its command");
			this.command();
			// throw new ErrorSyntaxException("sair");
		}

		// this.getNextToken();
		if (token.getClasse() != TypeToken.FECHA_BLOCO) {
			throw new ErrorSyntaxException("Fecha chaves esperado");
		}

		this.getNextToken();

	}

	private void declareVariable() {
		this.getNextToken();
		if (token.getClasse() != TypeToken.IDENTIFICADOR) {
			throw new ErrorSyntaxException("Identificador esperado");
		}

		this.getNextToken();
		while (token.getClasse() == TypeToken.VIRGULA) {

			this.getNextToken();
			if (token.getClasse() != TypeToken.IDENTIFICADOR) {
				throw new ErrorSyntaxException("Identificador nao esperado");
			}

			this.getNextToken();
		}

		if (token.getClasse() != TypeToken.PONTO_VIRGULA) {
			throw new ErrorSyntaxException("Ponto e virgula esperado");
		}

	}

	private void command() {
		if (this.isBasicCommand()) {
			System.out.println("basic command");

			this.basicCommand();
		} else if (this.isIteration()) {
			// this.iteracao();
			System.out.println("iteration");
		} else if (this.isCondition()) {
			System.out.println("condition");
			// this.condicional();

			this.getNextToken();
			if (token.getClasse() != TypeToken.ABRE_PARENTESES) {
				throw new ErrorSyntaxException("Abre parenteses esperado");
			}

			// aqui entra a expressao relacional

			this.getNextToken();
			if (token.getClasse() != TypeToken.FECHA_PARENTESES) {
				throw new ErrorSyntaxException("Fecha parenteses esperado");
			}

			block();

		} else {
			throw new ErrorSyntaxException("Esperado um comando IF, WHILE ou DO");
		}
	}

	private void basicCommand() {
		if (token.getClasse() == TypeToken.IDENTIFICADOR) {
			// this.atribuicao();

		} else if (token.getClasse() == TypeToken.ABRE_BLOCO) {
			this.block();
		}
	}

	private boolean isPrimaryType() {
		return token.getClasse() == TypeToken.PR_CHAR || token.getClasse() == TypeToken.PR_FLOAT
				|| token.getClasse() == TypeToken.PR_INT;
	}

	private boolean isCommand() {
		return this.isIteration() || this.isBasicCommand() || this.isCondition();
	}

	private boolean isCondition() {
		return token.getClasse() == TypeToken.PR_IF;
	}

	private boolean isIteration() {
		return token.getClasse() == TypeToken.PR_WHILE || token.getClasse() == TypeToken.PR_DO;
	}

	private boolean isBasicCommand() {
		return token.getClasse() == TypeToken.IDENTIFICADOR || token.getClasse() == TypeToken.ABRE_BLOCO;
	}

	private boolean isRelationalOperator() {
		return token.getClasse() == TypeToken.IGUALDADE || token.getClasse() == TypeToken.DIFERENCA
				|| token.getClasse() == TypeToken.MAIOR_IGUAL || token.getClasse() == TypeToken.MENOR_IGUAL
				|| token.getClasse() == TypeToken.MENOR_QUE || token.getClasse() == TypeToken.MAIOR_QUE;
	}

}
