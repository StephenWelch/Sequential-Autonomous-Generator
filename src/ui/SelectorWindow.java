package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stephen Welch on 5/19/2017.
 */
public class SelectorWindow extends Window {

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final String WINDOW_TITLE = "Autonomous Generator";
    public static final String FOLDER_DIALOG_TITLE = "Select Output Directory";
    private static final String OUTPUT_FILE_NAME = "\\Autonomous.java";

    private Map<String, CommandType> commandMap = initMap();
    private ListView<String> commandListing;
    private ListView<Command> runListing;
    private Button addButton, removeButton, refreshButton, generateButton, optionsButton, fieldButton;
    private DirectoryChooser directoryChooser;

    public SelectorWindow(Stage window) {
        super(window, WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    public Scene display() {

        commandListing = new ListView<>();
        commandListing.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (String s : commandMap.keySet()) {
            commandListing.getItems().add(s);
        }

        addButton = new Button("Add");

        removeButton = new Button("Remove");
        GridPane.setMargin(removeButton, new Insets(80, 0, 0, 0));

        runListing = new ListView<>();
        runListing.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(FOLDER_DIALOG_TITLE);

        generateButton = new Button("Generate");
        optionsButton = new Button("Options");
        refreshButton = new Button("Refresh");
        fieldButton = new Button("Field View");

        addButton.setOnAction(e -> {
            String selectedItem = commandListing.getSelectionModel().getSelectedItem();
            CommandType commandType = commandMap.get(selectedItem);
            openOptions(new Command(commandType));
        });
        removeButton.setOnAction(e -> removeFromRunList());
        runListing.setOnMouseClicked(e -> displayCommandOptions());
        generateButton.setOnAction(e -> {
            String outputPath = directoryChooser.showDialog(getWindow()).getAbsolutePath();
            if(outputPath != null) generateCode(runListing.getItems(), outputPath + OUTPUT_FILE_NAME);
        });
        optionsButton.setOnAction(e -> {
            Command selectedCommand = runListing.getSelectionModel().getSelectedItem();
            try {
                openOptions(selectedCommand);
            } catch (NullPointerException exception) {
                System.err.println("OptionsWindow cannot open a null command.");
            }
        });
        refreshButton.setOnAction(e -> runListing.refresh());
        fieldButton.setOnAction(e -> getWindow().setScene(openField()));

        GridPane.setConstraints(commandListing, 0, 0);
        GridPane.setConstraints(addButton, 1, 0);
        GridPane.setConstraints(removeButton, 1, 0);
        GridPane.setConstraints(runListing, 2, 0);
        GridPane.setConstraints(generateButton, 2, 2);
        GridPane.setConstraints(optionsButton, 2, 3);
        GridPane.setConstraints(refreshButton, 2, 4);
        GridPane.setConstraints(fieldButton, 2, 5);

        getLayout().getChildren().addAll(commandListing, addButton, removeButton, runListing, refreshButton, generateButton, optionsButton, fieldButton);

        setScene(new Scene(getLayout(), WINDOW_WIDTH, WINDOW_HEIGHT));

        return getScene();

    }

    private void generateCode(ObservableList<Command> runList, String outputDirectory) {
        List<String> codeList = new ArrayList<>();
        for (Command c : runList) {
            try {
                codeList.add(c.getCode());
            } catch (IOException e) {
                System.err.println("Invalid Parameter List");
            }
        }

        FileHandler.writeListToFile(codeList, outputDirectory);

    }

    private void displayCommandOptions() {
        runListing.getSelectionModel().getSelectedItem();
    }

    private OptionsWindow openOptions(Command command) throws NullPointerException {
        if (command == null) throw new NullPointerException();
        OptionsWindow optionsWindow = new OptionsWindow(getWindow(), this, command);
        Scene optionScene = optionsWindow.display();
        getWindow().setScene(optionScene);
        return optionsWindow;
    }

    private Scene openField() {
        FieldWindow fieldWindow = new FieldWindow(getWindow(), this);
        return fieldWindow.display();
    }

    public void addToRunList(Command command) {
        int selectedIndex = runListing.getSelectionModel().getSelectedIndex();
        runListing.getItems().add(selectedIndex, command);
    }

    private void removeFromRunList() {
        ObservableList<Integer> selectedCommands = runListing.getSelectionModel().getSelectedIndices();
        for (int i = selectedCommands.size() - 1; i >= 0; i--) {
            runListing.getItems().remove((int) selectedCommands.get(selectedCommands.size() - i - 1));
        }
    }

    public ListView<Command> getRunListing() {
        return runListing;
    }

    public void setRunListing(List<Command> runListing) {
        ObservableList<Command> observableList = FXCollections.observableArrayList(runListing);
        this.runListing.setItems(observableList);
        this.runListing.refresh();
    }

    private Map<String, CommandType> initMap() {
        Map<String, CommandType> map = new HashMap<>();
        for (CommandType c : CommandType.values()) {
            map.put(c.name, c);
        }
        return map;

    }

}
