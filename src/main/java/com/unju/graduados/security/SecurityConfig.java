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
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true) // 👈 Importante: ya no redirige a anuncios
                        .failureUrl("/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                );

        // Filtros custom
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
