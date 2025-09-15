import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class HeadsTailsClient {
    private Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static BufferedReader stdIn;


    public void startConnection(String ip, int port) throws IOException {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + ip);
            throw e;
        } catch (IOException e) {
            System.err.println("Couldn't connect to " + ip);
            throw e;
        }
    }

    public String sendMessage(String msg) {
        try {
            out.println(msg);
            return in.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    public void stopConnection() {
        try {
            if (stdIn != null) stdIn.close();
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.err.println("Error when closing connections: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println(
                    "Usage: java HeadsTailsClient <host name> <port number>");
            return;
        }

        String hostName = args[0];
        try {
            int portNumber = Integer.parseInt(args[1]);
            HeadsTailsClient client = new HeadsTailsClient();
            client.startConnection(hostName, portNumber);

            String userInput;
            while ((userInput = stdIn.readLine()) != null && (!userInput.equals("QUIT"))) {
                out.println(userInput);
                System.out.println("echo: " + in.readLine());
            }

            client.stopConnection();
        }
        catch(NumberFormatException e) {
            System.err.println("Invalid port number: " + args[1]);
        } catch (IOException e) {
            System.err.println("Exiting due to connection error");
        }
    }
}