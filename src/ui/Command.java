package ui;

import java.io.IOException;

/**
 * Created by Stephen Welch on 5/15/2017.
 */
public class Command {

    private CommandType commandType;
    private String code;
    private Object[] parameterArray;

    public Command(CommandType commandType, Object[] parameters) {
        this.commandType = commandType;
        this.parameterArray = parameters;
        this.code = commandType.code;
    }

    public Command(CommandType commandType) {
        this.commandType = commandType;
        this.parameterArray = new Object[0];
        this.code = commandType.code;
    }

    public String toString() {
        String parameterString = generateParamString(parameterArray);
        return commandType.name + " " + parameterString;
    }

    //Check whether the array of required parameter types matches the one passed in in size and name
    private boolean checkParamValidity() {

        for (int i = 0; i < commandType.params.length; i++) {
            String givenTypePackageName = parameterArray[i].getClass().getName();
            String requiredTypePackageName = commandType.params[i].packageName;

            if (!givenTypePackageName.equals(requiredTypePackageName)) return false;
        }

        return true;
    }

    public String getCode() throws IOException {
        if (!checkParamValidity()) throw new IOException();
        String parameterString = generateParamString(parameterArray);
        return insertParams(code, parameterString);
    }


    private String insertParams(String code, String parameterString) {
        String firstLine = code.substring(0, code.indexOf("{"));
        String first = firstLine.substring(0, firstLine.indexOf("(") + 1);
        String last = code.substring(firstLine.lastIndexOf(")"), code.length());

        return first + parameterString + last;
    }

    private String generateParamString(Object[] params) {
        String paramString = "";

        if (params.length <= 0) return paramString;
        if (params.length <= 1) return params[0].toString();

        for (Object o : params) {
            paramString += o.toString();
        }
        paramString = paramString.substring(0, paramString.length() - 1);
        return paramString;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public Object[] getParameterArray() {
        return parameterArray;
    }

    public void setParameterArray(Object... parameterArray) {
        this.parameterArray = parameterArray;
    }

}
