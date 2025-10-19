import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class HeadsTailsSocketServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening on port " + port);

            clientSocket = serverSocket.accept();
            System.out.println("Client connect: " + clientSocket.getInetAddress().getHostAddress());

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null){
                if (inputLine.startsWith("HELLO")) {
                    out.println("ACK");
                    System.out.println("Received HELLO, sent ACK");
                }else if (inputLine.startsWith("CHOICE:")){
                    String userChoice = inputLine.substring("CHOICE:".length()).trim();
                    handleCoinGame(userChoice);
                } else if (inputLine.equals("QUIT")) {
                    System.out.println("Received QUIT, closing connection.");
                    break;
                } else {
                    out.println("ERROR:INVALID_COMMAND");
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
            e.printStackTrace();
        }finally {
            stopConnection();
        }
    }

    public void stopConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            System.out.println("Server connection stopped.");
        } catch (IOException e) {
            System.out.println("Exception caught when trying to close the socket");
            e.printStackTrace();
        }
    }

    private void handleCoinGame(String userChoice) {
        if (!userChoice.equalsIgnoreCase("HEADS") && !userChoice.equalsIgnoreCase("TAILS")) {
            out.println("ERROR: INVALID CHOICE - HEADS OR TAILS");
        }

        String coinFace = flipCoin();
        String result;
        if (userChoice.equalsIgnoreCase(coinFace)) {
            result = "RESULT:WIN";
            System.out.println("User chose " + userChoice + ", coin was " + coinFace + ". User wins.");
        } else {
            result = "RESULT:LOSS";
            System.out.println("User chose " + userChoice + ", coin was " + coinFace + ". User losses.");
        }
        out.println(result);
    }

    private String flipCoin() {
            Random random = new Random();
            return random.nextBoolean() ? "HEADS" : "TAILS";
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java HeadsTailsSocketServer <port number>");
            return;
        }

        try {
            int portNumber = Integer.parseInt(args[0]);
            HeadsTailsSocketServer server = new HeadsTailsSocketServer();
            server.start(portNumber);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + args[0]);
        }
    }
}