package com.github.datasleo.mothsinmywallet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="payment_method_table")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name="payment_name", nullable=false)
    private String paymentName;

    @Column(name="payment_description", nullable=true)
    private String paymentDescription;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="fk_account_table", nullable=false)
    private Account account;

    public PaymentMethod() {}

    public PaymentMethod(String paymentName, String paymentDescription, Account account) {
        this.paymentName = paymentName;
        this.paymentDescription = paymentDescription;
        this.account = account;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPaymentDescription() {
        return paymentDescription;
    }

    public void setPaymentDescription(String paymentDescription) {
        this.paymentDescription = paymentDescription;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
