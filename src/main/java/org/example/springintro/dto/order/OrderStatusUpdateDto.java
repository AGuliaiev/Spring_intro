package org.example.springintro.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.springintro.model.Status;

@Data
public class OrderStatusUpdateDto {
    @NotNull
    private Status status;
}
