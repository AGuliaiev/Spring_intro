package org.example.springintro.dto.order;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import org.example.springintro.model.Status;

@Data
public class OrderUpdateDto {
    @NotNull
    private Status status;
    private List<OrderItemDto> items;
}
