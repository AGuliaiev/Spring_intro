package org.example.springintro.mapper;

import org.example.springintro.config.MapperConfig;
import org.example.springintro.dto.book.BookDto;
import org.example.springintro.dto.book.BookDtoWithoutCategoryIds;
import org.example.springintro.dto.book.CreateBookRequestDto;
import org.example.springintro.model.Book;
import org.example.springintro.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateBookFromDto(CreateBookRequestDto book, @MappingTarget Book entity);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        if (book.getCategories() != null && !book.getCategories().isEmpty()) {
            bookDto.setCategoryIds(book.getCategories().stream()
                    .map(Category::getId)
                    .toList());
        }
    }
}
