package org.example.springintro.dto.shoppingcart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddToCartRequestDto {
    @NotNull
    private Long bookId;
    @Min(1)
    private int quantity;
}
