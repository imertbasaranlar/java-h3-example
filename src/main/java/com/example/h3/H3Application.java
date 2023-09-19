package com.example.h3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class H3Application {

	public static void main(String[] args) {
		SpringApplication.run(H3Application.class, args);
	}

}
