package org.example.springintro.services;

import org.example.springintro.dto.shoppingcart.AddToCartRequestDto;
import org.example.springintro.dto.shoppingcart.ShoppingCartDto;
import org.example.springintro.dto.shoppingcart.UpdateCartItemRequestDto;
import org.example.springintro.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getCartForCurrentUser(Long userId);

    void createShoppingCart(User user);

    void removeBookFromCart(Long cartItemId, Long userId);

    ShoppingCartDto updateBookQuantity(
            Long cartItemId,
            UpdateCartItemRequestDto requestDto,
            User userId
    );

    ShoppingCartDto addBookToCart(AddToCartRequestDto itemDto, User user);
}
