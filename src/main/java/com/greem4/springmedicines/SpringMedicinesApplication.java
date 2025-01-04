package com.greem4.springmedicines;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SuppressWarnings("checkstyle:RegexpMultiline")
@SpringBootApplication
@EnableScheduling
public class SpringMedicinesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMedicinesApplication.class, args);
    }
}
