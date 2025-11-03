package com.github.datasleo.mothsinmywallet.controller;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.github.datasleo.mothsinmywallet.config.SecurityConfig;
import com.github.datasleo.mothsinmywallet.dto.TagDto;
import com.github.datasleo.mothsinmywallet.exception.TagNameAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.TagNotFoundOrNotAuthorizedException;
import com.github.datasleo.mothsinmywallet.exception.UnauthorizedAccountException;
import com.github.datasleo.mothsinmywallet.model.Tag;
import com.github.datasleo.mothsinmywallet.service.AccountService;
import com.github.datasleo.mothsinmywallet.service.TagService;



@WebMvcTest(TagController.class)
@Import({SecurityConfig.class, TagControllerTest.TestConfig.class})
@AutoConfigureMockMvc(addFilters=true)
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagService tagService;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    public void setup() {
        Mockito.reset(tagService);
        Mockito.reset(accountService);
    }


    @Test
    public void WhenGetTagPathButNotAuthorized_MustRedirectToLoginPath() throws Exception {


        // ACT & ASSERT
        mockMvc
            .perform(get("/tag-manager"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void WhenGetTagPathWithEmptyList_MustGetTagPathWithEmptyList() throws Exception {
        

        // ARRANGE
        long mockAccountId = 1L;

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(mockAccountId);


        // ACT & ASSERT
        mockMvc
            .perform(get("/tag-manager"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/tag_manager"))
            .andExpect(model().attributeExists("tags"))
            .andExpect(model().attribute("tags", hasSize(0)));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());

    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void WhenGetTagPathWithList_MustGetTagPathList() throws Exception {


        // ARRANGE
        String mockUsername = "testUser";
        long mockAccountId = 1L;

        List<Tag> mockTags = Arrays.asList(
            new Tag("service", "service description", null),
            new Tag("food", "food description", null)
        );

        mockTags.get(0).setId(1L);
        mockTags.get(1).setId(2L);

        when(accountService.getAccountIdByPrincipalName(mockUsername)).thenReturn(mockAccountId);
        when(tagService.getAllTagsByAccountId(mockAccountId)).thenReturn(mockTags);


        // ACT & ASSERT
        mockMvc
            .perform(get("/tag-manager"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/tag_manager"))
            .andExpect(model().attributeExists("tags"))
            .andExpect(model().attribute("tags", hasSize(2)))
            .andExpect(model().attribute("tags", hasItem(
                hasProperty("tagName", is("service"))
            )))
            .andExpect(model().attribute("tags", hasItem(
                hasProperty("tagName", is("food"))
            )));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(tagService, times(1)).getAllTagsByAccountId(eq(mockAccountId));

    }

    @Test
    @WithMockUser(username = "userTest", roles = "USER")
    public void WhenPostTagPath_MustReturnViewAuthBackSlashTagManager() throws Exception {


        // ARRANGE
        long mockAccountId = 1L;

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(mockAccountId);

        mockMvc
            .perform(post("/tag-manager")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("tagName", "service")
                .param("tagDescription", "service description"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/tag_manager"));

        verify(tagService, times(1)).createTag(any(TagDto.class));
        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
    
    } 
    
    @Test
    @WithMockUser(username = "userTest", roles = "USER")
    public void WhenPostTagPathButTagNameAlreadyExists_MustReturnModelByTagNameAlreadyExistsException() throws Exception {


        // ARRANGE
        long mockAccountId = 1L;

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(mockAccountId);
        
        String errorMessage = "Tag service already exists.";

        doThrow(new TagNameAlreadyExistsException(errorMessage))
            .when(tagService).createTag(any(TagDto.class));


        // ACT & ASSERT
        mockMvc
            .perform(post("/tag-manager")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("tagName", "service")
                .param("tagDescription", "service description"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/tag_manager"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", is(errorMessage)));

        verify(tagService, times(1)).createTag(any(TagDto.class));  
        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());

    }

    @Test
    @WithMockUser(username = "userTest", roles = "USER")
    public void WhenPostTagPathButAccountIdWasNotFounded_MustReturnModelByAccountIdWasNotFounded() throws Exception {


        // ARRANGE
        long mockAccountId = 1L;

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(mockAccountId);

        String errorMessage = "Account id 1 was not founded.";

        doThrow(new UnauthorizedAccountException(errorMessage))
            .when(tagService).createTag(any(TagDto.class));


        // ACT & ASSERT
        mockMvc
            .perform(post("/tag-manager")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("tagName", "service")
                .param("tagDescription", "service description"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/tag_manager"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", errorMessage));

        verify(tagService, times(1)).createTag(any(TagDto.class));
        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());


    }
    
    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void WhenDeleteTagPath_MustReturnViewAuthBackSlashTagManager() throws Exception {


        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(mockAccountId);
        

        // ACT & ASSERT
        mockMvc
            .perform(delete("/tag-manager/1")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/tag_manager"))
            .andExpect(model().attributeExists("success"))
            .andExpect(model().attribute("success", "Tag was deleted with success."));

        verify(tagService, times(1)).deleteTag(eq(mockTagId), eq(mockAccountId));
        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());

    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void WhenDeleteTagPathButTagNotFoundOrNotAuthorized_MustModelByTagNotFoundOrNotAuthorizedException() throws Exception {


        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(mockAccountId);

        doThrow(new TagNotFoundOrNotAuthorizedException("Tag not found or not authorized."))
            .when(tagService)
            .deleteTag(mockTagId, mockAccountId);


        // ACT & ASSERT
        mockMvc
            .perform(delete("/tag-manager/1")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/tag_manager"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Tag not found or not authorized."));

        verify(tagService, times(1)).deleteTag(eq(mockTagId), eq(mockAccountId));
        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());

    }

    @Test
    @WithMockUser(username = "userTest", roles = "USER")
    public void WhenGetEditTagPath_MustReturnViewAuthBackSlashTagEdit() throws Exception {


        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(mockAccountId);

        Tag mockTag = new Tag("service", "service description", null);
        mockTag.setId(mockTagId);

        when(tagService.getTagByIdAndAccountId(mockTagId, mockAccountId)).thenReturn(mockTag);


        // ACT & ASSERT
        mockMvc
            .perform(get("/tag-manager/1/edit"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/tag_edit"))
            .andExpect(model().attributeExists("tag"))
            .andExpect(model().attribute("tag", allOf(
                hasProperty("tagName", is("service")),
                hasProperty("tagDescription", is("service description"))
            )));

        verify(tagService, times(1)).getTagByIdAndAccountId(eq(mockTagId), eq(mockAccountId));
        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());

    }

    @Test
    @WithMockUser(username = "userTest", roles = "USER")
    public void WhenGetIdTagPathButTagNotFoundOrNotAuthorized_MustReturnModelByTagNotFoundOrNotAuthorizedException() throws Exception {


        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(mockAccountId);


        // ACT & ASSERT
        doThrow(new TagNotFoundOrNotAuthorizedException("Tag not found or not authorized."))
            .when(tagService)
            .getTagByIdAndAccountId(mockTagId, mockAccountId);


        mockMvc
            .perform(get("/tag-manager/1/edit"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/tag_edit"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Tag not found or not authorized."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(tagService, times(1)).getTagByIdAndAccountId(mockTagId, mockAccountId);

    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void WhenUpdateTag_MustReturnViewBackSlashTagManager() throws Exception {


        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;

        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(mockAccountId);
        when(tagService.updateTag(eq(mockTagId), eq(mockAccountId), any(TagDto.class))).thenReturn(any(Tag.class));


        // ACT & ASSERT
        mockMvc
            .perform(patch("/tag-manager/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("tagName", "service")
                .param("tagDescription", "tag description"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/tag_manager"))
            .andExpect(model().attributeExists("success"))
            .andExpect(model().attribute("success", "Tag was updated with success."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(tagService, times(1)).updateTag(eq(mockTagId), eq(mockAccountId), any(TagDto.class));

    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void WhenUpdateTagButTagNotFoundOrNotAuthorized_MustReturnModelByTagNotFoundOrNotAuthorizedException() throws Exception {


        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;
        
        when(accountService.getAccountIdByPrincipalName(anyString())).thenReturn(mockAccountId);
        

        // ACT & ASSERT
        doThrow(new TagNotFoundOrNotAuthorizedException("Tag not found or not authorized."))
            .when(tagService)
            .updateTag(eq(mockTagId), eq(mockAccountId), any(TagDto.class));
        

        mockMvc
            .perform(patch("/tag-manager/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("tagName", "service")
                .param("tagDescription", "tag description"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/tag_manager"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "Tag not found or not authorized."));

        verify(accountService, times(1)).getAccountIdByPrincipalName(anyString());
        verify(tagService, times(1)).updateTag(eq(mockTagId), eq(mockAccountId), any(TagDto.class));

    }

    @TestConfiguration
    static class TestConfig {
        
        @Bean
        public TagService tagService() {
            return Mockito.mock(TagService.class);
        }

        @Bean
        public AccountService accountService() {
            return Mockito.mock(AccountService.class);
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return username -> org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("hased_password")
                .roles("USER")
                .build();
        }

    }

}