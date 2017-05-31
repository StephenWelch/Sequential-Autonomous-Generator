package ui;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stephen Welch on 5/29/2017.
 */
public class FieldWindow extends Window {

    private static final String WINDOW_TITLE = "Field UI";
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 576;
    private static final int TEXT_X_OFFSET = 8;
    private static final int TEXT_Y_OFFSET = 8;
    private static final double DEFAULT_PPI = 0.71806167400881057268722466960352;
    private static final double DEFAULT_STARTING_X_INCHES = 0;
    private static final double DEFAULT_STARTING_Y_INCHES = 52;
    private static final double NEAR_TO_ZERO = 1;

    private Canvas fieldCanvas;
    private GraphicsContext canvasGraphics;

    private Image fieldImage;
    private Label ppiLabel, startXLabel, startYLabel;
    private TextField ppiField, startXInchesField, startYInchesField;
    private Button undoButton, redoButton, backButton;

    private LinkedList<ExtendedLine> lineList, removedLineList;
    private boolean addingLine;
    private boolean shouldRender;
    private SelectorWindow prevWindow;

    public FieldWindow(Stage window, SelectorWindow prevWindow) {
        super(window, WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.lineList = new LinkedList<>();
        this.removedLineList = new LinkedList<>();
        this.addingLine = false;
        this.prevWindow = prevWindow;

    }

    public Scene display() {

        fieldImage = new Image(getClass().getResourceAsStream("field.jpg"), 800, 600, true, true);

        fieldCanvas = new Canvas(fieldImage.getWidth(), fieldImage.getHeight());
        fieldCanvas.setOnMouseClicked(e -> {
            shouldRender = (lineList.peekLast() == null) ? false : true;
            updateLineList(lineList, e.getX(), e.getY());
            //Render last line in updated list
            //We don't want to render the first line, since it will appear as a line from (0,0) to (x,y) until another point is clicked
            if (shouldRender) render(lineList.peekLast());
        });
        canvasGraphics = fieldCanvas.getGraphicsContext2D();
        canvasGraphics.drawImage(fieldImage, 0, 0, fieldImage.getWidth(), fieldImage.getHeight());

        ppiLabel = new Label("Set PPI: ");
        ppiField = new TextField(Double.toString(DEFAULT_PPI));
        ppiField.setPrefWidth(50);

        startXLabel = new Label("Starting X (inches): ");
        startYLabel = new Label("Starting Y (inches): ");

        startXInchesField = new TextField(Double.toString(DEFAULT_STARTING_X_INCHES));
        startYInchesField = new TextField(Double.toString(DEFAULT_STARTING_Y_INCHES));

        undoButton = new Button("Undo");
        undoButton.setOnAction(e -> {
            if (lineList.peekLast() != null) removedLineList.addFirst(lineList.removeLast());
            refreshCanvas(fieldCanvas, fieldImage, lineList);
        });
        redoButton = new Button("Redo");
        redoButton.setOnAction(e -> {
            if (removedLineList.peek() != null) lineList.add(removedLineList.poll());
            refreshCanvas(fieldCanvas, fieldImage, lineList);
        });

        backButton = new Button("Back");
        backButton.setOnAction(e -> {
            prevWindow.setRunListing(getCommandList());
            getWindow().setScene(prevWindow.getScene());
        });


        GridPane.setConstraints(fieldCanvas, 2, 0);
        GridPane.setConstraints(ppiLabel, 0, 0);
        GridPane.setConstraints(ppiField, 1, 0);
        GridPane.setConstraints(undoButton, 0, 1);
        GridPane.setConstraints(redoButton, 0, 2);
        GridPane.setConstraints(backButton, 0, 3);
        GridPane.setConstraints(startXLabel, 0, 4);
        GridPane.setConstraints(startYLabel, 0, 5);
        GridPane.setConstraints(startXInchesField, 1, 4);
        GridPane.setConstraints(startYInchesField, 1, 5);

        getLayout().getChildren().addAll(fieldCanvas, ppiLabel, ppiField, undoButton, redoButton, backButton, startXLabel, startYLabel, startXInchesField, startYInchesField);

        setScene(new Scene(getLayout(), WINDOW_WIDTH, WINDOW_HEIGHT));
        return getScene();
    }

    public List<Command> getCommandList() {

        List<Command> list = new ArrayList<>();

        for (ExtendedLine l : lineList) {

            Object[] angle = {l.getAngleToPrev()};
            Object[] distance = {l.getLengthInches(getPPI())};

            //if (!isWithinError(l.getAngleToPrev(), NEAR_TO_ZERO))
                list.add(new Command(CommandType.TURN, angle));
            //if (!isWithinError(l.getLengthInches(getPPI()), NEAR_TO_ZERO))
                list.add(new Command(CommandType.MOVE, distance));

        }

        return list;

    }

    private void updateLineList(LinkedList<ExtendedLine> lineList, double x, double y) {
        ExtendedLine line = lineList.peekLast();

        if (line == null) {
            System.out.println("Set origin, adding new line.");
            ExtendedLine rootLine = new ExtendedLine(x, y);
            lineList.add(rootLine);
            addingLine = true;
        } else if (addingLine) {
            System.out.println("Set endpoint, drawing.");
            line.setEndX(x);
            line.setEndY(y);
            addingLine = false;
        } else if (addingLine == false) {
            //Grab last line
            ExtendedLine lineToAdd = new ExtendedLine(lineList.get(lineList.size() - 1), x, y);
            lineList.add(lineToAdd);
        }

    }

    private void refreshCanvas(Canvas canvas, Image img, LinkedList<ExtendedLine> lineList) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(img, 0, 0, canvas.getWidth(), canvas.getHeight());
        render(lineList);
    }

    private void render(LinkedList<ExtendedLine> lineList) {
        for (ExtendedLine l : lineList) {
            render(l);
        }
    }

    private void render(ExtendedLine line) {
        drawLine(line, canvasGraphics);
        canvasGraphics.setStroke(Color.WHITE);
        canvasGraphics.fillText(Double.toString(round(line.getAngleToPrev())) + "°", line.getStartX() + TEXT_Y_OFFSET, line.getStartY());
        canvasGraphics.fillText(Double.toString(round(line.getLengthInches(getPPI()))) + "\"", line.getMidpointX() + TEXT_X_OFFSET, line.getMidpointY());
    }

    private void drawLine(ExtendedLine line, GraphicsContext gc) {
        gc.setStroke(Color.RED);
        gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
    }

    private double inchesToPixels(double inches) {
        return inches * getPPI();
    }

    private double pixelsToInches(double pixels) {
        return pixels / getPPI();
    }

    private double getPPI() {
        return Double.valueOf(ppiField.getText());
    }

    private boolean isWithinError(double value, double error) {
        return ((value >= (value - error)) || (value <= (value + error))) ? true : false;
    }

    private double round(double d) {
        return Math.round(d * 100.0) / 100.0;
    }

}

//Ay varfoer aer du saa feransvaerd paa detta? Jag tyckerde att det skulle vara laett foer dig... -Mason Marche