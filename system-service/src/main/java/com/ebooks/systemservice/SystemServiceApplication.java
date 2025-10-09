package com.ebooks.systemservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ebooks.systemservice", "com.ebooks.commonservice"})
public class SystemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SystemServiceApplication.class, args);
	}

}
