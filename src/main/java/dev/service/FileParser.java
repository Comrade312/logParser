package dev.service;

import dev.db.LogDBWorker;
import dev.entity.Column;
import dev.entity.Table;
import lombok.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

/**
 * Responsible for parsing log files
 */
@Data
public class FileParser {
    /**
     * Parses the log file
     *
     * @param path       - the path to the file
     * @param table      - table for storing logs
     */
    public void parseFile(LogDBWorker logDBWorker, String path, Table table) {
        try (FileInputStream inputStream = new FileInputStream(path)) {
            Scanner sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                logDBWorker.createNewLogRecord(str, parseLogLine(str, table), table);
            }
            logDBWorker.getDbWorker().getConnection().commit();
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Parses the log line, then returns a list of parameters
     * or an empty list, if it does not match the parsing pattern
     *
     * @param logLine - log line
     * @param table   - table for storing logs
     * @return - returns a list of parameters as an ordered list
     */
    public List<String> parseLogLine(String logLine, Table table) {
        String str = logLine;
        List<String> params = new ArrayList<>();
        try {
            ListIterator<Column> iterator = table.getColumns().listIterator();

            while (iterator.hasNext()) {
                Column column = iterator.next();
                String leftLimiter = column.getLeftLimiter();
                String rightLimiter = column.getRightLimiter();
                String separator = table.getSeparator();

                //Is there a delimiter on the left. If so, then cut it off from the log line
                if (leftLimiter == null) {
                    //Whether the value is the first in the line. If not, then cut off the separator
                    if (iterator.previousIndex() != 0) {
                        str = str.substring(str.indexOf(separator) + separator.length());
                    }
                } else {
                    str = str.substring(str.indexOf(leftLimiter) + leftLimiter.length());
                }

                //Is there a delimiter on the right
                //If so, then cut the value to the limiter
                //If not, then cut out the value before the separator
                if (rightLimiter == null) {
                    //If the value is the last in the line, then add the rest of the line
                    //If not, then add the value to the separator and cut it out of the string
                    if (!iterator.hasNext()) {
                        params.add(str);
                    } else {
                        params.add(str.substring(0, str.indexOf(separator)));
                        str = str.substring(str.indexOf(separator));
                    }
                } else {
                    params.add(str.substring(0, str.indexOf(rightLimiter)));
                    str = str.substring(str.indexOf(rightLimiter) + rightLimiter.length());
                }

                //If the added value exceeds the column size, then we reset the parameters and
                //exit the loop (this log line will be written to the exception table)
                if (params.get(params.size() - 1).length() > column.getColumnSize()) {
                    params.clear();
                    break;
                }
            }
        } catch (StringIndexOutOfBoundsException ex) {
            params.clear();
        }
        return params;
    }
}

