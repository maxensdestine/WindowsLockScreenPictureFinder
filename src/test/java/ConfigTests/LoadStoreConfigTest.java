package ConfigTests;

import controller.ConfigurationsController;
import model.Configurations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static Utils.TestUtils.SRC_PATH;

public class LoadStoreConfigTest {

    private static Path configPath = Paths.get(SRC_PATH.toAbsolutePath().getParent().toString() + "\\" + "configs.txt");

    @AfterEach
    public void removeConfig(){
        File config = configPath.toFile();
        config.delete();
    }

    @Test
    public void storeConfig(){

        // assert the file does not exist
        assertFalse(Files.exists(configPath));

        Configurations.setMinImageSize(1);
        ConfigurationsController.storeLocationInformation(configPath.toAbsolutePath().toString());

        // assert the file was created
        assertTrue(Files.exists(configPath));

        Configurations.setMinImageSize(7);
        ConfigurationsController.loadLocationInformation(configPath.toAbsolutePath().toString());

        // assert the data is loaded correctly
        assertEquals(1, Configurations.getMinImageSize());
    }

    @Test
    public void gettingUsername(){
        String username = System.getProperty("user.name");
        System.out.println(username);
    }
}
