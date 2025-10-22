package com.github.datasleo.mothsinmywallet.repository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.datasleo.mothsinmywallet.model.Account;

import jakarta.persistence.EntityManager;

@DataJpaTest
public class AccountRepositoryTest {
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager;

//--------------------------------------------------------------
// Scenario 01: Insert the email and verify if exists in H2 db
//--------------------------------------------------------------

    @Test
    public void WhenFindByEmailThenReturnAccount() throws Exception {

        Account account = new Account("test@test.com", "password123", "fooandbar123");

        entityManager.persist(account);
        entityManager.flush();

        Optional<Account> result = accountRepository.findByEmail("test@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@test.com");
        

    }

//-------------------------------------------
// Scenario 02: Verify if the email not exists
//-------------------------------------------

    @Test
    public void VerifyIfEmailExistsThenReturnEmpty() throws Exception {

        Optional<Account> result = accountRepository.findByEmail("test@test.com");

        assertThat(result).isNotPresent();

    }

//----------------------------------------------------------------
// Scenario 03: Insert the username and verify if exists in H2 db
//----------------------------------------------------------------

    @Test
    public void WhenFindByUsernameThenReturnAccount() throws Exception {

        Account account = new Account("test@test.com", "password123", "fooandbar123");

        entityManager.persist(account);
        entityManager.flush();

        Optional<Account> result = accountRepository.findByUsername("fooandbar123");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("fooandbar123");

    }

//-----------------------------------------
// Scenario 04: Verify if username exists 
//-----------------------------------------

    @Test
    public void VerifyIfUsernameExistsThenReturnEmpty() throws Exception {

        Optional<Account> result = accountRepository.findByUsername("fooandbar123");

        assertThat(result).isNotPresent();

    }

}
