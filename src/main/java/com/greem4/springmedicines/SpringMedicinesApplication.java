package com.greem4.springmedicines;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableScheduling
public class SpringMedicinesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMedicinesApplication.class, args);
    }
}
