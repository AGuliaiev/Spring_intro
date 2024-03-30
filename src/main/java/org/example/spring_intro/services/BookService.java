package org.example.spring_intro.services;

import org.example.spring_intro.model.Book;

import java.util.List;

public interface BookService {
    Book save(Book book);
    List<Book> findAll();
}
