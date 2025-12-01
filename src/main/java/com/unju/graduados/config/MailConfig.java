package com.unju.graduados.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

@Configuration
@PropertySource("classpath:configuration.properties")
@Getter
public class MailConfig {

    @Value("${mail.sender.host}")
    private String host;
    @Value("${mail.sender.port}")
    private int port;
    @Value("${mail.sender.username}")
    private String username;
    @Value("${mail.sender.password}")
    private String password;
    @Value("${mail.sender.name:noresponder.graduados@unju.edu.ar}")
    private String senderName;
    @Value("${mail.sender.properties.mail.smtp.auth}")
    private boolean auth;
    @Value("${mail.sender.properties.mail.smtp.starttls.enable}")
    private boolean starttls;

    //hostUrl se agrega en esta clase solo con el fin de eliminar un warning en el configuration.properties
    @Value("${url.host:http://localhost:8080}")
    private String hostUrl;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        return mailSender;
    }
}
