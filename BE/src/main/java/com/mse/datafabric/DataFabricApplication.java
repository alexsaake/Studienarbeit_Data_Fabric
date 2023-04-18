package com.mse.datafabric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataFabricApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataFabricApplication.class, args);
	}

}
