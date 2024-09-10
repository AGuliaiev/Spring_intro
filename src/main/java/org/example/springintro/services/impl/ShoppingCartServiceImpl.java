package org.example.springintro.services.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.shoppingcart.AddToCartRequestDto;
import org.example.springintro.dto.shoppingcart.CartItemResponseDto;
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
import org.example.springintro.services.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getCartForCurrentUser(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        ShoppingCart cart = shoppingCartRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Shopping cart not found"));

        List<CartItemResponseDto> cartItemsDto = cart.getCartItems().stream()
                .map(shoppingCartMapper::toCartItemDto)
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .toList();

        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toDto(cart);
        shoppingCartDto.setCartItems(cartItemsDto);
        return shoppingCartDto;
    }

    @Override
    public ShoppingCartDto addBookToCart(Long bookId, int quantity) {
        User currentUser = userService.getCurrentUser();
        ShoppingCart cart = shoppingCartRepository.findByUser(currentUser)
                .orElseGet(() -> createNewCartForUser(currentUser));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        System.out.println("Book ID: " + book.getId() + ", Title: " + book.getTitle());

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().equals(book))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newCartItem = shoppingCartMapper.toEntity(
                            new AddToCartRequestDto(book.getId(), quantity
                            ));
                    newCartItem.setBook(book);
                    newCartItem.setShoppingCart(cart);
                    cart.getCartItems().add(newCartItem);
                    return newCartItem;
                });
        cartItemRepository.save(cartItem);
        shoppingCartRepository.save(cart);
        return shoppingCartMapper.toDto(cart);
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
            UpdateCartItemRequestDto requestDto
    ) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        shoppingCartMapper.updateCartItemFromDto(requestDto, cartItem);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cartItem.getShoppingCart());
    }

    private ShoppingCart createNewCartForUser(User user) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        return shoppingCartRepository.save(cart);
    }
}
