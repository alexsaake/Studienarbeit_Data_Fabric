package com.mse.datafabric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DataFabricApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DataFabricApplication.class, args);
		context.getBean(DataProductsProvider.class).saveImageToDbForId("einkommensentwicklung");
	}

}
