package org.example.springintro.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.example.springintro.dto.book.BookDto;
import org.example.springintro.dto.book.BookDtoWithoutCategoryIds;
import org.example.springintro.dto.book.BookSearchParameters;
import org.example.springintro.dto.book.CreateBookRequestDto;
import org.example.springintro.exception.EntityNotFoundException;
import org.example.springintro.mapper.BookMapper;
import org.example.springintro.model.Book;
import org.example.springintro.model.Category;
import org.example.springintro.repository.book.BookRepository;
import org.example.springintro.repository.book.BookSpecificationBuilder;
import org.example.springintro.repository.categoty.CategoryRepository;
import org.example.springintro.services.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("save() - Given valid CreateBookRequestDto, When saving, Then returns BookDto")
    public void save_ValidCreateBookRequestDto_ReturnsBookDto() {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Test Book");
        requestDto.setAuthor("Test Author");
        requestDto.setIsbn("123-4567890123");
        requestDto.setPrice(BigDecimal.valueOf(100));
        requestDto.setDescription("A test book description");
        requestDto.setCoverImage("test-image-url");
        requestDto.setCategoryIds(List.of(1L, 2L));

        Category categoryFirst = new Category();
        categoryFirst.setId(1L);
        Category categorySecond = new Category();
        categorySecond.setId(2L);

        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());
        book.setCategories(new HashSet<>(Arrays.asList(categoryFirst, categorySecond)));

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategoryIds(List.of(1L, 2L));

        // When
        when(categoryRepository.findAllById(requestDto.getCategoryIds()))
                .thenReturn(List.of(categoryFirst, categorySecond));
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto savedBookDto = bookService.save(requestDto);

        // Then
        assertThat(savedBookDto).isEqualTo(bookDto);
        verify(bookRepository, times(1)).save(book);
        verify(categoryRepository, times(1))
                .findAllById(requestDto.getCategoryIds());
        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, categoryRepository, bookMapper);
    }

    @Test
    @DisplayName("findAll() - Given pageable,"
            + " When finding all books, Then returns list of BookDtos")
    public void findAll_ReturnsListOfBookDtos() {
        // Given
        Pageable pageable = mock(Pageable.class);
        Page<Book> page = mock(Page.class);

        Book book = new Book();
        BookDto bookDto = new BookDto();

        // When
        when(bookRepository.findAll(pageable)).thenReturn(page);
        when(page.stream()).thenReturn(List.of(book).stream());
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.findAll(pageable);

        // Then
        assertThat(result).hasSize(1).contains(bookDto);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    @DisplayName("findById() - Given valid ID, When finding book, Then returns BookDto")
    public void findById_ValidId_ReturnsBookDto() {
        // Given
        Long id = 1L;
        Book book = new Book();
        BookDto bookDto = new BookDto();

        // When
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto result = bookService.findById(id);

        // Then
        assertThat(result).isEqualTo(bookDto);
        verify(bookRepository, times(1)).findById(id);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    @DisplayName("findById() - Given invalid ID,"
            + " When finding book, Then throws EntityNotFoundException")
    public void findById_InvalidId_ThrowsEntityNotFoundException() {
        // Given
        Long id = 1L;

        // When
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> bookService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Book not found by id: " + id);

        verify(bookRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("updateById() - Given valid ID and DTO,"
            + " When updating, Then returns updated BookDto")
    public void updateById_ValidIdAndDto_ReturnsUpdatedBookDto() {
        // Given
        Long id = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Updated Title");
        requestDto.setCategoryIds(List.of(1L));

        Book book = new Book();
        book.setId(id);
        book.setTitle("Old Title");

        Category category = new Category();
        category.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setTitle("Updated Title");

        // When
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(categoryRepository.findAllById(requestDto.getCategoryIds()))
                .thenReturn(List.of(category));
        doNothing().when(bookMapper).updateBookFromDto(requestDto, book);
        when(bookRepository.save(book)).thenReturn(book);
        doReturn(bookDto).when(bookMapper).toDto(book);

        BookDto result = bookService.updateById(id, requestDto);

        // Then
        assertThat(result).isEqualTo(bookDto);
        verify(bookRepository, times(1)).findById(id);
        verify(bookMapper, times(1)).updateBookFromDto(requestDto, book);
        verify(categoryRepository, times(1)).findAllById(requestDto.getCategoryIds());
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    @DisplayName("deleteById() - Given valid ID, When deleting book, Then deletes book")
    public void deleteById_ValidId_DeletesBook() {
        // Given
        Long id = 1L;

        // When
        bookService.deleteById(id);

        // Then
        verify(bookRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("search() - Given valid search params,"
            + " When searching, Then returns list of BookDtos")
    public void search_ValidSearchParams_ReturnsListOfBookDtos() {
        // Given
        String[] titles = {"Test Title 1", "Test Title 2"};
        String[] authors = {"Author 1", "Author 2"};
        BookSearchParameters params = new BookSearchParameters(titles, authors);

        Pageable pageable = PageRequest.of(0, 10);
        Specification<Book> spec = mock(Specification.class);

        Book book = new Book();
        BookDto bookDto = new BookDto();

        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        // When
        when(bookSpecificationBuilder.build(params)).thenReturn(spec);
        when(bookRepository.findAll(spec, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.search(params, pageable);

        // Then
        assertThat(result).contains(bookDto);
        verify(bookSpecificationBuilder, times(1)).build(params);
        verify(bookRepository, times(1)).findAll(spec, pageable);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    @DisplayName("findBooksByCategoryId() - Given valid category ID,"
            + " When finding books, Then returns list of BookDtoWithoutCategoryIds")
    public void findBooksByCategoryId_ValidCategoryId_ReturnsListOfBookDtoWithoutCategoryIds() {
        // Given
        Long categoryId = 1L;
        Book book = new Book();
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();

        // When
        when(bookRepository.findAllByCategories_Id(categoryId)).thenReturn(List.of(book));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryIds);

        List<BookDtoWithoutCategoryIds> result = bookService.findBooksByCategoryId(categoryId);

        // Then
        assertThat(result).contains(bookDtoWithoutCategoryIds);
        verify(bookRepository, times(1)).findAllByCategories_Id(categoryId);
        verify(bookMapper, times(1)).toDtoWithoutCategories(book);
    }
}
