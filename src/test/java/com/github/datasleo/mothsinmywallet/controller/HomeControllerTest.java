package com.github.datasleo.mothsinmywallet.controller;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.github.datasleo.mothsinmywallet.config.SecurityConfig;
import com.github.datasleo.mothsinmywallet.model.Account;
import com.github.datasleo.mothsinmywallet.repository.AccountRepository;


@WebMvcTest(HomeController.class)
@Import({SecurityConfig.class, HomeControllerTest.TestConfig.class})
@AutoConfigureMockMvc(addFilters = true)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @SuppressWarnings("unused")
    private AccountRepository accountRepository; 


    @Test
    public void whenGetRootPath_thenReturnIndexView() throws Exception {

        mockMvc
            .perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));

    }

    @Test
    public void WhenNotAuthenticated_MustRedirectToLogin() throws Exception {
        
        mockMvc
            .perform(get("/home"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login")); 
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void WhenAuthenticated_MustReturnHomeView() throws Exception {

        when(accountRepository.findByUsername("testUser"))
            .thenReturn(Optional.of(new Account("test@test.com", "hashed_password", "testUser")));

        mockMvc
            .perform(get("/home"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/home"))
            .andExpect(model().attributeExists("username"));

    }

    @TestConfiguration
    static class TestConfig {
        
        @Bean
        public AccountRepository accountRepository() {
            return Mockito.mock(AccountRepository.class);
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return username -> org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("password")
                .roles("USER")
                .build();
        }

    }

}