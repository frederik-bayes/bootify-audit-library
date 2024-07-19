package io.bootify.test.book;

import io.bootify.test.author.Author;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {

    Book findFirstByAuthor(Author author);

}
