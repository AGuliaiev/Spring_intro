package org.example.springintro.dto.shoppingcart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemResponseDto {
    @NotNull
    private Long id;
    @NotNull
    private Long bookId;
    @NotBlank
    private String bookTitle;
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
