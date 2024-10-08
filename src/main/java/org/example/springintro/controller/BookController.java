package org.example.springintro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.book.BookDto;
import org.example.springintro.dto.book.BookDtoWithoutCategoryIds;
import org.example.springintro.dto.book.BookSearchParameters;
import org.example.springintro.dto.book.CreateBookRequestDto;
import org.example.springintro.services.BookService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(
        name = "Book shop",
        description = "Endpoints for Endpoints for managing books in the book shop"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(summary = "Get all books", description = "Get all books from the book shop")
    public List<BookDto> getAll(
            Authentication authentication,
            @ParameterObject @PageableDefault Pageable pageable
    ) {
        String email = authentication.getName();
        return bookService.findAll(pageable);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get book by id", description = "Get book by id from the book shop")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create book", description = "Create book in the book shop")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update book", description = "Update book in the book shop")
    public BookDto updateBook(
            @PathVariable Long id,
            @RequestBody @Valid CreateBookRequestDto requestDto
    ) {
        return bookService.updateById(id, requestDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book", description = "Delete book from the book shop")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search books in the book shop")
    public List<BookDto> searchBooks(
            @ParameterObject @PageableDefault Pageable pageable,
            BookSearchParameters searchParameters
    ) {
        return bookService.search(searchParameters, pageable);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{id}/books")
    @Operation(
            summary = "Get books by category id",
            description = "Get all books associated with a specific category"
    )
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(@PathVariable Long id) {
        List<BookDtoWithoutCategoryIds> books = bookService.findBooksByCategoryId(id);
        if (books.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Category with id " + id + " not found"
            );
        }
        return books;
    }
}
