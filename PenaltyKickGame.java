import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class PenaltyKickGame extends Thread {
    private Socket playerSocket;
    private DataInputStream playerInput;
    private DataOutputStream playerOutput;

    public PenaltyKickGame(Socket playerSocket) {
        this.playerSocket = playerSocket;
        try {
            this.playerInput = new DataInputStream(playerSocket.getInputStream());
            this.playerOutput = new DataOutputStream(playerSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            playerOutput.writeBytes("Inizia il gioco! E' il tuo turno.\n");

            while (true) {
                // Ricevi la mossa del giocatore
                String playerMove = playerInput.readLine();
                System.out.println("Il giocatore ha tirato in direzione: " + playerMove);

                // Calcola la mossa del portiere
                String goalkeeperMove = calculateGoalkeeperMove();

                // Determina il risultato del tiro
                String result = calculateResult(playerMove, goalkeeperMove);

                // Invia la mossa del portiere e il risultato al giocatore
                playerOutput.writeBytes("Il portiere ha mosso in direzione: " + goalkeeperMove + "\n");
                playerOutput.writeBytes(result + "\n");

                // Verifica se il gioco è finito
                if (result.equals("Goal!") || result.equals("Parato!")) {
                    System.out.println("Il gioco è terminato. Grazie per giocare!");
                    break;
                }
            }

            // Chiudi le risorse
            playerInput.close();
            playerOutput.close();
            playerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String calculateGoalkeeperMove() {
        // Simula casualmente la mossa del portiere
        String[] possibleMoves = {"alto", "basso", "destra", "sinistra", "centro"};
        Random random = new Random();
        return possibleMoves[random.nextInt(possibleMoves.length)];
    }

    private String calculateResult(String playerMove, String goalkeeperMove) {
        // Simula logicamente il risultato del tiro
        if (playerMove.equals(goalkeeperMove)) {
            return "Parato!";
        } else {
            return "Goal!";
        }
    }
}
