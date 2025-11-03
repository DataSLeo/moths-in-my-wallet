package com.github.datasleo.mothsinmywallet.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="account_table")
public class Account {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String username;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval=true)
    private final Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval=true)
    private final Set<PaymentMethod> payments = new HashSet<>();

    public Account() {}

    public Account(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.setAccount(this);
    }

    public void addPayment(PaymentMethod payment) {
        payments.add(payment);
        payment.setAccount(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.setAccount(null);
    }

    public void removePayment(PaymentMethod payment) {
        payments.remove(payment);
        payment.setAccount(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Set<PaymentMethod> getPayments() {
        return payments;
    }

}
