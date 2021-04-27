package compilador_java;

public class Parser {
	private MeuScanner meuScanner;
	private Token token;
	private int escopo;
	
	public Parser(String arquivo) throws Exception {
		this.escopo = 0;
		this.meuScanner = new MeuScanner(arquivo);
		lerProximoToken();
		lerPrograma();
	}
	
	private void lerProximoToken() throws Exception {
		imprimirToken();
		this.token = this.meuScanner.proximoToken();
	}

	private void imprimirToken(){
		if (token != null)
			System.out.println(token.toString());
	}
	
	private boolean validarToken(int codigoToken) throws Exception {
		if (token != null && token.getCodigoToken() == codigoToken) {
			lerProximoToken();
			return true;
		} else
			return false;
	}

	private boolean validarToken(TipoToken identificador) throws Exception {
		if (token != null && token.getTipoToken() == identificador) {
			lerProximoToken();
			return true;
		} else
			return false;
	}

	private void lerPrograma() throws Exception {
		if (validarToken(CodigosTokens.INT)) {
			if (validarToken(CodigosTokens.MAIN)) {
				if (validarToken(CodigosTokens.ABRE_PARENTESES)) {
					if (validarToken(CodigosTokens.FECHA_PARENTESES)) {
						bloco();
					} else {
						String msg = "Token esperado ')'.";
						throw new Erros(token.getLinha(), token.getColuna(), msg);
					}
				} else {
					String msg = "Token esperado '('.";
					throw new Erros(token.getLinha(), token.getColuna(), msg);
				}
			} else {
				String msg = "Funcao main esperada.";
				throw new Erros(token.getLinha(), token.getColuna(), msg);
			}
		} else {
			String msg = "Palavra reservada 'int' esperada.";
			throw new Erros(token.getLinha(), token.getColuna(), msg);
		}
	}

	private ExpressaoSemantica bloco() throws Exception {
		
		if (!validarToken(CodigosTokens.ABRE_CHAVES)) {
			String msg = "Token esperado '{'.";
			throw new Erros(token.getLinha(), token.getColuna(), msg);
		}

		escopo++;

		while (token != null
				&& (token.getCodigoToken() == CodigosTokens.CHAR
				|| token.getCodigoToken() == CodigosTokens.FLOAT 
				|| token.getCodigoToken() == CodigosTokens.INT)) {
			lerDeclVariavel();
		}

		while (token != null
				&& (token.getCodigoToken() == CodigosTokens.WHILE
				|| token.getCodigoToken() == CodigosTokens.DO
				|| token.getCodigoToken() == CodigosTokens.IF
				|| token.getCodigoToken() == CodigosTokens.ABRE_CHAVES 
				|| token.getTipoToken() == TipoToken.Id)) {
			lerComando();
		}

		if (!validarToken(CodigosTokens.FECHA_CHAVES)) {
			String msg = "Token esperado '}'.";
			throw new Erros(token.getLinha(), token.getColuna(), msg);
		}

		//limpar simbolos
		TabelaDeSimbolos.excluirSimbolos(this.escopo);

		escopo--;

		return null;
	}

	private ExpressaoSemantica lerComando() throws Exception {
		if (token != null && (token.getTipoToken() == TipoToken.Id || token.getCodigoToken() == CodigosTokens.ABRE_CHAVES)) {
			return lerComandoBasico();
		}else if (token != null && (token.getCodigoToken() == CodigosTokens.WHILE || token.getCodigoToken() == CodigosTokens.DO)) {
			return lerIteracao();
		}else if (validarToken(CodigosTokens.IF)) {
			if (validarToken(CodigosTokens.ABRE_PARENTESES)) {
				lerExprRelacional();
				if (validarToken(CodigosTokens.FECHA_PARENTESES)) {
					lerComando();
					if (validarToken(CodigosTokens.ELSE)) {
						lerComando();
					}
				} else {
					String msg = "Token esperado ')'.";
					throw new Erros(token.getLinha(), token.getColuna(), msg);
				}				
			} else {
				String msg = "Token esperado '('.";
				throw new Erros(token.getLinha(), token.getColuna(), msg);
			}
		} else {
			String msg = "Esperado um comando if, while ou do, identificador ou token '{'.";
			throw new Erros(token.getLinha(), token.getColuna(), msg);
		}
		return null;
	}

	private ExpressaoSemantica lerExprRelacional() throws Exception {

		ExpressaoSemantica expr1 = lerExprAritmetica();

		String opera = PalavrasReservadas.getPalavra(token.getCodigoToken());

		if (!(validarToken(CodigosTokens.DIFERENTE)
				|| validarToken(CodigosTokens.IGUAL)
				|| validarToken(CodigosTokens.MAIOR)
				|| validarToken(CodigosTokens.MAIOR_IGUAL)
				|| validarToken(CodigosTokens.MENOR) 
				|| validarToken(CodigosTokens.MENOR_IGUAL))) {
			String msg = "Operador relacional esperado.";
			throw new Erros(token.getLinha(), token.getColuna(), msg);
		}

		ExpressaoSemantica expr2 = lerExprAritmetica();

		// converte p float
		converterParaFloat(expr1,expr2);
		
		// gera saida
		int tipo = coercaoTipos(expr1.getTipo(), expr2.getTipo());
		String var = expr1.getVarTemp() + opera + expr2.getVarTemp();
		
		//
		return new ExpressaoSemantica(var, tipo, opera);
	}

	private ExpressaoSemantica lerIteracao() throws Exception {
		if (validarToken(CodigosTokens.WHILE)) {
			if (validarToken(CodigosTokens.ABRE_PARENTESES)) {
				lerExprRelacional();
				if (validarToken(CodigosTokens.FECHA_PARENTESES)) {
					lerComando();
				} else {
					String msg = "Token ')' esperado.";
					throw new Erros(token.getLinha(), token.getColuna(), msg);
				}
			} else {
				String msg = "Token '(' esperado.";
				throw new Erros(token.getLinha(), token.getColuna(), msg);
			}
		} else if (validarToken(CodigosTokens.DO)) {
			lerComando();
			if (validarToken(CodigosTokens.WHILE)) {
				if (validarToken(CodigosTokens.ABRE_PARENTESES)) {
					lerExprRelacional();
					if (!validarToken(CodigosTokens.FECHA_PARENTESES)) {
						String msg = "Token ')' esperado.";
						throw new Erros(token.getLinha(), token.getColuna(), msg);
					}
					if (!validarToken(CodigosTokens.PONTO_E_VIRGULA)) {
						String msg = "Token ';' esperado.";
						throw new Erros(token.getLinha(), token.getColuna(), msg);
					}
				} else {
					String msg = "Token '(' esperado.";
					throw new Erros(token.getLinha(), token.getColuna(), msg);
				}
			} else {
				String msg = "Token 'while' esperado.";
				throw new Erros(token.getLinha(), token.getColuna(), msg);
			}
		} else {
			String msg = "Comando while ou do esperado.";
			throw new Erros(token.getLinha(), token.getColuna(), msg);
		}
		return null;
	}

	private ExpressaoSemantica lerComandoBasico() throws Exception {
		if (token.getTipoToken() == TipoToken.Id) {
			return lerAtribuicao();
		}
		else if (token.getCodigoToken() == CodigosTokens.ABRE_CHAVES) {
			return bloco();
		} else {
			String msg = "Identificador ou '{' esperado.";
			throw new Erros(token.getLinha(), token.getColuna(), msg);
		}
	}

	private ExpressaoSemantica lerAtribuicao() throws Exception {

		Token tk = this.token;

		if (!validarToken(TipoToken.Id)) {
			System.err.println("Identificador de Variavel esperado.");
		}

		if (!TabelaDeSimbolos.existeSimbolo(tk.getLexema())) {
			String msg = "Variavel " + tk.getLexema() + " nao declarada.";
			throw new Erros(tk.getLinha(), tk.getColuna(), msg);
		}

		if (!validarToken(CodigosTokens.ATRIBUICAO)) {
			System.err.println("Token '=' esperado.");
		}

		ExpressaoSemantica expr = lerExprAritmetica();
		Simbolo simb = TabelaDeSimbolos.getSimbolo(tk.getLexema(), escopo);
		if (simb.getTipo() != expr.getTipo()) {
			if (simb.getTipo() == CodigosTokens.FLOAT && expr.getTipo() == CodigosTokens.INT) {
				expr.setVarTemp(expr.getVarTemp());
			} else {
				String msg = "Tipos incompativeis.";
				throw new Erros(tk.getLinha(), tk.getColuna(), msg);
			}
		}

		if (!validarToken(CodigosTokens.PONTO_E_VIRGULA)) {
			System.err.println("Token ';' esperado.");
		}

		expr.setVarTemp(simb.getNome() + expr.getVarTemp());
		return expr;
	}

	private ExpressaoSemantica lerExprAritmetica() throws Exception {
		ExpressaoSemantica expr1 = lerTermo();
		ExpressaoSemantica expr2 = auxExprAritmetica();

		if (expr2 == null)
			return expr1;
		
		Object[] objeto = new Object[2];
		objeto = (Object[]) converterParaFloat(expr1,expr2,objeto);

		return new ExpressaoSemantica((String)objeto[1],(Integer)objeto[0]);
	}

	private ExpressaoSemantica auxExprAritmetica() throws Exception {
		if (validarToken(CodigosTokens.ADICAO)) {
			ExpressaoSemantica expr1 = lerTermo();
			ExpressaoSemantica expr2 = auxExprAritmetica();

			if (expr2 == null) {
				expr1.setVarTemp(expr1.getVarTemp());
				expr1.setOperador("+");
				return expr1;
			}

			Object[] objeto = new Object[2];
			objeto = (Object[]) converterParaFloat(expr1,expr2,objeto);

			return new ExpressaoSemantica((String)objeto[1],(Integer)objeto[0], "+");
			
		} else if (validarToken(CodigosTokens.SUBTRACAO)) {
			ExpressaoSemantica expr1 = lerTermo();
			ExpressaoSemantica expr2 = auxExprAritmetica();

			if (expr2 == null) {
				expr1.setVarTemp(expr1.getVarTemp());
				expr1.setOperador("-");
				return expr1;
			}

			Object[] objeto = new Object[2];
			objeto = (Object[]) converterParaFloat(expr1,expr2,objeto);

			return new ExpressaoSemantica((String)objeto[1],(Integer)objeto[0], "-");

		}
		return null;
	}

	private ExpressaoSemantica lerTermo() throws Exception {
		ExpressaoSemantica expr1 = lerFator();
		ExpressaoSemantica expr2 = auxTermo();

		if (expr2 == null)
			return expr1;

		Object[] objeto = new Object[2];
		objeto = (Object[]) converterParaFloat(expr1,expr2,objeto);
		
		if (expr2.getOperador() == "/" && (Integer)objeto[0] == CodigosTokens.INT){
			objeto[0] = CodigosTokens.FLOAT;
		}

		return new ExpressaoSemantica((String)objeto[1],(Integer)objeto[0]);
	}

	private ExpressaoSemantica auxTermo() throws Exception {
		if (validarToken(CodigosTokens.DIVISAO)) {

			ExpressaoSemantica expr1 = lerFator();
			ExpressaoSemantica expr2 = auxTermo();

			if (expr2 == null) {
				expr1.setVarTemp(expr1.getVarTemp());
				expr1.setOperador("/");
				return expr1;
			}

			Object[] objeto = new Object[2];
			objeto = (Object[]) converterParaFloat(expr1,expr2,objeto);
			
			return new ExpressaoSemantica((String)objeto[1],(Integer)objeto[0], "/");

		} else if (validarToken(CodigosTokens.MULTIPLICACAO)) {
			ExpressaoSemantica expr1 = lerFator();
			ExpressaoSemantica expr2 = auxTermo();

			if (expr2 == null) {
				expr1.setVarTemp(expr1.getVarTemp());
				expr1.setOperador("*");
				return expr1;
			}

			Object[] objeto = new Object[2];
			objeto = (Object[]) converterParaFloat(expr1,expr2,objeto);
			
			return new ExpressaoSemantica((String)objeto[1], (Integer)objeto[0], "*");
		}

		return null;
	}
		
	private Object converterParaFloat(ExpressaoSemantica expressao1, ExpressaoSemantica expressao2, Object objeto[]) throws Erros{
		if (expressao1.getTipo() == CodigosTokens.FLOAT && expressao2.getTipo() == CodigosTokens.INT) {
			expressao2.setVarTemp((expressao2.getVarTemp()));
		} else if (expressao2.getTipo() == CodigosTokens.FLOAT && expressao1.getTipo() == CodigosTokens.INT) {
			expressao1.setVarTemp((expressao1.getVarTemp()));
		}
		
		objeto[0] = coercaoTipos(expressao1.getTipo(), expressao2.getTipo());
		objeto[1] = expressao1.getVarTemp() + expressao2.getOperador() + expressao2.getVarTemp();
		
		return objeto;
	}
		
	private void converterParaFloat(ExpressaoSemantica expressao1, ExpressaoSemantica expressao2){
		if (expressao1.getTipo() == CodigosTokens.FLOAT && expressao2.getTipo() == CodigosTokens.INT) {
			expressao2.setVarTemp((expressao2.getVarTemp()));
		} else if (expressao2.getTipo() == CodigosTokens.FLOAT && expressao1.getTipo() == CodigosTokens.INT) {
			expressao1.setVarTemp((expressao1.getVarTemp()));
		}
	}

	private ExpressaoSemantica lerFator() throws Exception {
		Token tk = token;
		ExpressaoSemantica expressao = new ExpressaoSemantica();

		if (validarToken(CodigosTokens.ABRE_PARENTESES)) {
			expressao = lerExprAritmetica();
			if (!validarToken(CodigosTokens.FECHA_PARENTESES)) {
				String msg = "Token ')' esperado.";
				throw new Erros(tk.getLinha(), tk.getColuna(), msg);
			}

		} else if (validarToken(TipoToken.Id)) {
			if (!TabelaDeSimbolos.existeSimbolo(tk.getLexema())) {
				String msg = "Variavel " + tk.getLexema() + " nao declarada.";
				throw new Erros(tk.getLinha(), tk.getColuna(), msg);
			}
			
			Simbolo s = TabelaDeSimbolos.getSimbolo(tk.getLexema(), escopo);
			expressao.setTipo(s.getTipo());
			expressao.setVarTemp(s.getNome());

		} else if (validarToken(TipoToken.ConstanteChar)) {
			expressao.setTipo(CodigosTokens.CHAR);
			expressao.setVarTemp(tk.getLexema());

		} else if (validarToken(TipoToken.ConstanteFloat)) {
			expressao.setTipo(CodigosTokens.FLOAT);
			expressao.setVarTemp(tk.getLexema());

		} else if (validarToken(TipoToken.ConstanteInt)) {
			expressao.setTipo(CodigosTokens.INT);
			expressao.setVarTemp(tk.getLexema());

		} else {
			String msg = "Uma constante float, char, int, um nome de Variavel ou uma expressao aritmetica esperada.";
			throw new Erros(tk.getLinha(), tk.getColuna(), msg);
		}
		return expressao;
	}

	private void lerDeclVariavel() throws Exception {

		int codigoTipo = token.getCodigoToken();

		if (!(validarToken(CodigosTokens.CHAR)|| validarToken(CodigosTokens.INT) || validarToken(CodigosTokens.FLOAT))) {
			String msg = "Palavra chave int, float ou char esperado.";
			throw new Erros(token.getLinha(), token.getColuna(), msg);
		}

		auxLerVariavel(codigoTipo);

		while (validarToken(CodigosTokens.VIRGULA)) {
			auxLerVariavel(codigoTipo);
		}

		if (!validarToken(CodigosTokens.PONTO_E_VIRGULA)) System.err.println("Token ';' esperado.");
	}

	private void auxLerVariavel(int codigoTipo) throws Exception {
		Token auxToken = this.token;

		if (validarToken(TipoToken.Id)) {
			if (TabelaDeSimbolos.existeSimbolo(auxToken.getLexema(), escopo)) {
				String msg = "Variavel " + auxToken.getLexema() + " ja declarada.";
				throw new Erros(auxToken.getLinha(), auxToken.getColuna(), msg);
			}
			TabelaDeSimbolos.novoSimbolo(new Simbolo(codigoTipo, auxToken.getLexema(), escopo));
		} else System.err.println("Identificador da Variavel esperado.");
	}

	private int coercaoTipos(int tipo1, int tipo2) throws Erros {
		if (tipo1 == CodigosTokens.CHAR || tipo2 == CodigosTokens.CHAR) {
			String msg = "Operacao nao permitida para tipos caractere.";
			throw new Erros(token.getLinha(), token.getColuna(), msg);
		}
		
		if (tipo1 == CodigosTokens.FLOAT || tipo2 == CodigosTokens.FLOAT) {
			return CodigosTokens.FLOAT;
		}
		
		return CodigosTokens.INT;
	}

}
