package Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

    public static final String SRC_PATH_STR = "src/test/resources/source/";
    public static final Path SRC_PATH = Paths.get(SRC_PATH_STR);
    public static final String DST_PATH_STR = "src/test/resources/destination/";
    public static final Path DST_PATH = Paths.get(DST_PATH_STR);

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static void deleteDestinationDirectory() throws IOException {
        boolean deletionResult = TestUtils.deleteDirectory(DST_PATH.toFile());

        if (!deletionResult) {
            throw new IOException("Could not delete the destination directory");
        }
    }
}

