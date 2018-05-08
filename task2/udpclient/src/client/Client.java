package client;

import controller.Controller;
import java.io.IOException;
import java.net.*;

public class Client {
    private DatagramSocket socket;
    private static int SERVER_PORT;
    private static String IP;
    private static final int BUFFER_LENGTH = 1024;
    private byte[] bufferReceive;
    private InetAddress server;

    public Client(String ip, int port) {
        SERVER_PORT = port;
        IP = ip;
    }

    public void connectToServer() {
        socket = null;
        bufferReceive = new byte[BUFFER_LENGTH];

        try {
            server = InetAddress.getByName(IP);
            socket = new DatagramSocket();
            socket.setSoTimeout(20000); // 20 sekunder
            Controller.setCONNECTED(true);
        } catch (SocketException e) {
            Controller.appendText("Server is unreachable.");
            disconnect();
            //System.exit(-1);
        } catch (UnknownHostException e) {
            Controller.appendText("Unknown host.");
            disconnect();
            //System.exit(-1);
        }
    }

    public void sendAndReceiveMessage(String message) {
        try {
            String sendMessage = message;
            byte[] bufferSend = sendMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(bufferSend, bufferSend.length, server, SERVER_PORT);
            socket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(bufferReceive, bufferReceive.length);
            socket.receive(receivePacket);
            String receiveMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
            Controller.appendText("Pong: " + receiveMessage);
        } catch (IOException e) {
            Controller.appendText(e.getMessage());
            disconnect();
        } catch (Exception e) { // probably timeout
            Controller.appendText(e.getMessage());
            disconnect();
        }
    }

    public void disconnect() {
        if (socket != null) {
            socket.close();
        }
        Controller.setCONNECTED(false);
    }

}
