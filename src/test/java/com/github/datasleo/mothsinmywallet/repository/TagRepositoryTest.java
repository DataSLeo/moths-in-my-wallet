package com.github.datasleo.mothsinmywallet.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.github.datasleo.mothsinmywallet.model.Account;
import com.github.datasleo.mothsinmywallet.model.Tag;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;


@DataJpaTest
@Transactional
public class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EntityManager entityManager;


// ------------------------------------ findByTagNameAndAccountId ------------------------------------ 

    @Test
    public void WhenFindByTagNameAndAccountId_MustReturnTag() throws Exception {

        Account account = new Account("test@test.com", "hashed_password", "test_");

        entityManager.persist(account);

        Tag tag = new Tag("service", "service description", account);

        entityManager.persist(tag);

        Optional<Tag> optionalTag = tagRepository.findByTagNameAndAccountId(tag.getTagName(), account.getId());

        assertThat(optionalTag).isPresent();
        assertThat(optionalTag.get().getTagName()).isEqualTo("service");
        assertThat(optionalTag.get().getTagDescription()).isEqualTo("service description");
        assertThat(optionalTag.get().getId()).isGreaterThan(0L);

    }

    @Test
    public void WhenFindByTagNameAndAccountIdButTagNameDoesntExists_MustReturnEmptyOptinal() throws Exception {

        Account account = new Account("test@test.com", "hashed_password", "test_");

        entityManager.persist(account);

        Tag tag = new Tag("service", "service description", account);

        entityManager.persist(tag);

        Optional<Tag> optionalTag = tagRepository.findByTagNameAndAccountId("finnancial", account.getId());
        
        assertThat(optionalTag).isNotPresent();

    }

    @Test
    public void WhenFindByTagNameAndAccountIdButAccountIdDoenstExists_MustReturnEmptyOptional() throws Exception {

        Account account = new Account("test@test.com", "hashed_password", "test_");

        entityManager.persist(account);

        Tag tag = new Tag("service", "service description", account);

        entityManager.persist(tag);

        Optional<Tag> optionalTag = tagRepository.findByTagNameAndAccountId(tag.getTagName(), 2L);
        
        assertThat(optionalTag).isNotPresent();

    }

    @Test
    public void WhenFindByTagNameAndAccountIdButTagNameIsNotFromThatAccountId_MustReturnEmptyOptional() throws Exception {

        Account account1 = new Account("test@test.com", "hashed_password", "test_");
        Account account2 = new Account("fooandbar@fooandbar.com", "hashed_password", "fooandbar");

        entityManager.persist(account1);

        entityManager.persist(account2);

        Tag tag = new Tag("service", "service description", account1);

        entityManager.persist(tag);

        Optional<Tag> optionalTag = tagRepository.findByTagNameAndAccountId(tag.getTagName(), account2.getId());
        
        assertThat(optionalTag).isNotPresent();

    }


// ------------------------------------ findByIdAndAccountId ------------------------------------ 

    @Test
    public void WhenFindByIdAndAccountId_MustReturnTag() throws Exception {

        Account account = new Account("test@test.com", "hashed_password", "test_");

        entityManager.persist(account);

        Tag tag = new Tag("service", "service description", account);

        entityManager.persist(tag);

        Optional<Tag> optionalTag = tagRepository.findByIdAndAccountId(tag.getId(), account.getId());

        assertThat(optionalTag).isPresent();
        assertThat(optionalTag.get().getId()).isGreaterThan(0L);
        assertThat(optionalTag.get().getTagName()).isEqualTo("service");
        assertThat(optionalTag.get().getTagDescription()).isEqualTo("service description");

    }

    @Test
    public void WhenFindByIdAndAccountIdButTagIdDoesntExists_MustReturnEmptyOptinal() throws Exception {

        Account account = new Account("test@test.com", "hashed_password", "test_");

        entityManager.persist(account);

        Tag tag = new Tag("service", "service description", account);

        entityManager.persist(tag);

        Optional<Tag> optionalTag = tagRepository.findByIdAndAccountId(2L, account.getId());

        assertThat(optionalTag).isNotPresent();

    } 

    @Test
    public void WhenFindByIdAndAccountIdButAccountIdDoenstExists_MustReturnEmptyOptional() throws Exception {

        Account account = new Account("test@test.com", "hashed_password", "test_");

        entityManager.persist(account);

        Tag tag = new Tag("service", "service description", account);

        entityManager.persist(tag);

        Optional<Tag> optionalTag = tagRepository.findByIdAndAccountId(tag.getId(), 3L);

        assertThat(optionalTag).isNotPresent();

    }

    @Test
    public void WhenFindByIdAndAccountIdButTagNameIsNotFromThatAccountId_MustReturnEmptyOptional() throws Exception {

        Account account1 = new Account("test@test.com", "hashed_password", "test_");
        Account account2 = new Account("fooandbar@fooandbar.com", "hashed_password", "fooandbar");

        entityManager.persist(account1);

        entityManager.persist(account2);


        Tag tag = new Tag("service", "service description", account1);

        entityManager.persist(tag);

        Optional<Tag> optionalTag = tagRepository.findByIdAndAccountId(tag.getId(), account2.getId());

        assertThat(optionalTag).isNotPresent();

    }

// ------------------------------------ findAllByAccountId ------------------------------------ 

    @Test
    public void WhenFindAllByAccountId_MustReturnTags() throws Exception {

        Account account = new Account("test@test.com", "hashed_password", "test_");

        entityManager.persist(account);

        Tag tag1 = new Tag("service", "service description", account);
        Tag tag2 = new Tag("finnancial", "finnancial description", account);
        Tag tag3 = new Tag("food", "food description", account);

        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.persist(tag3);

        List<Tag> listTag = tagRepository.findAllByAccountId(account.getId());


        assertThat(listTag).isNotEmpty();
        assertThat(listTag.size()).isEqualTo(3);

    }

    @Test
    public void WhenFindAllByAccountIdButAccountIdDoesntExists_MustReturnEmptyList() throws Exception {

        Account account = new Account("test@test.com", "hashed_password", "test_");

        entityManager.persist(account);

        Tag tag1 = new Tag("service", "service description", account);
        Tag tag2 = new Tag("finnancial", "finnancial description", account);
        Tag tag3 = new Tag("food", "food description", account);

        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.persist(tag3);

        List<Tag> listTag = tagRepository.findAllByAccountId(2L);

        assertThat(listTag).isEmpty();

    }

    @Test
    public void WhenFindAllByAccountIdButTagsAreNotFromThatAccountId_MustReturnEmptyList() throws Exception {

        Account account1 = new Account("test@test.com", "hashed_password", "test_");
        Account account2 = new Account("fooandbar@fooandbar.com", "hashed_password", "fooandbar");

        entityManager.persist(account1);

        entityManager.persist(account2);

        Tag tag1 = new Tag("service", "service description", account1);
        Tag tag2 = new Tag("finnancial", "finnancial description", account1);
        Tag tag3 = new Tag("food", "food description", account1);

        entityManager.persist(tag1);
        entityManager.persist(tag2);
        entityManager.persist(tag3);

        List<Tag> listTag = tagRepository.findAllByAccountId(account2.getId());

        assertThat(listTag).isEmpty();

    }

}