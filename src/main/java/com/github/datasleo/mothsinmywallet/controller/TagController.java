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

import com.github.datasleo.mothsinmywallet.dto.TagDto;
import com.github.datasleo.mothsinmywallet.exception.UnauthorizedAccountException;
import com.github.datasleo.mothsinmywallet.exception.TagNameAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.TagNotFoundOrNotAuthorizedException;
import com.github.datasleo.mothsinmywallet.model.Tag;
import com.github.datasleo.mothsinmywallet.service.AccountService;
import com.github.datasleo.mothsinmywallet.service.TagService;



@Controller
public class TagController {
    
    private final TagService tagService;
    private final AccountService accountService;
    
    public TagController(TagService tagService, AccountService accountService) {
        this.tagService = tagService;
        this.accountService = accountService;
    } 

    @GetMapping("/tag-manager")
    public String getTag(Principal principal, Model model) {

        String identify = principal.getName();
        Long accountId = accountService.getAccountIdByPrincipalName(identify);

        if(accountId != null) {
            List<Tag> tags = tagService.getAllTagsByAccountId(accountId);
            model.addAttribute("tags", tags);
        }

        return "auth/tag_manager";
    }

    @PostMapping("/tag-manager")
    public String setTag(@ModelAttribute TagDto tagDto, Principal principal, Model model) {
        
        String identify = principal.getName();
        Long accountId = accountService.getAccountIdByPrincipalName(identify);

        tagDto.setAccountId(accountId);

        try {

            tagService.createTag(tagDto);
            model.addAttribute("success", "Tag '" + tagDto.getTagName() + "' was created with succcess.");

        } catch (TagNameAlreadyExistsException | UnauthorizedAccountException e) {
            
            model.addAttribute("error", e.getMessage());

        }

        return "auth/tag_manager";

    }

    @DeleteMapping("/tag-manager/{id}")
    public String deleteTag(@PathVariable long id, Principal principal, Model model) {
        
        String identify = principal.getName();
        Long accountId = accountService.getAccountIdByPrincipalName(identify);

        try {

            tagService.deleteTag(id, accountId);
            model.addAttribute("success", "Tag was deleted with success.");
                
        } catch (TagNotFoundOrNotAuthorizedException e) {

            model.addAttribute("error", e.getMessage());
        }

        return "auth/tag_manager";

    }

    @GetMapping("/tag-manager/{id}/edit")
    public String editTag(@PathVariable long id, Principal principal, Model model) {
        
        String identify = principal.getName();
        Long accountId = accountService.getAccountIdByPrincipalName(identify);
        
        try {

            Tag tagToEdit = tagService.getTagByIdAndAccountId(id, accountId);
            model.addAttribute("tag", tagToEdit);

        } catch (TagNotFoundOrNotAuthorizedException e) {
            
            model.addAttribute("error", e.getMessage());

        }

        return "auth/tag_edit";
    }


    @PatchMapping("/tag-manager/{id}")
    public String updateTag(@PathVariable long id, @ModelAttribute TagDto tagDto, Principal principal, Model model) {
        
        String identify = principal.getName();
        Long accountId = accountService.getAccountIdByPrincipalName(identify);

        try {

            tagService.updateTag(id, accountId, tagDto);
            model.addAttribute("success", "Tag was updated with success.");

        } catch (TagNotFoundOrNotAuthorizedException e) {
            
            model.addAttribute("error", e.getMessage());

        }

        return "auth/tag_manager";
        
    }

}
