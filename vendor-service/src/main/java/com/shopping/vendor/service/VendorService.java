package com.shopping.vendor.service;

import com.shopping.vendor.model.Vendor;
import com.shopping.vendor.repository.VendorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorService {
    private final VendorRepository vendorRepository;

    public Page<Vendor> getAllVendors(Pageable pageable) {
        return vendorRepository.findByActiveTrue(pageable);
    }

    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found with id: " + id));
    }

    public Page<Vendor> getVendorsByVerificationStatus(Vendor.VerificationStatus status, Pageable pageable) {
        return vendorRepository.findByVerificationStatus(status, pageable);
    }

    public Page<Vendor> searchVendors(String businessName, Pageable pageable) {
        return vendorRepository.findByBusinessNameContainingIgnoreCase(businessName, pageable);
    }

    @Transactional
    public Vendor createVendor(Vendor vendor) {
        if (vendorRepository.existsByEmail(vendor.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        vendor.setActive(true);
        vendor.setVerificationStatus(Vendor.VerificationStatus.PENDING);
        return vendorRepository.save(vendor);
    }

    @Transactional
    public Vendor updateVendor(Long id, Vendor vendor) {
        Vendor existingVendor = getVendorById(id);
        
        if (!existingVendor.getEmail().equals(vendor.getEmail()) && 
            vendorRepository.existsByEmail(vendor.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        existingVendor.setBusinessName(vendor.getBusinessName());
        existingVendor.setContactName(vendor.getContactName());
        existingVendor.setEmail(vendor.getEmail());
        existingVendor.setPhoneNumber(vendor.getPhoneNumber());
        existingVendor.setDescription(vendor.getDescription());
        existingVendor.setBusinessAddress(vendor.getBusinessAddress());
        existingVendor.setTaxId(vendor.getTaxId());
        existingVendor.setBankAccount(vendor.getBankAccount());
        existingVendor.setLogoUrl(vendor.getLogoUrl());
        
        return vendorRepository.save(existingVendor);
    }

    @Transactional
    public void deleteVendor(Long id) {
        Vendor vendor = getVendorById(id);
        vendor.setActive(false);
        vendorRepository.save(vendor);
    }

    @Transactional
    public Vendor updateVerificationStatus(Long id, Vendor.VerificationStatus status) {
        Vendor vendor = getVendorById(id);
        vendor.setVerificationStatus(status);
        return vendorRepository.save(vendor);
    }
} 