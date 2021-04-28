package projeto_compilador.parser;

import projeto_compilador.scanner.Scanner;

import projeto_compilador.ClasseTokens;
import projeto_compilador.Token;
import projeto_compilador.exceptions.ErrorSyntaxException;

public class Parser {

	private Scanner scanner;
	private Token token;
	private int whileContador;

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

		this.getNextToken();
		while (isPrimaryType()) {
			variableDeclaration();
		}

		while (this.primeiroComando()) {
			this.comando();
		}

		this.getNextToken();
		if (token.getClasse() != ClasseTokens.FECHA_BLOCO.getClasse()) {
			throw new ErrorSyntaxException("Fecha chaves Expected");
		}
	}

	private void variableDeclaration() {
		this.getNextToken();
		if (token.getClasse() == ClasseTokens.IDENTIFICADOR.getClasse()) {
			System.out.println("é identificador");
		}

		while (token.getClasse() == ClasseTokens.VIRGULA.getClasse()) {
			// repete tudo
		}

		this.getNextToken();
		if (token.getClasse() == ClasseTokens.PONTO_VIRGULA.getClasse()) {
			System.out.println("Ponto e virgula");
		}

		this.getNextToken();
	}

	private boolean isPrimaryType() {
		return token.getClasse() == ClasseTokens.PR_CHAR.getClasse()
				|| token.getClasse() == ClasseTokens.PR_FLOAT.getClasse()
				|| token.getClasse() == ClasseTokens.PR_INT.getClasse();
	}

	public boolean primeiroComando() {
		return this.primeiroIteracao() || this.primeiroComandoBasico()
				|| token.getClasse() == ClasseTokens.PR_IF.getClasse();
	}

	public boolean primeiroIteracao() {
		return token.getClasse() == ClasseTokens.PR_WHILE.getClasse()
				|| token.getClasse() == ClasseTokens.PR_DO.getClasse();
	}

	public boolean primeiroComandoBasico() {
		return token.getClasse() == ClasseTokens.IDENTIFICADOR.getClasse()
				|| token.getClasse() == ClasseTokens.ABRE_BLOCO.getClasse();
	}

	public void comando() {
		if (this.primeiroComandoBasico()) {
			this.comandoBasico();
		} else if (this.primeiroIteracao()) {
			// this.iteracao();
		} else {
			// this.condicional();
		}
	}


	public void comandoBasico() {
		if (token.getClasse() == ClasseTokens.IDENTIFICADOR.getClasse()) {
			// this.atribuicao();

		} else if (token.getClasse() == ClasseTokens.ABRE_BLOCO.getClasse()) {
			this.block();
		}
	}

	public void iteracao() {
		if (token.getClasse() == ClasseTokens.PR_WHILE.getClasse()) {
			//this.comandoWhile();
		} else if (token.getClasse() == ClasseTokens.PR_DO.getClasse()) {
			//this.comandoDoWhile();
		}
	}

//	public void comandoDoWhile() {
//		String expRel = null;
//		int cont = doWhileContador;
//		this.doWhileContador++;
//		this.getNextToken();
//		if (this.primeiroComando()) {
//			this.comando();
//		} else {
//			//error, deveria ter um comando aqui
//			this.error.tokenErrado(scanner.getPosicaoArquivo().toString(), "Comando(s)", token.getLexema());
//		}
//
//		if (token.getClasse() != ClasseTokens.PR_WHILE.getClasse()) {
//			//error, deveria ter um while aqui
//			this.error.tokenErrado(scanner.getPosicaoArquivo().toString(), "while", token.getLexema());
//		}
//
//		this.getNextToken();
//		if (token.getClasse() != ClasseTokens.ABRE_PARENTESES.getClasse()) {
//			//error, deveria ter um abre parenteses '('
//			this.error.tokenErrado(scanner.getPosicaoArquivo().toString(), "(", token.getLexema());
//		}
//		this.getNextToken();
//		if (this.primeiroExpRelacional()) {
//			expRel = this.expRelacional();
//		} else {
//			//error, deveria ter uma expressão relacional aqui
//			this.error.tokenErrado(scanner.getPosicaoArquivo().toString(), "Expressão Relacional", token.getLexema());
//		}
//		if (token.getClasse() != ClasseTokens.FECHA_PARENTESES.getClasse()) {
//			//error, aqui deveria ter uma fecha parenteses aqui
//			this.error.tokenErrado(scanner.getPosicaoArquivo().toString(), ")", token.getLexema());
//		}
//		this.getNextToken();
//		if (token.getClasse() != ClasseTokens.PONTO_VIRGULA.getClasse()) {
//			//error, aqui deveria ter uma ponto e virgula aqui
//			this.error.tokenErrado(scanner.getPosicaoArquivo().toString(), ";", token.getLexema());
//		}
//		this.getNextToken();//proximo
//	}
}
