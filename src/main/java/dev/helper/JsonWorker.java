package dev.helper;

import com.google.gson.Gson;
import dev.entity.Table;

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

/**
 * Works with JSON files
 */
public class JsonWorker {
    /**
     * Creates a table object {@link Table} based on a JSON file
     *
     * @param path - JSON file path
     * @return - table object {@link Table}
     * @throws IOException
     */
    public Table getTableFromJson(String path) throws IOException {
        Table table;
        try (FileReader reader = new FileReader(path)) {
            Gson gson = new Gson();
            table = gson.fromJson(reader, Table.class);
        }
        return Optional.ofNullable(table).orElse(new Table());
    }
}
