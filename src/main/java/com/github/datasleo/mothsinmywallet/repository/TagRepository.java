package com.github.datasleo.mothsinmywallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.datasleo.mothsinmywallet.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>{
    Optional<Tag> findByTagNameAndAccountId (String tag, Long accountId);
    Optional<Tag> findByIdAndAccountId (long id, long accountId);

    List<Tag> findAllByAccountId (Long accountId);
}
