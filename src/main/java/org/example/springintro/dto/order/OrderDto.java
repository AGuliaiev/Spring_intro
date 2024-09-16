package org.example.springintro.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.example.springintro.model.Status;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private String shippingAddress;
    private Status status;
    private List<OrderItemDto> orderItems;
}
