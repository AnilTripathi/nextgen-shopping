package com.shopping.vendor.controller;

import com.shopping.vendor.model.Vendor;
import com.shopping.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendor API", description = "Endpoints for managing vendors")
public class VendorController {
    private final VendorService vendorService;

    @GetMapping
    @Operation(summary = "Get all active vendors")
    public ResponseEntity<Page<Vendor>> getAllVendors(Pageable pageable) {
        return ResponseEntity.ok(vendorService.getAllVendors(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vendor by ID")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getVendorById(id));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get vendors by verification status")
    public ResponseEntity<Page<Vendor>> getVendorsByStatus(
            @PathVariable Vendor.VerificationStatus status, Pageable pageable) {
        return ResponseEntity.ok(vendorService.getVendorsByVerificationStatus(status, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search vendors by business name")
    public ResponseEntity<Page<Vendor>> searchVendors(
            @RequestParam String businessName, Pageable pageable) {
        return ResponseEntity.ok(vendorService.searchVendors(businessName, pageable));
    }

    @PostMapping
    @Operation(summary = "Register a new vendor")
    public ResponseEntity<Vendor> createVendor(@Valid @RequestBody Vendor vendor) {
        return ResponseEntity.ok(vendorService.createVendor(vendor));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    @Operation(summary = "Update vendor profile")
    public ResponseEntity<Vendor> updateVendor(
            @PathVariable Long id, @Valid @RequestBody Vendor vendor) {
        return ResponseEntity.ok(vendorService.updateVendor(id, vendor));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a vendor")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/verification")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update vendor verification status")
    public ResponseEntity<Vendor> updateVerificationStatus(
            @PathVariable Long id, @RequestParam Vendor.VerificationStatus status) {
        return ResponseEntity.ok(vendorService.updateVerificationStatus(id, status));
    }
} 