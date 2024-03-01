import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TCPParallelServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clientHandlers = new ArrayList<>();
    private int connectedClients = 0;
    private int[] goals = new int[2]; // Array per memorizzare i gol dei due client

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server avviato. In attesa di connessioni...");

            while (connectedClients < 2) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connesso: " + clientSocket);

                // Aggiungi il clientHandler alla lista
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);

                // Creazione di un thread per gestire la connessione del client
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();

                connectedClients++;
            }
            System.out.println("Sono connessi 2 client. Inizia la partita.");
        } catch (IOException e) {
            e.printStackTrace();
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
        private int clientId;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                os = new DataOutputStream(clientSocket.getOutputStream());
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                System.out.println("Il client si è connesso.");

                // Assegna un ID al client
                clientId = connectedClients - 1;

                int gol = 0;
                int tiri = 0;
                while (tiri < 5) {
                    String serverMessage = "Scegli la direzione del tiro (D = destra, S = sinistra, C = centro): ";
                    os.writeBytes(serverMessage + '\n');
                    os.flush();

                    String mossaUtente = is.readLine();
                    if (mossaUtente == null || mossaUtente.isEmpty()) {
                        break;
                    }

                    // Controlla se l'input dell'utente è valido
                    if (!isValidInput(mossaUtente)) {
                        os.writeBytes("Input non valido. Utilizza solo D, S o C per indicare la direzione del tiro.\n");
                        os.flush();
                        continue; // Continua con il prossimo ciclo
                    }

                    tiri++;

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

                // Aggiorna i gol del client
                goals[clientId] = gol;

                // Se entrambi i client hanno completato i loro tiri, confronta i punteggi
                if (clientId == 1) {
                    compareScores();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Metodo per controllare se l'input dell'utente è valido
        private boolean isValidInput(String input) {
            return input != null && !input.isEmpty() && (input.equals("D") || input.equals("S") || input.equals("C"));
        }
    }

    // Metodo per confrontare i punteggi dei due client
    private void compareScores() {
        int score1 = goals[0];
        int score2 = goals[1];

        if (score1 > score2) {
            System.out.println("Il client 1 ha vinto con " + score1 + " gol!");
        } else if (score1 < score2) {
            System.out.println("Il client 2 ha vinto con " + score2 + " gol!");
        } else {
            System.out.println("La partita è finita in pareggio con " + score1 + " gol ciascuno!");
        }
    }
}
