package projeto_compilador.parser;

public class Codigo {

	public int type;
	public String lexema;
	private int op;

	public Codigo(int type, String lexema) {
		this.type = type;
		this.lexema = lexema;
		this.setOp(0);
	}

	public void setClasse(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

	public String getLexema() {
		return lexema;
	}

	public void setOp(int op) {
		this.op = op;
	}

	public int getOp() {
		return op;
	}

}
