package projeto_compilador.parser;

public class Codigo {

	
	public int classe;
	public String lexema;
	private int op;
	
	public Codigo(int classe, String lexema){
		this.classe = classe;
		this.lexema = lexema;
		this.setOp(0);
	}
	
	public void setClasse(int classe) {
		this.classe = classe;
	}
	
	public int getClasse() {
		return classe;
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
