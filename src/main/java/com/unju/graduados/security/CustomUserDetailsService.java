package com.unju.graduados.security;

import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.service.IUsuarioLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IUsuarioLoginService usuarioLoginService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioLogin login = usuarioLoginService.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Set<GrantedAuthority> authorities = login.getPerfiles().stream()
                .map(p -> new SimpleGrantedAuthority("ROLE_" + p.getPerfil().toUpperCase()))
                .collect(Collectors.toSet());

        boolean enabled = login.getHabilitado() == null || login.getHabilitado();
        return new User(login.getUsuario(), login.getPassword(), enabled, true, true, true, authorities);
    }
}
