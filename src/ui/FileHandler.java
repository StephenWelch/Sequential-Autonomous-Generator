package ui;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Created by Stephen Welch on 5/16/2017.
 */
public class FileHandler {

    public static void writeListToFile(List list, String filePath) {
        Path file = Paths.get(filePath);
        try {
            Files.write(file, list, Charset.forName("UTF-8"));
            Files.write(file, list, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Error writing to file.");
        }
    }

}
