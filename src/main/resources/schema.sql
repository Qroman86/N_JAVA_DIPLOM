CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS cloud_files (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    original_name VARCHAR(255),
    size BIGINT NOT NULL,
    content_type VARCHAR(255),
    owner_login VARCHAR(255) NOT NULL,
    CONSTRAINT fk_owner FOREIGN KEY (owner_login) REFERENCES users(login) ON DELETE CASCADE,
    CONSTRAINT unique_user_filename UNIQUE (owner_login, filename)
);