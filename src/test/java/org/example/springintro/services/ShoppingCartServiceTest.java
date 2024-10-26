package org.example.springintro.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
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
import org.example.springintro.services.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    private static final long CART_ITEM_ID = 1L;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("getCartForCurrentUser() - Given userId,"
            + " When fetching shopping cart, Then returns ShoppingCartDto")
    public void getCartForCurrentUser_UserId_ReturnShoppingCart() {
        // Given
        Long userId = 1L;
        ShoppingCart shoppingCart = new ShoppingCart();
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCart.setId(userId);

        // When
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.getCartForCurrentUser(userId);

        // Then
        assertThat(result).isEqualTo(shoppingCartDto);
        verify(shoppingCartRepository, times(1)).findByUserId(userId);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
    }

    @Test
    @DisplayName("createShoppingCart() - Given User,"
            + " When creating a new shopping cart, Then saves shopping cart")
    public void createShoppingCart_User_CreatesAndSavesShoppingCart() {
        // Give
        User user = new User();
        user.setId(1L);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);

        // When
        shoppingCartService.createShoppingCart(user);

        // Then
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    @DisplayName("removeBookFromCart() - Given valid cartItemId and user, "
            + "When removing book, Then deletes cart item")
    public void removeBookFromCart_ValidCartItemIdAndUser_DeletesCartItem() {
        User user = new User();
        user.setId(1L);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);

        CartItem cartItem = new CartItem();
        cartItem.setId(CART_ITEM_ID);

        shoppingCart.getCartItems().add(cartItem);

        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(shoppingCart);
        when(cartItemRepository.findByIdAndShoppingCartId(CART_ITEM_ID, shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));

        shoppingCartService.removeBookFromCart(CART_ITEM_ID, user);

        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    @DisplayName("updateBookQuantity() - Given valid cartItemId,"
            + " requestDto, and user, When updating, Then returns updated ShoppingCartDto")
    public void updateBookQuantity_ValidCartItemId_ReturnsUpdatedShoppingCartDto() {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto();
        requestDto.setQuantity(5);

        User user = new User();
        user.setId(1L);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);

        CartItem cartItem = new CartItem();
        cartItem.setId(CART_ITEM_ID);
        cartItem.setShoppingCart(shoppingCart);

        ShoppingCartDto expected = new ShoppingCartDto();

        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(shoppingCart);
        when(cartItemRepository.findByIdAndShoppingCartId(CART_ITEM_ID, shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.updateBookQuantity(
                CART_ITEM_ID,
                requestDto,
                user
        );

        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
    }

    @Test
    @DisplayName("addBookToCart() - Book already in the cart, should update quantity")
    void addBookToCart_BookAlreadyInCart_ShouldUpdateQuantity() {
        // Given
        AddToCartRequestDto requestDto = new AddToCartRequestDto();
        requestDto.setBookId(100L);
        requestDto.setQuantity(2);

        User user = new User();
        user.setId(1L);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);

        Book book = new Book();
        book.setId(100L);

        CartItem existingCartItem = new CartItem();
        existingCartItem.setBook(book);
        existingCartItem.setQuantity(1);

        shoppingCart.setCartItems(new HashSet<>(Collections.singleton(existingCartItem)));

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(shoppingCart);
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(book));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.addBookToCart(requestDto, user);

        // Then
        assertThat(existingCartItem.getQuantity()).isEqualTo(3);
        assertThat(result).isEqualTo(shoppingCartDto);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
    }

    @Test
    @DisplayName("addBookToCart() - Book not in cart, should add new item")
    void addBookToCart_BookNotInCart_ShouldAddNewItem() {
        // Given
        AddToCartRequestDto requestDto = new AddToCartRequestDto();
        requestDto.setBookId(101L);
        requestDto.setQuantity(2);

        User user = new User();
        user.setId(1L);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>());

        Book book = new Book();
        book.setId(101L);

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(shoppingCart);
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(book));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.addBookToCart(requestDto, user);

        // Then
        assertThat(shoppingCart.getCartItems()).hasSize(1);
        CartItem newItem = shoppingCart.getCartItems().iterator().next();
        assertThat(newItem.getBook().getId()).isEqualTo(101L);
        assertThat(newItem.getQuantity()).isEqualTo(2);

        verify(shoppingCartRepository).save(shoppingCart);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("getCartForCurrentUser() - Given invalid userId,"
            + " When fetching shopping cart, Then throws EntityNotFoundException")
    public void getCartForCurrentUser_InvalidUserId_ThrowsEntityNotFoundException() {
        Long invalidUserId = 999L;

        when(shoppingCartRepository.findByUserId(invalidUserId)).thenReturn(null);

        assertThatThrownBy(() -> shoppingCartService.getCartForCurrentUser(invalidUserId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Shopping cart not found for user id: " + invalidUserId);
    }
}
