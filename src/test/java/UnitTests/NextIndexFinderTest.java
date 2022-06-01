package UnitTests;

import controller.FileController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class NextIndexFinderTest {

    private static MockedStatic<Files> files;

    @BeforeAll
    public static void setup() {
        files = Mockito.mockStatic(Files.class);
        files.when(() -> Files.exists(any())).thenReturn(true);
    }

    @Test
    public void testMaxIndexFinderNormal() throws IOException {

        ArrayList<Path> paths = new ArrayList<>();
        paths.add(Paths.get("lol3"));
        paths.add(Paths.get("lol2"));
        paths.add(Paths.get("lol1"));
        paths.add(Paths.get("lol10"));
        paths.add(Paths.get("loll12"));

        DirectoryStream<Path> stream = new DirectoryStream<Path>() {
            @Override
            public Iterator<Path> iterator() {
                return paths.iterator();
            }

            @Override
            public void close() throws IOException {

            }
        };

        File file = new File("someFile");
        Path path = file.toPath();

        files.when(() -> Files.newDirectoryStream(path)).thenReturn(stream);

        assertEquals(11, FileController.findNextNameIndex(path, "lol"));
    }

    @Test
    public void testMaxIndexFinderEmptyNamingScheme() throws IOException {

        ArrayList<Path> paths = new ArrayList<>();
        paths.add(Paths.get("3"));
        paths.add(Paths.get("lol2"));
        paths.add(Paths.get("lol1"));
        paths.add(Paths.get("18"));
        paths.add(Paths.get("loll12"));

        DirectoryStream<Path> stream = new DirectoryStream<Path>() {
            @Override
            public Iterator<Path> iterator() {
                return paths.iterator();
            }

            @Override
            public void close() throws IOException {

            }
        };

        File file = new File("someFile");
        Path path = file.toPath();

        files.when(() -> Files.newDirectoryStream(path)).thenReturn(stream);

        assertEquals(19, FileController.findNextNameIndex(path, ""));
    }

    @Test
    public void testMaxIndexFinderEmpty() throws IOException {

        ArrayList<Path> paths = new ArrayList<>();

        DirectoryStream<Path> stream = new DirectoryStream<Path>() {
            @Override
            public Iterator<Path> iterator() {
                return paths.iterator();
            }

            @Override
            public void close() throws IOException {
            }
        };

        File file = new File("someFile");
        Path path = file.toPath();
        files.when(() -> Files.newDirectoryStream(path)).thenReturn(stream);

        assertEquals(1, FileController.findNextNameIndex(path, "lol"));
    }
}
