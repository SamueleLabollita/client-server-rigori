import java.net.*;
import java.io.*;

public class TCPParallelServer {
    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(7777);

        System.out.println("In attesa di connessioni da due client...");

        // Accetta la connessione del primo client
        Socket client1Socket = serverSocket.accept();
        System.out.println("Connessione stabilita con il primo client: " + client1Socket);

        // Invia un messaggio al primo client
        DataOutputStream client1OutputStream = new DataOutputStream(client1Socket.getOutputStream());
        client1OutputStream.writeBytes("Benvenuto! Sei il primo giocatore.\n");

        // Accetta la connessione del secondo client
        Socket client2Socket = serverSocket.accept();
        System.out.println("Connessione stabilita con il secondo client: " + client2Socket);

        // Invia un messaggio al secondo client
        DataOutputStream client2OutputStream = new DataOutputStream(client2Socket.getOutputStream());
        client2OutputStream.writeBytes("Benvenuto! Sei il secondo giocatore.\n");

        // Avvia il processo per gestire la partita per entrambi i client
        new PenaltyKickGame(client1Socket, client2Socket).start();

        // Chiudi il serverSocket
        serverSocket.close();
    }

    public static void main(String[] args) throws Exception {
        TCPParallelServer tcpServer = new TCPParallelServer();
        tcpServer.start();
    }
}
