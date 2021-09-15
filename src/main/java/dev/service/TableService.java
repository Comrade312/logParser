package dev.service;

import dev.db.DBWorker;
import dev.db.TableDBWorker;
import dev.entity.Table;
import lombok.Data;

/**
 * Responsible for the operation of tables for storing parsed logs
 */
@Data
public class TableService {
    /**
     * Initializes the table. Creates tables, sequences and triggers if not created.
     */
    public void prepareDbStructure(DBWorker dbWorker, Table table) {
        TableDBWorker tableDBWorker = new TableDBWorker(dbWorker);

        tableDBWorker.createTable(table);
        tableDBWorker.createTableSequence(table.getTableName());
        tableDBWorker.createInsertTrigger(table.getTableName());

        tableDBWorker.createExceptTable(table.getTableNameForExceptRows());
        tableDBWorker.createExceptInsertTrigger(table.getTableName(), table.getTableNameForExceptRows());

    }
}
