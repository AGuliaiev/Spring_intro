package org.example.springintro.mapper;

import org.example.springintro.config.MapperConfig;
import org.example.springintro.dto.shoppingcart.CartItemResponseDto;
import org.example.springintro.dto.shoppingcart.ShoppingCartDto;
import org.example.springintro.dto.shoppingcart.UpdateCartItemRequestDto;
import org.example.springintro.model.Book;
import org.example.springintro.model.CartItem;
import org.example.springintro.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemResponseDto toCartItemDto(CartItem cartItem);

    void updateCartItemFromDto(UpdateCartItemRequestDto requestDto, @MappingTarget CartItem entity);

    @Named("bookFromId")
    default Book bookFromId(Long id) {
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
