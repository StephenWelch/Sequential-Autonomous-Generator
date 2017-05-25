package ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephen Welch on 5/19/2017.
 */
public class OptionsWindow implements Window {

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final String WINDOW_TITLE = "Autonomous Generator";

    private static final int LAYOUT_PADDING_TOP = 10;
    private static final int LAYOUT_PADDING_RIGHT = 10;
    private static final int LAYOUT_PADDING_BOTTOM = 10;
    private static final int LAYOUT_PADDING_LEFT = 10;

    private static final int HORIZ_CELL_PADDING = 10;
    private static final int VERT_CELL_PADDING = 10;

    private Stage window;
    SelectorWindow prevWindow;
    private Scene scene;
    private GridPane gridLayout;
    private Button backButton, saveButton;

    private List<Label> paramLabels;
    private List<TextField> paramFields;

    private Command command;
    private CommandType commandType;

    public OptionsWindow(Stage window, SelectorWindow prevWindow, Command command) {
        this.window = window;
        this.prevWindow = prevWindow;
        this.command = command;

        this.commandType = command.getCommandType();
        this.paramLabels = new ArrayList<>();
        this.paramFields = new ArrayList<>();
    }

    public Scene display() {

        gridLayout = new GridPane();
        gridLayout.setPadding(new Insets(LAYOUT_PADDING_TOP, LAYOUT_PADDING_RIGHT, LAYOUT_PADDING_BOTTOM, LAYOUT_PADDING_LEFT));
        gridLayout.setHgap(HORIZ_CELL_PADDING); //Set horizontal/vertical padding for layout cells
        gridLayout.setVgap(VERT_CELL_PADDING);

        backButton = new Button("Back");
        backButton.setOnAction(e -> {
            prevWindow.getRunListing().refresh();
            window.setScene(prevWindow.getScene());
        });
        GridPane.setConstraints(backButton, 0, 3);

        saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if(command.getParameterArray().length <= 0) {
                setParams(command);
                prevWindow.getRunListing().getItems().add(command);
            } else {
                setParams(command);
            }
        });
        GridPane.setConstraints(saveButton, 1, 3);

        //Create Labels and Fields for each parameter of the given CommandType
        for (int i = 0; i < commandType.params.length; i++) {
            Label label = new Label(commandType.params[i].name() + ": ");
            TextField field;

            //If command has parameters, display them in the text box. Otherwise, don't.
            if(command.getParameterArray().length > 0) {
                String parameterString = command.getParameterArray()[i].toString();
                field = new TextField(parameterString);
            } else {
                field = new TextField();
            }

            paramLabels.add(label);
            paramFields.add(field);

            GridPane.setConstraints(label, 0, i + 1);
            GridPane.setConstraints(field, 1, i + 1);
        }

        for (Label l : paramLabels) {
            gridLayout.getChildren().add(l);
        }
        for (TextField t : paramFields) {
            gridLayout.getChildren().add(t);
        }

        gridLayout.getChildren().addAll(backButton, saveButton);

        scene = new Scene(gridLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
        return scene;

    }

    private void setParams(Command command) {

        Object[] params = new Object[paramFields.size()];
        String[] paramStrings = getAllFieldText();
        for(int i = 0; i < paramStrings.length; i++) {
            params[i] = stringToParam(paramStrings[i], command.getCommandType().params[i]);
        }
        command.setParameterArray(params);
    }

    private String[] getAllFieldText() {
        String[] paramStrings = new String[paramFields.size()];
        for (int i = 0; i < paramFields.size(); i++) {
            paramStrings[i] =  paramFields.get(i).getText();
        }
        return paramStrings;
    }

    private Object stringToParam(String s, ParamType p) {
        Object param;
        if (s.isEmpty()) p = null; //If String is empty, force default case
        switch (p) {
            case Double:
                param = Double.valueOf(s);
                break;
            case String:
                param = s;
                break;
            default:
                param = new Double(0);
                break;
        }
        return param;
    }

    public Scene getScene() {
        return scene;
    }

}
