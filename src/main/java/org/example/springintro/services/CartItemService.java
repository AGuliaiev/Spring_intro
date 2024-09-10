package org.example.springintro.services;

import org.example.springintro.model.CartItem;

public interface CartItemService {
    CartItem findById(Long id);

    void deleteById(Long id);

    CartItem updateQuantity(Long cartItemId, int quantity);
}
