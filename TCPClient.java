import java.net.*;
import java.io.*;

public class TCPClient {
    public void start() throws IOException {
        // Connessione della Socket con il Server
        Socket socket = new Socket("localhost", 7777);

        // Stream di byte da passare al Socket
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        DataInputStream is = new DataInputStream(socket.getInputStream());
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("Benvenuto al gioco dei calci di rigore!");

        // Ciclo per il gioco dei calci di rigore
        while (true) {
            // Ricevi il messaggio dal server
            String serverMessage = is.readLine();
            System.out.println(serverMessage);

            // Input dell'utente per il calcio di rigore
            System.out.print("Premi 'T' per tirare: ");
            String userInput = stdIn.readLine();

            // Invia la mossa al server
            os.writeBytes(userInput + '\n');

            // Ricevi il risultato dal server
            String result = is.readLine();
            System.out.println("Risultato: " + result);

            // Verifica se il gioco è finito
            if (result.equals("Vittoria!") || result.equals("Sconfitta!") || result.equals("Pareggio!")) {
                System.out.println("Il gioco è terminato. Grazie per giocare!");
                break;
            }
        }

        // Chiusura dello Stream e del Socket
        os.close();
        is.close();
        socket.close();
    }

    public static void main(String[] args) throws Exception {
        TCPClient tcpClient = new TCPClient();
        tcpClient.start();
    }
}