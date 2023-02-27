package com.mse.datafabric;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class DataFabricApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataFabricApplication.class, args);
	}

}
