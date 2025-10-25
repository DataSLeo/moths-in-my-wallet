package com.github.datasleo.mothsinmywallet.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.datasleo.mothsinmywallet.model.Account;
import com.github.datasleo.mothsinmywallet.repository.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public CustomUserDetailsService (AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername (String emailOrUsername) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername)
            .orElseThrow(() -> new UsernameNotFoundException("Username " + emailOrUsername + " not found."));

        return User.builder()
            .username(account.getUsername())
            .password(account.getPassword())
            .roles("USER")
            .build();

    }

}
