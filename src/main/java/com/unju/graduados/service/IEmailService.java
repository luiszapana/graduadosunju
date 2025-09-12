package com.unju.graduados.service;

public interface IEmailService {
    void sendVerificationEmail(String to, String token);
}
