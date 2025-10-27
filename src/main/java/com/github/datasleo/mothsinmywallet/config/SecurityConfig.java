package com.github.datasleo.mothsinmywallet.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @SuppressWarnings("unused")
    private final UserDetailsService userDetailsService;

    public SecurityConfig (UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/signup").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error")
            
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
    
    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilterRegistration() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);

        FilterRegistrationBean<CharacterEncodingFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(0); 
        return registration;
    }

}   
