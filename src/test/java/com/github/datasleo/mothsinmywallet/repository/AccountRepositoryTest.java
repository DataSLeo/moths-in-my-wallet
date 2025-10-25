package com.github.datasleo.mothsinmywallet.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.datasleo.mothsinmywallet.model.Account;

import jakarta.persistence.EntityManager;

@DataJpaTest
public class AccountRepositoryTest {
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void WhenFindEmail_MustReturnAccount() throws Exception {

        Account account = new Account("test@test.com", "password123", "fooandbar123");

        entityManager.persist(account);
        entityManager.flush();

        Optional<Account> result = accountRepository.findByEmail("test@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@test.com");
        

    }

    @Test
    public void WhenEmailNotExists_MustReturnEmptyAccount() throws Exception {

        Optional<Account> result = accountRepository.findByEmail("test@test.com");

        assertThat(result).isNotPresent();

    }

    @Test
    public void WhenFindUsername_MustReturnAccount() throws Exception {

        Account account = new Account("test@test.com", "password123", "fooandbar123");

        entityManager.persist(account);
        entityManager.flush();

        Optional<Account> result = accountRepository.findByUsername("fooandbar123");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("fooandbar123");

    }

    @Test
    public void WhenUsernameNotExists_MustReturnEmptyAccount() throws Exception {

        Optional<Account> result = accountRepository.findByUsername("fooandbar123");

        assertThat(result).isNotPresent();

    }

}
