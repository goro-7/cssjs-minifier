package hu.szrnkapeter.cssjsminifier.util;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class FileUtils {

    public static void createFile(String fileLocation) {

        Path file = new File(fileLocation).toPath();

        try {
            // create parent directory if not exists
            Path directory = file.getParent();
            if(!Files.exists(directory))
            Files.createDirectory(directory);

            // delete if exists
            Files.deleteIfExists(file);

            // create new empty file
            Files.createFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
