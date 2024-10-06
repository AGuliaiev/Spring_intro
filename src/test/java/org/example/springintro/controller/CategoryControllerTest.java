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

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.sql.Connection;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.example.springintro.dto.category.CategoryDto;
import org.example.springintro.dto.category.CreateCategoryRequestDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
@Transactional
class CategoryControllerTest {
    protected static MockMvc mockMvc;

    private static final long EXISTING_ID = 1L;
    private static final long NON_EXISTING_ID = 999L;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource, @Autowired WebApplicationContext applicationContext
    ) throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        setupData(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardownData(dataSource);
    }

    @SneakyThrows
    static void setupData(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/add-categories.sql")
            );
        }
    }

    @SneakyThrows
    static void teardownData(DataSource dataSource) {
        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/remove-categories.sql")
            );
        }
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Create a new Category")
    void createCategory_ValidRequestDto_Success() throws Exception {
        // Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("New Category");
        requestDto.setDescription("Category description");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );
        assertNotNull(actual.getId());
        assertEquals(requestDto.getName(), actual.getName());
        assertEquals(requestDto.getDescription(), actual.getDescription());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Create Category - Invalid Request")
    void createCategory_InvalidRequest_BadRequest() throws Exception {
        // Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("");
        requestDto.setDescription("Category description");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Then
        String errorResponse = result.getResponse().getContentAsString();
        assertNotNull(errorResponse);
        assertTrue(errorResponse.contains("name must not be blank"));
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get all Categories")
    void getAll_ShouldReturnAllCategories() throws Exception {
        // When
        MvcResult result = mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto[].class
        );
        assertTrue(actual.length > 0);
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get Category by ID")
    void getCategoryById_ExistingCategory_ReturnsCategory() throws Exception {
        // When
        MvcResult result = mockMvc.perform(get("/categories/" + EXISTING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto.class
        );
        assertEquals(1L, actual.getId());
        assertEquals("Fiction", actual.getName());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @DisplayName("Get Category by ID - Non-existing Category")
    void getCategoryById_NonExistingCategory_ReturnsNotFound() throws Exception {
        // When
        MvcResult result = mockMvc.perform(get("/categories/" + NON_EXISTING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        // Then
        String errorMessage = result.getResponse().getContentAsString();
        assertNotNull(errorMessage);
        assertTrue(errorMessage.contains("Category not found"));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Update a Category")
    void updateCategory_ValidRequestDto_Success() throws Exception {
        // Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Updated Category Name");
        requestDto.setDescription("Updated description");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/categories/" + EXISTING_ID)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto.class
        );
        assertEquals("Updated Category Name", actual.getName());
        assertEquals("Updated description", actual.getDescription());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Update Category - Invalid Request")
    void updateCategory_InvalidRequest_BadRequest() throws Exception {
        // Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("");
        requestDto.setDescription("Updated description");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/categories/" + EXISTING_ID)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Then
        String errorResponse = result.getResponse().getContentAsString();
        assertNotNull(errorResponse);
        assertTrue(errorResponse.contains("name must not be blank"));
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Delete a Category")
    void deleteCategory_ExistingCategory_Success() throws Exception {
        // When
        mockMvc.perform(delete("/categories/" + EXISTING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
