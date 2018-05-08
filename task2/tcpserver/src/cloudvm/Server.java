package cloudvm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private ServerSocket serverSocket;
    private static final int PORT = 12345;

    public Server() {
        
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                System.out.println("Server is listening...");
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress() + " connected.");

                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println("Unknown error occurred.");
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (Exception e) {
            }
        }
    }

}
