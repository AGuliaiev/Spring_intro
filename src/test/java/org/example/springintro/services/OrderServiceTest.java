package org.example.springintro.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.example.springintro.dto.order.OrderDto;
import org.example.springintro.dto.order.OrderItemDto;
import org.example.springintro.dto.order.OrderRequestDto;
import org.example.springintro.dto.order.OrderStatusUpdateDto;
import org.example.springintro.mapper.OrderMapper;
import org.example.springintro.model.Book;
import org.example.springintro.model.CartItem;
import org.example.springintro.model.Order;
import org.example.springintro.model.OrderItem;
import org.example.springintro.model.ShoppingCart;
import org.example.springintro.model.Status;
import org.example.springintro.model.User;
import org.example.springintro.repository.order.OrderItemRepository;
import org.example.springintro.repository.order.OrderRepository;
import org.example.springintro.repository.shoppingcart.ShoppingCartRepository;
import org.example.springintro.services.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("placeOrder() - Given valid User and OrderRequestDto,"
            + " When placing order, Then returns OrderDto and saves order")
    public void placeOrder_WhenValidUserAndOrderRequestDto_ThenReturnsOrderDtoAndSavesOrder() {
        // Given
        User user = new User();
        user.setId(1L);

        Book book = new Book();
        book.setPrice(BigDecimal.valueOf(49.99));

        CartItem cartItem = new CartItem();
        cartItem.setQuantity(3);
        cartItem.setBook(book);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(Set.of(cartItem));

        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setShippingAddress("Test Street");

        Order order = new Order();
        order.setUser(user);
        order.setOrderItems(Set.of(new OrderItem()));

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(shoppingCart);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        OrderDto result = orderService.placeOrder(user, requestDto);

        // Then
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(shoppingCartRepository, times(1)).delete(shoppingCart);
        assertThat(result).isNotNull();
        verify(orderMapper, times(1)).toDto(any(Order.class)); // Проверяем вызов маппера
    }

    @Test
    @DisplayName("placeOrder() - Throws IllegalStateException when shopping cart is not found")
    public void placeOrder_ThrowsException_WhenShoppingCartNotFound() {
        // Given
        User user = new User();
        user.setId(1L);

        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setShippingAddress("Test Street");

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(null);

        // Then
        assertThatThrownBy(() -> orderService.placeOrder(user, requestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Shopping cart is empty or not found");
    }

    @Test
    @DisplayName("placeOrder() - Throws IllegalStateException when shopping cart is empty")
    public void placeOrder_ThrowsException_WhenShoppingCartIsEmpty() {
        // Given
        User user = new User();
        user.setId(1L);

        ShoppingCart emptyCart = new ShoppingCart();
        emptyCart.setUser(user);
        emptyCart.setCartItems(Set.of());

        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setShippingAddress("Test Street");

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(emptyCart);

        // Then
        assertThatThrownBy(() -> orderService.placeOrder(user, requestDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Shopping cart is empty or not found");
    }

    @Test
    @DisplayName("getUserOrders() - Given userId,"
            + " When fetching orders, Then returns List of OrderDto")
    public void getUserOrders_WhenUserIdIsGiven_ThenReturnsListOfOrderDto() {
        // Given
        Long userId = 1L;
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(new OrderDto());

        List<OrderDto> orders = orderService.getUserOrders(userId);

        assertThat(orders).hasSize(1);
        verify(orderRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("updateOrderStatus() - Given valid orderId and OrderStatusUpdateDto,"
            + " When updating status, Then returns updated OrderDto")
    void updateOrderStatus_WhenValidOrderIdAndStatusUpdateDto_ThenReturnsUpdatedOrderDto() {
        // Given
        Long orderId = 1L;
        Order order = new Order();
        OrderStatusUpdateDto statusUpdateDto = new OrderStatusUpdateDto();
        statusUpdateDto.setStatus(Status.COMPLETED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderDto orderDto = new OrderDto();
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        doAnswer(invocation -> {
            OrderStatusUpdateDto dto = invocation.getArgument(0);
            Order ord = invocation.getArgument(1);
            ord.setStatus(dto.getStatus());
            return null;
        }).when(orderMapper).updateOrderFromDto(any(OrderStatusUpdateDto.class), any(Order.class));

        // When
        OrderDto result = orderService.updateOrderStatus(orderId, statusUpdateDto);

        // Then
        verify(orderRepository, times(1)).save(order);
        assertThat(order.getStatus()).isEqualTo(Status.COMPLETED);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("getOrderItems() - Given valid orderId and userId,"
            + " When fetching order items, Then returns List of OrderItemDto")
    void getOrderItems_WhenValidOrderIdAndUserId_ThenReturnsListOfOrderItemDto() {
        // Given
        Long orderId = 1L;
        Long userId = 1L;
        Order order = new Order();
        OrderItem item = new OrderItem();
        order.setOrderItems(Set.of(item));

        // When
        when(orderRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderItemDtoList(order.getOrderItems()))
                .thenReturn(List.of(new OrderItemDto()));

        List<OrderItemDto> items = orderService.getOrderItems(orderId, userId);

        // Then
        assertThat(items).hasSize(1);
        verify(orderRepository, times(1)).findByIdAndUserId(orderId, userId);
    }

    @Test
    @DisplayName("getOrderItemByOrderIdAndItemId() - Given valid orderId,"
            + " orderItemId, and userId, When fetching order item, Then returns OrderItemDto")
    void getOrderItemByOrderIdAndItemId_WhenValidIds_ThenReturnsOrderItemDto() {
        // Given
        Long orderId = 1L;
        Long orderItemId = 1L;
        Long userId = 1L;
        OrderItem item = new OrderItem();

        // When
        when(orderItemRepository.findByIdAndOrderIdAndUserId(orderItemId, orderId, userId))
                .thenReturn(Optional.of(item));
        when(orderMapper.toOrderItemDto(item)).thenReturn(new OrderItemDto());

        OrderItemDto result = orderService
                .getOrderItemByOrderIdAndItemId(orderId, orderItemId, userId);

        // Then
        assertThat(result).isNotNull();
        verify(orderItemRepository, times(1))
                .findByIdAndOrderIdAndUserId(orderItemId, orderId, userId);
    }

    @Test
    @DisplayName("placeOrder() - Given empty cart,"
            + " When placing order, Then throws IllegalStateException")
    void placeOrder_WhenEmptyCart_ThenThrowsIllegalStateException() {
        // Given
        User user = new User();
        user.setId(1L);

        // When
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(null);

        // Then
        assertThrows(IllegalStateException.class, () -> orderService
                .placeOrder(user, new OrderRequestDto()));
    }
}
