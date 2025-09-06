package com.unju.graduados.constants;

public final class SecurityConstants {
    private SecurityConstants() {}

    // JWT (opcional para APIs)
    public static final String JWT_SECRET = "change-me-secret";
    public static final long JWT_EXPIRATION = 1000L * 60 * 15; // 15 min
    public static final long JWT_REFRESH_EXPIRATION = 1000L * 60 * 60 * 24; // 24h

    // Public/Private paths
    public static final String[] PUBLIC_MATCHERS = new String[]{
            "/login", "/registro/**", "/recuperar/**", "/css/**", "/js/**", "/images/**", "/int/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
    };

    public static final String[] PRIVATE_MATCHERS = new String[]{
            "/anuncios/**", "/admin/**", "/usuarios/**", "/empresas/**"
    };

    // Roles
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MODERADOR = "ROLE_MODERADOR";
    public static final String ROLE_USUARIO = "ROLE_USUARIO";
    public static final String ROLE_GRADUADO = "ROLE_GRADUADO";
}
