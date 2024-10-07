package org.example.springintro.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.example.springintro.dto.book.BookDto;
import org.example.springintro.dto.book.BookDtoWithoutCategoryIds;
import org.example.springintro.dto.book.CreateBookRequestDto;
import org.example.springintro.model.Book;
import org.example.springintro.model.Category;
import org.example.springintro.util.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;

    private static final long EXISTING_ID = 1L;
    private static final long NON_EXISTING_ID = 999L;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
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

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-books-and-categories.sql")
            );
        }
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

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-books-and-categories.sql")
            );
        }
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Create a new Book")
    void createBook_ValidRequestDto_Success() throws Exception {
        // Given
        CreateBookRequestDto requestDto = TestUtils.createBookRequestDto();
        Category categoryFirst = TestUtils.createCategory(1L, "Category First");
        Category categorySecond = TestUtils.createCategory(2L, "Category Second");

        Book book = TestUtils.createBook(requestDto, categoryFirst, categorySecond);
        BookDto expected = TestUtils.createBookDto(book, List.of(1L, 2L));

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
                result.getResponse().getContentAsString(),
                BookDto.class);
        assertEquals(expected.getTitle(), actual.getTitle());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Create a new Book - Invalid Request")
    void createBook_InvalidRequestDto_BadRequest() throws Exception {
        // Given
        CreateBookRequestDto invalidRequestDto = new CreateBookRequestDto();
        invalidRequestDto.setTitle(null);
        invalidRequestDto.setAuthor(null);
        invalidRequestDto.setPrice(null);
        invalidRequestDto.setCoverImage(null);
        invalidRequestDto.setIsbn(null);
        invalidRequestDto.setDescription(null);
        invalidRequestDto.setCategoryIds(null);

        String jsonRequest = objectMapper.writeValueAsString(invalidRequestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Then
        String errorMessage = result.getResponse().getContentAsString();

        assertTrue(errorMessage.contains(
                "must not be blank") || errorMessage.contains("must not be null")
        );
        assertTrue(errorMessage.contains("categoryIds must not be empty"));
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get all books")
    void getAll_GivenBooksInCatalog_ShouldReturnAllBooks() throws Exception {
        // Given
        BookDto bookFirst = TestUtils.createBookDto(
                1L,
                "Book Title 1",
                "Author 1",
                "ISBN1234567890",
                BigDecimal.valueOf(19.99),
                "Description 1",
                "cover1.jpg",
                List.of(1L)
        );
        BookDto bookSecond = TestUtils.createBookDto(
                2L,
                "Book Title 2",
                "Author 2",
                "ISBN0987654321",
                BigDecimal.valueOf(29.99),
                "Description 2",
                "cover2.jpg",
                List.of(1L)
        );
        BookDto bookThird = TestUtils.createBookDto(
                3L,
                "Book Title 3",
                "Author 3",
                "ISBN4546774667",
                BigDecimal.valueOf(39.99),
                "Description 3",
                "cover3.jpg",
                List.of(1L)
        );

        List<BookDto> expected = List.of(bookFirst, bookSecond, bookThird);

        // When
        MvcResult result = mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                BookDto[].class);
        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get book by ID")
    void getBookById_ExistingBook_ReturnsBook() throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                        get("/books/" + EXISTING_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                BookDto.class);
        assertEquals(1L, actual.getId());
        assertEquals("Book Title 1", actual.getTitle());
        assertEquals("Author 1", actual.getAuthor());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get book by ID - Non Existing Book")
    void getBookById_NonExistingBook_ReturnsNotFound() throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                        get("/books/" + NON_EXISTING_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        // Then
        String responseMessage = result.getResponse().getContentAsString();
        assertTrue(responseMessage.contains("Book not found by id: " + NON_EXISTING_ID));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Update a Book")
    void updateBook_ValidRequestDto_Success() throws Exception {
        // Given
        CreateBookRequestDto requestDto = TestUtils.createBookRequestDto();
        requestDto.setTitle("Updated Book Title");
        requestDto.setAuthor("Updated Author");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/books/" + EXISTING_ID)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                BookDto.class
        );
        assertEquals("Updated Book Title", actual.getTitle());
        assertEquals("Updated Author", actual.getAuthor());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Update a Book - Non Existing Book")
    void updateBook_NonExistingBook_ReturnsNotFound() throws Exception {
        // Given
        CreateBookRequestDto requestDto = TestUtils.createBookRequestDto();
        requestDto.setTitle("Updated Book Title");
        requestDto.setAuthor("Updated Author");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/books/" + NON_EXISTING_ID)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        // Then
        String responseMessage = result.getResponse().getContentAsString();
        assertTrue(responseMessage.contains("Book not found by id: " + NON_EXISTING_ID));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Delete a Book")
    void deleteBook_ExistingBook_Success() throws Exception {
        // When
        mockMvc.perform(delete("/books/" + EXISTING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get books by category ID - Existing Category with Books")
    void getBooksByCategoryId_ExistingCategoryWithBooks_ReturnsBooks() throws Exception {
        // Given
        BookDtoWithoutCategoryIds bookFirst = TestUtils.createBookDtoWithoutCategoryIds(
                1L,
                "Book Title 1",
                "Author 1",
                "ISBN1234567890",
                BigDecimal.valueOf(19.99),
                "Description of Book 1",
                "cover1.jpg"
        );
        BookDtoWithoutCategoryIds bookSecond = TestUtils.createBookDtoWithoutCategoryIds(
                2L,
                "Book Title 2",
                "Author 2",
                "ISBN0987654321",
                BigDecimal.valueOf(29.99),
                "Description of Book 2",
                "cover2.jpg"
        );
        BookDtoWithoutCategoryIds bookThird = TestUtils.createBookDtoWithoutCategoryIds(
                3L,
                "Book Title 3",
                "Author 3",
                "ISBN4546774667",
                BigDecimal.valueOf(39.99),
                "Description of Book 3",
                "cover3.jpg"
        );

        // When
        MvcResult result = mockMvc.perform(get("/books/" + EXISTING_ID + "/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        List<BookDtoWithoutCategoryIds> actualBooks = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), new TypeReference<>() {});

        assertEquals(3, actualBooks.size(), "Expected book size does not match.");
        assertEquals("Book Title 1", actualBooks.get(0).getTitle());
        assertEquals("Book Title 2", actualBooks.get(1).getTitle());
        assertEquals("Book Title 3", actualBooks.get(2).getTitle());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get books by category ID - Non Existing Category")
    void getBooksByCategoryId_NonExistingCategory_ReturnsNotFound() throws Exception {
        // When
        MvcResult result = mockMvc.perform(get("/books/" + NON_EXISTING_ID + "/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        // Then
        assertNotNull(result.getResolvedException());
        String expectedMessage = "Category with id " + NON_EXISTING_ID + " not found";
        assertTrue(result.getResolvedException().getMessage().contains(expectedMessage));
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
        assertEquals(1, actual.length);
        assertEquals("Book Title 1", actual[0].getTitle());
        assertEquals("Author 1", actual[0].getAuthor());
    }
}
