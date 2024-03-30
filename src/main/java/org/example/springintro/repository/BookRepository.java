package org.example.springintro.repository;

import java.util.List;
import org.example.springintro.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
