package dev.db;

import dev.entity.Table;
import lombok.Data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * Works with entities {@link Table} in the database
 */
@Data
public class TableDBWorker {
    DBWorker dbWorker;

    public TableDBWorker(DBWorker dbWorker) {
        this.dbWorker = dbWorker;
    }

    /**
     * Creates a table for logs
     *
     * @param table - table for storing logs {@link Table}
     */
    public void createTable(Table table) {
        String sqlStatement = "CREATE TABLE " + table.getTableName() + "(id NUMBER NOT NULL, ";
        sqlStatement +=
                table.getColumns().stream()
                        .map(v -> v.getColumnName() + " VARCHAR(" + v.getColumnSize() + "), ")
                        .collect(Collectors.joining());

        sqlStatement += "PRIMARY KEY(id))";

        try (PreparedStatement preparedStatement =
                     dbWorker.getConnection().prepareStatement(sqlStatement)) {
            preparedStatement.executeUpdate(sqlStatement);
            dbWorker.getConnection().commit();
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 955) {
                System.out.println("Table " + table.getTableName() +
                        " already exists. The existing one will be used");
            } else {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Creates a sequence for table that storing logs
     *
     * @param tableName - table name for storing logs
     */
    public void createTableSequence(String tableName) {
        String sqlStatement = "CREATE SEQUENCE " + tableName + "_seq";

        try (PreparedStatement preparedStatement =
                     dbWorker.getConnection().prepareStatement(sqlStatement)) {
            preparedStatement.executeUpdate(sqlStatement);
            dbWorker.getConnection().commit();
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 955) {
                System.out.println("Sequence " + tableName +
                        "_seq already exists. The existing one will be used");
            } else {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Creates an insert trigger for table that storing logs
     *
     * @param tableName - table name for storing logs
     */
    public void createInsertTrigger(String tableName) {
        String sqlStatement =
                "CREATE TRIGGER " + tableName + "_on_insert\n" +
                        "BEFORE INSERT ON " + tableName +
                        "\nFOR EACH ROW \n" +
                        "  BEGIN\n" +
                        "    SELECT " + tableName + "_seq.NEXTVAL\n" +
                        "    INTO   :new.id\n" +
                        "    FROM   dual;\n" +
                        "  END;";

        try (PreparedStatement preparedStatement =
                     dbWorker.getConnection().prepareStatement(sqlStatement)) {
            preparedStatement.executeUpdate(sqlStatement);
            dbWorker.getConnection().commit();
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 4081) {
                System.out.println("Trigger " + tableName +
                        "_on_insert already exists. The existing one will be used");
            } else {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Creates a table for unparsed log entries
     *
     * @param exceptTableName - exception table name
     */
    public void createExceptTable(String exceptTableName) {
        String sqlStatement =
                "CREATE TABLE " + exceptTableName +
                        "(id NUMBER NOT NULL, log_record CLOB, PRIMARY KEY(id))";

        try (PreparedStatement preparedStatement =
                     dbWorker.getConnection().prepareStatement(sqlStatement)) {
            preparedStatement.executeUpdate(sqlStatement);
            dbWorker.getConnection().commit();
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 955) {
                System.out.println("Table " + exceptTableName +
                        " already exists. The existing one will be used");
            } else {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Creates a trigger for an exception table (using the sequence of the main table)
     *
     * @param exceptTableName - exception table name
     */
    public void createExceptInsertTrigger(String tableName, String exceptTableName) {
        String sqlStatement =
                "CREATE TRIGGER " + exceptTableName + "_on_insert\n" +
                        "BEFORE INSERT ON " + exceptTableName +
                        "\nFOR EACH ROW \n" +
                        "  BEGIN\n" +
                        "    SELECT " + tableName + "_seq.NEXTVAL\n" +
                        "    INTO   :new.id\n" +
                        "    FROM   dual;\n" +
                        "  END;";

        try (PreparedStatement preparedStatement =
                     dbWorker.getConnection().prepareStatement(sqlStatement)) {
            preparedStatement.executeUpdate(sqlStatement);
            dbWorker.getConnection().commit();
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 4081) {
                System.out.println("Trigger " + exceptTableName +
                        "_on_insert already exists. The existing one will be used");
            } else {
                exception.printStackTrace();
            }
        }
    }

}
