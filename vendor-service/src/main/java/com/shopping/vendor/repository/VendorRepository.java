package com.shopping.vendor.repository;

import com.shopping.vendor.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Page<Vendor> findByActiveTrue(Pageable pageable);
    Optional<Vendor> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<Vendor> findByVerificationStatus(Vendor.VerificationStatus status, Pageable pageable);
    Page<Vendor> findByBusinessNameContainingIgnoreCase(String businessName, Pageable pageable);
} 