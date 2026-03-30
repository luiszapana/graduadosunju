package com.unju.graduados.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:configuration.properties")
public class MailConfig {

    @Bean(name = "noreplyMailProps")
    @ConfigurationProperties(prefix = "mail.noreply")
    public MailProperties noreplyMailProps() {
        return new MailProperties();
    }

    @Bean(name = "announcerMailProps")
    @ConfigurationProperties(prefix = "mail.announcer")
    public MailProperties announcerMailProps() {
        return new MailProperties();
    }

    @Bean(name = "noreplyMailSender")
    public JavaMailSender noreplyMailSender() {
        MailProperties props = noreplyMailProps();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(props.getHost());
        mailSender.setPort(props.getPort());
        mailSender.setUsername(props.getUsername());
        mailSender.setPassword(props.getPassword());
        mailSender.setJavaMailProperties(props.getProperties()); // <--- USA LAS PROPS DEL ARCHIVO
        return mailSender;
    }

    @Bean(name = "announcerMailSender")
    public JavaMailSender announcerMailSender() {
        MailProperties props = announcerMailProps();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(props.getHost());
        mailSender.setPort(props.getPort());
        mailSender.setUsername(props.getUsername());
        mailSender.setPassword(props.getPassword());
        mailSender.setJavaMailProperties(props.getProperties()); // <--- ACÁ ESTÁ LA SOLUCIÓN
        return mailSender;
    }
}
