package com.nextgen.shopping.cart.repository;

import com.nextgen.shopping.cart.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserIdAndStatus(String userId, Cart.CartStatus status);
    boolean existsByUserIdAndStatus(String userId, Cart.CartStatus status);
} 