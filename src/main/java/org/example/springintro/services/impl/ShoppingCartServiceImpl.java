package org.example.springintro.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.shoppingcart.ShoppingCartDto;
import org.example.springintro.dto.shoppingcart.UpdateCartItemRequestDto;
import org.example.springintro.mapper.ShoppingCartMapper;
import org.example.springintro.model.Book;
import org.example.springintro.model.CartItem;
import org.example.springintro.model.ShoppingCart;
import org.example.springintro.model.User;
import org.example.springintro.repository.book.BookRepository;
import org.example.springintro.repository.shoppingcart.CartItemRepository;
import org.example.springintro.repository.shoppingcart.ShoppingCartRepository;
import org.example.springintro.services.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getCartForCurrentUser(Long userId) {
        return shoppingCartMapper.toDto(shoppingCartRepository.findByUserId(userId));
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void removeBookFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        cartItemRepository.delete(cartItem);
    }

    @Override
    public ShoppingCartDto updateBookQuantity(
            Long cartItemId,
            UpdateCartItemRequestDto requestDto,
            User user
    ) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId());
        CartItem cartItem = cartItemRepository.findByBookIdAndShoppingCartId(
                cartItemId, shoppingCart.getId()
                )
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        shoppingCartMapper.updateCartItemFromDto(requestDto, cartItem);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cartItem.getShoppingCart());
    }

    @Override
    public void addBookToCart(Long bookId, int quantity, User user) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId());
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        CartItem cartItem = cartItemRepository.findByBookIdAndShoppingCartId(
                bookId, shoppingCart.getId()
                )
                .orElse(new CartItem());
        cartItem.setBook(book);
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItem.setShoppingCart(shoppingCart);
        cartItemRepository.save(cartItem);
    }
}
