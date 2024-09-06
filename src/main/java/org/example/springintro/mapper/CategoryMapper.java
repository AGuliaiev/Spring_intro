package org.example.springintro.mapper;

import org.example.springintro.config.MapperConfig;
import org.example.springintro.dto.category.CategoryDto;
import org.example.springintro.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);
}
