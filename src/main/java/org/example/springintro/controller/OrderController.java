package org.example.springintro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.springintro.dto.order.CreateOrderRequestDto;
import org.example.springintro.dto.order.OrderDto;
import org.example.springintro.dto.order.OrderItemDto;
import org.example.springintro.dto.order.OrderRequestDto;
import org.example.springintro.dto.order.OrderStatusUpdateDto;
import org.example.springintro.model.User;
import org.example.springintro.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Order",
        description = "Endpoints for managing orders"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Place an order",
            description = "Place a new order based on the user's shopping cart"
    )
    public CreateOrderRequestDto placeOrder(
            Authentication authentication,
            @RequestBody @Valid OrderRequestDto requestDto
    ) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(user, requestDto);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(
            summary = "Retrieve order history",
            description = "Get the list of orders for the current user"
    )
    public List<OrderDto> getUserOrders(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.getUserOrders(user.getId());
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items")
    @Operation(
            summary = "Retrieve all items in a specific order",
            description = "Get all items for a specific order"
    )
    public List<OrderItemDto> getOrderItems(@PathVariable Long orderId) {
        return orderService.getOrderItems(orderId);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(
            summary = "Retrieve a specific item in an order",
            description = "Get details of a specific item in a specific order"
    )
    public OrderItemDto getOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.getOrderItemByOrderIdAndItemId(orderId, itemId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{orderId}")
    @Operation(
            summary = "Update order status",
            description = "Update the status of a specific order"
    )
    public OrderDto updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody @Valid OrderStatusUpdateDto orderUpdateDto
    ) {
        return orderService.updateOrderStatus(orderId, orderUpdateDto);
    }
}
