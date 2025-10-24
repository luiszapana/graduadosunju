package com.unju.graduados.services.impl;

import com.unju.graduados.model.UsuarioLogin;
import com.unju.graduados.repositories.IUsuarioLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IUsuarioLoginRepository usuarioLoginDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioLogin usuarioLogin = usuarioLoginDao.findByUsuarioConPerfiles(username)
                                                   .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        Set<GrantedAuthority> authorities = usuarioLogin.getPerfiles().stream()
                .map(perfil -> new SimpleGrantedAuthority("ROLE_" + perfil.getPerfil().toUpperCase()))
                .collect(Collectors.toSet());

        // Si el campo habilitado es null, asumimos que está habilitado
        boolean enabled = usuarioLogin.getHabilitado() == null || usuarioLogin.getHabilitado();

        return new org.springframework.security.core.userdetails.User(
                usuarioLogin.getUsuario(),
                usuarioLogin.getPassword(),
                enabled,     // habilitado
                true,        // accountNonExpired
                true,        // credentialsNonExpired
                true,        // accountNonLocked
                authorities
        );
    }
}