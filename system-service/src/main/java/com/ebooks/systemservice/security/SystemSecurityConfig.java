package com.ebooks.systemservice.security;

import com.ebooks.commonservice.config.CommonSecurityConfig;
import com.ebooks.systemservice.services.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SystemSecurityConfig extends CommonSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject the bean from common-service

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/access-groups/**").hasRole("GOD")
                        .requestMatchers("/api/banks/**").hasRole("GOD")
                        .requestMatchers("/api/roles/**").hasRole("GOD")
                        .requestMatchers("/api/users/**").hasRole("GOD")
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                );
    }
}