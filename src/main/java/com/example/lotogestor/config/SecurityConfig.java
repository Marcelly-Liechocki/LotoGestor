package com.example.lotogestor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/login", "/css/**").permitAll()
        .anyRequest().authenticated()
      )
      .formLogin(form -> form
        .loginPage("/login")
        .defaultSuccessUrl("/dashboard", true)
        .permitAll()
      )
      .logout(l -> l.logoutSuccessUrl("/login?logout").permitAll())
      .csrf(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  UserDetailsService users(PasswordEncoder encoder) {
    UserDetails operador = User.withUsername("operador")
      .password(encoder.encode("123456"))
      .roles("OPERADOR").build();
    UserDetails gerente = User.withUsername("gerente")
      .password(encoder.encode("123456"))
      .roles("GERENTE").build();
    return new InMemoryUserDetailsManager(operador, gerente);
  }

  @Bean
  PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
