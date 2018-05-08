package view;

import controller.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChatView extends BorderPane {
    private final Stage primaryStage;
    private final Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private static TextArea textArea;
    private TextField textField;
    private static Controller CONTROLLER;
    private Dialog dialog;
    private TextField ipField;
    private TextField portField;

    public ChatView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initView();
        CONTROLLER = new Controller();
    }

    public static TextArea getTextArea() {
        return textArea;
    }

    private void initView() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Server");
        MenuItem connect = new MenuItem("Connect");
        MenuItem disconnect = new MenuItem("Disconnect");
        connect.setOnAction(new ConnectHandler());
        disconnect.setOnAction(new DisconnectHandler());
        menu.getItems().addAll(connect,disconnect);
        menuBar.getMenus().add(menu);

        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textField = new TextField();
        textField.setPromptText("Enter message");
        textField.setOnKeyPressed(new EnterHandler());

        this.setTop(menuBar);
        this.setCenter(textArea);
        this.setBottom(textField);

        initAddDialogView();
        primaryStage.setOnCloseRequest(new CloseHandler());
    }

    private void initAddDialogView() {
        HBox container = new HBox();

        ipField = new TextField();
        ipField.setPromptText("Enter IP address");
        portField = new TextField();
        portField.setPromptText("Enter port number");

        container.getChildren().addAll(ipField,portField);

        ButtonType connectButton = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog = new Dialog();
        dialog.setTitle("Connect");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(container);
        dialog.getDialogPane().getButtonTypes().addAll(connectButton, cancelButton);
    }

    public void showAlert(String message) {
        alert.setHeaderText("");
        alert.setTitle("Alert!");
        alert.setContentText(message);
        alert.show();
    }

    private class CloseHandler implements EventHandler<WindowEvent> {

        @Override
        public void handle(WindowEvent event) {
            event.consume();
            CONTROLLER.disconnect();
            primaryStage.close();
        }
    }

    private class EnterHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            if(event.getCode().equals(KeyCode.ENTER)) {
                if(!Controller.isCONNECTED()) {
                    showAlert("Connect first");
                } else {
                    if(!textField.getText().isEmpty()) {
                        CONTROLLER.sendAndReceiveMessage(textField.getText());
                        textField.clear();
                    }
                }
            }
        }
    }

    private class ConnectHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            dialog.showAndWait();
            if(!ipField.getText().isEmpty() && !portField.getText().isEmpty()) {
                String ip = ipField.getText();
                int port = Integer.parseInt(portField.getText());
                if(port > 1024) {
                    CONTROLLER.connect(ip, port);
                    ipField.clear();
                    portField.clear();
                } else {
                    showAlert("Incorrect port");
                }
            } else {
                showAlert("Incorrect ip or port");
            }
        }
    }

    private class DisconnectHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            CONTROLLER.disconnect();
        }
    }
}
