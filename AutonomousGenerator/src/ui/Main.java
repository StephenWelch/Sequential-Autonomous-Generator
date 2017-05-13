package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {


    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final String WINDOW_TITLE = "Autonomous Generator";

    private static final int LAYOUT_PADDING_TOP = 10;
    private static final int LAYOUT_PADDING_RIGHT = 10;
    private static final int LAYOUT_PADDING_BOTTOM = 10;
    private static final int LAYOUT_PADDING_LEFT = 10;

    private static final int HORIZ_CELL_PADDING = 10;
    private static final int VERT_CELL_PADDING = 0;

    private Stage window;
    private Scene scene;
    private GridPane gridLayout;

    private Map<String, String> commandMap = initMap();
    private ListView<String> commandListing;

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle(WINDOW_TITLE);
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });

        gridLayout = new GridPane();
        gridLayout.setPadding(new Insets(LAYOUT_PADDING_TOP, LAYOUT_PADDING_RIGHT, LAYOUT_PADDING_BOTTOM, LAYOUT_PADDING_LEFT));
        gridLayout.setHgap(HORIZ_CELL_PADDING); //Set vorizontal/vertical padding for layout cells
        gridLayout.setVgap(VERT_CELL_PADDING);

        commandListing = new ListView<>();
        for(String s : commandMap.keySet()) {
            commandListing.getItems().add(s);
        }
        GridPane.setConstraints(commandListing, 0, 0);

        gridLayout.getChildren().addAll(commandListing);

        scene = new Scene(gridLayout, WINDOW_WIDTH, WINDOW_HEIGHT);

        window.setScene(scene);
        window.show();

    }

    private static Map<String, String> initMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Turn", "public void turn();");
        map.put("Turn", "public void blah();");
        map.put("Move", "public void turn();");
        return map;
    }

    private void closeProgram() {
        //Run cleanup tasks
        System.out.println("Cleaning up. Bye!");
        window.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
