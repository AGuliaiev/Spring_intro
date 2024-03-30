package org.example.springintro.services;

import java.util.List;
import org.example.springintro.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
