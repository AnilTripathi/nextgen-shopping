package com.nextgen.shopping.cart.controller;

import com.nextgen.shopping.cart.model.Cart;
import com.nextgen.shopping.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Cart API", description = "Endpoints for managing shopping carts")
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get active cart for a user")
    public ResponseEntity<Cart> getActiveCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getActiveCart(userId));
    }

    @PostMapping("/{userId}/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<Cart> addToCart(
            @PathVariable String userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity));
    }

    @PutMapping("/{userId}/items/{productId}")
    @Operation(summary = "Update item quantity in cart")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @PathVariable String userId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<Cart> removeFromCart(
            @PathVariable String userId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Clear cart")
    public ResponseEntity<Cart> clearCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }

    @PostMapping("/{userId}/checkout")
    @Operation(summary = "Checkout cart")
    public ResponseEntity<Cart> checkoutCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.checkoutCart(userId));
    }
} 