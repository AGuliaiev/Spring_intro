package org.example.springintro.services.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.book.BookDtoWithoutCategoryIds;
import org.example.springintro.dto.category.CategoryDto;
import org.example.springintro.exception.EntityNotFoundException;
import org.example.springintro.mapper.BookMapper;
import org.example.springintro.mapper.CategoryMapper;
import org.example.springintro.model.Book;
import org.example.springintro.model.Category;
import org.example.springintro.repository.book.BookRepository;
import org.example.springintro.repository.categoty.CategoryRepository;
import org.example.springintro.services.CategoryService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = findByIdOrThrow(id);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateById(Long id, CategoryDto categoryDto) {
        Category category = findByIdOrThrow(id);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long id) {
        List<Book> books = bookRepository.findAllByCategories_Id(id);
        return books.stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    private Category findByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Category not found by id: " + id)
                );
    }
}
