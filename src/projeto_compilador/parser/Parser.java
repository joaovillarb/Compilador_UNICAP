package projeto_compilador.parser;

import projeto_compilador.scanner.Scanner;

import projeto_compilador.TypeToken;
import projeto_compilador.Token;
import projeto_compilador.exceptions.ErrorSyntaxException;

public class Parser {

	private Scanner scanner;
	static Token token;
	private int doWhileContador;
	private int ifContador;
	private int whileContador;
	int tipo;
	String lex;


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
			this.iteracao();
			System.out.println("iteration");
		} else if (this.isCondition()) {
			System.out.println("condition");
			this.condicional();

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

	public void iteracao() {

		if( token.getClasse() == TypeToken.PR_WHILE ) {			
			this.comandoWhile();
		}else
		if( token.getClasse() == TypeToken.PR_DO ) {
			this.comandoDoWhile();
		}
	}

	public void condicional() {
		if( token.getClasse() == TypeToken.PR_IF ) {

			this.getNextToken();
			if( token.getClasse() != TypeToken.ABRE_PARENTESES ) {
				throw new ErrorSyntaxException("Abre parenteses esperado");
			}

			this.getNextToken();
			this.expRelacional();

			if( token.getClasse() != TypeToken.FECHA_PARENTESES ) {
				throw new ErrorSyntaxException("Fecha parenteses esperado");
			}

			this.ifContador++;
			this.getNextToken();
			this.command();


			if( token.getClasse() == TypeToken.PR_ELSE ) {
				this.getNextToken();
				this.command();
			}

		}else {
			throw new ErrorSyntaxException("Deveria ter um IF aqui");
		}
	}

	public void comandoDoWhile() {
		this.doWhileContador++;
		this.getNextToken();
		if( this.isCommand() ) {
			this.command();
		}else {			
			throw new ErrorSyntaxException("Deveria ter um comando aqui");
		}

		if( token.getClasse() != TypeToken.PR_WHILE ) {			
			throw new ErrorSyntaxException("Deveria ter um WHILE aqui");
		}

		this.getNextToken();
		if(token.getClasse() != TypeToken.ABRE_PARENTESES) {
			throw new ErrorSyntaxException("Deveria ter um abre parenteses '(' aqui");
		}
		this.getNextToken();
		if( this.primeiroFator() ) {
			expRelacional();
		}else {
			throw new ErrorSyntaxException("deveria ter uma expressão relacional aqui");
		}
		if( token.getClasse() != TypeToken.FECHA_PARENTESES ) {
			throw new ErrorSyntaxException("Deveria ter uma fecha parenteses ')' aqui");
		}
		this.getNextToken();
		if( token.getClasse() != TypeToken.PONTO_VIRGULA) {			
			throw new ErrorSyntaxException("Deveria ter uma ponto e virgula ';' aqui");
		}
		this.getNextToken();//proximo
	}

	public void comandoWhile() {
		if( token.getClasse() != TypeToken.ABRE_PARENTESES ) {
			throw new ErrorSyntaxException("Abre parenteses esperado");
		}
		this.getNextToken();
		this.expRelacional();
		if( token.getClasse() != TypeToken.FECHA_PARENTESES ) {
			throw new ErrorSyntaxException("Fecha parenteses esperado");
		}
		this.whileContador++;
		this.getNextToken();
		this.command();
	}

	public void expRelacional() {
		Codigo esqRel, dirRel;
		esqRel = this.expAritmetica();

		if( this.isOpRelacional() ) {
			this.getNextToken();
			dirRel = this.expAritmetica();
		}
	}

	private Codigo exp() {
		Codigo ladoEsquerdo = null;
		Codigo ladoDireito;

		if( token.getClasse() == TypeToken.SOMA || token.getClasse() == TypeToken.SUBTRACAO ) {
			this.getNextToken();
			ladoEsquerdo = this.termo();
			Token op = token;
			ladoDireito = this.exp();
			if(ladoDireito != null) {
				ladoEsquerdo.classe =  verificarTipo(ladoEsquerdo.getClasse(), ladoDireito.getClasse());
			}else {
				return ladoEsquerdo;
			}
		}
		return ladoEsquerdo;
	}

	public Codigo termo() {
		Codigo ladoEsquerdo = this.fator();
		Codigo ladoDireito;
		Token op;

		while( token.getClasse() == TypeToken.MULTIPLICAO || token.getClasse() == TypeToken.DIVISAO ) {
			op = token;
			this.getNextToken();
			ladoDireito = this.fator();

			if(op.getClasse() == TypeToken.DIVISAO ) {

				if(ladoEsquerdo.getClasse() ==  ladoDireito.getClasse() ) {
					ladoDireito.classe = TypeToken.CARACTER.getClasse();
				}else
				if( (ladoEsquerdo.getClasse() == TypeToken.INTEIRO.getClasse() && ladoDireito.getClasse() == TypeToken.DECIMAL.getClasse()) || (ladoEsquerdo.getClasse() == TypeToken.DECIMAL.getClasse() || ladoDireito.getClasse() == TypeToken.INTEIRO.getClasse()) ) {
					ladoDireito.classe = TypeToken.DECIMAL.getClasse(); //converte em DECIMAL
				}
			}
			ladoEsquerdo.classe = verificarTipo(ladoEsquerdo.getClasse(), ladoDireito.getClasse());
		}
		return ladoEsquerdo;
	}


	public Codigo fator() {
		Codigo code;
		tipo = token.getClasse().getClasse();
		lex = token.getLexema();

		if( token.getClasse() == TypeToken.IDENTIFICADOR) {

			if(isPrimaryType() == false){
				throw new ErrorSyntaxException("Identificador nao encontrado");
			}
			this.getNextToken();
		}else
		if( token.getClasse() == TypeToken.INTEIRO ||  token.getClasse() == TypeToken.DECIMAL || token.getClasse() == TypeToken.CARACTER ) {
			this.getNextToken();
		}else
		if( token.getClasse() == TypeToken.ABRE_PARENTESES ) {

			this.getNextToken();
			Codigo exp = this.exp();
			Codigo termo = this.termo();

			if(exp != null) {
				termo.classe = verificarTipo(termo.getClasse(), exp.getClasse());
			}

			code = termo;
			lex = code.getLexema();
			tipo = code.getClasse();

			if( token.getClasse() != TypeToken.FECHA_PARENTESES ) {
				throw new ErrorSyntaxException("Fecha parenteses esperado");
			}

			this.getNextToken();

		}else {
			throw new ErrorSyntaxException("Era esperado uma variavel, '(' ou [interiro, float, char]");
		}

		code = new Codigo(tipo, lex);
		return code;
	}

	public Codigo expAritmetica() {
		Codigo ladoEsquerdo = this.termo();
		Codigo ladoDireito = this.exp();

		if(ladoDireito != null) {
			ladoEsquerdo.classe = verificarTipo(ladoEsquerdo.getClasse(), ladoDireito.getClasse());
		}
		return ladoEsquerdo;
	}

	public int verificarTipo(int ladoEsquerdo, int ladoDireito) {
		
		if(ladoEsquerdo == ladoDireito) {
			return ladoEsquerdo;

		}else if( (ladoEsquerdo == TypeToken.DECIMAL.getClasse() && ladoDireito == TypeToken.INTEIRO.getClasse()) || (ladoEsquerdo == TypeToken.INTEIRO.getClasse() && ladoDireito == TypeToken.DECIMAL.getClasse()) ) {
			return TypeToken.DECIMAL.getClasse();

		}else if(ladoEsquerdo == TypeToken.CARACTER.getClasse() && ladoDireito == TypeToken.CARACTER.getClasse()) {
			return TypeToken.CARACTER.getClasse();

		}
		return -1;
	}

	public boolean isOpRelacional() {
		if(token.getClasse() == TypeToken.IGUALDADE) {
			return true;
		}else
		if(token.getClasse() == TypeToken.DIFERENCA) {
			return true;
		}else
		if(token.getClasse() == TypeToken.MAIOR_IGUAL) {
			return true;
		}else
		if(token.getClasse() == TypeToken.MENOR_IGUAL) {
			return true;
		}else
		if(token.getClasse() == TypeToken.MENOR_QUE) {
			return true;
		}else
		if(token.getClasse() == TypeToken.MAIOR_QUE) {
			return true;
		}else {
			throw new ErrorSyntaxException("Tokens esperados: \">\" | \"<\" | \">=\" | \"<=\" | \"==\" | \"!=\"");
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

	public boolean primeiroFator() {
		return token.getClasse() == TypeToken.ABRE_PARENTESES
				|| token.getClasse() == TypeToken.IDENTIFICADOR
				|| token.getClasse() == TypeToken.INTEIRO
				|| token.getClasse() == TypeToken.DECIMAL
				|| token.getClasse() == TypeToken.CARACTER;
	}


}
