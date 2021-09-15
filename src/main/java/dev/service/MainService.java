package dev.service;

import dev.db.DBWorker;
import dev.db.LogDBWorker;
import dev.entity.Table;
import dev.helper.JsonWorker;
import dev.helper.PropertiesWorker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static dev.constants.Constants.PROPERTIES_PATH;

/**
 * The main service responsible for the general operation of the program
 */
public class MainService {
    /**
     * Launching the program
     */
    public void work() {
        try {
            PropertiesWorker prop = new PropertiesWorker(PROPERTIES_PATH);

            DBWorker dbWorker =
                    new DBWorker(prop.getLogin(), prop.getPassword(), prop.getUrl());
            LogDBWorker logDBWorker = new LogDBWorker(dbWorker);

            Table table = new JsonWorker().getTableFromJson(prop.getTableJson());
            new TableService().prepareDbStructure(dbWorker, table);

            System.out.println("Started - "
                    + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));

            if (prop.isArchive()) {
                new ArchiveService().parseArchive(logDBWorker, prop.getLogFile(), prop.getLogFolder(), table);
            } else {
                FileSearcher fileSearcher = new FileSearcher();
                FileParser fileParser = new FileParser();

                for (File file : fileSearcher.getFilesFromFolder(prop.getLogFile(), prop.getLogFolder())) {
                    System.out.println("File - " + file.getName());
                    fileParser.parseFile(logDBWorker, file.getPath(), table);
                }
            }

            System.out.println("Finished - "
                    + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
