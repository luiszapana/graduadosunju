package com.unju.graduados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GraduadosunjuApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduadosunjuApplication.class, args);
    }

}
