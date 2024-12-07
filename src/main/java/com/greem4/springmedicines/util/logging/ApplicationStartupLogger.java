package com.greem4.springmedicines.util.logging;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ApplicationStartupLogger implements CommandLineRunner {

    private final DatabaseLogger databaseLogger;

    @Override
    public void run(String... args) {
        databaseLogger.logDatabaseInfo();
        databaseLogger.logHikariPoolInfo();
    }
}
