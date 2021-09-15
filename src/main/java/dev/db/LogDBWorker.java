package dev.db;

import dev.entity.Column;
import dev.entity.Table;
import lombok.Data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Works with log entries in the database
 */
@Data
public class LogDBWorker {
    private DBWorker dbWorker;

    public LogDBWorker(DBWorker dbWorker) {
        this.dbWorker = dbWorker;
    }

    /**
     *
     * Creates a parsed log line entry in the database or, if the entry fails parsing,
     * creates a log line entry in an exception table
     *
     * @param logRecord - log line
     * @param params    - parsed log parameters
     * @param table     - table for storing logs {@link Table}
     */
    public void createNewLogRecord(String logRecord, List<String> params, Table table) {
        if (!params.isEmpty()) {
            insertRow(params, table);
        } else {
            insertExceptRow(logRecord, table.getTableNameForExceptRows());
        }
    }

    /**
     * Inserts a parsed log line record into the log table
     *
     * @param params - parsed log parameters
     * @param table  - table for storing logs {@link Table}
     */
    public void insertRow(List<String> params, Table table) {
        String sqlStatement = insertRowSql(table);

        try (PreparedStatement createRecords
                     = dbWorker.getConnection().prepareStatement(sqlStatement)) {
            for (int i = 1; i < params.size() + 1; i++) {
                createRecords.setString(i, params.get(i - 1));
            }

            createRecords.executeUpdate();
            dbWorker.customCommit();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Inserting an unparsed log line entry into an exception table
     *
     * @param logRecord       - log line
     * @param exceptTableName - exception table name
     */
    public void insertExceptRow(String logRecord, String exceptTableName) {
        String sqlStatement = "INSERT INTO " + exceptTableName + "(log_record) VALUES (?)";

        try (PreparedStatement createRecords
                     = dbWorker.getConnection().prepareStatement(sqlStatement)) {

            createRecords.setString(1, logRecord);
            createRecords.executeUpdate();
            dbWorker.customCommit();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates a SQL query for {@link java.sql.PreparedStatement}
     * to insert a log record into the table for storing logs
     *
     * @param table - table for storing logs {@link Table}
     * @return - SQL query for {@link java.sql.PreparedStatement}
     */
    public String insertRowSql(Table table) {
        StringBuilder sqlStatement = new StringBuilder("INSERT INTO " + table.getTableName() + "(");

        sqlStatement.append(
                table.getColumns().stream()
                        .map(Column::getColumnName)
                        .collect(Collectors.joining(",")));

        sqlStatement.append(") VALUES(");

        for (int i = 0; i < table.getColumns().size(); i++) {
            sqlStatement.append("?,");
        }

        sqlStatement.setLength(sqlStatement.length() - 1);
        sqlStatement.append(")");

        return sqlStatement.toString();
    }

}
