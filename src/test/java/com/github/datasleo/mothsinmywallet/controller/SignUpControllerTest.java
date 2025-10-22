package com.github.datasleo.mothsinmywallet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import com.github.datasleo.mothsinmywallet.config.SecurityConfig;
import com.github.datasleo.mothsinmywallet.dto.SignUpDto;
import com.github.datasleo.mothsinmywallet.exception.EmailAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.PasswordAndRepeatPasswordAreNotEqualsException;
import com.github.datasleo.mothsinmywallet.exception.UsernameAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.service.AccountService;


@WebMvcTest(SignUpController.class)
@Import(SecurityConfig.class)
public class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

//-------------------------------------------------------------------------------------------
// Scenario 01: The SignUpController using verb get must returns 200 and view called signup.
//-------------------------------------------------------------------------------------------

    @Test
    public void MustReturnStatus200AndViewSignup() throws Exception {

        mockMvc
            .perform(get("/signup"))
            .andExpect(status().isOk())
            .andExpect(view().name("signup"));

    }

//------------------------------------------------------------------------------------------------------
// Scenario 02: If email exists in DB the SignUpController need cacth the EmailAlreadyExistsException.
//------------------------------------------------------------------------------------------------------
    
    @Test
    public void MustCatchTheErrorEmailAlreadyExistsException() throws Exception {

        String email = "test@test.com";
        String errorMessage = "Email " + email + " already exists.";

        doThrow(new EmailAlreadyExistsException(errorMessage))
            .when(accountService).createAccount(any(SignUpDto.class));

        mockMvc
            .perform(post("/signup")
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

//-----------------------------------------------------------------------------------------------------------------------------
// Scenario 03: If password are not equals the SignUpController need cacth the PasswordAndRepeatPasswordAreNotEqualsException
//-----------------------------------------------------------------------------------------------------------------------------

    @Test
    public void MustCatchTheErrorPasswordAndRepeatPasswordAreNotEqualsException() throws Exception {

        String errorMessage = "Passwords are not equals.";

        doThrow(new PasswordAndRepeatPasswordAreNotEqualsException(errorMessage))
            .when(accountService).createAccount(any(SignUpDto.class));

        mockMvc
            .perform(post("/signup")
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

//-----------------------------------------------------------------------------------------------------------
// Scenario 04: If username exists in DB the SignUpController need cacth the UsernameAlreadyExistsException
//-----------------------------------------------------------------------------------------------------------

    @Test
    public void MustCatchTheErrorUsernameAlreadyExists() throws Exception {

        String username = "fooandbar123";
        String errorMessage = "Username " + username + " already exists.";

        doThrow(new UsernameAlreadyExistsException(errorMessage))
            .when(accountService).createAccount(any(SignUpDto.class));

        mockMvc
            .perform(post("/signup")
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

//-----------------------------------------------
// Scenario 05: If all okay redirect to route /
//-----------------------------------------------

    @Test
    public void IfAllOkRedirectToIndex() throws Exception {

        mockMvc
            .perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "test@test.com")
                .param("password", "12345678")
                .param("repeatPassword", "1234567")
                .param("username", "fooandbar123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));

        verify(accountService, times(1)).createAccount(any(SignUpDto.class));
        
    }


}
