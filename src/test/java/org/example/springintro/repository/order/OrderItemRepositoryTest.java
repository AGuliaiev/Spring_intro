package org.example.springintro.repository.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import org.example.springintro.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderItemRepositoryTest {

    private static final Long USER_ID = 1L;
    private static final Long ORDER_ITEM_ID = 1L;
    private static final Long ORDER_ID = 1L;
    private static final int EXPECTED_QUANTITY = 3;
    private static final BigDecimal EXPECTED_PRICE = new BigDecimal("19.99");
    private static final String EXPECTED_BOOK_TITLE = "Book Title 1";

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("Find OrderItem by ID, Order ID, and User ID")
    @Sql(scripts = {
            "classpath:database/users/add-users.sql",
            "classpath:database/books/add-books-and-categories.sql",
            "classpath:database/orders/add-orders.sql",
            "classpath:database/orderitems/add-orders-items.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/orderitems/remove-orders-items.sql",
            "classpath:database/orders/remove-orders.sql",
            "classpath:database/books/remove-books-and-categories.sql",
            "classpath:database/users/remove-users.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdAndOrderIdAndUserId() {
        Optional<OrderItem> optionalOrderItem = orderItemRepository
                .findByIdAndOrderIdAndUserId(ORDER_ITEM_ID, ORDER_ID, USER_ID);
        assertThat(optionalOrderItem).isPresent();

        OrderItem orderItem = optionalOrderItem.get();
        assertThat(orderItem.getQuantity()).isEqualTo(EXPECTED_QUANTITY);
        assertThat(orderItem.getPrice()).isEqualTo(EXPECTED_PRICE);
        assertThat(orderItem.getBook().getTitle()).isEqualTo(EXPECTED_BOOK_TITLE);
    }
}
