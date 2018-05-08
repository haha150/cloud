package org.cloud.client.start;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cloud.client.controller.Controller;
import org.cloud.client.view.GameView;

public class Main extends Application {

    /**
     * Create the view and scene and start the program
     * @param primaryStage the specified stage
     */
    @Override
    public void start(Stage primaryStage) {
        GameView view = new GameView(primaryStage);
        Scene scene = new Scene(view, 400, 400);
        Controller controller = new Controller(view);
        view.addEventHandlers(controller);
        
        primaryStage.setTitle("Cloud");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
