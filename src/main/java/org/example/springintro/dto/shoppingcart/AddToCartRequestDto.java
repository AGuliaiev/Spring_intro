package org.example.springintro.dto.shoppingcart;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddToCartRequestDto {
    @Positive
    private Long bookId;
    @Positive
    private int quantity;
}
