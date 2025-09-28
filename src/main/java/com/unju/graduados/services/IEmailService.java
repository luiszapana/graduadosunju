package com.unju.graduados.services;

public interface IEmailService {
    void sendVerificationEmail(String to, String token);
}
