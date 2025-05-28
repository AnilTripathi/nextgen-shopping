package com.nextgen.shopping.cart.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "carts")
public class Cart {
    @Id
    private String id;
    private String userId;
    private List<CartItem> items = new ArrayList<>();
    private Double totalAmount = 0.0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CartStatus status = CartStatus.ACTIVE;

    public enum CartStatus {
        ACTIVE,
        CHECKED_OUT,
        ABANDONED
    }

    public void updateTotalAmount() {
        this.totalAmount = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
} 