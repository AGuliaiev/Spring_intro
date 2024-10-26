package org.example.springintro.repository.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.example.springintro.model.CartItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartItemRepositoryTest {

    private static final Long CART_ITEM_ID = 1L;
    private static final Long SHOPPING_CART_ID = 1L;
    private static final int EXPECTED_QUANTITY = 2;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Find CartItem by ID and ShoppingCart ID")
    @Sql(scripts = {
            "classpath:database/users/add-users.sql",
            "classpath:database/books/add-books-and-categories.sql",
            "classpath:database/shoppingcart/add-shopping-cart.sql",
            "classpath:database/cartitems/add-cart-items.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/cartitems/remove-cart-items.sql",
            "classpath:database/shoppingcart/remove-shopping-cart.sql",
            "classpath:database/books/remove-books-and-categories.sql",
            "classpath:database/users/remove-users.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdAndShoppingCartId_ReturnsCartItem() {
        Optional<CartItem> cartItemOptional = cartItemRepository
                .findByIdAndShoppingCartId(CART_ITEM_ID, SHOPPING_CART_ID);
        assertThat(cartItemOptional).isPresent();

        CartItem cartItem = cartItemOptional.get();
        assertThat(cartItem.getQuantity()).isEqualTo(EXPECTED_QUANTITY);
    }
}
