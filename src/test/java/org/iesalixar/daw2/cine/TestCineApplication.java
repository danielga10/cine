package org.iesalixar.daw2.cine;

import org.springframework.boot.SpringApplication;

public class TestCineApplication {

	public static void main(String[] args) {
		SpringApplication.from(CineApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
