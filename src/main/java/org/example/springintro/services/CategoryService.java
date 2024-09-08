package org.example.springintro.services;

import java.util.List;
import org.example.springintro.dto.category.CategoryDto;
import org.example.springintro.dto.category.CreateCategoryRequestDto;

public interface CategoryService {
    List<CategoryDto> findAll();

    CategoryDto getById(Long id);

    CategoryDto save(CreateCategoryRequestDto categoryDto);

    CategoryDto updateById(Long id, CreateCategoryRequestDto categoryDto);

    void deleteById(Long id);
}
