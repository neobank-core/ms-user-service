ALTER TABLE user_kyc
ADD COLUMN document_number VARCHAR(255),
ADD COLUMN reviewed_at TIMESTAMP;

CREATE TABLE user_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id),
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    postal_code VARCHAR(255) NOT NULL,
    created_at TIMESTAMP
);
