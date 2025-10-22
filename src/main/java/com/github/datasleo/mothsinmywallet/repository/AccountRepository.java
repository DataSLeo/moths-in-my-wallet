package com.github.datasleo.mothsinmywallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.datasleo.mothsinmywallet.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail (String email);
    Optional<Account> findByUsername (String username);
}
