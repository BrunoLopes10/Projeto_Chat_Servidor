import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Para conectar use: java Cliente <srv-ip> <porta-srv>");
            return;
        }

        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(in.readLine()); 
            String nome = teclado.readLine();
            out.println(nome);

            System.out.println(in.readLine()); 
            String ip = teclado.readLine();
            out.println(ip);

            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                if (serverResponse.equals("NAMEACCEPTED")) {
                    break;
                }
                System.out.println(serverResponse);
            }

            System.out.println("Conectado ao servidor como " + nome);

            Thread recebimento = new Thread(() -> {
                try {
                    String mensagemRecebida;
                    while ((mensagemRecebida = in.readLine()) != null) {
                        System.out.println(mensagemRecebida);
                    }
                } catch (IOException e) {
                    System.out.println("Conexão com o servidor perdida.");
                }
            });
            recebimento.start();

            String mensagem;
            while ((mensagem = teclado.readLine()) != null) {
                out.println(mensagem);
            }
        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }
}
