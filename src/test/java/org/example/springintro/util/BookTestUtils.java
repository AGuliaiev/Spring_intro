package org.example.springintro.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.example.springintro.dto.book.BookDto;
import org.example.springintro.dto.book.BookDtoWithoutCategoryIds;
import org.example.springintro.dto.book.CreateBookRequestDto;
import org.example.springintro.dto.category.CategoryDto;
import org.example.springintro.dto.category.CreateCategoryRequestDto;
import org.example.springintro.model.Book;
import org.example.springintro.model.Category;

public class BookTestUtils {

    public static BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds(
            Long id,
            String title,
            String author,
            String isbn,
            BigDecimal price,
            String description,
            String coverImage
    ) {
        BookDtoWithoutCategoryIds dto = new BookDtoWithoutCategoryIds();
        dto.setId(id);
        dto.setTitle(title);
        dto.setAuthor(author);
        dto.setIsbn(isbn);
        dto.setPrice(price);
        dto.setDescription(description);
        dto.setCoverImage(coverImage);
        return dto;
    }

    public static CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Test Book 1");
        requestDto.setAuthor("Test Author");
        requestDto.setIsbn("767898776678");
        requestDto.setPrice(BigDecimal.valueOf(49.99));
        requestDto.setDescription("Some description");
        requestDto.setCoverImage("Test.jpg");
        requestDto.setCategoryIds(List.of(1L, 2L));
        return requestDto;
    }

    public static Book createBook(CreateBookRequestDto requestDto, Category... categories) {
        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());
        book.setCategories(new HashSet<>(Arrays.asList(categories)));
        return book;
    }

    public static BookDto createBookDto(Book book, List<Long> categoryIds) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPrice(book.getPrice());
        dto.setDescription(book.getDescription());
        dto.setCoverImage(book.getCoverImage());
        dto.setCategoryIds(categoryIds);
        return dto;
    }

    public static BookDto createBookDto(
            Long id,
            String title,
            String author,
            String isbn,
            BigDecimal price,
            String description,
            String coverImage,
            List<Long> categoryIds) {
        BookDto dto = new BookDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setAuthor(author);
        dto.setIsbn(isbn);
        dto.setPrice(price);
        dto.setDescription(description);
        dto.setCoverImage(coverImage);
        dto.setCategoryIds(categoryIds);
        return dto;
    }

    public static CreateCategoryRequestDto createCategoryRequestDto(
            String name,
            String description
    ) {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(name);
        requestDto.setDescription(description);
        return requestDto;
    }

    public static Category createCategory(long id, String name, String description) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    public static Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    public static Category createCategory(long id, String name) {
        return createCategory(id, name, null);
    }

    public static CategoryDto createCategoryDto(long id, String name) {
        return createCategoryDto(id, name, null);
    }

    public static CategoryDto createCategoryDto(long id, String name, String description) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setName(name);
        categoryDto.setDescription(description);
        return categoryDto;
    }
}
