package com.mse.datafabric;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
class DataFabricApplicationTests {
	@Autowired
	private DataProductsController dataProductsController;
	@Test
	void contextLoads() {
		assertThat(dataProductsController).isNotNull();
	}

}
