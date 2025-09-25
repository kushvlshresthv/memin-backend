package com.sep.mmms_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class MmmsBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(MmmsBackendApplication.class, args);
	}
}
