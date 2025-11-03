package com.github.datasleo.mothsinmywallet.dto;

public class PaymentMethodDto {
    
    private String paymentName;
    private String paymentDescription;
    private Long accountId;

    public PaymentMethodDto(String paymentName, String paymentDescription, Long accountId) {
        this.paymentName = paymentName;
        this.paymentDescription = paymentDescription;
        this.accountId = accountId;
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

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

}
