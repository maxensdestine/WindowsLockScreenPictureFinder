package UnitTests;

import controller.FileController;
import model.Configurations;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static Utils.TestUtils.*;

class CopyPasteTest {

    @BeforeEach
    public void createDestinationFolder() throws IOException {
        Configurations.setSplitLandscape(false);
        Files.createDirectories(DST_PATH);
    }

    @AfterEach
    public void resetTestFolders() throws IOException {
        deleteDestinationDirectory();
    }


    @Test
    public void testCopyPasteExistingFile() throws IOException {

        File file1 = new File(SRC_PATH_STR + "krul.png");
        Path path1 = Paths.get(DST_PATH_STR + "img1.png");

        // assert the method indicates success with a "true" return
        assertTrue(FileController.copyPasteFile(file1, path1));

        DirectoryStream<Path> stream = Files.newDirectoryStream(DST_PATH);
        Iterator<Path> ite = stream.iterator();

        // assert the file was copied correctly
        assertEquals(ite.next(), path1);
        assertFalse(ite.hasNext());
        stream.close();
    }


    @Test
    public void testCopyPasteMultiCase() throws IOException {

        File file1 = new File(SRC_PATH_STR + "krul.png");
        File file2 = new File(SRC_PATH_STR + "flower");

        // this file is not an image
        File file3 = new File(SRC_PATH_STR + "first_test_file.png");

        Path path1 = Paths.get(DST_PATH_STR + "img1.png");
        Path path2 = Paths.get(DST_PATH_STR + "img2.png");
        Path path3 = Paths.get(DST_PATH_STR + "img3.png");

        // this path does not exist on the computer
        Path path4 = Paths.get("src/test/resources/destinationnnn/img3.png");

        assertTrue(FileController.copyPasteFile(file1, path1));
        assertTrue(FileController.copyPasteFile(file2, path2));
        assertFalse(FileController.copyPasteFile(file3, path3));
        assertFalse(FileController.copyPasteFile(file1, path4));

        DirectoryStream<Path> stream = Files.newDirectoryStream(DST_PATH);
        Iterator<Path> ite = stream.iterator();
        assertEquals(ite.next(), path1);
        assertEquals(ite.next(), path2);
        assertFalse(ite.hasNext());
        stream.close();
    }

    @Test
    public void testCopyPasteNonImageFile() throws IOException {
        File file3 = new File(SRC_PATH_STR + "first_test_file.png");
        Path path3 = Paths.get(DST_PATH_STR + "img3.png");

        // assert the method indicates failure with a "false" return
        assertFalse(FileController.copyPasteFile(file3, path3));

        File file = DST_PATH.toFile();
        int nbFiles = file.list().length;

        // assert the file was not pasted
        assertEquals(0, nbFiles);
    }

    @Test
    public void testCopyPasteToNonExistingFolder() throws IOException {
        File file3 = new File(SRC_PATH_STR + "krul.png");
        Path path3 = Paths.get("src/test/resources/destinationnnnn/img3.png");

        // assert the method indicates failure with a "false" return
        assertFalse(FileController.copyPasteFile(file3, path3));

        // assert that the directory does not exist
        assertThrows(NoSuchFileException.class,
                () -> Files.newDirectoryStream(Paths.get("src/test/resources/destinationnnnn")));
    }

}