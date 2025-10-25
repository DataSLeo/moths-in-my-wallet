package com.github.datasleo.mothsinmywallet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.github.datasleo.mothsinmywallet.config.SecurityConfig;
import com.github.datasleo.mothsinmywallet.dto.SignUpDto;
import com.github.datasleo.mothsinmywallet.exception.EmailAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.PasswordAndRepeatPasswordAreNotEqualsException;
import com.github.datasleo.mothsinmywallet.exception.UsernameAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.service.AccountService;


@WebMvcTest(SignUpController.class)
@Import({SecurityConfig.class, SignUpControllerTest.TestConfig.class})
@AutoConfigureMockMvc(addFilters = true)
public class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    public void stup() {
        Mockito.reset(accountService);
    }

    @Test
    public void WhenGetSignUpPath_MustReturnSignUpView() throws Exception {

        mockMvc
            .perform(get("/signup"))
            .andExpect(status().isOk())
            .andExpect(view().name("signup"));

    }
    
    @Test
    public void WhenPostSignUpPathWitExistsEmail_MustReturnEmailAlreadyExistsException() throws Exception {

        String email = "test@test.com";
        String errorMessage = "Email " + email + " already exists.";

        doThrow(new EmailAlreadyExistsException(errorMessage))
            .when(accountService).createAccount(any(SignUpDto.class));

        mockMvc
            .perform(post("/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", email)
                .param("password", "12345678")
                .param("repeatPassword", "12345678")
                .param("username", "fooandbar123"))
            .andExpect(status().isOk())
            .andExpect(view().name("signup"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", errorMessage));

        verify(accountService, times(1)).createAccount(any(SignUpDto.class));

    }

    @Test
    public void WhenPostSignUpPathWithDifferentPasswords_MustReturnPasswordAndRepeatPasswordAreNotEqualsException() throws Exception {

        String errorMessage = "Passwords are not equals.";

        doThrow(new PasswordAndRepeatPasswordAreNotEqualsException(errorMessage))
            .when(accountService).createAccount(any(SignUpDto.class));

        mockMvc
            .perform(post("/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "test@test.com")
                .param("password", "12345678")
                .param("repeatPassword", "123456789")
                .param("username", "fooandbar123"))
            .andExpect(status().isOk())
            .andExpect(view().name("signup"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", errorMessage));
        
        verify(accountService, times(1)).createAccount(any(SignUpDto.class));

    }

    @Test
    public void WhenPostSignUpPathWithExistsUsername_MustReturnUsernameAlreadyExistsException() throws Exception {

        String username = "fooandbar123";
        String errorMessage = "Username " + username + " already exists.";

        doThrow(new UsernameAlreadyExistsException(errorMessage))
            .when(accountService).createAccount(any(SignUpDto.class));

        mockMvc
            .perform(post("/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "test@test.com")
                .param("password", "12345678")
                .param("repeatPassword", "12345678")
                .param("username", username))
            .andExpect(status().isOk())
            .andExpect(view().name("signup"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", errorMessage));

        verify(accountService, times(1)).createAccount(any(SignUpDto.class));

    }

    @Test
    public void WhenPostSignUpPath_MustRedirectToLoginPath() throws Exception {

        mockMvc
            .perform(post("/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "test@test.com")
                .param("password", "12345678")
                .param("repeatPassword", "1234567")
                .param("username", "fooandbar123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));

        verify(accountService, times(1)).createAccount(any(SignUpDto.class));
        
    }


    @TestConfiguration
    static class TestConfig {

        @Bean
        public AccountService accountService() {
            return Mockito.mock(AccountService.class);
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
