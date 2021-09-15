package dev.entity;

import lombok.Data;

import java.util.List;

/**
 * Log table
 */
@Data
public class Table {
    /** Name of the table for logs */
    private String tableName;

    /** Log values separator */
    private String separator;

    /** List of log table columns */
    private List<Column> columns;

    /** Table name for exceptional records */
    private String tableNameForExceptRows;
}
