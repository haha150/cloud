package view;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {

    /**
     * Create the view and scene and start the program
     * @param primaryStage the specified stage
     */
    @Override
    public void start(Stage primaryStage) {
        ChatView view = new ChatView(primaryStage);
        
        Scene scene = new Scene(view, 400, 400);
        
        primaryStage.setTitle("Chat");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
