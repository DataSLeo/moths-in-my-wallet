package com.github.datasleo.mothsinmywallet.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.datasleo.mothsinmywallet.dto.TagDto;
import com.github.datasleo.mothsinmywallet.exception.AccountIdWasNotFoundedException;
import com.github.datasleo.mothsinmywallet.exception.TagNameAlreadyExistsException;
import com.github.datasleo.mothsinmywallet.exception.TagNotFoundOrNotAuthorizedException;
import com.github.datasleo.mothsinmywallet.model.Account;
import com.github.datasleo.mothsinmywallet.model.Tag;
import com.github.datasleo.mothsinmywallet.repository.AccountRepository;
import com.github.datasleo.mothsinmywallet.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TagService tagService;

    @Test
    public void WhenCreateTag_MustCreateTag() throws Exception {


        // ARRANGE
        Account mockAccount = new Account("test@test.com", "hashed_passowrd", "test_");
        long mockAccountId = 1L;

        when(accountRepository.findById(eq(mockAccountId))).thenReturn(Optional.of(mockAccount));
        when(tagRepository.findByTagNameAndAccountId(anyString(), eq(mockAccountId))).thenReturn(Optional.empty());

        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag savedTag = invocation.getArgument(0);

            savedTag.setId(1L);

            return savedTag;
        });


        // ACT
        TagDto dto = new TagDto("service", "service description", mockAccountId);

        Tag result = tagService.createTag(dto);
        

        // ASSERT
        assertNotNull(result);
        assertEquals("service", result.getTagName());
        assertEquals("service description", result.getTagDescription());

        verify(accountRepository, times(1)).findById(eq(mockAccountId));
        verify(tagRepository, times(1)).findByTagNameAndAccountId(anyString(), eq(mockAccountId));
        verify(tagRepository, times(1)).save(any(Tag.class));

    }

    @Test
    public void WhenCreateTagWithNotAccountExisting_MustReturnAccountIdWasNotFoundedException() {


        // ARRANGE
        long mockAccountId = 1L;

        when(accountRepository.findById(eq(mockAccountId))).thenReturn(Optional.empty());

        TagDto dto = new TagDto("service", "service description", mockAccountId);


        // ACT & ASSERT
        AccountIdWasNotFoundedException thrown = assertThrows(
            AccountIdWasNotFoundedException.class,
            () -> tagService.createTag(dto)
        );

        assertEquals("Account id 1 was not founded.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(eq(mockAccountId));
        verify(tagRepository, never()).findByTagNameAndAccountId(anyString(), eq(mockAccountId));
        verify(tagRepository, never()).save(any(Tag.class));


    }

    @Test
    public void WhenCreateTagWithTagAlreadyExists_MustReturnTagNameAlreadyExistsException() throws Exception {


        // ARRANGE
        Account mockAccount = new Account("test@test.com", "hashed_password", "test_");
        long mockAccountId = 1L;

        when(accountRepository.findById(eq(mockAccountId))).thenReturn(Optional.of(mockAccount));
        when(tagRepository.findByTagNameAndAccountId(anyString(), eq(mockAccountId))).thenReturn(Optional.of(new Tag()));

        TagDto dto = new TagDto("service", "service description", mockAccountId);

        
        // ACT & ASSERT
        TagNameAlreadyExistsException thrown = assertThrows(
            TagNameAlreadyExistsException.class,
            () -> tagService.createTag(dto)
        );

        assertEquals("Tag service already exists.", thrown.getMessage());

        verify(accountRepository, times(1)).findById(eq(mockAccountId));
        verify(tagRepository, times(1)).findByTagNameAndAccountId(anyString(), eq(mockAccountId));
        verify(tagRepository, never()).save(any(Tag.class));

    }

    @Test
    public void WhenGetTagByIdAndAccountId_MustReturnTag() throws Exception {
        

        // ARRANGE
        Account mockAccount = new Account("test@test.com", "hashed_password", "test_");
        Tag mockTag = new Tag("service", "service description", mockAccount);

        long mockTagId = 1L;
        long mockAccountId = 1L;

        when(tagRepository.findByIdAndAccountId(eq(mockTagId), eq(mockAccountId))).thenReturn(Optional.of(mockTag));


        // ACT
        Tag result = tagService.getTagByIdAndAccountId(mockTagId, mockAccountId);


        // ASSERT
        assertNotNull(result);
        assertEquals("service", result.getTagName());
        assertEquals("service description", result.getTagDescription());

        verify(tagRepository, times(1)).findByIdAndAccountId(eq(mockTagId), eq(mockAccountId));

    }

    @Test
    public void WhenGetTagByIdAndAccountIdWithIdTagDoesNotExists_MustReturnOptionalEmpty() throws Exception {

        
        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;

        when(tagRepository.findByIdAndAccountId(eq(mockTagId), eq(mockAccountId))).thenReturn(Optional.empty());


        // ACT & ASSERT
        TagNotFoundOrNotAuthorizedException thrown = assertThrows(
            TagNotFoundOrNotAuthorizedException.class,
            () -> tagService.getTagByIdAndAccountId(mockTagId, mockAccountId)
        );

        assertEquals("Tag not found or not authorized.", thrown.getMessage());

        verify(tagRepository, times(1)).findByIdAndAccountId(eq(mockTagId), eq(mockAccountId));

    }

    @Test
    public void WhenDeleteTag_MustDeleteTag() throws Exception {


        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;

        when(tagRepository.findByIdAndAccountId(eq(mockTagId), eq(mockAccountId))).thenReturn(Optional.of(new Tag()));
        

        // ACT
        tagService.deleteTag(mockTagId, mockAccountId);


        // ASSERT
        verify(tagRepository, times(1)).findByIdAndAccountId(mockTagId, mockAccountId);
        verify(tagRepository, times(1)).delete(any(Tag.class));


    }

    @Test
    public void WhenDeleteTagWithTagIdOrAccountIdNotFound_MustReturnTagNotFoundOrNotAuthorizedException() throws Exception {


        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;

        when(tagRepository.findByIdAndAccountId(eq(mockTagId), eq(mockAccountId))).thenReturn(Optional.empty());


        // ACT & ASSERT
        TagNotFoundOrNotAuthorizedException thrown = assertThrows(
            TagNotFoundOrNotAuthorizedException.class,
            () -> tagService.deleteTag(mockTagId, mockAccountId)  
        );

        assertEquals("Tag not found or not authorized.", thrown.getMessage());

        verify(tagRepository, times(1)).findByIdAndAccountId(mockTagId, mockAccountId);
        verify(tagRepository, never()).delete(any(Tag.class));

    }

    @Test
    public void WhenUpdateTag_MustUpdateTag() throws Exception {


        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;

        Account mockAccount = new Account("test@test.com", "hashed_password", "test");
        Tag mockTag = new Tag("service", "service description", mockAccount); 
        TagDto mockTagDto = new TagDto("sport", "sport description", mockAccountId);

        when(tagRepository.findByIdAndAccountId(mockTagId, mockAccountId)).thenReturn(Optional.of(mockTag));

        mockTag.setTagName(mockTagDto.getTagName());
        mockTag.setTagDescription(mockTagDto.getTagDescription());

        when(tagRepository.save(mockTag)).thenAnswer(invocation -> invocation.getArgument(0));    


        // ACT
        Tag result = tagService.updateTag(mockTagId, mockAccountId, mockTagDto);


        // ASSERT
        assertNotNull(result);
        assertEquals("sport", result.getTagName());
        assertEquals("sport description", result.getTagDescription());

        verify(tagRepository, times(1)).findByIdAndAccountId(mockTagId, mockAccountId);
        verify(tagRepository, never()).findByTagNameAndAccountId(mockTagDto.getTagName(), mockAccountId);
        verify(tagRepository, times(1)).save(mockTag);

    }

    @Test
    public void WhenUpdateTagWithTagIdOrAccountIdNotFound_MustReturnTagNotFoundOrNotAuthorizedException() throws Exception {


        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;

        TagDto mockTagDto = new TagDto("sport", "sport description", mockAccountId);

        when(tagRepository.findByIdAndAccountId(mockTagId, mockAccountId)).thenReturn(Optional.empty());

        // ACT & ASSERT

        TagNotFoundOrNotAuthorizedException thrown = assertThrows(
            TagNotFoundOrNotAuthorizedException.class,
            () -> tagService.updateTag(mockTagId, mockAccountId, mockTagDto)
        );

        assertEquals("Tag not found or not authorized.", thrown.getMessage());
        
        verify(tagRepository, times(1)).findByIdAndAccountId(mockTagId, mockAccountId);
        verify(tagRepository, never()).findByTagNameAndAccountId(mockTagDto.getTagName(), mockAccountId);
        verify(tagRepository, never()).save(any(Tag.class));

    }

    @Test
    public void WhenUpdateTagWithSameTagNameAlreadyPersist_MustReturnTagNameAlreadyExistsException() throws Exception {

        
        // ARRANGE
        long mockTagId = 1L;
        long mockAccountId = 1L;

        Account mockAccount = new Account("test@test.com", "hashed_password", "test");
        Tag mockTag = new Tag("old tag", "old description tag", mockAccount); 
        TagDto mockTagDto = new TagDto("service", "service description", mockAccountId);

        when(tagRepository.findByIdAndAccountId(mockTagId, mockAccountId)).thenReturn(Optional.of(mockTag));
        when(tagRepository.findByTagNameAndAccountId(mockTagDto.getTagName(), mockAccountId)).thenReturn(Optional.of(mockTag));


        // ACT & ASSERT

        TagNameAlreadyExistsException thrown = assertThrows(
            TagNameAlreadyExistsException.class,
            () -> tagService.updateTag(mockTagId, mockAccountId, mockTagDto)
        );  

        assertEquals("Tag 'service' already exists in this account.", thrown.getMessage());

        verify(tagRepository, times(1)).findByIdAndAccountId(mockTagId, mockAccountId);
        verify(tagRepository, times(1)).findByTagNameAndAccountId(mockTagDto.getTagName(), mockAccountId);
        verify(tagRepository, never()).save(any(Tag.class));

    }

}
