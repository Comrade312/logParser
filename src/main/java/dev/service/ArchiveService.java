package dev.service;

import dev.db.LogDBWorker;
import dev.entity.Table;
import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dev.constants.Constants.FILE_ARCHIVE_MASK;
import static dev.constants.Constants.WINRAR_ABSOLUTE_PATH;

/**
 * Responsible for working with .rar archives
 */
@Data
public class ArchiveService {
    /**
     * Parses log files in the archive
     *
     * @param table - table for storing logs
     */
    public void parseArchive(LogDBWorker logDBWorker, String archiveFile, String archiveFolderPath, Table table) {
        try {
            FileSearcher fileSearcher = new FileSearcher();
            for (File file : fileSearcher.getFilesFromFolder(archiveFile, archiveFolderPath)) {
                List<String> filenames = getLogNamesFromArchive(file);
                for (String filename : filenames) {
                    System.out.println("Файл - " + filename);
                    parseLogFileInArchive(logDBWorker, file, filename, table);
                    logDBWorker.getDbWorker().getConnection().commit();
                }
            }
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the names of log files
     * from the archive according to the mask {@value dev.constants.Constants#FILE_ARCHIVE_MASK}
     *
     * @param file - archive file
     * @return - list of log files from archive
     */
    public List<String> getLogNamesFromArchive(File file) throws IOException {
        List<String> logNames = new ArrayList<>();
        Process process = Runtime
                .getRuntime()
                .exec("cmd /c (set path=\"" + WINRAR_ABSOLUTE_PATH + "\") && " +
                        "(unrar lb " + file.getAbsolutePath() + " " + FILE_ARCHIVE_MASK + ")");

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream(), "CP866"))) {
            String filenameInArchive;
            while ((filenameInArchive = reader.readLine()) != null) {
                logNames.add(filenameInArchive);
            }
        }
        return logNames;
    }

    /**
     * Opens a file in an archive and parses it
     *
     * @param archiveFile - archive file
     * @param fileLogName - the log file in the archive
     * @param table       - table for storing logs
     */
    public void parseLogFileInArchive(LogDBWorker logDBWorker, File archiveFile, String fileLogName, Table table) throws IOException {
        FileParser fileParser = new FileParser();
        Process process = Runtime
                .getRuntime()
                .exec("cmd /c (set path=\"" + WINRAR_ABSOLUTE_PATH + "\") && " +
                        "(unrar p " + archiveFile.getAbsolutePath() + " " + fileLogName + ")");
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream(), "CP866"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logDBWorker.createNewLogRecord(line, fileParser.parseLogLine(line, table), table);
            }
        }
    }
}
