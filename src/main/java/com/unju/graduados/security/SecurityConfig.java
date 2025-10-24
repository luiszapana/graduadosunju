package com.unju.graduados.security;

import com.unju.graduados.filters.PrivateAreaFilter;
import com.unju.graduados.filters.PrivateAreaModServicesFilter;
import com.unju.graduados.filters.VerificationFilter;
import com.unju.graduados.services.IUsuarioLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final IUsuarioLoginService usuarioLoginService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Recursos estáticos accesibles a cualquiera
                        .requestMatchers("/images/**", "/css/**", "/js/**").permitAll()
                        // Endpoints públicos
                        .requestMatchers("/recuperar/**", "/registro/**", "/login", "/auth/**", "/api/carreras/**").permitAll()
                        // Dashboard requiere login
                        .requestMatchers("/dashboard/**").authenticated()
                        // Área admin: moderador/administrador
                        .requestMatchers("/admin/**").hasAnyRole("MODERADOR", "ADMINISTRADOR")
                        // Todo lo demás: autenticado
                        .anyRequest().authenticated()
                )
                // Configuración de Formulario de Login
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                )
                // Configuración de Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                );
        http.addFilterAfter(new VerificationFilter(usuarioLoginService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new PrivateAreaFilter(), VerificationFilter.class);
        http.addFilterAfter(new PrivateAreaModServicesFilter(), PrivateAreaFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
