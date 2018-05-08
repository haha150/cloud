package org.cloud.client.net;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private InetAddress serverAddress;
    private Socket socket;
    private ObjectOutputStream sout;
    private static String IP;
    private static int SERVER_PORT;
    private Thread receiver;
    private OutputHandler outputHandler;

    public Client(String ip, int port, OutputHandler outputHandler) {
        this.IP = ip;
        this.SERVER_PORT = port;
        this.outputHandler = outputHandler;
    }

    public void sendMessage(String message) throws IOException {
        sout.writeObject(message);
        sout.flush();
    }

    public void connect() throws IOException {
        serverAddress = InetAddress.getByName(IP);
        socket = new Socket(serverAddress, SERVER_PORT);
        sout = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream sin = new ObjectInputStream(socket.getInputStream());
        socket.setKeepAlive(true);
        receiver = new Thread(new Receiver(sin, outputHandler));
        receiver.start();
    }

    public void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    private class Receiver implements Runnable {

        private ObjectInputStream sin;
        private OutputHandler outputHandler;

        public Receiver(ObjectInputStream sin, OutputHandler outputHandler) {
            this.sin = sin;
            this.outputHandler = outputHandler;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String s = (String)sin.readObject();
                    outputHandler.handleMessage(s);
                }
            } catch (IOException e2) {

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}