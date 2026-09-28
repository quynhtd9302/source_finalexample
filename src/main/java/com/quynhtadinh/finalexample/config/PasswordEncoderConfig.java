package com.quynhtadinh.finalexample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Kept separate from WebSecurityConfig: that class now depends (via the OAuth2
 * user services it wires into oauth2Login()) on BCryptPasswordEncoder, so
 * defining the bean there too created a circular reference at startup.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
