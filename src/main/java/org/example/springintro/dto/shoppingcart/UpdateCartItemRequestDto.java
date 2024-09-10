package org.example.springintro.dto.shoppingcart;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateCartItemRequestDto {
    @Min(1)
    private int quantity;
}
