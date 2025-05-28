package com.nextgen.shopping.cart.service;

import com.nextgen.shopping.cart.client.ProductServiceClient;
import com.nextgen.shopping.cart.model.Cart;
import com.nextgen.shopping.cart.model.CartItem;
import com.nextgen.shopping.cart.repository.CartRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final ProductServiceClient productService;

    public Cart getActiveCart(String userId) {
        return cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseGet(() -> createNewCart(userId));
    }

    private Cart createNewCart(String userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "addToCartFallback")
    public Cart addToCart(String userId, Long productId, Integer quantity) {
        var product = productService.getProduct(productId);
        if (product == null) {
            throw new EntityNotFoundException("Product not found: " + productId);
        }

        Cart cart = getActiveCart(userId);
        
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setProductId(productId);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        cartItem.setProductName(product.name());
        cartItem.setPrice(product.price());
        cartItem.setQuantity(quantity);
        cartItem.setImageUrl(product.imageUrl());
        cartItem.setInStock(product.stockQuantity() > 0);

        cart.updateTotalAmount();
        cart.setUpdatedAt(LocalDateTime.now());
        
        return cartRepository.save(cart);
    }

    private Cart addToCartFallback(String userId, Long productId, Integer quantity, Exception ex) {
        log.error("Failed to fetch product details. Using fallback for productId: {}", productId, ex);
        Cart cart = getActiveCart(userId);
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(quantity);
            cart.updateTotalAmount();
            cart.setUpdatedAt(LocalDateTime.now());
            return cartRepository.save(cart);
        }
        
        throw new RuntimeException("Cannot add new item to cart: Product service is unavailable");
    }

    public Cart updateCartItemQuantity(String userId, Long productId, Integer quantity) {
        Cart cart = getActiveCart(userId);
        
        cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    cart.updateTotalAmount();
                    cart.setUpdatedAt(LocalDateTime.now());
                });

        return cartRepository.save(cart);
    }

    public Cart removeFromCart(String userId, Long productId) {
        Cart cart = getActiveCart(userId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cart.updateTotalAmount();
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public Cart clearCart(String userId) {
        Cart cart = getActiveCart(userId);
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public Cart checkoutCart(String userId) {
        Cart cart = getActiveCart(userId);
        cart.setStatus(Cart.CartStatus.CHECKED_OUT);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
} 