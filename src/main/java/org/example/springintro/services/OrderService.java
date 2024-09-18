package org.example.springintro.services;

import java.util.List;
import org.example.springintro.dto.order.OrderDto;
import org.example.springintro.dto.order.OrderItemDto;
import org.example.springintro.dto.order.OrderRequestDto;
import org.example.springintro.dto.order.OrderStatusUpdateDto;
import org.example.springintro.model.User;

public interface OrderService {
    OrderDto placeOrder(User user, OrderRequestDto requestDto);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto updateOrderStatus(Long orderId, OrderStatusUpdateDto orderUpdateDto);

    List<OrderItemDto> getOrderItems(Long orderId, Long userId);

    OrderItemDto getOrderItemByOrderIdAndItemId(Long orderId, Long orderItemId, Long userId);
}
