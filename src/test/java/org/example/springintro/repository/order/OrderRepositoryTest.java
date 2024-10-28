package org.example.springintro.repository.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.example.springintro.model.Order;
import org.example.springintro.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    private static final Long USER_ID = 1L;
    private static final Long ORDER_ID = 1L;

    private static final String SHIPPING_ADDRESS = "Kiev";
    private static final String TOTAL_ORDER_ONE = "100.00";
    private static final String TOTAL_ORDER_TWO = "400.00";
    private static final String TOTAL_ORDER_THREE = "300.95";
    private static final String USER_EMAIL = "john.doe@example.com";

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Find by User ID")
    @Sql(scripts = {
            "classpath:database/users/add-users.sql",
            "classpath:database/orders/add-orders.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/orders/remove-orders.sql",
            "classpath:database/users/remove-users.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserId() {
        List<Order> orders = orderRepository.findByUserId(USER_ID);
        assertThat(orders).isNotEmpty();
        assertThat(orders.size()).isEqualTo(3);

        Order orderFirst = orders.get(0);
        Order orderSecond = orders.get(1);
        Order orderThird = orders.get(2);

        assertThat(orderFirst.getStatus()).isEqualTo(Status.PENDING);
        assertThat(orderFirst.getShippingAddress()).isEqualTo(SHIPPING_ADDRESS);
        assertThat(orderFirst.getTotal()).isEqualTo(TOTAL_ORDER_ONE);

        assertThat(orderSecond.getStatus()).isEqualTo(Status.COMPLETED);
        assertThat(orderSecond.getShippingAddress()).isEqualTo(SHIPPING_ADDRESS);
        assertThat(orderSecond.getTotal()).isEqualTo(TOTAL_ORDER_TWO);

        assertThat(orderThird.getStatus()).isEqualTo(Status.DELIVERED);
        assertThat(orderThird.getShippingAddress()).isEqualTo(SHIPPING_ADDRESS);
        assertThat(orderThird.getTotal()).isEqualTo(TOTAL_ORDER_THREE);
    }

    @Test
    @DisplayName("Find by Order ID and User Id")
    @Sql(scripts = {
            "classpath:database/users/add-users.sql",
            "classpath:database/orders/add-orders.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/orders/remove-orders.sql",
            "classpath:database/users/remove-users.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdAndUserId() {
        Optional<Order> optionalOrder = orderRepository.findByIdAndUserId(ORDER_ID, USER_ID);
        assertThat(optionalOrder).isPresent();
        Order order = optionalOrder.get();

        assertThat(order.getTotal()).isEqualTo(TOTAL_ORDER_ONE);
        assertThat(order.getUser().getEmail()).isEqualTo(USER_EMAIL);
        assertThat(order.getShippingAddress()).isEqualTo(SHIPPING_ADDRESS);
    }
}
