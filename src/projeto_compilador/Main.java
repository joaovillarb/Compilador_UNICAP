package projeto_compilador;

import projeto_compilador.parser.Parser;
import projeto_compilador.scanner.Scanner;

public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Arquivo fonte nao especificado.");
		} else {
			String fonte = args[0];
			try {

				Scanner s = new Scanner(fonte);
				Parser p = new Parser(s);

				p.init();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
}