package org.example.springintro.repository;

import java.util.List;
import java.util.Optional;
import org.example.springintro.model.Book;

public interface BookRepository {
    Book save(Book book);

    Optional<Book> findById(Long id);

    List<Book> findAll();
}
