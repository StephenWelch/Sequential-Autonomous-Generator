package ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stephen Welch on 5/29/2017.
 */
public class FieldWindow extends Window {

    private static final String WINDOW_TITLE = "Field UI";
    private static final int WINDOW_WIDTH = 825;
    private static final int WINDOW_HEIGHT = 535;

    private static final int TEXT_X_OFFSET = 8;
    private static final int TEXT_Y_OFFSET = 8;
    private static final Color START_MARKER_COLOR = Color.GREEN;
    private static final double START_MARKER_RADIUS = 20;
    private static final Color LINE_COLOR = Color.RED;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final double DEFAULT_STARTING_X_INCHES = 50;
    private static final double DEFAULT_STARTING_Y_INCHES = 310;
    private static final double FIELD_LENGTH_INCHES = 652;
    private double ppi;

    private static final double ANGLE_ERROR_TOLERANCE = 3;

    private Canvas fieldCanvas;
    private GraphicsContext canvasGraphics;

    private Image fieldImage;
    private Label ppiLabel, startXLabel, startYLabel, mousePosLabel;
    private TextField ppiField, startXInchesField, startYInchesField;
    private Button undoButton, redoButton, backButton, resetButton;
    private HBox configLayout, optionsLayout;

    private LinkedList<ExtendedLine> lineList, removedLineList;
    private SelectorWindow prevWindow;

    public FieldWindow(Stage window, SelectorWindow prevWindow) {
        super(window, WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.lineList = new LinkedList<>();
        this.removedLineList = new LinkedList<>();
        this.prevWindow = prevWindow;
    }

    public Scene display() {

        fieldImage = new Image(getClass().getResourceAsStream("field.png"), 800, 600, true, true);

        fieldCanvas = new Canvas(fieldImage.getWidth(), fieldImage.getHeight());

        canvasGraphics = fieldCanvas.getGraphicsContext2D();
        canvasGraphics.drawImage(fieldImage, 0, 0, fieldImage.getWidth(), fieldImage.getHeight());
        ppi = FIELD_LENGTH_INCHES / fieldImage.getWidth();


        ppiLabel = new Label("Set PPI: ");
        ppiField = new TextField(Double.toString(ppi));
        ppiField.setPrefWidth(50);

        startXLabel = new Label("Starting X (inches): ");
        startYLabel = new Label("Starting Y (inches): ");

        startXInchesField = new TextField(Double.toString(DEFAULT_STARTING_X_INCHES));
        startYInchesField = new TextField(Double.toString(DEFAULT_STARTING_Y_INCHES));

        undoButton = new Button("Undo");
        redoButton = new Button("Redo");
        backButton = new Button("Back");
        resetButton = new Button("Reset");
        mousePosLabel = new Label();

        configLayout = new HBox();
        configLayout.setSpacing(9);
        configLayout.setPadding(new Insets(0, 10, 0, 0));


        optionsLayout = new HBox();
        optionsLayout.setSpacing(9);
        optionsLayout.setPadding(new Insets(0, 10, 0, 0));

        //Initial render
        refreshCanvas(fieldCanvas, fieldImage, lineList, new Circle(getStartingPosX(), getStartingPosY(), START_MARKER_RADIUS), START_MARKER_COLOR, LINE_COLOR, TEXT_COLOR);
        fieldCanvas.setOnMouseClicked(e -> {
            updateLineList(lineList, e.getX(), e.getY());
            refreshCanvas(fieldCanvas, fieldImage, lineList, new Circle(getStartingPosX(), getStartingPosY(), START_MARKER_RADIUS), START_MARKER_COLOR, LINE_COLOR, TEXT_COLOR);
        });
        fieldCanvas.setOnMouseMoved(e -> mousePosLabel.setText("X: " + round(pixelsToInches(e.getX())) + " Y: " + round(pixelsToInches(e.getY()))));

        redoButton.setOnAction(e -> {
            if (removedLineList.peek() != null) lineList.add(removedLineList.poll());
            refreshCanvas(fieldCanvas, fieldImage, lineList, new Circle(getStartingPosX(), getStartingPosY(), START_MARKER_RADIUS), START_MARKER_COLOR, LINE_COLOR, TEXT_COLOR);
        });

        undoButton.setOnAction(e -> {
            if (lineList.peekLast() != null) removedLineList.addFirst(lineList.removeLast());
            refreshCanvas(fieldCanvas, fieldImage, lineList, new Circle(getStartingPosX(), getStartingPosY(), START_MARKER_RADIUS), START_MARKER_COLOR, LINE_COLOR, TEXT_COLOR);
        });

        backButton.setOnAction(e -> {
            prevWindow.setRunListing(getCommandList());
            getWindow().setScene(prevWindow.getScene());
        });

        resetButton.setOnAction(e -> {
            lineList.clear();
            refreshCanvas(fieldCanvas, fieldImage, lineList, new Circle(getStartingPosX(), getStartingPosY(), START_MARKER_RADIUS), START_MARKER_COLOR, LINE_COLOR, TEXT_COLOR);
        });

        GridPane.setConstraints(fieldCanvas, 0, 0);
        GridPane.setConstraints(configLayout, 0, 1);
        GridPane.setConstraints(optionsLayout, 0, 2);

        optionsLayout.getChildren().addAll(backButton, undoButton, redoButton, resetButton);
        configLayout.getChildren().addAll(ppiLabel, ppiField, startXLabel, startXInchesField, startYLabel, startYInchesField, mousePosLabel);
        getLayout().getChildren().addAll(fieldCanvas, configLayout, optionsLayout);

        setScene(new Scene(getLayout(), WINDOW_WIDTH, WINDOW_HEIGHT));
        return getScene();
    }

    public List<Command> getCommandList() {

        List<Command> list = new ArrayList<>();

        for (ExtendedLine l : lineList) {

            Object[] angle = {l.getAngleToPrev(), ANGLE_ERROR_TOLERANCE};
            Object[] distance = {l.getLengthInches(getPPI())};

            list.add(new Command(CommandType.TURN, angle));
            list.add(new Command(CommandType.MOVE, distance));

        }

        return list;

    }

    private void updateLineList(LinkedList<ExtendedLine> lineList, double x, double y) {
        ExtendedLine line = lineList.peekLast();

        if (line == null) {
            System.out.println("Starting chain");
            ExtendedLine rootLine = new ExtendedLine(null, getStartingPosX(), getStartingPosY(), x, y);
            lineList.add(rootLine);
        } else {
            System.out.println("Adding to chain");
            //Grab last line
            ExtendedLine lineToAdd = new ExtendedLine(lineList.get(lineList.size() - 1), x, y);
            lineList.add(lineToAdd);
        }

    }

    private void refreshCanvas(Canvas canvas, Image img, LinkedList<ExtendedLine> lineList, Circle startMarker, Color startMarkerColor, Color pathColor, Color textColor) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(img, 0, 0, canvas.getWidth(), canvas.getHeight());
        for (ExtendedLine l : lineList) {
            renderLine(l, pathColor, textColor, TEXT_X_OFFSET, TEXT_Y_OFFSET);
        }
        drawCircle(startMarker, startMarkerColor, canvasGraphics);
    }

    private void renderLine(ExtendedLine line, Color lineColor, Color textColor, double textOffsetX, double textOffsetY) {
        if(line != null) drawLine(line, lineColor, canvasGraphics);
        drawText(Double.toString(round(line.getAngleToPrev())) + "°", textColor, line.getStartX() + textOffsetY,  line.getStartY(), canvasGraphics);
        drawText(Double.toString(round(line.getLengthInches(getPPI()))) + "\"", textColor, line.getMidpointX() + textOffsetX, line.getMidpointY(), canvasGraphics);
    }

    private void drawCircle(Circle circle, Color color, GraphicsContext gc) {
        gc.setStroke(color);
        //Because oval takes coordinates for the top-left corner, we have to adjust the circle's coordinates
        double trueCenterX = circle.getCenterX() - (circle.getRadius() / 2);
        double trueCenterY = circle.getCenterY() - (circle.getRadius() / 2);
        gc.strokeOval(trueCenterX, trueCenterY, circle.getRadius(), circle.getRadius());
    }

    private void drawLine(ExtendedLine line, Color color, GraphicsContext gc) {
        gc.setStroke(color);
        gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
    }

    private void drawText(String text, Color color, double x, double y, GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillText(text, x, y);
    }

    public double getStartingPosX() {
        return inchesToPixels(Double.valueOf(startXInchesField.getText()));
    }

    public double getStartingPosY() {
        return inchesToPixels(Double.valueOf(startYInchesField.getText()));
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