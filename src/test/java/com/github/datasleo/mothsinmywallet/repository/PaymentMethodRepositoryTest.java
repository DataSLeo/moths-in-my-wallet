package com.github.datasleo.mothsinmywallet.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.github.datasleo.mothsinmywallet.model.Account;
import com.github.datasleo.mothsinmywallet.model.PaymentMethod;


@DataJpaTest
public class PaymentMethodRepositoryTest {

    @Autowired
    private PaymentMethodRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;
    
    private PaymentMethod payment;
    private Account account;

    @BeforeEach
    public void setup() {
       
        account = new Account("foo@foo.com", "hashed_password", "foo");
        payment = new PaymentMethod("credit card", "description", account);
        
        entityManager.persist(account);
        entityManager.persist(payment);

    }

    @AfterEach
    public void tearDown() {

        entityManager.clear();
    
    }
    
// -------------------------------------------------- findByPaymentNameAndAccountId method -------------------------------------------------- 

    @Test
    public void WhenFindByPaymentNameAndAccountId_ThenReturnPayment() throws Exception {

        // ARRANGE
        String paymentName = "credit card";


        // ACT
        PaymentMethod result = paymentRepository.findByPaymentNameAndAccountId(paymentName, account.getId());


        // ASSERT
        assertThat(result).isNotNull();

        assertThat(result.getPaymentName()).isEqualTo(paymentName);
        assertThat(result.getPaymentDescription()).isEqualTo("description");
        assertThat(result.getId()).isNotNull();
        
        assertThat(result.getAccount().getId()).isEqualTo(account.getId());

    }

// -------------------------------------------------- findAllByAccountId method -------------------------------------------------- 

    @Test
    public void WhenFindAllByAccountId_ThenReturnListOfPayments() throws Exception {

        // ARRANGE
        PaymentMethod paymentData1 = new PaymentMethod("data 1", "description data 1", account);
        PaymentMethod paymentData2 = new PaymentMethod("data 2", "description data 2", account);
        PaymentMethod paymentData3 = new PaymentMethod("data 3", "description data 3", account);

        entityManager.persist(paymentData1);

        entityManager.persist(paymentData2);

        entityManager.persist(paymentData3);


        // ACT
        List<PaymentMethod> result = paymentRepository.findAllByAccountId(account.getId());


        // ASSERT
        assertThat(result.size()).isEqualTo(4);

        assertThat(result.get(0).getPaymentName()).isEqualTo("credit card");
        assertThat(result.get(0).getPaymentDescription()).isEqualTo("description");
        assertThat(result.get(0).getId()).isNotNull();

        assertThat(result.get(1).getPaymentName()).isEqualTo("data 1");
        assertThat(result.get(1).getPaymentDescription()).isEqualTo("description data 1");
        assertThat(result.get(1).getId()).isNotNull();

        assertThat(result.get(2).getPaymentName()).isEqualTo("data 2");
        assertThat(result.get(2).getPaymentDescription()).isEqualTo("description data 2");
        assertThat(result.get(2).getId()).isNotNull();

        assertThat(result.get(3).getPaymentName()).isEqualTo("data 3");
        assertThat(result.get(3).getPaymentDescription()).isEqualTo("description data 3");
        assertThat(result.get(3).getId()).isNotNull();

        assertThat(account.getId()).isNotNull();

    }

// -------------------------------------------------- findOneByPaymentIdAndAccountId method -------------------------------------------------- 

    @Test
    public void WhenFindOneByPaymentIdAndAccountId_ThenReturnOnePayment() throws Exception {

        // ARRANGE
        

        // ACT
        PaymentMethod result = paymentRepository.findOneByIdAndAccountId(payment.getId(), account.getId());


        // ASSERT
        assertThat(result.getPaymentName()).isEqualTo("credit card");
        assertThat(result.getPaymentDescription()).isEqualTo("description");
        
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAccount().getId()).isNotNull();

    }


}
