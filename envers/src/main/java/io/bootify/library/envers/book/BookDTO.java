package io.bootify.library.envers.book;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class BookDTO {

    private Long id;

    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String description;

    @NotNull
    private Long author;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Long getAuthor() {
        return author;
    }

    public void setAuthor(final Long author) {
        this.author = author;
    }

}
