package com.unju.graduados.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MailConfigCheck implements CommandLineRunner {

    // Cambiado de mail.sender.username a mail.noreply.username
    @Value("${mail.noreply.username}")
    private String username;

    // Cambiado de mail.sender.password a mail.noreply.password
    @Value("${mail.noreply.password}")
    private String password;

    @Override
    public void run(String... args) {
        System.out.println("DEBUG CHECK: USERNAME (Noreply): " + username);
        System.out.println("DEBUG CHECK: PASSWORD (Noreply) is set? " + (password != null && !password.isEmpty() ? "YES" : "NO/NULL"));
    }
}

