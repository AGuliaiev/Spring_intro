package org.example.spring_intro.repository;

import org.example.spring_intro.model.Book;

import java.util.List;

public interface BookRepository {
    Book save(Book book);
    List<Book> findAll();
}
