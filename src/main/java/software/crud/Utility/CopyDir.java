package software.crud.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class CopyDir {
    private static final String TEXT_MIME_TYPE_PREFIX = "text";
    private static final byte[] BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

    public static void copy(String sourceDirectory, String targetDirectory, String projectName, String existingProjectName) {
        File source = new File(sourceDirectory);
        File target = new File(targetDirectory);
        copyAll(source, target, projectName, existingProjectName);
    }

    public static void copyAll(File source, File target, String projectName, String existingProjectName) {
        if (!target.exists() && !target.mkdirs()) {
            System.err.println("Failed to create target directory: " + target.getAbsolutePath());
            return;
        }

        File[] files = source.listFiles();
        if (files == null) {
            System.err.println("Failed to list files in source directory: " + source.getAbsolutePath());
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                copyFile(file, target, projectName, existingProjectName);
            } else if (file.isDirectory() && !file.getName().equals(".vs")) {
                File targetDir = new File(target, file.getName());
                copyAll(file, targetDir, projectName, existingProjectName);
            }
        }
    }

    private static void copyFile(File sourceFile, File targetDirectory, String projectName, String existingProjectName) {
        Path targetFilePath = new File(targetDirectory, sourceFile.getName()).toPath();

        try {
            String mimeType = Files.probeContentType(sourceFile.toPath());
            if (mimeType != null && mimeType.startsWith(TEXT_MIME_TYPE_PREFIX)) {
                byte[] bytes = Files.readAllBytes(sourceFile.toPath());
                String content = removeBomIfPresent(bytes);
                content = content.replace(existingProjectName, projectName.trim());
                Files.write(targetFilePath, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } else {
                Files.copy(sourceFile.toPath(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("Failed to copy file: " + sourceFile.getAbsolutePath());
            e.printStackTrace();
        }
    }

    private static String removeBomIfPresent(byte[] bytes) {
        if (bytes.length > 3 && bytes[0] == BOM[0] && bytes[1] == BOM[1] && bytes[2] == BOM[2]) {
            return new String(java.util.Arrays.copyOfRange(bytes, 3, bytes.length), StandardCharsets.UTF_8);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void writeWithoutBOM(String filePath, String content) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(new File(filePath))) {
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            outputStream.write(bytes);
        }
    }
}
