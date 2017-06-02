package ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephen Welch on 5/19/2017.
 */
public class OptionsWindow extends Window {

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final String WINDOW_TITLE = "Autonomous Generator";
    SelectorWindow prevWindow;
    private Button backButton, saveButton;

    private List<Label> paramLabels;
    private List<TextField> paramFields;

    private Command command;
    private CommandType commandType;

    public OptionsWindow(Stage window, SelectorWindow prevWindow, Command command) {
        super(window, WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);

        this.prevWindow = prevWindow;
        this.command = command;

        this.commandType = command.getCommandType();
        this.paramLabels = new ArrayList<>();
        this.paramFields = new ArrayList<>();
    }

    public Scene display() {

        backButton = new Button("Back");
        saveButton = new Button("Save");

        //Create Labels and Fields for each parameter of the given CommandType
        for (int i = 0; i < commandType.params.length; i++) {
            Label label = new Label(commandType.params[i].name() + ": ");
            TextField field;

            //If command has parameters, display them in the text box. Otherwise, don't.
            if (command.getParameterArray().length > 0) {
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

        for (Label l : paramLabels) getLayout().getChildren().add(l);
        for (TextField t : paramFields) getLayout().getChildren().add(t);

        backButton.setOnAction(e -> {
            prevWindow.getRunListing().refresh();
            getWindow().setScene(prevWindow.getScene());
        });

        saveButton.setOnAction(e -> {
            if (command.getParameterArray().length <= 0) {
                setParams(command);
                prevWindow.getRunListing().getItems().add(command);
            } else {
                setParams(command);
            }
        });

        GridPane.setConstraints(backButton, 0, 3);
        GridPane.setConstraints(saveButton, 1, 3);

        getLayout().getChildren().addAll(backButton, saveButton);

        setScene(new Scene(getLayout(), WINDOW_WIDTH, WINDOW_HEIGHT));
        return getScene();

    }

    private void setParams(Command command) {

        Object[] params = new Object[paramFields.size()];
        String[] paramStrings = getAllFieldText();
        for (int i = 0; i < paramStrings.length; i++) {
            params[i] = stringToParam(paramStrings[i], command.getCommandType().params[i]);
        }
        command.setParameterArray(params);
    }

    private String[] getAllFieldText() {
        String[] paramStrings = new String[paramFields.size()];
        for (int i = 0; i < paramFields.size(); i++) {
            paramStrings[i] = paramFields.get(i).getText();
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

}
