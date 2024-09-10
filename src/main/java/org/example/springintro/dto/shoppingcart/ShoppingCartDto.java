package org.example.springintro.dto.shoppingcart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class ShoppingCartDto {
    @NotNull
    private Long id;
    @NotNull
    private Long userId;
    @NotEmpty
    @Valid
    private List<CartItemResponseDto> cartItems;
}
