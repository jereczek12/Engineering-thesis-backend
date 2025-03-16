package com.jereczek.checkers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin
@EnableAsync
public class CheckersLearningSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckersLearningSimulatorApplication.class, args);
    }

}
