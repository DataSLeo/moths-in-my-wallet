package com.github.datasleo.mothsinmywallet.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.datasleo.mothsinmywallet.dto.SignUpDto;
import com.github.datasleo.mothsinmywallet.exception.EmailAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.PasswordAndRepeatPasswordAreNotEqualsException;
import com.github.datasleo.mothsinmywallet.exception.UsernameAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.model.Account;
import com.github.datasleo.mothsinmywallet.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    private SignUpDto validSignUpDto;

    @BeforeEach
    public void setup() {
        validSignUpDto = new SignUpDto("test@test.com", "password123", "password123", "fooandbar123");
    }

    
// -----------------------------
// Scenario 01: Created account
// -----------------------------

    @Test
    public void MustCreateAccount () throws Exception {

        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");

        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account savedAccount = invocation.getArgument(0);

            savedAccount.setId(1L);

            return savedAccount;

        });

        Account result = accountService.createAccount(validSignUpDto);

        assertNotNull(result);
        assertEquals("fooandbar123", result.getUsername());
        assertEquals("test@test.com", result.getEmail());
        assertEquals("hashed_password", result.getPassword());

        verify(accountRepository, times(1)).findByEmail(anyString());
        verify(accountRepository, times(1)).findByUsername(anyString());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(accountRepository, times(1)).save(any(Account.class));

    }


// --------------------------------------------------------
// Scenario 02: Account not created, email already exists
// --------------------------------------------------------

    @Test
    public void MustGenerateEmailAlreadyExistsException () throws Exception {

        Account existsEmail = new Account();

        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.of(existsEmail));

        assertThrows(EmailAlreadyExistsException.class, () -> {
            accountService.createAccount(validSignUpDto);
        });

        verify(accountRepository, times(1)).findByEmail(anyString());
        verify(accountRepository, never()).findByUsername(anyString());
        verify(passwordEncoder, never()).encode("password123");
        verify(accountRepository, never()).save(any(Account.class));
    
    }

// -----------------------------------------------------------
// Scenario 03: Account not created, password are not equals
// -----------------------------------------------------------

    @Test
    public void MustGeneratePasswordAndRepeatPasswordAreNotEqualsException() throws Exception {

        SignUpDto passwordsAreNotEquals = new SignUpDto("test@test.com", "password123", "123456", "fooandbar123");

        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(PasswordAndRepeatPasswordAreNotEqualsException.class, () -> {
            accountService.createAccount(passwordsAreNotEquals);
        });

        verify(accountRepository, times(1)).findByEmail(anyString());
        verify(accountRepository, never()).findByUsername(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(accountRepository, never()).save(any(Account.class));

    }

// ---------------------------------------------------
// Scenario 04: Account not created, username exists
// ---------------------------------------------------

    @Test
    public void MustGenerateUsernameAlreadyExistsException () throws Exception {

        Account existsAccount = new Account();

        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(accountRepository.findByUsername(anyString())).thenReturn(Optional.of(existsAccount));

        assertThrows(UsernameAlreadyExistsException.class, () -> {
            accountService.createAccount(validSignUpDto);
        });

        verify(accountRepository, times(1)).findByEmail(anyString());
        verify(accountRepository, times(1)).findByUsername(anyString());    
        verify(passwordEncoder, never()).encode(anyString());
        verify(accountRepository, never()).save(any(Account.class));

    }

}
