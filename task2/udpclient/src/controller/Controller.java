package controller;

import client.*;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import view.ChatView;

public class Controller {
    private static TextArea textArea;
    private Client client;
    private static boolean CONNECTED = false;

    public Controller() {
        this.textArea = ChatView.getTextArea();
    }

    public static boolean isCONNECTED() {
        return CONNECTED;
    }

    public static void setCONNECTED(boolean CONNECTED) {
        Controller.CONNECTED = CONNECTED;
    }

    public void sendAndReceiveMessage(String message) {
        appendText("Ping: " + message);
        client.sendAndReceiveMessage(message);
    }

    public static void appendText(String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textArea.appendText(text + "\n");
            }
        });
    }

    public void connect(String ip, int port) {
        client = new Client(ip, port);
        client.connectToServer();
        textArea.clear();
    }

    public void disconnect() {
        if(CONNECTED) {
            client.disconnect();
        }
    }

}
