package com.github.datasleo.mothsinmywallet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.datasleo.mothsinmywallet.dto.SignUpDto;
import com.github.datasleo.mothsinmywallet.exception.EmailAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.PasswordAndRepeatPasswordAreNotEqualsException;
import com.github.datasleo.mothsinmywallet.exception.UsernameAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.model.Account;
import com.github.datasleo.mothsinmywallet.repository.AccountRepository;


@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account createAccount(SignUpDto signUpDto) {

        String email = signUpDto.getEmail();
        String password = signUpDto.getPassword();
        String repeatPassword = signUpDto.getRepeatPassword();
        String username = signUpDto.getUsername();

        if(accountRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + email + " already exists.");
        }

        if(!password.equals(repeatPassword)) {
            throw new PasswordAndRepeatPasswordAreNotEqualsException("Passwords are not equals.");
        }

        if(accountRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("Username " + username + " already exists.");
        }

        Account account = new Account();

        String encrypt = passwordEncoder.encode(password);

        account.setEmail(email);
        account.setPassword(encrypt);
        account.setUsername(username);

        return accountRepository.save(account);

    }

}
