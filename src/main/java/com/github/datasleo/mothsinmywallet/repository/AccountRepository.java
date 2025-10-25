package com.github.datasleo.mothsinmywallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.datasleo.mothsinmywallet.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail (String email);
    Optional<Account> findByUsername (String username);
    Optional<Account> findByEmailOrUsername (String email, String username);
}
