package org.example.springintro.util;

import java.util.List;
import org.example.springintro.dto.shoppingcart.AddToCartRequestDto;
import org.example.springintro.dto.shoppingcart.CartItemResponseDto;
import org.example.springintro.dto.shoppingcart.ShoppingCartDto;
import org.example.springintro.dto.shoppingcart.UpdateCartItemRequestDto;

public class ShoppingCartTestUtils {

    public static AddToCartRequestDto createAddToCartRequestDto(Long bookId, int quantity) {
        AddToCartRequestDto requestDto = new AddToCartRequestDto();
        requestDto.setBookId(bookId);
        requestDto.setQuantity(quantity);
        return requestDto;
    }

    public static CartItemResponseDto createCartItemResponseDto(
            Long id,
            Long bookId,
            String bookTitle,
            int quantity
    ) {
        CartItemResponseDto itemDto = new CartItemResponseDto();
        itemDto.setId(id);
        itemDto.setBookId(bookId);
        itemDto.setBookTitle(bookTitle);
        itemDto.setQuantity(quantity);
        return itemDto;
    }

    public static ShoppingCartDto createShoppingCartDto(
            Long id,
            Long userId,
            List<CartItemResponseDto> cartItems
    ) {
        ShoppingCartDto shoppingCart = new ShoppingCartDto();
        shoppingCart.setId(id);
        shoppingCart.setUserId(userId);
        shoppingCart.setCartItems(cartItems);
        return shoppingCart;
    }

    public static UpdateCartItemRequestDto createUpdateCartItemRequestDto(int quantity) {
        UpdateCartItemRequestDto updateDto = new UpdateCartItemRequestDto();
        updateDto.setQuantity(quantity);
        return updateDto;
    }
}
