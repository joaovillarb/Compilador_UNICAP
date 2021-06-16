package projeto_compilador;

import projeto_compilador.parser.Parser;
import projeto_compilador.scanner.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Arquivo fonte nao especificado.");
        } else {
            String fonte = args[0];
            try {
                var scanner = new Scanner(fonte);
                var parser = new Parser(scanner);

                parser.init();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}