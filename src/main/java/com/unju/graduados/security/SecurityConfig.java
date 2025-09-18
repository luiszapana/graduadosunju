package com.unju.graduados.security;

import com.unju.graduados.filter.BaseFilter;
import com.unju.graduados.filter.PrivateAreaFilter;
import com.unju.graduados.filter.PrivateAreaModServicesFilter;
import com.unju.graduados.filter.VerificationFilter;
import com.unju.graduados.service.IUsuarioLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
    //private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        //return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/images/**",
                                "/css/**",
                                "/js/**",
                                "/recuperar/**",
                                "/registro/**",
                                "/login",
                                "/auth/**",
                                "/api/carreras/**" // 👈 agregado para permitir acceso público
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/anuncios", true)
                        .failureUrl("/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                )
                .sessionManagement(sess -> sess
                        .maximumSessions(1)
                );

        // Registrás primero tu VerificationFilter en un punto conocido
        http.addFilterAfter(new VerificationFilter(usuarioLoginService), UsernamePasswordAuthenticationFilter.class);

        // Ahora sí podés enganchar los demás
        http.addFilterAfter(new PrivateAreaFilter(), VerificationFilter.class);
        http.addFilterAfter(new PrivateAreaModServicesFilter(), PrivateAreaFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
