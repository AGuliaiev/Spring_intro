package org.example.springintro.services;

import org.example.springintro.dto.shoppingcart.ShoppingCartDto;
import org.example.springintro.dto.shoppingcart.UpdateCartItemRequestDto;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {
    ShoppingCartDto getCartForCurrentUser(Pageable pageable);

    ShoppingCartDto addBookToCart(Long bookId, int quantity);

    void removeBookFromCart(Long cartItemId);

    ShoppingCartDto updateBookQuantity(Long cartItemId, UpdateCartItemRequestDto requestDto);
}
