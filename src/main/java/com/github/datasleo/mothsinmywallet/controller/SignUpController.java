package com.github.datasleo.mothsinmywallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.datasleo.mothsinmywallet.dto.SignUpDto;
import com.github.datasleo.mothsinmywallet.exception.EmailAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.PasswordAndRepeatPasswordAreNotEqualsException;
import com.github.datasleo.mothsinmywallet.exception.UsernameAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.service.AccountService;

@Controller
@RequestMapping("/signup")
public class SignUpController {
  
    @Autowired
    private AccountService accountService;

    @GetMapping
    public String showForms() {
        return "signup";
    }

    @PostMapping
    public String registerAccount(@ModelAttribute SignUpDto signUpDto, Model model){
        
        try {
      
            accountService.createAccount(signUpDto);
            return "redirect:/";
      
        } catch (EmailAlreadyExistsException | PasswordAndRepeatPasswordAreNotEqualsException | UsernameAlreadyExistsException e) {
      
          model.addAttribute("error", e.getMessage());            
    
        }

        return "signup";
        
    }

}
