package org.example.springintro.repository.shoppingcart;

import java.util.Optional;
import org.example.springintro.model.ShoppingCart;
import org.example.springintro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);
}
