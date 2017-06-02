package ui;

/**
 * Created by Stephen Welch on 5/15/2017.
 */

public enum CommandType {

    TURN("Turn", "new TurnToDegree(driveTrain, navx, angle);", "driveTrain, navx, ", ParamType.Double, ParamType.Double), MOVE("Move", "new DriveStraightDistance(driveTrain, navx,  distance);", "driveTrain, navx, ", ParamType.Double);

    String name, code;
    ParamType[] params;
    String unusedParams;

    CommandType(String name, String code, String unusedParams, ParamType... params) {
        this.name = name;
        this.code = code;
        this.unusedParams = unusedParams;
        this.params = params;
    }

}
