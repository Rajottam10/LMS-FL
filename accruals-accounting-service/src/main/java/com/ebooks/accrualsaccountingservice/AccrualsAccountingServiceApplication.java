package com.ebooks.accrualsaccountingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.ebooks.accrualsaccountingservice", "com.ebooks.commonmoduleloan"})
@EntityScan(basePackages = {
        "com.ebooks.accrualsaccountingservice.entities",  // Local entities
        "com.ebooks.commonmoduleloan.entities"            // Common entities
})
@EnableJpaRepositories(basePackages = {
//        "com.ebooks.accrualsaccountingservice.repositories",
        "com.ebooks.commonmoduleloan.repositories"  // If any
})
@EnableScheduling
public class AccrualsAccountingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccrualsAccountingServiceApplication.class, args);
    }

}
