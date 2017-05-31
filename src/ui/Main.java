package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage window;
    private Scene selectScene;
    private SelectorWindow selectWindow;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;

        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        selectWindow = new SelectorWindow(window);
        selectScene = selectWindow.display();

        window.setScene(selectScene);
        window.show();

    }

    private void closeProgram() {
        //Run cleanup tasks
        System.out.println("Closing Window");
        window.close();
    }
}
