package projeto_compilador.scanner;

import java.util.LinkedList;

public class ErroScanner {

    private final String COR_VERDE = "\033[0m";
    private final String COR_VERDE2 = "\033[1;32m";
    private final String COR_VERMELHA = "\033[91m";

    private LinkedList<String> ErroLista;

    public ErroScanner() {
        this.ErroLista = new LinkedList<>();
    }

    public void mostrarTodosOsErrors() {
        for (String log : this.ErroLista) {
            System.out.println(log);
        }
    }

    public void Errorlog(int op, String pos, char caractere) {
        String msg = COR_VERMELHA + "ERRO: " + pos + COR_VERDE2 + "\nULTIMO caractere LIDO: ";
        switch (op) {
        case 1:
            System.out.println(msg + caractere + "\nCARACTERE INVALIDO, AQUI DEVERIA TER UM NUMERO\n\n" + COR_VERDE);
            System.exit(0);
            break;
        case 2:
            System.out.println(msg + caractere + "\nDEVE-SE USAR !=, PARA VERIFICAR UMA DIFERENCA\n\n" + COR_VERDE);
            System.exit(0);
            break;
        case 3:
            System.out.println(
                    msg + caractere + "\nDEVE SE USAR 2 '=', PARA UMA COMPARACAO ENTRE VALORES\n\n" + COR_VERDE);
            System.exit(0);
            break;
        case 4:
            System.out.println(msg + "EOF"
                    + "\nANTES DE TERMINAR O ARQUIVO O COMENTARIO MULTILINHA DEVE SER FECHADO\n\n" + COR_VERDE);
            System.exit(0);
            break;
        case 5:
            System.out.println(msg + caractere
                    + "\nCARACTERES ESPECIAIS SÃO INVALIDOS, (APENAS LETRAS E NUMEROS PODEM SER UM CHAR)\n\n"
                    + COR_VERDE);
            System.exit(0);
            break;
        case 6:
            System.out.println(msg + caractere + "\nUM CHAR PARA SER FORMADO, PRECISA ESTAR ENTRE ASPAS SIMPLES\n\n");
            System.exit(0);
            break;
        case 7:
            System.out.println(msg + caractere + "\nCARACTERES ESPECIAIS SÃO INVALIDOS\n\n");
            System.exit(0);
            break;
        }
    }
}