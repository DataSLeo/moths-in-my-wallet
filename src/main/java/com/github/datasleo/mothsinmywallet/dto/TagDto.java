package com.github.datasleo.mothsinmywallet.dto;

public class TagDto {
    
    private String tagName;
    private String tagDescription;
    private Long accountId;

    public TagDto(String tagName, String tagDescription, Long accountId) {
        this.tagName = tagName;
        this.tagDescription = tagDescription;
        this.accountId = accountId;
    }

    public String getTagName() {
        return tagName;
    }
    
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    
    public String getTagDescription() {
        return tagDescription;
    }
    
    public void setTagDescription(String tagDescription) {
        this.tagDescription = tagDescription;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

}
