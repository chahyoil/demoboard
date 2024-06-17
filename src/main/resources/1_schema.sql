-- CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS plpython3u;

CREATE OR REPLACE FUNCTION bcrypt_hash(password TEXT) RETURNS TEXT AS $$
import bcrypt
hashed = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())
return hashed.decode('utf-8')
$$ LANGUAGE plpython3u;

CREATE OR REPLACE FUNCTION bcrypt_check(password TEXT, hashed TEXT) RETURNS BOOLEAN AS $$
import bcrypt
return bcrypt.checkpw(password.encode('utf-8'), hashed.encode('utf-8'))
$$ LANGUAGE plpython3u;

-- Drop existing tables if they exist
DROP TABLE IF EXISTS comment CASCADE;
DROP TABLE IF EXISTS post CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS role CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS user_recent_posts CASCADE;
DROP TABLE IF EXISTS refresh_token CASCADE;

-- Table for roles
CREATE TABLE role (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL
);

-- Table for users
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255),
                       created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Table for categories
CREATE TABLE category (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE
);

-- Table for posts
CREATE TABLE post (
                      id SERIAL PRIMARY KEY,
                      user_id BIGINT,
                      title VARCHAR(255) NOT NULL,
                      content TEXT NOT NULL,
                      is_open BOOLEAN NOT NULL,
                      created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                      category_id BIGINT,
                      original_filename VARCHAR(255),
                      stored_filename VARCHAR(255),
                      view_count INT DEFAULT 0 NOT NULL, -- Add view_count column
                      FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                      FOREIGN KEY (category_id) REFERENCES category (id)
);

-- Table for comments
CREATE TABLE comment (
                         id SERIAL PRIMARY KEY,
                         content TEXT NOT NULL,
                         created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                         post_id BIGINT,
                         user_id BIGINT,
                         FOREIGN KEY (post_id) REFERENCES post(id),
                         FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Table for user roles (many-to-many relationship)
CREATE TABLE user_roles (
                            user_id BIGINT,
                            role_id BIGINT,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE
);

CREATE TABLE user_recent_posts (
                                   user_id BIGINT NOT NULL,
                                   post_id BIGINT NOT NULL,
                                   PRIMARY KEY (user_id, post_id),
                                   FOREIGN KEY (user_id) REFERENCES users(id),
                                   FOREIGN KEY (post_id) REFERENCES post(id)
);

CREATE TABLE refresh_token (
                               id BIGSERIAL PRIMARY KEY,
                               username VARCHAR(255) NOT NULL,
                               token VARCHAR(255) NOT NULL,
                               expiry_date TIMESTAMP NOT NULL
);