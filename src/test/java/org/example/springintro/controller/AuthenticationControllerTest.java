package org.example.springintro.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.example.springintro.dto.user.UserLoginRequestDto;
import org.example.springintro.dto.user.UserLoginResponseDto;
import org.example.springintro.dto.user.UserRegistrationRequestDto;
import org.example.springintro.dto.user.UserResponseDto;
import org.example.springintro.util.UserTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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
class AuthenticationControllerTest {
    protected static MockMvc mockMvc;

    private static final String EXISTING_EMAIL = "john.doe@example.com";
    private static final String VALID_PASSWORD = "uekbyb32";
    private static final String INVALID_PASSWORD = "wrongpassword";
    private static final String NEW_EMAIL = "new.user@example.com";
    private static final String DUPLICATE_EMAIL = EXISTING_EMAIL;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationController authenticationController;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        setupDatabase(dataSource);
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        setupDatabase(dataSource);
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void setupDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/roles/add-roles.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/add-users.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/add-users-roles.sql")
            );
        }
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,

                    new ClassPathResource("database/users/remove-users-roles.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,

                    new ClassPathResource("database/users/remove-users.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,

                    new ClassPathResource("database/roles/remove-roles.sql")
            );
        }
    }

    @WithMockUser(username = EXISTING_EMAIL, authorities = {"USER"})
    @Test
    @DisplayName("Login with valid credentials returns token")
    void loginValidCredentialsReturnsToken() throws Exception {
        // Given
        UserLoginRequestDto loginRequest = UserTestUtils.createLoginRequest(VALID_PASSWORD);

        // When
        MvcResult result = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        UserLoginResponseDto response = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                UserLoginResponseDto.class);

        assertNotNull(response.token(), "JWT token should be returned");
        assertFalse(response.token().isEmpty(), "JWT token should not be empty");
    }

    @Test
    @DisplayName("Register user successfully")
    void registerValidRequestDtoReturnsUserResponse() throws Exception {
        // Given
        UserRegistrationRequestDto registrationRequest = UserTestUtils.createRegistrationRequest(
                NEW_EMAIL,
                "New",
                "User",
                "Zabolotnogo 13"
        );

        // When
        MvcResult result = mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Then
        UserResponseDto actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                UserResponseDto.class);

        assertEquals(NEW_EMAIL, actualResponse.getEmail());
    }

    @Test
    @DisplayName("Register user - Invalid Email")
    void registerInvalidEmailReturnsBadRequest() throws Exception {
        // Given
        UserRegistrationRequestDto registrationRequest = UserTestUtils.createRegistrationRequest(
                "invalid-email",
                "Invalid",
                "User",
                null
        );

        // When
        MvcResult result = mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Then
        String errorMessage = result.getResponse().getContentAsString();
        assertTrue(errorMessage.contains("must be a well-formed email address"));
    }

    @Test
    @DisplayName("Login user - Invalid Credentials")
    void loginInvalidCredentialsReturnsUnauthorized() throws Exception {
        // Given
        UserLoginRequestDto loginRequest = UserTestUtils.createLoginRequest(INVALID_PASSWORD);

        // When
        MvcResult result = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Then
        String errorMessage = result.getResponse().getContentAsString();
        assertTrue(errorMessage.isBlank());
    }

    @Test
    @DisplayName("Register user - Duplicate Email")
    void registerDuplicateEmailReturnsConflict() throws Exception {
        // Given
        UserRegistrationRequestDto registrationRequest = UserTestUtils.createRegistrationRequest(
                DUPLICATE_EMAIL,
                "John",
                "Doe",
                null
        );

        // When
        MvcResult result = mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isConflict())
                .andReturn();

        // Then
        String errorMessage = result.getResponse().getContentAsString();
        System.out.println(errorMessage);
        assertTrue(errorMessage.contains("Email already in use"));
    }
}
