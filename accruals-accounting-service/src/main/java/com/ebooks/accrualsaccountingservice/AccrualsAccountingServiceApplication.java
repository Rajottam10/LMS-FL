package com.ebooks.accrualsaccountingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccrualsAccountingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccrualsAccountingServiceApplication.class, args);
    }

}
