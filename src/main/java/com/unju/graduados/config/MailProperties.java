package com.unju.graduados.config;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.Properties;

@Getter
@Setter
public class MailProperties {

    private String host;
    private int port;
    private String username;
    private String password;

    // mail.noreply.name / mail.announcer.name
    private String name;

    // Mapeo correcto: propiedades SMTP personalizadas
    private Map<String, String> properties;

    public Properties getProperties() {
        Properties props = new Properties();
        if (properties != null) {
            properties.forEach(props::setProperty);
        }
        return props;
    }
}
