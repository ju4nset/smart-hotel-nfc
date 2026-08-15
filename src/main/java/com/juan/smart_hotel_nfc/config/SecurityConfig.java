package com.juan.smart_hotel_nfc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desliga a proteção contra ataques CSRF (necessário para o Postman conseguir fazer POST)
                .csrf(csrf -> csrf.disable())

                // Libera o acesso para qualquer requisição que comece com /api/
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/asaas/clientes**","/pagamento-teste.html").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}