package com.github.datasleo.mothsinmywallet.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.datasleo.mothsinmywallet.dto.TagDto;
import com.github.datasleo.mothsinmywallet.exception.AccountIdWasNotFoundedException;
import com.github.datasleo.mothsinmywallet.exception.TagNameAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.TagNotFoundOrNotAuthorizedException;
import com.github.datasleo.mothsinmywallet.model.Account;
import com.github.datasleo.mothsinmywallet.model.Tag;
import com.github.datasleo.mothsinmywallet.repository.AccountRepository;
import com.github.datasleo.mothsinmywallet.repository.TagRepository;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final AccountRepository accountRepository;

    public TagService(TagRepository tagRepository, AccountRepository accountRepository) {
        this.tagRepository = tagRepository;
        this.accountRepository = accountRepository;
    }

    public Tag createTag(TagDto dto) {
        
        String tagName = dto.getTagName();
        String tagDescription = dto.getTagDescription();
        Long accountId = dto.getAccountId();

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountIdWasNotFoundedException("Account id " + accountId + " was not founded."));

        if(tagRepository.findByTagNameAndAccountId(tagName, accountId).isPresent()){
            throw new TagNameAlreadyExistsException("Tag " + tagName + " already exists.");
        }

        Tag tag = new Tag();

        tag.setTagName(tagName);
        tag.setTagDescription(tagDescription);
        tag.setAccount(account);

        return tagRepository.save(tag);

    }

    public List<Tag> getAllTagsByAccountId(Long accountId) {
        return tagRepository.findAllByAccountId(accountId);
    }

    public Tag getTagByIdAndAccountId(long tagId, long accountId) {
        
        Optional<Tag> optionalTag = tagRepository.findByIdAndAccountId(tagId, accountId);

        if(optionalTag.isEmpty()) {
            throw new TagNotFoundOrNotAuthorizedException("Tag not found or not authorized.");
        }

        return optionalTag.get();

    }

    public void deleteTag(long tagId, long accountId) {

        Optional<Tag> optionalTag = tagRepository.findByIdAndAccountId(tagId, accountId);

        if (optionalTag.isEmpty()) {
            throw new TagNotFoundOrNotAuthorizedException("Tag not found or not authorized.");
        }

        Tag tagToDelete = optionalTag.get();

        tagRepository.delete(tagToDelete);

    }

    public Tag updateTag(Long tagId, long accountId, TagDto dto) {

        String tagNameDto = dto.getTagName();
        String tagDescriptionDto = dto.getTagDescription();


        Tag existingTag = tagRepository.findByIdAndAccountId(tagId, accountId)
            .orElseThrow(() -> new TagNotFoundOrNotAuthorizedException("Tag not found or not authorized."));

        if(!existingTag.getTagName().equals(tagNameDto)) {
            if(tagRepository.findByTagNameAndAccountId(tagNameDto, accountId).isPresent()) {
                throw new TagNameAlreadyExistsException("Tag '" + tagNameDto + "' already exists in this account.");
            }
        }

        existingTag.setTagName(tagNameDto);
        existingTag.setTagDescription(tagDescriptionDto);

        return tagRepository.save(existingTag);

    }

}
