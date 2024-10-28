package org.example.springintro.repository.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.springintro.model.ShoppingCart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Find ShoppingCart by User ID with Cart Items and Books")
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
    void findByUserId_ReturnsShoppingCartWithItemsAndBooks() {
        ShoppingCart cart = shoppingCartRepository.findByUserId(USER_ID);

        assertThat(cart).isNotNull();
        assertThat(cart.getUser()).isNotNull();
        assertThat(cart.getUser().getId()).isEqualTo(USER_ID);
        assertThat(cart.getCartItems()).isNotEmpty();

        cart.getCartItems().forEach(cartItem -> {
            assertThat(cartItem.getBook()).isNotNull();
        });
    }
}
