package IntegrationTests;

import controller.FileController;
import model.Configurations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import Utils.TestUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static Utils.TestUtils.*;

public class CopyAllTest {

    @BeforeAll
    public static void setup(){
        Configurations.setLockScreenPicturesPath(Paths.get(SRC_PATH.toAbsolutePath().toString()));
        Configurations.setTargetDirectoryStringPath(DST_PATH.toAbsolutePath().toString());
        Configurations.setNamingScheme("img");

        // we should set this to the size of the smallest test image (real image)
        Configurations.setMinImageSize(140000);
    }

    @AfterEach
    public void resetTestFolders() throws IOException {
        TestUtils.deleteDestinationDirectory();
    }

    @Test
    public void testCopyPasteSeparateLandscape() throws IOException {

        Configurations.setSplitLandscape(true);

        Path path1 = Paths.get(DST_PATH.toAbsolutePath().toString() + "\\" + "Landscape" + "\\" + "img1.png");
        Path path2 = Paths.get(DST_PATH.toAbsolutePath().toString() + "\\" + "Portrait" + "\\" + "img2.png");
        Path path3 = Paths.get(SRC_PATH_STR + "\\" + "first_test_file.png");

        HashMap<String, List<String>> map = FileController.copyPasteAllFiles();
        List<String> copied = map.get("copied");
        List<String> notCopied = map.get("notCopied");

        // validate the output of the function
        assertEquals(2, copied.size());
        assertEquals(path1.toAbsolutePath().toString(), copied.get(0));
        assertEquals(path2.toAbsolutePath().toString(), copied.get(1));
        assertEquals(1, notCopied.size());
        assertEquals(path3.toAbsolutePath().toString(), notCopied.get(0));


        DirectoryStream<Path> landscapeStream = Files.newDirectoryStream(Paths.get(DST_PATH_STR + "\\" + "Landscape"));
        DirectoryStream<Path> portraitStream = Files.newDirectoryStream(Paths.get(DST_PATH_STR + "\\" + "Portrait"));
        Iterator<Path> landscapeIte = landscapeStream.iterator();
        Iterator<Path> portraitIte = portraitStream.iterator();

        // assert the images are in the right folders
        assertEquals(landscapeIte.next().toAbsolutePath(), path1);
        assertEquals(portraitIte.next().toAbsolutePath(), path2);

        // assert they do not have extra elements
        assertFalse(landscapeIte.hasNext());
        assertFalse(portraitIte.hasNext());

        // close both streams
        landscapeStream.close();
        portraitStream.close();
    }
}
