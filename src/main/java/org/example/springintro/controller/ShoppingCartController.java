package org.example.springintro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.shoppingcart.AddToCartRequestDto;
import org.example.springintro.dto.shoppingcart.ShoppingCartDto;
import org.example.springintro.dto.shoppingcart.UpdateCartItemRequestDto;
import org.example.springintro.model.User;
import org.example.springintro.services.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Shopping Cart",
        description = "Endpoints for managing shopping cart operations"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(
            summary = "Retrieve user's shopping cart",
            description = "Get the current user's shopping cart"
    )
    public ShoppingCartDto getCartForCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getCartForCurrentUser(user.getId());
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Add book to shopping cart",
            description = "Add a book to the current user's shopping cart"
    )
    public ShoppingCartDto addBookToCart(
            Authentication authentication, @RequestBody @Valid AddToCartRequestDto requestDto
    ) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.addBookToCart(requestDto.getBookId(), requestDto.getQuantity(), user);
        return shoppingCartService.getCartForCurrentUser(user.getId());
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/items/{cartItemId}")
    @Operation(
            summary = "Update book quantity in shopping cart",
            description = "Update the quantity of a book in the shopping cart"
    )
    public ShoppingCartDto updateBookQuantity(
            Authentication authentication,
            @RequestBody @Valid Long cartItemId,
            UpdateCartItemRequestDto requestDto
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateBookQuantity(cartItemId, requestDto, user);
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Remove book from shopping cart",
            description = "Remove a book from the shopping cart"
    )
    public void removeBookFromCart(Authentication authentication, @PathVariable Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.removeBookFromCart(cartItemId);
    }
}
