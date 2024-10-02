package org.example.springintro.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.example.springintro.dto.book.BookDto;
import org.example.springintro.dto.book.CreateBookRequestDto;
import org.example.springintro.model.Book;
import org.example.springintro.model.Category;
import org.example.springintro.services.BookService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class BookControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-books-and-categories.sql")
            );
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/remove-books-and-categories.sql")
            );
        }
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Create a new Book")
    void createBook_ValidRequestDto_Success() throws Exception {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Test Book 1");
        requestDto.setAuthor("Test Author");
        requestDto.setIsbn("767898776678");
        requestDto.setPrice(BigDecimal.valueOf(49.99));
        requestDto.setDescription("Some description");
        requestDto.setCoverImage("Test.jpg");
        requestDto.setCategoryIds(List.of(1L, 2L));

        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category2.setId(2L);

        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());
        book.setCategories(new HashSet<>(Arrays.asList(category1, category2)));

        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle(book.getTitle());
        expected.setAuthor(book.getAuthor());
        expected.setIsbn(book.getIsbn());
        expected.setPrice(book.getPrice());
        expected.setDescription(book.getDescription());
        expected.setCoverImage(book.getCoverImage());
        expected.setCategoryIds(List.of(1L, 2L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get all books")
    void getAll_GivenBooksInCatalog_ShouldReturnAllBooks() throws Exception {
        // Given
        BookDto book1 = new BookDto();
        book1.setId(1L);
        book1.setTitle("Book Title 1");
        book1.setAuthor("Author 1");
        book1.setIsbn("ISBN1234567890");
        book1.setPrice(BigDecimal.valueOf(19.99));
        book1.setDescription("Description 1");
        book1.setCoverImage("cover1.jpg");
        book1.setCategoryIds(List.of(1L));

        BookDto book2 = new BookDto();
        book2.setId(2L);
        book2.setTitle("Book Title 2");
        book2.setAuthor("Author 2");
        book2.setIsbn("ISBN0987654321");
        book2.setPrice(BigDecimal.valueOf(29.99));
        book2.setDescription("Description 2");
        book2.setCoverImage("cover2.jpg");
        book2.setCategoryIds(List.of(1L));

        BookDto book3 = new BookDto();
        book3.setId(3L);
        book3.setTitle("Book Title 3");
        book3.setAuthor("Author 3");
        book3.setIsbn("ISBN4546774667");
        book3.setPrice(BigDecimal.valueOf(39.99));
        book3.setDescription("Description 3");
        book3.setCoverImage("cover3.jpg");
        book3.setCategoryIds(List.of(1L));

        List<BookDto> expected = new ArrayList<>();
        expected.add(book1);
        expected.add(book2);
        expected.add(book3);

        // When
        MvcResult result = mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class
        );
        Assertions.assertEquals(3, actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get book by ID")
    void getBookById_ExistingBook_ReturnsBook() throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                get("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto.class
        );
        Assertions.assertEquals(1L, actual.getId());
        Assertions.assertEquals("Book Title 1", actual.getTitle());
        Assertions.assertEquals("Author 1", actual.getAuthor());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Update a Book")
    void updateBook_ValidRequestDto_Success() throws Exception {
        // Given
        long bookId = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Updated Book Title");
        requestDto.setAuthor("Updated Author");
        requestDto.setIsbn("Updated ISBN");
        requestDto.setPrice(BigDecimal.valueOf(59.99));
        requestDto.setDescription("Updated description");
        requestDto.setCoverImage("Updated.jpg");
        requestDto.setCategoryIds(List.of(1L, 2L));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        // When
        MvcResult result = mockMvc.perform(
                        put("/books/" + bookId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto.class
        );
        Assertions.assertEquals("Updated Book Title", actual.getTitle());
        Assertions.assertEquals("Updated Author", actual.getAuthor());
        Assertions.assertEquals(BigDecimal.valueOf(59.99), actual.getPrice());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Delete a Book")
    void deleteBook_ExistingBook_Success() throws Exception {
        // When
        mockMvc.perform(delete("/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Search books")
    void searchBooks_ValidParameters_ReturnsBooks() throws Exception {
        // When
        MvcResult result = mockMvc.perform(get("/books/search?title=Book Title 1&author=Author 1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class
        );
        Assertions.assertEquals(1, actual.length);
        Assertions.assertEquals("Book Title 1", actual[0].getTitle());
        Assertions.assertEquals("Author 1", actual[0].getAuthor());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get books by category ID - Non Existing Category")
    void getBooksByCategoryId_NonExistingCategory_ReturnsNotFound() throws Exception {
        // Given
        long nonExistingCategoryId = 999L;

        when(bookService.findBooksByCategoryId(
                nonExistingCategoryId)).thenReturn(Collections.emptyList()
        );

        // When
        MvcResult result = mockMvc.perform(get("/books/" + nonExistingCategoryId + "/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        // Then
        Assertions.assertNotNull(result.getResolvedException());
        String expectedMessage = "Category with id " + nonExistingCategoryId + " not found";
        Assertions.assertTrue(result.getResolvedException().getMessage().contains(expectedMessage));
    }
}
