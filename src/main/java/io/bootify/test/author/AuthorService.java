package io.bootify.test.author;

import io.bootify.test.book.Book;
import io.bootify.test.book.BookRepository;
import io.bootify.test.util.NotFoundException;
import io.bootify.test.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorService(final AuthorRepository authorRepository,
            final BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public List<AuthorDTO> findAll() {
        final List<Author> authors = authorRepository.findAll(Sort.by("id"));
        return authors.stream()
                .map(author -> mapToDTO(author, new AuthorDTO()))
                .toList();
    }

    public AuthorDTO get(final Long id) {
        return authorRepository.findById(id)
                .map(author -> mapToDTO(author, new AuthorDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AuthorDTO authorDTO) {
        final Author author = new Author();
        mapToEntity(authorDTO, author);
        return authorRepository.save(author).getId();
    }

    public void update(final Long id, final AuthorDTO authorDTO) {
        final Author author = authorRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(authorDTO, author);
        authorRepository.save(author);
    }

    public void delete(final Long id) {
        authorRepository.deleteById(id);
    }

    private AuthorDTO mapToDTO(final Author author, final AuthorDTO authorDTO) {
        authorDTO.setId(author.getId());
        authorDTO.setFirstName(author.getFirstName());
        authorDTO.setLastName(author.getLastName());
        return authorDTO;
    }

    private Author mapToEntity(final AuthorDTO authorDTO, final Author author) {
        author.setFirstName(authorDTO.getFirstName());
        author.setLastName(authorDTO.getLastName());
        return author;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Author author = authorRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Book authorBook = bookRepository.findFirstByAuthor(author);
        if (authorBook != null) {
            referencedWarning.setKey("author.book.author.referenced");
            referencedWarning.addParam(authorBook.getId());
            return referencedWarning;
        }
        return null;
    }

}
