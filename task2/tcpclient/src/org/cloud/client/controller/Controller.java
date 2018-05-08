package org.cloud.client.controller;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.cloud.client.view.GameView;
import org.cloud.client.net.Client;
import org.cloud.client.net.OutputHandler;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Controller {
    private Client client;
    private boolean connected = false;
    private static GameView view;
    private final Alert alert = new Alert(Alert.AlertType.INFORMATION);

    public Controller(GameView view) {
        this.view = view;
    }

    public synchronized void appendText(String text) {
            Platform.runLater(() -> view.getTextArea().appendText(text + "\n"));
    }

    public synchronized void appendNewConnection(String text) {
        Platform.runLater(() -> view.getTextArea().appendText(text + "\n"));
    }

    public void connect(String ip, int port, OutputHandler outputHandler) {
        view.getTextArea().clear();
        CompletableFuture.runAsync(() -> {
            disconnect();
            client = new Client(ip, port, outputHandler);
            try {
                client.connect();
                connected = true;
            } catch (IOException ie) {
                appendText("Failed to establish connection.");
                try {
                    client.disconnect();
                    connected = false;
                } catch (Exception e) {
                    System.out.println("Cleanup failed.");
                }
            }
        }).thenRun(() -> outputHandler.handleNewConnection("Connected to " + ip + ":" + port));
    }

    public void disconnect() {
        if(connected) {
            try {
                client.disconnect();
                connected = false;
                appendText("Disconnected.");
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Failed to disconnect."));
            }
        }
    }

    public void sendMessage(String guess) {
        CompletableFuture.runAsync(() -> {
            try {
                client.sendMessage(guess);
            } catch (IOException e) {
                System.out.println(e);
            }
        });
    }

    public void showAlert(String message) {
        alert.setHeaderText("");
        alert.setTitle("Alert!");
        alert.setContentText(message);
        alert.show();
    }

    public void handleClose(WindowEvent event, Stage primaryStage) {
        event.consume();
        disconnect();
        primaryStage.close();
    }

    public void enterMessageHandler(KeyEvent event, TextField textField) {
        if(event.getCode().equals(KeyCode.ENTER)) {
            if(!connected) {
                showAlert("Connect first");
            } else {
                if(!textField.getText().isEmpty()) {
                    appendText("Ping: "+textField.getText());
                    sendMessage(textField.getText());
                    textField.clear();
                }
            }
        }
    }

    public void connectHandler(Dialog dialog, TextField ipField, TextField portField) {
        dialog.showAndWait();
        if(!ipField.getText().isEmpty() && !portField.getText().isEmpty()) {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());
            if(port > 1024) {
                connect(ip, port, new ViewOutput());
                ipField.clear();
                portField.clear();
            } else {
                showAlert("Incorrect port");
            }
        }
    }


    public void disconnectHandler() {
        disconnect();
    }

    private class ViewOutput implements OutputHandler {

        @Override
        public void handleNewConnection(String message) {
            appendNewConnection(message);
        }

        @Override
        public void handleMessage(String message) {
            appendText("Pong: " + message);
        }

    }

}
