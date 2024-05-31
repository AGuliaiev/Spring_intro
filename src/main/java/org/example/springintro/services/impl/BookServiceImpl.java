package org.example.springintro.services.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.BookDto;
import org.example.springintro.dto.BookSearchParameters;
import org.example.springintro.dto.CreateBookRequestDto;
import org.example.springintro.exception.EntityNotFoundException;
import org.example.springintro.mapper.BookMapper;
import org.example.springintro.model.Book;
import org.example.springintro.repository.book.BookRepository;
import org.example.springintro.repository.book.BookSpecificationBuilder;
import org.example.springintro.services.BookService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Book not found by id: " + id)
                );
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Book not found by id: " + id)
                );
        bookMapper.updateBookFromDto(requestDto, book);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParameters params) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        return bookRepository.findAll(bookSpecification)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
