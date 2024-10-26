package org.example.springintro.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.example.springintro.dto.order.OrderDto;
import org.example.springintro.dto.order.OrderItemDto;
import org.example.springintro.dto.order.OrderRequestDto;
import org.example.springintro.model.Status;

public class OrderTestUtils {

    public static OrderRequestDto createOrderRequestDto(String deliveryAddress) {
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setShippingAddress(deliveryAddress);
        return requestDto;
    }

    public static OrderDto createOrderDto(
            Long id,
            Long userId,
            BigDecimal totalPrice,
            Status status
    ) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(id);
        orderDto.setUserId(userId);
        orderDto.setTotal(totalPrice);
        orderDto.setStatus(status);
        orderDto.setOrderDate(LocalDateTime.now());
        orderDto.setOrderItems(new ArrayList<>());
        return orderDto;
    }

    public static OrderItemDto createOrderItemDto(Long id, Long bookId, int quantity) {
        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setId(id);
        itemDto.setBookId(bookId);
        itemDto.setQuantity(quantity);
        return itemDto;
    }
}
