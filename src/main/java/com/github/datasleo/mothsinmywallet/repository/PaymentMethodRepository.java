package com.github.datasleo.mothsinmywallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.datasleo.mothsinmywallet.model.PaymentMethod;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    PaymentMethod findByPaymentNameAndAccountId(String paymentName, long accountId);
    List<PaymentMethod> findAllByAccountId(long accountId);
    PaymentMethod findOneByIdAndAccountId(long id, long accountId);

}
