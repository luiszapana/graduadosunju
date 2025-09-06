package com.unju.graduados.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mail.sender")
public class MailProperties {
    private String host;
    private Integer port;
    private Principal principal = new Principal();
    private Noreply noreply = new Noreply();

    @Getter @Setter
    public static class Principal {
        private String mail;
        private String password;
        private String name;
    }

    @Getter @Setter
    public static class Noreply {
        private String mail;
        private String password;
        private String name;
    }
}
