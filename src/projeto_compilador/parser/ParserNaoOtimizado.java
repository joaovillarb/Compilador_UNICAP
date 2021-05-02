package projeto_compilador.parser;

import projeto_compilador.scanner.Scanner;

import projeto_compilador.TypeToken;
import projeto_compilador.Token;
import projeto_compilador.exceptions.ErrorSyntaxException;

public class ParserNaoOtimizado {

	private Scanner scanner;
	static Token token;
	private int doWhileContador;
	private int ifContador;
	private int whileContador;
	int tipo;
	String lex;

	public ParserNaoOtimizado(Scanner scanner) {
		this.scanner = scanner;
	}

	public void init() {
		execute();
	}

	private void getNextToken() {
		do {
			token = scanner.getNextToken();
		} while (token.getType() == TypeToken.COMENTARIO);
		System.out.println(token);
	}

	private void execute() {
		this.getNextToken();
		if (token.getType() != TypeToken.PR_INT) {
			String msg = "Palavra reservada int esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}

		this.getNextToken();
		if (token.getType() != TypeToken.PR_MAIN) {
			String msg = "Palavra reservada main esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}

		this.getNextToken();
		if (token.getType() != TypeToken.ABRE_PARENTESES) {
			String msg = "Abre parenteses esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}

		this.getNextToken();
		if (token.getType() != TypeToken.FECHA_PARENTESES) {
			String msg = "Fecha parenteses esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}

		this.getNextToken();
		block();

		if (token.getType() != TypeToken.ENDFILE) {
			String msg = "Fim de arquivo esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}

	}

	private void block() {
		if (token.getType() != TypeToken.ABRE_BLOCO) {
			String msg = "Abre chaves esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}

		this.getNextToken();
		recursiva();
		recursiva2();

		if (token.getType() != TypeToken.FECHA_BLOCO) {
			String msg = "Fecha chaves esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}

		this.getNextToken();

	}

	private void recursiva() {
		if (!this.isPrimaryType()) {
		} else {
			this.declareVariable();
			recursiva();
		}
	}

	private void recursiva2() {
		if (!this.isCommand()) {
		} else {
			this.command();
			recursiva2();
		}
	}

	private void recursiva3() {
		if (token.getType() != TypeToken.VIRGULA) {
		} else {
			this.getNextToken();
			if (token.getType() != TypeToken.IDENTIFICADOR) {
				String msg = "Identificador esperado";
				throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
			}
			this.getNextToken();
			recursiva3();
		}
	}

	private void declareVariable() {

		this.getNextToken();
		if (token.getType() != TypeToken.IDENTIFICADOR) {
			String msg = "Identificador esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}

		this.getNextToken();
		recursiva3();

		if (token.getType() != TypeToken.PONTO_VIRGULA) {
			String msg = "Ponto e virgula esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}
		this.getNextToken();

	}

	private void command() {
		if (this.isBasicCommand()) {
			System.out.println("basic command");
			this.basicCommand();
			// throw new ErrorSyntaxException("Ponto e virgula esperado");
		} else if (this.isIteration()) {
			System.out.println("iteration");
			this.iteracao();
		} else if (this.isCondition()) {
			System.out.println("condition");
			this.condicional();
		}
	}

	public void iteracao() {

		if (token.getType() == TypeToken.PR_WHILE) {
			this.comandoWhile();
		} else if (token.getType() == TypeToken.PR_DO) {
			this.comandoDoWhile();
		}
	}

	public void condicional() {
		if (token.getType() == TypeToken.PR_IF) {

			this.getNextToken();
			if (token.getType() != TypeToken.ABRE_PARENTESES) {
				String msg = "Abre parenteses esperado";
				throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
			}

			this.getNextToken();
			// this.expRelacional();

			if (token.getType() != TypeToken.FECHA_PARENTESES) {
				String msg = "Fecha parenteses esperado";
				throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
			}

			this.ifContador++;
			this.getNextToken();
			this.command();

			if (token.getType() == TypeToken.PR_ELSE) {
				this.getNextToken();
				this.command();
			}

		} else {
			String msg = "Deveria ter um IF aqui";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}
	}

	public void comandoDoWhile() {
		this.doWhileContador++;
		this.getNextToken();
		if (!this.isCommand()) {
			String msg = "Deveria ter um comando aqui";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}

		this.command();

		if (token.getType() != TypeToken.PR_WHILE) {
			String msg = "Palavra reservada while esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}

		this.getNextToken();
		if (token.getType() != TypeToken.ABRE_PARENTESES) {
			String msg = "Abre parenteses esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}
		this.getNextToken();
		if (this.primeiroFator()) {
			expRelacional();
		} else {
			String msg = "Expressão relacional esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}
		if (token.getType() != TypeToken.FECHA_PARENTESES) {
			String msg = "Fecha parenteses esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}
		this.getNextToken();
		if (token.getType() != TypeToken.PONTO_VIRGULA) {
			String msg = "Ponto e virgula esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}
		this.getNextToken();// proximo
	}

	public void comandoWhile() {
		this.getNextToken();
		if (token.getType() != TypeToken.ABRE_PARENTESES) {
			String msg = "Abre parenteses esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}
		this.getNextToken();
		this.expRelacional();
		if (token.getType() != TypeToken.FECHA_PARENTESES) {
			String msg = "Fecha parenteses esperado";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}
		this.whileContador++;
		this.getNextToken();
		this.command();
	}

	public void expRelacional() {
		Codigo esqRel, dirRel;
		esqRel = this.expAritmetica();

		if (this.isRelationalOperator()) {
			this.getNextToken();
			dirRel = this.expAritmetica();
		} else {
			String msg = "Tokens esperados: \">\" | \"<\" | \">=\" | \"<=\" | \"==\" | \"!=\"";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}
	}

	private Codigo exp() {
		Codigo ladoEsquerdo = null;
		Codigo ladoDireito;

		if (token.getType() == TypeToken.SOMA || token.getType() == TypeToken.SUBTRACAO) {
			this.getNextToken();
			ladoEsquerdo = this.termo();
			Token op = token;
			ladoDireito = this.exp();
			if (ladoDireito != null) {
				ladoEsquerdo.type = verificarTipo(ladoEsquerdo.getType(), ladoDireito.getType());
			} else {
				return ladoEsquerdo;
			}
		}
		return ladoEsquerdo;
	}

	public Codigo termo() {
		Codigo ladoEsquerdo = this.fator();
		Codigo ladoDireito;
		Token op;

		while (token.getType() == TypeToken.MULTIPLICAO || token.getType() == TypeToken.DIVISAO) {
			op = token;
			this.getNextToken();
			ladoDireito = this.fator();

			if (op.getType() == TypeToken.DIVISAO) {

				if (ladoEsquerdo.getType() == ladoDireito.getType()) {
					ladoDireito.type = TypeToken.CARACTER.getClasse();
				} else if ((ladoEsquerdo.getType() == TypeToken.INTEIRO.getClasse()
						&& ladoDireito.getType() == TypeToken.DECIMAL.getClasse())
						|| (ladoEsquerdo.getType() == TypeToken.DECIMAL.getClasse()
								|| ladoDireito.getType() == TypeToken.INTEIRO.getClasse())) {
					ladoDireito.type = TypeToken.DECIMAL.getClasse(); // converte em DECIMAL
				}
			}
			ladoEsquerdo.type = verificarTipo(ladoEsquerdo.getType(), ladoDireito.getType());
		}
		return ladoEsquerdo;
	}

	public Codigo fator() {
		Codigo code;
		tipo = token.getType().getClasse();
		lex = token.getLexema();

		if (token.getType() == TypeToken.IDENTIFICADOR) {

			if (primeiroFator() == false) {
				String msg = "Identificador esperado";
				throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
			}
			this.getNextToken();
		} else if (token.getType() == TypeToken.INTEIRO || token.getType() == TypeToken.DECIMAL
				|| token.getType() == TypeToken.CARACTER) {
			this.getNextToken();
		} else if (token.getType() == TypeToken.ABRE_PARENTESES) {

			this.getNextToken();
			Codigo exp = this.exp();
			Codigo termo = this.termo();

			if (exp != null) {
				termo.type = verificarTipo(termo.getType(), exp.getType());
			}

			code = termo;
			lex = code.getLexema();
			tipo = code.getType();

			if (token.getType() != TypeToken.FECHA_PARENTESES) {
				String msg = "Fecha parenteses esperado";
				throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
			}

			this.getNextToken();

		} else {
			String msg = "Esperado um IDENTIFICADOR, INTEIRO, FLOAT, CHAR ou ABRE PARENTESES";
			throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
		}

		code = new Codigo(tipo, lex);
		return code;
	}

	public Codigo expAritmetica() {
		Codigo ladoEsquerdo = this.termo();
		Codigo ladoDireito = this.exp();

		if (ladoDireito != null) {
			ladoEsquerdo.type = verificarTipo(ladoEsquerdo.getType(), ladoDireito.getType());
		}
		return ladoEsquerdo;
	}

	public int verificarTipo(int ladoEsquerdo, int ladoDireito) {

		if (ladoEsquerdo == ladoDireito) {
			return ladoEsquerdo;

		} else if ((ladoEsquerdo == TypeToken.DECIMAL.getClasse() && ladoDireito == TypeToken.INTEIRO.getClasse())
				|| (ladoEsquerdo == TypeToken.INTEIRO.getClasse() && ladoDireito == TypeToken.DECIMAL.getClasse())) {
			return TypeToken.DECIMAL.getClasse();

		} else if (ladoEsquerdo == TypeToken.CARACTER.getClasse() && ladoDireito == TypeToken.CARACTER.getClasse()) {
			return TypeToken.CARACTER.getClasse();

		}
		return -1;
	}

	private void basicCommand() {
		if (token.getType() == TypeToken.IDENTIFICADOR) {
			this.attribution();

		} else if (token.getType() == TypeToken.ABRE_BLOCO) {
			this.block();
		}
	}

	private void attribution() {
		this.getNextToken();
		if (token.getType() == TypeToken.ATRIBUICAO) {
			this.getNextToken();
			if (this.primeiroFator()) {
				// this.ladoDireitoeiroAtrib = this.expAritmetica();
				// if (this.ladoDireitoeiroAtrib.getType() == ClasseTokens.INTEIRO.getType()
				// && this.ladoEsquerdouerdoAtrib.getType() ==
				// ClasseTokens.DECIMAL.getType()) {
				// System.out.println(this.newTemp() + " = (float)" +
				// this.ladoDireitoeiroAtrib.getLexema());
				// this.ladoDireitoeiroAtrib.lexema = "T" + contador;
				// }

				// this.ladoEsquerdouerdoAtrib.type =
				// verificador.verificarAtributo(this.ladoEsquerdouerdoAtrib.getType(),
				// this.ladoDireitoeiroAtrib.getType());
				// if (this.ladoEsquerdouerdoAtrib.type == -1) {
				// this.error.tiposIcompativeis(this.scanner.getPosicaoArquivo().toString());
				// }
				this.getNextToken();

			} else {
				// this.error.tokenErrado(scanner.getPosicaoArquivo().toString(), "Expressão
				// aritmetica", token.getLexema());
			}

			if (token.getType() != TypeToken.PONTO_VIRGULA) {
				String msg = "Ponto e virgula esperado";
				throw new ErrorSyntaxException(token.getLine(), token.getColumn(), msg);
			}
			this.getNextToken();

		}

	}

	private boolean isPrimaryType() {
		return token.getType() == TypeToken.PR_CHAR || token.getType() == TypeToken.PR_FLOAT
				|| token.getType() == TypeToken.PR_INT;
	}

	private boolean isCommand() {
		return this.isIteration() || this.isBasicCommand() || this.isCondition();
	}

	private boolean isCondition() {
		return token.getType() == TypeToken.PR_IF;
	}

	private boolean isIteration() {
		return token.getType() == TypeToken.PR_WHILE || token.getType() == TypeToken.PR_DO;
	}

	private boolean isBasicCommand() {
		return token.getType() == TypeToken.IDENTIFICADOR || token.getType() == TypeToken.ABRE_BLOCO;
	}

	private boolean isRelationalOperator() {
		return token.getType() == TypeToken.IGUALDADE || token.getType() == TypeToken.DIFERENCA
				|| token.getType() == TypeToken.MAIOR_IGUAL || token.getType() == TypeToken.MENOR_IGUAL
				|| token.getType() == TypeToken.MENOR_QUE || token.getType() == TypeToken.MAIOR_QUE;
	}

	public boolean primeiroFator() {
		return token.getType() == TypeToken.ABRE_PARENTESES || token.getType() == TypeToken.IDENTIFICADOR
				|| token.getType() == TypeToken.INTEIRO || token.getType() == TypeToken.DECIMAL
				|| token.getType() == TypeToken.CARACTER;
	}

}
