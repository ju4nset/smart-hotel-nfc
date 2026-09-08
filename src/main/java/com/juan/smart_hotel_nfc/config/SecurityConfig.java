package com.juan.smart_hotel_nfc.config;

import jakarta.servlet.http.HttpServletResponse;
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
               // Desliga o CSRF para testar via Postman (apenas em dev)
               .csrf(csrf -> csrf.disable()) //Autorização
               .authorizeHttpRequests(auth -> auth

                         // Libera todas as rodas do Asaas )
                       .requestMatchers("/api/asaas/**", "/pagamento-teste.html","/error").permitAll()
                       .anyRequest().authenticated()
               )

               // Opcional: retorna 401 (UNAUTHORIZED) em vez de 403 quando não autenticado
               .exceptionHandling(e -> e.authenticationEntryPoint
                       ((request, response, authException)
                               -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)));
       return http.build();

    }
}