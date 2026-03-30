package com.unju.graduados.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MailConfigTestLoader {

    @Autowired
    @Qualifier("noreplyMailProps")
    private MailProperties noreplyMailProps;

    @Autowired
    @Qualifier("announcerMailProps")
    private MailProperties announcerMailProps;

    @PostConstruct
    public void testProperties() {
        System.out.println("NO-REPLY PROPS: " + noreplyMailProps.getProperties());
        System.out.println("ANNOUNCER PROPS: " + announcerMailProps.getProperties());
    }
}

