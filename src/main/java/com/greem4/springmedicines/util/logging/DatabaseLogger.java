package com.greem4.springmedicines.util.logging;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Slf4j
@RequiredArgsConstructor
@Component
public class DatabaseLogger {

    private final DataSource dataSource;

    public void logDatabaseInfo() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            log.info("Database URL: {}", metaData.getURL());
            log.info("Database User: {}", metaData.getUserName());
            log.info("Database Product: {} {}", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
            log.info("Database Driver: {} {}", metaData.getDriverName(), metaData.getDriverVersion());
        } catch (Exception e) {
            log.error("Failed to retrieve database information", e);
        }
    }

    public void logHikariPoolInfo() {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            log.info("Hikari Pool Name: {}", hikariDataSource.getPoolName());
            log.info("Hikari Maximum Pool Size: {}", hikariDataSource.getMaximumPoolSize());
            log.info("Hikari Minimum Idle Connections: {}", hikariDataSource.getMinimumIdle());
            log.info("Hikari Idle Timeout: {}", hikariDataSource.getIdleTimeout());
            log.info("Hikari Max Lifetime: {}", hikariDataSource.getMaxLifetime());
            log.info("Hikari Connection Timeout: {}", hikariDataSource.getConnectionTimeout());
        } else {
            log.warn("DataSource is not an instance of HikariDataSource");
        }
    }
}
