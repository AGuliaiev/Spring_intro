package org.example.springintro.mapper;

import java.util.List;
import java.util.Set;
import org.example.springintro.config.MapperConfig;
import org.example.springintro.dto.order.OrderDto;
import org.example.springintro.dto.order.OrderItemDto;
import org.example.springintro.dto.order.OrderStatusUpdateDto;
import org.example.springintro.model.Order;
import org.example.springintro.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);

    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    @Mapping(target = "bookId", source = "book.id")
    List<OrderItemDto> toOrderItemDtoList(Set<OrderItem> orderItems);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateOrderFromDto(OrderStatusUpdateDto statusUpdateDto, @MappingTarget Order order);
}
