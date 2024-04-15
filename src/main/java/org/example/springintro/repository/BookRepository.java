package org.example.springintro.repository;

import java.util.List;
import java.util.Optional;
import org.example.springintro.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book save(Book book);

    Optional<Book> findById(Long id);

    @Query("SELECT b FROM Book b WHERE b.isDelete = false")
    List<Book> findAll();

    void deleteById(Long id);
}
