CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255),
    keycloak_user_id VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE user_kyc (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id),
    status VARCHAR(20),
    document_type VARCHAR(255),
    submitted_at TIMESTAMP
);
