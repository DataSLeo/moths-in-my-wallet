package com.github.datasleo.mothsinmywallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.datasleo.mothsinmywallet.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail (String email);
    Optional<Account> findByUsername (String username);
    Optional<Account> findByEmailOrUsername (String email, String username);

    @Query("SELECT a.id FROM Account a WHERE a.email = :identify OR a.username = :identify")
    Long findIdByIdentify(@Param("identify") String identify);
}
