package io.bootify.library.envers.book;

import io.bootify.library.envers.author.Author;
import io.bootify.library.envers.author.AuthorRepository;
import io.bootify.library.envers.revision.RevisionDTO;
import io.bootify.library.envers.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(final BookRepository bookRepository,
            final AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public List<BookDTO> findAll() {
        final List<Book> books = bookRepository.findAll(Sort.by("id"));
        return books.stream()
                .map(book -> mapToDTO(book, new BookDTO()))
                .toList();
    }

    public BookDTO get(final Long id) {
        return bookRepository.findById(id)
                .map(book -> mapToDTO(book, new BookDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final BookDTO bookDTO) {
        final Book book = new Book();
        mapToEntity(bookDTO, book);
        return bookRepository.save(book).getId();
    }

    public void update(final Long id, final BookDTO bookDTO) {
        final Book book = bookRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(bookDTO, book);
        bookRepository.save(book);
    }

    public void delete(final Long id) {
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<RevisionDTO<BookDTO>> findRevisions(final Long id) {
        return bookRepository
                .findRevisions(id)
                .reverse()
                .map(it -> mapToDTO(it, new RevisionDTO<>()))
                .toList();
    }

    private RevisionDTO<BookDTO> mapToDTO(final Revision<Long, Book> revision, final RevisionDTO<BookDTO> revisionDTO) {
        Book entity = revision.getEntity();
        revisionDTO.setModifiedAt(entity.getLastModifiedAt());
        revisionDTO.setModifiedBy(entity.getLastModifiedBy());
        revisionDTO.setValue(mapToDTO(entity, new BookDTO()));
        return revisionDTO;
    }

    private BookDTO mapToDTO(final Book book, final BookDTO bookDTO) {
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setDescription(book.getDescription());
        bookDTO.setAuthor(book.getAuthor() == null ? null : book.getAuthor().getId());
        return bookDTO;
    }

    private Book mapToEntity(final BookDTO bookDTO, final Book book) {
        book.setTitle(bookDTO.getTitle());
        book.setDescription(bookDTO.getDescription());
        final Author author = bookDTO.getAuthor() == null ? null : authorRepository.findById(bookDTO.getAuthor())
                .orElseThrow(() -> new NotFoundException("author not found"));
        book.setAuthor(author);
        return book;
    }

}
