package io.bootify.test.author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;


public interface AuthorRepository extends JpaRepository<Author, Long>, RevisionRepository<Author, Long, Long> {
}
