package com.github.datasleo.mothsinmywallet.dto;

public class SignUpDto {

    private String email;
    private String password;
    private String repeatPassword;
    private String username;

    public SignUpDto(String email, String password, String repeatPassword, String username) {
        this.email = email;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.username = username;
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
    public String getRepeatPassword() {
        return repeatPassword;
    }
    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

}
