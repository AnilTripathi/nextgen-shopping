-- Create product schema if not exists
CREATE SCHEMA IF NOT EXISTS product;

-- Set the search path to product schema
SET search_path TO product;

-- Create product category table
CREATE TABLE IF NOT EXISTS product_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create product table
CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    category_id BIGINT REFERENCES product_category(id),
    vendor_id BIGINT NOT NULL,
    image_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create product review table
CREATE TABLE IF NOT EXISTS product_review (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT REFERENCES product(id),
    user_id BIGINT NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_product_category ON product(category_id);
CREATE INDEX IF NOT EXISTS idx_product_vendor ON product(vendor_id);
CREATE INDEX IF NOT EXISTS idx_product_review_product ON product_review(product_id);
CREATE INDEX IF NOT EXISTS idx_product_review_user ON product_review(user_id); 