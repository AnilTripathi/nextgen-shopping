package com.nextgen.shopping.cart.client;

import lombok.Builder;

@Builder
public record ProductResponse(
    Long id,
    String name,
    Double price,
    Integer stockQuantity,
    String imageUrl
) {} 