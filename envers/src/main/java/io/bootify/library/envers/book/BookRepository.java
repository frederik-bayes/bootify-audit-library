package io.bootify.library.envers.book;

import io.bootify.library.envers.author.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;


public interface BookRepository extends JpaRepository<Book, Long>, RevisionRepository<Book, Long, Long> {

    Book findFirstByAuthor(Author author);

}
