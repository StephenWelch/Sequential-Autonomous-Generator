package ui;

/**
 * Created by Stephen Welch on 5/16/2017.
 */

//Consider switching to automatic package detection by getting example of variable then using getClass().getName()?
public enum ParamType {
    Double("java.lang.Double"), String("java.lang.String");

    String packageName;

    ParamType(String packageName) {
        this.packageName = packageName;
    }
}
