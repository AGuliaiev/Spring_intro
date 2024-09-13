package org.example.springintro.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.shoppingcart.AddToCartRequestDto;
import org.example.springintro.dto.shoppingcart.ShoppingCartDto;
import org.example.springintro.dto.shoppingcart.UpdateCartItemRequestDto;
import org.example.springintro.exception.EntityNotFoundException;
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
        return shoppingCartMapper.toDto(findShoppingCartByUserId(userId));
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void removeBookFromCart(Long cartItemId, User user) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(user.getId());
        CartItem cartItem = findCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        shoppingCart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
    }

    @Override
    public ShoppingCartDto updateBookQuantity(
            Long cartItemId,
            UpdateCartItemRequestDto requestDto,
            User user) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(user.getId());
        CartItem cartItem = findCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        shoppingCartMapper.updateCartItemFromDto(requestDto, cartItem);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cartItem.getShoppingCart());
    }

    @Override
    @Transactional
    public ShoppingCartDto addBookToCart(AddToCartRequestDto itemDto, User user) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(user.getId());
        Book book = findBookById(itemDto.getBookId());
        shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(itemDto.getBookId()))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + itemDto.getQuantity()),
                        () -> addCartItemToCart(itemDto, book, shoppingCart));
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    private void addCartItemToCart(
            AddToCartRequestDto itemDto,
            Book book,
            ShoppingCart shoppingCart) {
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(itemDto.getQuantity());
        cartItem.setShoppingCart(shoppingCart);
        shoppingCart.getCartItems().add(cartItem);
    }

    private ShoppingCart findShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId);
    }

    private CartItem findCartItemByIdAndShoppingCartId(Long cartItemId, Long shoppingCartId) {
        return cartItemRepository.findByIdAndShoppingCartId(cartItemId, shoppingCartId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "CartItem with ID " + cartItemId + " not found"));
    }

    private Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }
}
