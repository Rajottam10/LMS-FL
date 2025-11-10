package com.ebooks.loandisbursementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.ebooks.loandisbursementservice",
        "com.ebooks.commonmoduleloan.entities"
})
@EnableJpaRepositories(basePackages = {
        "com.ebooks.loandisbursementservice.repositories"
})
public class LoanDisbursementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanDisbursementServiceApplication.class, args);
    }

}
