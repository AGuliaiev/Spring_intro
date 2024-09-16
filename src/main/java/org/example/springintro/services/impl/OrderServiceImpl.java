package org.example.springintro.services.impl;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.order.CreateOrderRequestDto;
import org.example.springintro.dto.order.OrderDto;
import org.example.springintro.dto.order.OrderItemDto;
import org.example.springintro.dto.order.OrderRequestDto;
import org.example.springintro.dto.order.OrderStatusUpdateDto;
import org.example.springintro.mapper.OrderMapper;
import org.example.springintro.model.Order;
import org.example.springintro.model.OrderItem;
import org.example.springintro.model.ShoppingCart;
import org.example.springintro.model.Status;
import org.example.springintro.model.User;
import org.example.springintro.repository.order.OrderItemRepository;
import org.example.springintro.repository.order.OrderRepository;
import org.example.springintro.repository.shoppingcart.ShoppingCartRepository;
import org.example.springintro.services.OrderService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    @Transactional
    public CreateOrderRequestDto placeOrder(User user, OrderRequestDto requestDto) {
        ShoppingCart cart = getAndValidateCart(user.getId());
        Order order = createOrderFromCart(cart, requestDto);
        orderRepository.save(order);
        shoppingCartRepository.delete(cart);
        return orderMapper.toCreateOrderRequestDto(order);
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(orderMapper::toDto).toList();
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatusUpdateDto orderUpdateDto) {
        Order order = findOrderById(orderId);
        orderMapper.updateOrderFromDto(orderUpdateDto, order);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDto(updatedOrder);
    }

    @Override
    public List<OrderItemDto> getOrderItems(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException("No order items found for order ID: " + orderId);
        }
        return orderItems.stream().map(orderMapper::toOrderItemDto).toList();
    }

    @Override
    public OrderItemDto getOrderItemByOrderIdAndItemId(Long orderId, Long itemId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        OrderItem orderItem = orderItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order item not found"));
        return orderMapper.toOrderItemDto(orderItem);
    }

    private ShoppingCart getAndValidateCart(Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId);
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Shopping cart is empty or not found");
        }
        return cart;
    }

    private Order createOrderFromCart(ShoppingCart cart, OrderRequestDto requestDto) {
        Order order = new Order();
        order.setUser(cart.getUser());
        Set<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(
                            cartItem.getBook()
                                    .getPrice()
                                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setTotal(calculateTotal(orderItems));
        order.setStatus(Status.PENDING);
        return order;
    }

    private BigDecimal calculateTotal(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }
}
