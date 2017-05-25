package ui;

/**
 * Created by Stephen Welch on 5/15/2017.
 */

public enum CommandType {

    TURN("Turn", "public void turn(double degrees) {}", ParamType.Double), MOVE("Move", "public void move(double inches) {}", ParamType.Double);

    String name, code;
    ParamType[] params;

    CommandType(String name, String code, ParamType... params) {
        this.name = name;
        this.code = code;
        this.params = params;
    }

}
