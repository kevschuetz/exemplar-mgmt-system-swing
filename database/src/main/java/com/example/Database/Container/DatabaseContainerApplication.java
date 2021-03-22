package com.example.Database.Container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@SpringBootApplication
@RestController
public class DatabaseContainerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabaseContainerApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		System.out.println("Reached Controller;");
		Datenbankverbindung.main(new String[0]);
		return "Spring Boot + Docker + Azure = :)";
	}
}
