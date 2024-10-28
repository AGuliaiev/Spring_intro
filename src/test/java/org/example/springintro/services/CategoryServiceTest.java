package org.example.springintro.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.example.springintro.dto.category.CategoryDto;
import org.example.springintro.dto.category.CreateCategoryRequestDto;
import org.example.springintro.exception.EntityNotFoundException;
import org.example.springintro.mapper.CategoryMapper;
import org.example.springintro.model.Category;
import org.example.springintro.repository.categoty.CategoryRepository;
import org.example.springintro.services.impl.CategoryServiceImpl;
import org.example.springintro.util.BookTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Given categories exist,"
            + " when findAll is called, then return list of categoryDtos")
    public void findAll_CategoriesExist_ReturnsListOfCategoryDtos() {
        // given
        Category categoryFirst = BookTestUtils.createCategory(1L, "Fiction");
        Category categorySecond = BookTestUtils.createCategory(2L, "Non-Fiction");

        CategoryDto categoryDtoFirst = BookTestUtils.createCategoryDto(1L, "Fiction");
        CategoryDto categoryDtoSecond = BookTestUtils.createCategoryDto(2L, "Non-Fiction");

        when(categoryRepository.findAll()).thenReturn(List.of(categoryFirst, categorySecond));
        when(categoryMapper.toDto(categoryFirst)).thenReturn(categoryDtoFirst);
        when(categoryMapper.toDto(categorySecond)).thenReturn(categoryDtoSecond);

        // when
        List<CategoryDto> result = categoryService.findAll();

        // then
        assertThat(result).containsExactlyInAnyOrder(categoryDtoFirst, categoryDtoSecond);
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toDto(categoryFirst);
        verify(categoryMapper, times(1)).toDto(categorySecond);
    }

    @Test
    @DisplayName("Given category exists,"
            + " when getById is called, then return categoryDto")
    public void getById_CategoryExists_ReturnsCategoryDto() {
        // given
        Long id = 1L;
        Category category = BookTestUtils.createCategory(id, "Fiction");
        CategoryDto categoryDto = BookTestUtils.createCategoryDto(id, "Fiction");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        // when
        CategoryDto result = categoryService.getById(id);

        // then
        assertThat(result).isEqualTo(categoryDto);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("Given category does not exist,"
            + " when getById is called, then throw EntityNotFoundException")
    public void getById_CategoryDoesNotExist_ThrowsEntityNotFoundException() {
        // given
        Long id = 1L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.getById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Category not found by id: " + id);

        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Given a valid categoryDto,"
            + " when save is called, then return saved categoryDto")
    public void save_ValidCategoryDto_ReturnsSavedCategoryDto() {
        // given
        CreateCategoryRequestDto requestDto = BookTestUtils.createCategoryRequestDto(
                "New Category",
                "A new category"
        );
        Category category = BookTestUtils.createCategory(
                1L,
                "New Category",
                "A new category"
        );
        CategoryDto categoryDto = BookTestUtils.createCategoryDto(
                1L,
                "New Category",
                "A new category"
        );

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        // when
        CategoryDto result = categoryService.save(requestDto);

        // then
        assertThat(result).isEqualTo(categoryDto);
        verify(categoryMapper, times(1)).toEntity(requestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("Given a valid categoryId and categoryDto,"
            + " when updateById is called, then return updated categoryDto")
    public void updateById_ValidIdAndDto_ReturnsUpdatedCategoryDto() {
        // given
        Long id = 1L;
        CreateCategoryRequestDto requestDto = BookTestUtils.createCategoryRequestDto(
                "Updated Category",
                "we update category"
        );
        Category existingCategory = BookTestUtils.createCategory(id, "Old Category");
        CategoryDto updatedCategoryDto = BookTestUtils.createCategoryDto(id, "Updated Category");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));

        // Mock the void method to do nothing
        doNothing().when(categoryMapper).updateCategoryFromDto(requestDto, existingCategory);

        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        when(categoryMapper.toDto(existingCategory)).thenReturn(updatedCategoryDto);

        // when
        CategoryDto result = categoryService.updateById(id, requestDto);

        // then
        assertThat(result).isEqualTo(updatedCategoryDto);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1))
                .updateCategoryFromDto(requestDto, existingCategory);
        verify(categoryRepository, times(1)).save(existingCategory);
        verify(categoryMapper, times(1)).toDto(existingCategory);
    }

    @Test
    @DisplayName("Given a valid categoryId, when deleteById is called, then delete the category")
    public void deleteById_ValidId_DeletesCategory() {
        // given
        Long id = 1L;

        // when
        categoryService.deleteById(id);

        // then
        verify(categoryRepository, times(1)).deleteById(id);
    }
}
