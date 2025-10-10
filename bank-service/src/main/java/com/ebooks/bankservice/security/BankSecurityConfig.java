package com.ebooks.bankservice.security;

import com.ebooks.commonservice.config.CommonSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

//@Configuration
public class BankSecurityConfig extends CommonSecurityConfig {

    @Override
    protected void configureHttpSecurity(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers(
//                        "/api/v1/banks/public/**",
//                        "/v3/api-docs/**",
//                        "/swagger-ui/**",
//                        "/swagger-ui.html",
//                        "/actuator/health"
//                ).permitAll()
                .requestMatchers("/**").permitAll()
                .requestMatchers("/api/v1/bank-users/**").hasAnyRole("BANK_ADMIN")
                .requestMatchers("/api/v1/banks/**").hasAnyRole("GOD", "BANK_ADMIN")
                .requestMatchers("/api/v1/bank-admins/**").hasAnyRole("GOD", "BANK_ADMIN")
                .anyRequest().authenticated()
        );
    }
}