package com.github.datasleo.mothsinmywallet.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.datasleo.mothsinmywallet.model.Account;
import com.github.datasleo.mothsinmywallet.repository.AccountRepository;

@Controller
public class HomeController {

    private final AccountRepository accountRepository;

    public HomeController (AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/")
    public String getIndex() {
        return "index";
    }

    @RequestMapping("/home")
    public String getHome(Model model) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if(principal instanceof UserDetails userDetails) {
            
            username = userDetails.getUsername();
        
        } else {
            
            // username = principal.toString(); I commented this code beacuse VSCode is warning a possible null pointer
            username = principal != null ? principal.toString() : "anonymousUser";
        
        }

        Account account = accountRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found."));

        model.addAttribute("username", account.getUsername());

        return "auth/home";
    }

}
