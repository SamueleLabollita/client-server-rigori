import java.io.*;
import java.net.*;

public class TCPClient {
    private Socket socket;
    private BufferedReader is;
    private DataOutputStream os;
    private BufferedReader stdIn;

    public TCPClient(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new DataOutputStream(socket.getOutputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            System.out.println("Benvenuto al gioco dei calci di rigore!");

            while (true) {
                // Ricevi il messaggio dal server (prompt per il tiro)
                String serverMessage = is.readLine();
                if (serverMessage == null || serverMessage.isEmpty()) {
                    break;
                }
                System.out.println(serverMessage);

                // Input dell'utente per il calcio di rigore
                String userInput = stdIn.readLine();
                if (userInput == null || userInput.isEmpty()) {
                    break;
                }

                // Invia la mossa al server
                os.writeBytes(userInput + '\n');
                os.flush();

                // Ricevi il risultato dal server e stampalo
                String result = is.readLine();
                System.out.println("Risultato: " + result);

                // Verifica se il gioco è terminato
                if (result.equals("Vittoria!") || result.equals("Sconfitta!") || result.equals("Pareggio!")) {
                    System.out.println("Il gioco è terminato. Grazie per giocare!");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Chiudi tutte le risorse
                if (os != null) os.close();
                if (is != null) is.close();
                if (stdIn != null) stdIn.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 9999;
        TCPClient tcpClient = new TCPClient(serverAddress, serverPort);
        tcpClient.start();
    }
}