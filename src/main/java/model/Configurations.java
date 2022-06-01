package model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Configurations {

    private static Path lockScreenPicturesPath;

    private static String targetDirectoryStringPath = System.getProperty("user.name") != null ?
            "C:\\Users\\" + System.getProperty("user.name")+ "\\Downloads" : "";

    private static String namingScheme = "image";

    private static boolean splitLandscape = true;

    private static long minImageSize = 400000L;

    private static String saveDir = System.getenv("APPDATA") != null ? System.getenv("APPDATA") +
            "\\LockScreenPictureFinder\\configs.txt":
            "LockScreenPictureFinder\\configs.txt";

    public static long getMinImageSize() {
        return minImageSize;
    }

    public static void setMinImageSize(long minImageSize) {
        Configurations.minImageSize = minImageSize;
    }

    public static Path getLockScreenPicturesPath() {
        return lockScreenPicturesPath;
    }

    public static String getTargetDirectoryStringPath() {
        return targetDirectoryStringPath;
    }

    public static void setTargetDirectoryStringPath(String targetDirectoryStringPath) {
        Configurations.targetDirectoryStringPath = targetDirectoryStringPath;
    }

    public static String getNamingScheme() {
        return namingScheme;
    }

    public static void setNamingScheme(String namingScheme) {
        Configurations.namingScheme = namingScheme;
    }

    public static boolean isSplitLandscape() {
        return splitLandscape;
    }

    public static void setSplitLandscape(boolean splitLandscape) {
        Configurations.splitLandscape = splitLandscape;
    }

    public static void setLockScreenPicturesPath(Path lockScreenPicturesPath) {
        Configurations.lockScreenPicturesPath = lockScreenPicturesPath;
    }

    public static boolean generateLockScreenPath(String username) {

        if (username != null) {
            Path path = Paths.get("C:\\Users\\" + username +
                    "\\AppData\\Local\\Packages\\" +
                    "Microsoft.Windows.ContentDeliveryManager_cw5n1h2txyewy\\LocalState\\Assets");
            if (Files.exists(path)) {
                lockScreenPicturesPath = path;
                return true;
            } else {
                return false;
            }
        }

        try {
            username = System.getProperty("user.name");
            if (username == null) {
                return false;
            } else {
                return generateLockScreenPath(username);
            }

        } catch (Exception e) {
            return false;
        }
    }

    public static String getSaveDir() {
        return saveDir;
    }

    public static void setSaveDir(String saveDir) {
        Configurations.saveDir = saveDir;
    }
}
