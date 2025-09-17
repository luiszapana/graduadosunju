package com.unju.graduados.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MailConfigCheck implements CommandLineRunner {

    @Value("${mail.sender.username}")
    private String username;

    @Value("${mail.sender.password}")
    private String password;

    @Override
    public void run(String... args) {
        System.out.println("USERNAME: " + username);
        System.out.println("PASSWORD: " + (password != null ? "******" : "NULL"));
    }
}

