package org.example.springintro.repository.shoppingcart;

import java.util.List;
import org.example.springintro.model.CartItem;
import org.example.springintro.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByShoppingCart(ShoppingCart shoppingCart);
}
