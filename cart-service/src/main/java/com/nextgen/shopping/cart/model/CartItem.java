package com.nextgen.shopping.cart.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartItem {
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private Boolean inStock;
} 