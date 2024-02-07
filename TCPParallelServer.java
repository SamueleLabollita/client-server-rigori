import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TCPParallelServer {
    private ServerSocket serverSocket;
    private boolean turnoClient1 = true;
    private final Lock lock = new ReentrantLock();
    private final Condition turno = lock.newCondition();

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server avviato. In attesa di connessioni...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connesso: " + clientSocket);

                // Creazione di un thread per gestire la connessione del client
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
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

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            lock.lock();
            try {
                this.myTurn = turnoClient1;
                turnoClient1 = !turnoClient1; // Alternare il turno
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void run() {
            try {
                os = new DataOutputStream(clientSocket.getOutputStream());
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                System.out.println("Il client si è connesso.");

                while (true) {
                    lock.lock();
                    try {
                        while (!myTurn) {
                            turno.await();
                        }

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
                        }

                        os.writeBytes(result + '\n');
                        os.flush();

                        myTurn = false; // Il turno del cliente è finito
                        turno.signalAll(); // Notifica all'altro client che è il suo turno

                        // Ora attendi il tuo turno
                        this.myTurn = false;
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (IOException | InterruptedException e) {
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
