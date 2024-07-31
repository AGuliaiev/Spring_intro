package org.example.springintro.services;

import java.util.List;
import org.example.springintro.dto.BookDto;
import org.example.springintro.dto.BookSearchParameters;
import org.example.springintro.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();
  
    BookDto findById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParameters params);
}
