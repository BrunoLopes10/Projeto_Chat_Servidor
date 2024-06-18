import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static Map<String, ClienteInfo> clientes = new HashMap<>();

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Servidor iniciado na porta 8080...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClienteHandler(clientSocket)).start();
        }
    }

    private static class ClienteHandler implements Runnable {
        private String nome;
        private String ip;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Por favor, informe seu nome:");
                nome = in.readLine();
                out.println("Por favor, informe seu IP:");
                ip = in.readLine();

                if (nome == null || nome.isBlank() || ip == null || ip.isBlank()) {
                    return;
                }

                synchronized (clientes) {
                    if (!clientes.containsKey(nome)) {
                        clientes.put(nome, new ClienteInfo(nome, ip, out));
                    } else {
                        out.println("Nome já em uso, desconectando...");
                        return;
                    }
                }

                out.println("NAMEACCEPTED");
                notificarConexao(nome);

                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    } else if (input.startsWith("/msg")) {
                        String[] parts = input.split(" ", 3);
                        if (parts.length < 3) {
                            out.println("Uso correto: /msg <destinatário> <mensagem>");
                            continue;
                        }
                        String destinatario = parts[1];
                        String mensagem = parts[2];
                        enviarMensagemPrivada(destinatario, mensagem);
                    } else {
                        out.println("Comando não reconhecido");
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (nome != null) {
                    synchronized (clientes) {
                        clientes.remove(nome);
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        private void notificarConexao(String nome) {
            String mensagem = "Cliente " + nome + " conectado ao servidor!";
            synchronized (clientes) {
                for (ClienteInfo cliente : clientes.values()) {
                    cliente.getOut().println(mensagem);
                }
            }
        }

        private void enviarMensagemPrivada(String destinatario, String mensagem) {
            ClienteInfo clienteInfo = clientes.get(destinatario);
            if (clienteInfo != null) {
                String mensagemFormatada = "Mensagem de " + nome + ", IP: " + ip + ": " + mensagem;
                clienteInfo.getOut().println(mensagemFormatada);
            } else {
                out.println("Usuário " + destinatario + " não encontrado.");
            }
        }
    }

    private static class ClienteInfo {
        private String nome;
        private String ip;
        private PrintWriter out;

        public ClienteInfo(String nome, String ip, PrintWriter out) {
            this.nome = nome;
            this.ip = ip;
            this.out = out;
        }

        public String getNome() {
            return nome;
        }

        public String getIp() {
            return ip;
        }

        public PrintWriter getOut() {
            return out;
        }
    }
}
