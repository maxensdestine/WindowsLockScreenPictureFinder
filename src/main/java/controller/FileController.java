package controller;

import model.Configurations;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileController {

    public static boolean isLandscape(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        return image.getWidth() > image.getHeight();
    }


    public static boolean copyPasteFile(File src, Path dst) {
        try {
            // validates that the file is an image
            BufferedImage image = ImageIO.read(src);
            if (image == null || !Files.exists(dst.toAbsolutePath().getParent())) {
                return false;
            } else {
                Files.copy(src.toPath(), dst, StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static HashMap<String, List<String>> copyPasteAllFiles() {
        List<String> copiedFilePaths = new ArrayList<>();
        List<String> notCopiedFilePaths = new ArrayList<>();
        HashMap<String, List<String>> map = new HashMap<>();

        map.put("copied", copiedFilePaths);
        map.put("notCopied", notCopiedFilePaths);

        final Path lockScreenDirPath = Configurations.getLockScreenPicturesPath();
        Path targetDir = Paths.get(Configurations.getTargetDirectoryStringPath());
        String namingScheme = Configurations.getNamingScheme();


        int currentIndex = findNextNameIndex(targetDir, namingScheme);
        if(currentIndex < 1){
            return null;
        }

        try {
            Files.createDirectories(targetDir);
            if (Configurations.isSplitLandscape()) {
                Files.createDirectories(Paths.get
                        (Configurations.getTargetDirectoryStringPath() + "\\" + "Landscape"));
                Files.createDirectories(Paths.get
                        (Configurations.getTargetDirectoryStringPath() + "\\" + "Portrait"));
            }
        } catch (IOException ioException) {
            ErrorLogController.addError("Could not create or access the target directory. " +
                    "Pictures will not be retrieved.");
            return map;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(lockScreenDirPath)) {

            for (Path path : stream) {
                File currentFile = path.toFile();

                if (currentFile.length() > Configurations.getMinImageSize()) {
                    String filePath = null;
                    if (Configurations.isSplitLandscape()) {
                        filePath = isLandscape(currentFile) ?
                                targetDir.toString() + "\\" + "Landscape" + "\\" + namingScheme + currentIndex + ".png" :
                                targetDir.toString() + "\\" + "Portrait" + "\\" + namingScheme + currentIndex + ".png";
                    } else {
                        filePath = targetDir.toString() + "\\" + namingScheme + currentIndex + ".png";
                    }

                    if (copyPasteFile(currentFile, Paths.get(filePath))) {
                        copiedFilePaths.add(filePath);
                        currentIndex++;
                    } else {
                        notCopiedFilePaths.add(path.toAbsolutePath().toString());
                    }
                } else {
                    notCopiedFilePaths.add(path.toAbsolutePath().toString());
                }
            }
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            ErrorLogController.addError("Error while fetching the lock screen pictures. " +
                    "Try running the app in admin mode.");
        }
        return map;
    }

    public static int findNextNameIndex(Path targetDir, String namingScheme) {
        int maxIndex = 0;
        if (Files.exists(targetDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(targetDir)) {
                for (Path path : stream) {
                    String name = path.getFileName().toString();
                    String strippedName = namingScheme.equals("") ? name : name.replace(namingScheme, "");
                    try {
                        int nb = Integer.parseInt(strippedName);
                        if (nb > maxIndex) {
                            maxIndex = nb;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            } catch (IOException | DirectoryIteratorException x) {
                // IOException can never be thrown by the iteration.
                // In this snippet, it can only be thrown by newDirectoryStream.
                ErrorLogController.addError("Could not read the output directory. Pictures will not be retrieved.");
                return -1;
            }
        }
        return maxIndex + 1;
    }

    public static boolean isFilenameValid(String file) {
        File f = new File(file);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
