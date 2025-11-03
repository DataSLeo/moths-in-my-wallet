package com.github.datasleo.mothsinmywallet.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.datasleo.mothsinmywallet.dto.PaymentMethodDto;
import com.github.datasleo.mothsinmywallet.exception.PaymentMethodAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.PaymentMethodNotFoundOrNotAuthorizedException;
import com.github.datasleo.mothsinmywallet.exception.UnauthorizedAccountException;
import com.github.datasleo.mothsinmywallet.model.PaymentMethod;
import com.github.datasleo.mothsinmywallet.service.AccountService;
import com.github.datasleo.mothsinmywallet.service.PaymentMethodService;

@Controller
@RequestMapping("/payment-method")
public class PaymentMethodController {

    private final PaymentMethodService paymentService;
    private final AccountService accountService;

    public PaymentMethodController (PaymentMethodService paymentService, AccountService accountService) {
        this.paymentService = paymentService;
        this.accountService = accountService;
    }

    // GET /payment-method/
    @GetMapping
    public String getPaymentMethod(Principal principal, Model model) {
        
        String identify = principal.getName();
        Long accountId = accountService.getAccountIdByPrincipalName(identify);

        if(accountId != null) {

            List<PaymentMethod> listOfPaymentsMethod = paymentService.getAllPaymentsByAccountId(accountId);
            model.addAttribute("payments_method", listOfPaymentsMethod);

        }
        
        return "auth/payment_method_man";
    
    }

    // GET /payment-method/add/
    @GetMapping("/add")
    public String getPaymentMethodAdd() {
        return "auth/payment_method_add";
    }

    // POST /payment-method/add/
    @PostMapping("/add")
    public String setPaymentMethod(@ModelAttribute PaymentMethodDto paymentDto, Principal principal, Model model) {
      
        String identify = principal.getName();
        Long accountId = accountService.getAccountIdByPrincipalName(identify);
        
        paymentDto.setAccountId(accountId);

        try {

            PaymentMethod createdPayment = paymentService.createPayment(paymentDto); 
            model.addAttribute("success", "Payment method '" + createdPayment.getPaymentName() + "' created with success.");

        } catch (UnauthorizedAccountException | PaymentMethodAlreadyExistsException e) {

            model.addAttribute("error", e.getMessage());

        }

        return "auth/payment_method_add";

    }

    // GET /payment-method/edit/{id}
    @GetMapping("/edit/{id}")
    public String getPaymentMethodByEdit(@PathVariable("id") Long id, Principal principal, Model model) {
        
        String identify = principal.getName();
        Long accountId = accountService.getAccountIdByPrincipalName(identify);
        
        try {

            PaymentMethod payment = paymentService.getOnePaymentMethod(id, accountId);
            model.addAttribute("id", id);
            model.addAttribute("paymentName", payment.getPaymentName());
            model.addAttribute("paymentDescription", payment.getPaymentDescription());

        } catch (UnauthorizedAccountException | PaymentMethodNotFoundOrNotAuthorizedException e) {

            model.addAttribute("error", e.getMessage());

        }
        
        return "auth/payment_method_edit";
    }    

    // PATCH /payment-method/edit
    @PatchMapping("/edit/{id}")
    public String setPaymentMethodById(@PathVariable("id") Long id, @ModelAttribute PaymentMethodDto paymentDto, Principal principal, Model model) {

        String identify = principal.getName();
        Long accountId = accountService.getAccountIdByPrincipalName(identify);
        
        try {

            PaymentMethod payment = paymentService.updatePayment(id, accountId, paymentDto);
            model.addAttribute("success", "Payment method '" + payment.getPaymentName() + "' was updated with success");

        } catch (UnauthorizedAccountException | PaymentMethodNotFoundOrNotAuthorizedException | PaymentMethodAlreadyExistsException e) {

            model.addAttribute("error", e.getMessage());

        }

        return "auth/payment_method_man";
    }

    // DELETE /payment-method/delete
    @DeleteMapping("/delete/{id}")
    public String removePaymentMethod(@PathVariable("id") Long id, Principal principal, Model model) {

        String identify = principal.getName();
        Long accountId = accountService.getAccountIdByPrincipalName(identify);

        try {

            PaymentMethod payment = paymentService.deletePayment(id, accountId);
            model.addAttribute("success", "Payment method '" + payment.getPaymentName() + "' was deleted with success.");
            
        } catch (UnauthorizedAccountException | PaymentMethodNotFoundOrNotAuthorizedException e) {

            model.addAttribute("error", e.getMessage());

        }

        return "auth/payment_method_man";

    }

}
