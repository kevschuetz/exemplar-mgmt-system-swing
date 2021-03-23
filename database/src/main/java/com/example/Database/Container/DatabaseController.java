package com.example.Database.Container;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class DatabaseController {

    @GetMapping("/hello")
    public String hello() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        System.out.println("Reached Controller;");
        //Datenbankverbindung.main(new String[0]);
        return "Spring Boot + Docker + Azure = :)";
    }
}
