package org.example.springintro.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.example.springintro.model.Status;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrderRequestDto {
    @Positive
    private Long id;
    @Positive
    @NotNull
    private Long userId;
    @NotNull
    private LocalDateTime orderDate;
    @Positive
    @NotNull
    private BigDecimal total;
    @NotNull
    private Status status;
    @NotEmpty
    @Valid
    private List<OrderItemDto> orderItems;
}
