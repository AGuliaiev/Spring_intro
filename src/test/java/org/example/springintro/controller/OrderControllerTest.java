package org.example.springintro.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.springintro.dto.order.OrderDto;
import org.example.springintro.dto.order.OrderItemDto;
import org.example.springintro.dto.order.OrderRequestDto;
import org.example.springintro.dto.order.OrderStatusUpdateDto;
import org.example.springintro.model.Status;
import org.example.springintro.model.User;
import org.example.springintro.services.OrderService;
import org.example.springintro.util.OrderTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    protected static MockMvc mockMvc;

    private static final long ORDER_ID = 1L;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

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
        teardownDatabase(dataSource);
        setupDatabase(dataSource);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardownDatabase(dataSource);
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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shoppingcart/add-shopping-cart.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-books-and-categories.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cartitems/add-cart-items.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/orders/add-orders.sql")
            );
        }
    }

    @SneakyThrows
    static void teardownDatabase(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,

                    new ClassPathResource("database/orders/remove-orders.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,

                    new ClassPathResource("database/cartitems/remove-cart-items.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,

                    new ClassPathResource("database/books/remove-books-and-categories.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,

                    new ClassPathResource("database/shoppingcart/remove-shopping-cart.sql")
            );
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

    @Test
    @WithUserDetails("john.doe@example.com")
    @DisplayName("Place an order successfully")
    void placeOrder_Success() throws Exception {
        OrderRequestDto requestDto = OrderTestUtils.createOrderRequestDto("Kiev");

        OrderDto expected = OrderTestUtils.createOrderDto(
                1L,
                1L,
                new BigDecimal("100.00"),
                Status.PENDING
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        when(orderService.placeOrder(any(User.class), any(OrderRequestDto.class)))
                .thenReturn(expected);
        MvcResult result = mockMvc.perform(post("/orders")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        OrderRequestDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                OrderRequestDto.class
        );
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithUserDetails("john.doe@example.com")
    @DisplayName("Retrieve order history successfully")
    void getUserOrders_Success() throws Exception {
        OrderItemDto itemDto = OrderTestUtils.createOrderItemDto(
                1L,
                1L,
                5
        );

        OrderDto order = OrderTestUtils.createOrderDto(
                1L,
                1L,
                new BigDecimal(100),
                Status.PENDING
        );
        order.setOrderItems(List.of(itemDto));

        List<OrderDto> expected = List.of(order);

        String jsonRequest = objectMapper.writeValueAsString(expected);

        MvcResult result = mockMvc.perform(get("/orders")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<OrderDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                new TypeReference<List<OrderDto>>() {}
        );

        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithUserDetails("john.doe@example.com")
    @DisplayName("Retrieve all items in a specific order successfully")
    void getOrderItems_Success() throws Exception {

        OrderItemDto itemDto = OrderTestUtils.createOrderItemDto(
                1L,
                1L,
                4
        );

        List<OrderItemDto> expected = List.of(itemDto);

        String jsonRequest = objectMapper.writeValueAsString(expected);

        MvcResult result = mockMvc.perform(get("/orders/{orderId}/items", ORDER_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<OrderItemDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                new TypeReference<List<OrderItemDto>>() {}
        );

        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithUserDetails("john.doe@example.com")
    @DisplayName("Retrieve a specific item in an order successfully")
    void getOrderItem_Success() throws Exception {
        long itemId = 1L;

        OrderItemDto expected = OrderTestUtils.createOrderItemDto(
                itemId,
                1L,
                4
        );

        String jsonRequest = objectMapper.writeValueAsString(expected);

        when(orderService.getOrderItemByOrderIdAndItemId(eq(ORDER_ID), eq(itemId), anyLong()))
                .thenReturn(expected);

        MvcResult result = mockMvc.perform(get(
                "/orders/{orderId}/items/{itemId}",
                        ORDER_ID, itemId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        OrderItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                OrderItemDto.class
        );

        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithUserDetails("jane.doe@example.com")
    @DisplayName("Update order status successfully")
    void updateOrderStatus_Success() throws Exception {
        OrderStatusUpdateDto orderUpdateDto = new OrderStatusUpdateDto();
        orderUpdateDto.setStatus(Status.PENDING);

        OrderDto expected = OrderTestUtils.createOrderDto(
                ORDER_ID,
                1L,
                new BigDecimal("100.00"),
                Status.COMPLETED
        );

        when(orderService.updateOrderStatus(any(Long.class), any(OrderStatusUpdateDto.class)))
                .thenReturn(expected);

        MvcResult result = mockMvc.perform(patch("/orders/{orderId}", ORDER_ID)
                        .content(objectMapper.writeValueAsString(orderUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        OrderStatusUpdateDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                OrderStatusUpdateDto.class
        );

        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }
}
