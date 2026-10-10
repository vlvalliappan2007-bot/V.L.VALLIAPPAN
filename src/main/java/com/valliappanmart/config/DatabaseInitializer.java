package com.valliappanmart.config;

import org.h2.tools.RunScript;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());
    private static volatile boolean initialized = false;

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        LOGGER.info("Starting Valliappan Mart Database Initialization...");
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            // Run schema.sql
            try (InputStream schemaStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream("schema.sql")) {
                if (schemaStream != null) {
                    RunScript.execute(conn, new InputStreamReader(schemaStream, StandardCharsets.UTF_8));
                    LOGGER.info("Successfully executed schema.sql");
                } else {
                    LOGGER.warning("schema.sql not found on classpath!");
                }
            }

            // Run data.sql
            try (InputStream dataStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream("data.sql")) {
                if (dataStream != null) {
                    RunScript.execute(conn, new InputStreamReader(dataStream, StandardCharsets.UTF_8));
                    LOGGER.info("Successfully executed data.sql");
                } else {
                    LOGGER.warning("data.sql not found on classpath!");
                }
            }

            initialized = true;
            LOGGER.info("Valliappan Mart Database Initialization completed successfully!");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize Valliappan Mart database", e);
        } finally {
            DBConnection.closeQuietly(conn);
        }
    }
}
