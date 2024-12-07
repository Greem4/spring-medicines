package com.greem4.springmedicines.util.logging;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@RequiredArgsConstructor
@Component
public class DatabaseLogger {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseLogger.class);

    private final DataSource dataSource;

    public void logDatabaseInfo() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            logger.info("Database URL: {}", metaData.getURL());
            logger.info("Database User: {}", metaData.getUserName());
            logger.info("Database Product: {} {}", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
            logger.info("Database Driver: {} {}", metaData.getDriverName(), metaData.getDriverVersion());
        } catch (Exception e) {
            logger.error("Failed to retrieve database information", e);
        }
    }

    public void logHikariPoolInfo() {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            logger.info("Hikari Pool Name: {}", hikariDataSource.getPoolName());
            logger.info("Hikari Maximum Pool Size: {}", hikariDataSource.getMaximumPoolSize());
            logger.info("Hikari Minimum Idle Connections: {}", hikariDataSource.getMinimumIdle());
            logger.info("Hikari Idle Timeout: {}", hikariDataSource.getIdleTimeout());
            logger.info("Hikari Max Lifetime: {}", hikariDataSource.getMaxLifetime());
            logger.info("Hikari Connection Timeout: {}", hikariDataSource.getConnectionTimeout());
        } else {
            logger.warn("DataSource is not an instance of HikariDataSource");
        }
    }
}
