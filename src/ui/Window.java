package ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Created by Stephen Welch on 5/29/2017.
 */
public abstract class Window {

    public final String WINDOW_TITLE;
    public final int WINDOW_WIDTH;
    public final int WINDOW_HEIGHT;

    public final int LAYOUT_PADDING_TOP;
    public final int LAYOUT_PADDING_RIGHT;
    public  final int LAYOUT_PADDING_BOTTOM;
    public final int LAYOUT_PADDING_LEFT;

    public final int HORIZ_CELL_PADDING;
    public final int VERT_CELL_PADDING;

    private Stage window;
    private Scene scene;
    private GridPane layout;

    public Window(Stage window, String title, int width, int height, int topPadding, int rightPadding, int bottomPadding, int leftPadding, int horizCellPadding, int vertCellPadding) {
        this.window = window;
        this.WINDOW_TITLE = title;
        this.WINDOW_WIDTH = width;
        this.WINDOW_HEIGHT = height;
        this.LAYOUT_PADDING_TOP = topPadding;
        this.LAYOUT_PADDING_RIGHT = rightPadding;
        this.LAYOUT_PADDING_BOTTOM = bottomPadding;
        this.LAYOUT_PADDING_LEFT = leftPadding;
        this.HORIZ_CELL_PADDING = horizCellPadding;
        this.VERT_CELL_PADDING = vertCellPadding;
    }

    public Window(Stage window, String title, int width, int height) {
        this(window, title, width, height, 10, 10, 10, 10, 10, 10);
    }


    public void init() {
        window.setTitle(WINDOW_TITLE);
        layout = new GridPane();
        layout.setPadding(new Insets(LAYOUT_PADDING_TOP, LAYOUT_PADDING_RIGHT, LAYOUT_PADDING_BOTTOM, LAYOUT_PADDING_LEFT));
        layout.setHgap(HORIZ_CELL_PADDING); //Set horizontal/vertical padding for layout cells
        layout.setVgap(VERT_CELL_PADDING);
    }

    public abstract Scene display();

    public Scene getScene() {
        return scene;
    }

    public Stage getWindow() {
        return window;
    }

    public void setWindow(Stage window) {
        this.window = window;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public GridPane getLayout() {
        return layout;
    }

    public void setLayout(GridPane layout) {
        this.layout = layout;
    }

}
