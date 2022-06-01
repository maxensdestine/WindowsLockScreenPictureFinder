package controller;

import model.Configurations;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigurationsController {

    private static final String KEY_NAMING_SCHEME = "namingScheme";
    private static final String KEY_TARGET_DIR_PATH = "targetDirectoryPath";
    private static final String KEY_SPLIT_LANDSCAPE = "splitLandscape";
    private static final String KEY_MIN_IMG_SIZE = "minImageSize";
    private static final String DEFAULT_CONFIG_FILE_NAME = Configurations.getSaveDir();

    public static void storeLocationInformation(String filePath) {

        if (filePath == null) {
            filePath = DEFAULT_CONFIG_FILE_NAME;
        }

        Properties prop = new Properties();

        if (Configurations.getNamingScheme() != null) {
            prop.setProperty(KEY_NAMING_SCHEME, Configurations.getNamingScheme());
        }
        if (Configurations.getTargetDirectoryStringPath() != null) {
            prop.setProperty(KEY_TARGET_DIR_PATH, Configurations.getTargetDirectoryStringPath());
        }

        prop.setProperty(KEY_SPLIT_LANDSCAPE, Configurations.isSplitLandscape() ? "true" : "false");

        prop.setProperty(KEY_MIN_IMG_SIZE, Configurations.getMinImageSize() + "");
        try (OutputStream output = new FileOutputStream(filePath)) {
            prop.store(output, null);
        } catch (IOException io) {

        }
    }

    public static void loadLocationInformation(String filePath) {
        if (filePath == null) {
            filePath = DEFAULT_CONFIG_FILE_NAME;
        }
        String parent = new File(DEFAULT_CONFIG_FILE_NAME).getParent();
        if(!Files.exists(Paths.get(parent))){
            try {
                Files.createDirectories(Paths.get(parent));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Properties prop = new Properties();
        boolean noError = true;

        try (InputStream input = new FileInputStream(filePath)) {
            prop.load(input);
        } catch (IOException io) {
            noError = false;
        }

        if (noError) {
            if (prop.containsKey(KEY_NAMING_SCHEME)) {
                Configurations.setNamingScheme(prop.getProperty(KEY_NAMING_SCHEME));
            }

            if (prop.containsKey(KEY_TARGET_DIR_PATH)) {
                String targetDir = prop.getProperty(KEY_TARGET_DIR_PATH);
                try {
                    if (Files.exists(Paths.get(targetDir))) {
                        Configurations.setTargetDirectoryStringPath(prop.getProperty(KEY_TARGET_DIR_PATH));
                    } else {
                        Configurations.setTargetDirectoryStringPath("");
                    }
                } catch (Exception e) {
                    Configurations.setTargetDirectoryStringPath("");
                }
            }

            if (prop.containsKey(KEY_MIN_IMG_SIZE)) {
                Long minSize = null;
                try {
                    minSize = Long.parseLong(prop.getProperty(KEY_MIN_IMG_SIZE));
                    Configurations.setMinImageSize(minSize);
                } catch (NumberFormatException e) {

                }
            }

            if (prop.containsKey(KEY_SPLIT_LANDSCAPE)) {
                Configurations.setSplitLandscape(Boolean.parseBoolean(prop.getProperty(KEY_SPLIT_LANDSCAPE)));
            }
        }
    }

    public static boolean changeNamingScheme(String namingScheme) {
        namingScheme = namingScheme.trim();
        if (namingScheme != null && !namingScheme.equals("")) {
            Configurations.setNamingScheme(namingScheme);
            return true;
        }
        return false;
    }

    public static boolean changeTargetDir(String targetDir) {
        targetDir = targetDir.trim();
        if (targetDir != null && !targetDir.equals("")) {
            Configurations.setTargetDirectoryStringPath(targetDir);
            return true;
        }
        return false;
    }

    public static boolean changeSplitLandscape(boolean isSplitLandscape) {
        Configurations.setSplitLandscape(isSplitLandscape);
        return true;
    }

    public static boolean changeMinImageSize(long kbSize) {
        if (kbSize >= 0) {
            Configurations.setMinImageSize(kbSize * 1000);
            return true;
        }
        return false;

    }

    public static boolean generateLockScreenPath(String username) {
        return Configurations.generateLockScreenPath(username);
    }

    public static long getMinImageSize() {
        return Configurations.getMinImageSize();
    }

    public static String getNamingScheme() {
        return Configurations.getNamingScheme();
    }

    public static String getTargetDir() {
        String targetDir = Configurations.getTargetDirectoryStringPath();
        if (targetDir == null) {
            return "";
        }
        return targetDir;
    }

    public static boolean getSplitLandscape() {
        return Configurations.isSplitLandscape();
    }
}
