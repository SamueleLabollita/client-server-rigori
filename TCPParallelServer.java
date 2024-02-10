import java.io.*;
import java.net.*;
import java.util.Random;

public class TCPParallelServer {
    private ServerSocket serverSocket;
    private boolean turnoClient1 = true;
    private int golClient1 = 0;
    private int golClient2 = 0;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server avviato. In attesa di connessioni...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connesso: " + clientSocket);

                // Creazione di un thread per gestire la connessione del client
                Thread clientThread = new Thread(new ClientHandler(clientSocket, turnoClient1));
                clientThread.start();

                turnoClient1 = !turnoClient1; // Alternare il turno per il prossimo client
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TCPParallelServer server = new TCPParallelServer();
        server.start(9999);
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private DataOutputStream os;
        private BufferedReader is;
        private boolean myTurn;

        public ClientHandler(Socket clientSocket, boolean turnoClient1) {
            this.clientSocket = clientSocket;
            this.myTurn = turnoClient1;
        }

        @Override
        public void run() {
            try {
                os = new DataOutputStream(clientSocket.getOutputStream());
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                System.out.println("Il client si è connesso.");

                int gol = 0;

                while (true) {
                    String serverMessage = "Scegli la direzione del tiro (D = destra, S = sinistra, C = centro): ";
                    os.writeBytes(serverMessage + '\n');
                    os.flush();

                    String mossaUtente = is.readLine();
                    if (mossaUtente == null || mossaUtente.isEmpty()) {
                        break;
                    }

                    String[] mosse = {"D", "S", "C"};
                    String mossaPortiere = mosse[new Random().nextInt(mosse.length)];

                    String result;
                    if (mossaUtente.equals(mossaPortiere)) {
                        result = "Parato!";
                    } else {
                        result = "Gol!";
                        gol++;
                    }

                    os.writeBytes(result + '\n');
                    os.writeBytes("Gol totali: " + gol + '\n'); // Invia il conteggio dei gol
                    os.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) os.close();
                    if (is != null) is.close();
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
