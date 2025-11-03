package com.github.datasleo.mothsinmywallet.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.datasleo.mothsinmywallet.dto.PaymentMethodDto;
import com.github.datasleo.mothsinmywallet.exception.PaymentMethodAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.PaymentMethodNotFoundOrNotAuthorizedException;
import com.github.datasleo.mothsinmywallet.exception.UnauthorizedAccountException;
import com.github.datasleo.mothsinmywallet.model.Account;
import com.github.datasleo.mothsinmywallet.model.PaymentMethod;
import com.github.datasleo.mothsinmywallet.repository.AccountRepository;
import com.github.datasleo.mothsinmywallet.repository.PaymentMethodRepository;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentRepository;
    private final AccountRepository accountRepository;

    public PaymentMethodService(PaymentMethodRepository paymentRepository, AccountRepository accountRepository) {
        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
    }

    // CREATE
    public PaymentMethod createPayment(PaymentMethodDto dto) {

        String paymentNameByDto = dto.getPaymentName();
        String paymentDescriptionByDto = dto.getPaymentDescription();
        long paymentAccountIdByDto = dto.getAccountId();

        Optional<Account> account = accountRepository.findById(paymentAccountIdByDto);

        if(account.isEmpty()) {
            throw new UnauthorizedAccountException("Unauthorized account."); 
        }

        PaymentMethod resultByPaymentRepository = paymentRepository.findByPaymentNameAndAccountId(paymentNameByDto, paymentAccountIdByDto);

        if(resultByPaymentRepository != null) {
            throw new PaymentMethodAlreadyExistsException("Payment method '" + paymentNameByDto + "' already exists.");
        } 

        PaymentMethod newPayment = new PaymentMethod(paymentNameByDto, paymentDescriptionByDto, account.get());

        return paymentRepository.save(newPayment);

    }

    // READ ALL
    public List<PaymentMethod> getAllPaymentsByAccountId(long accountId) {

        Optional<Account> account = accountRepository.findById(accountId);

        if(account.isEmpty()) {
            throw new UnauthorizedAccountException("Unauthorized account.");        
        }

        return paymentRepository.findAllByAccountId(accountId);
    }

    // READ ONE
    public PaymentMethod getOnePaymentMethod(long paymentId, long accountId) {
        
        Optional<Account> account = accountRepository.findById(accountId);
        
        if(account.isEmpty()) {
            throw new UnauthorizedAccountException("Unauthorized account.");               
        }

        PaymentMethod payment = paymentRepository.findOneByIdAndAccountId(paymentId, accountId);

        if(payment == null) {
            throw new PaymentMethodNotFoundOrNotAuthorizedException("Payment method not found or not authorized.");
        }

        return payment;
    }

    // UPDATE
    public PaymentMethod updatePayment(long paymentId, long accountId, PaymentMethodDto dto) {
        
        String paymentNameByDto = dto.getPaymentName();
        String paymentDescriptionByDto = dto.getPaymentDescription();

        Optional<Account> account = accountRepository.findById(accountId);

        if(account.isEmpty()) {
            throw new UnauthorizedAccountException("Unauthorized account.");
        }
        
        PaymentMethod payment = paymentRepository.findOneByIdAndAccountId(paymentId, accountId);

        if(payment == null) {
            throw new PaymentMethodNotFoundOrNotAuthorizedException("Payment method not found or not authorized.");
        }

        PaymentMethod isExistsPayment = paymentRepository.findByPaymentNameAndAccountId(paymentNameByDto, accountId);

        if(isExistsPayment != null) {
            if((!isExistsPayment.getPaymentName().equals(paymentNameByDto)) && isExistsPayment.getId() != paymentId) {
                throw new PaymentMethodAlreadyExistsException("Payment method '" + paymentNameByDto + "' already exists.");
        
            }
        }

        payment.setPaymentName(paymentNameByDto);
        payment.setPaymentDescription(paymentDescriptionByDto);

        paymentRepository.save(payment);

        return payment;

    }

    // DELETE
    public PaymentMethod deletePayment(long paymentId, long accountId) { 

        Optional<Account> account = accountRepository.findById(accountId);

        if(account.isEmpty()) {
            throw new UnauthorizedAccountException("Unauthorized account.");            
        }

        PaymentMethod deletedPayment = paymentRepository.findOneByIdAndAccountId(paymentId, accountId);

        if(deletedPayment == null) {
            throw new PaymentMethodNotFoundOrNotAuthorizedException("Payment method not found or not authorized.");
        }

        paymentRepository.delete(deletedPayment);

        return deletedPayment;

    }

}