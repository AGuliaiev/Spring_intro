package org.example.springintro.repository.order;

import java.util.Optional;
import org.example.springintro.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query(value = """
        SELECT oi FROM OrderItem oi
        JOIN FETCH oi.order o
        WHERE oi.id = :orderItemId AND o.id = :orderId AND o.user.id = :userId""")
    Optional<OrderItem> findByIdAndOrderIdAndUserId(@Param("orderItemId") Long orderItemId,
                                                    @Param("orderId") Long orderId,
                                                    @Param("userId") Long userId);
}
