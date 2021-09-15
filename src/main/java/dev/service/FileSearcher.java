package dev.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static dev.constants.Constants.ALL_FILES_FLAG;

/**
 * Responsible for searching files according to certain criteria
 */
public class FileSearcher {
    /**
     * Returns a list of all files from a folder when the flag {@value dev.constants.Constants#ALL_FILES_FLAG} provided
     * or a specific file from a folder
     *
     * @param fileName   - the name of the required file or a flag for all files
     * @param folderPath - folder path
     * @return - list of files
     * @throws NullPointerException
     */
    public List<File> getFilesFromFolder(String fileName, String folderPath) throws NullPointerException {
        File files = new File(folderPath);
        if (files.exists() && files.isDirectory()) {

            if (fileName.trim().equals(ALL_FILES_FLAG)) {
                return Arrays.stream(Objects.requireNonNull(files.listFiles()))
                        .sorted(Comparator.comparing(this::fileCreationTime))
                        .collect(Collectors.toList());
            } else {
                return Arrays.stream(Objects.requireNonNull(files.listFiles()))
                        .filter(v -> v.getName().equals(fileName.trim()))
                        .collect(Collectors.toList());
            }
        } else {
            System.out.println("Folder does not exist or path not to folder");
            return new ArrayList<>();
        }
    }

    /**
     * Returns the creation date of the file
     *
     * @param file - file
     * @return - file creation date
     */
    private FileTime fileCreationTime(File file) {
        try {
            return (FileTime) Files.getAttribute(Paths.get(file.getPath()), "creationTime");
        } catch (IOException e) {
            e.printStackTrace();
            return FileTime.fromMillis(System.currentTimeMillis());
        }
    }
}
