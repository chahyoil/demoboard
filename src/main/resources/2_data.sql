

-- Insert roles
INSERT INTO role (name) VALUES ('ROLE_ANONYMOUS');
INSERT INTO role (name) VALUES ('ROLE_BRONZE');
INSERT INTO role (name) VALUES ('ROLE_SILVER');
INSERT INTO role (name) VALUES ('ROLE_GOLD');
INSERT INTO role (name) VALUES ('ROLE_DIAMOND');
INSERT INTO role (name) VALUES ('ROLE_MASTER');
INSERT INTO role (name) VALUES ('ROLE_ADMIN');

-- Insert categories
INSERT INTO category (name) VALUES ('ALL');
INSERT INTO category (name) VALUES ('MOVIE');
INSERT INTO category (name) VALUES ('BOOK');
INSERT INTO category (name) VALUES ('RECIPE');
INSERT INTO category (name) VALUES ('GAME');
INSERT INTO category (name) VALUES ('MUSIC');
-- INSERT INTO category (name) VALUES ('SECRET');

-- 사용자 데이터 삽입
-- INSERT INTO users (username, password, email) VALUES ('ygim', 'WQ5X$8WwOl', 'seunghyeon42@example.org') RETURNING id;
-- INSERT INTO users (username, password, email) VALUES ('asong', '#5XpNgJa+U', 'areum23@example.net') RETURNING id;
-- INSERT INTO users (username, password, email) VALUES ('jiyeonggim', 'FcCSiQFW(3', 'njo@example.net') RETURNING id;
-- INSERT INTO users (username, password, email) VALUES ('jaehyeoncoe', '+dMnBxW03$', 'iujin@example.org') RETURNING id;
-- INSERT INTO users (username, password, email) VALUES ('ni', ')^21GtAacK', 'ujin66@example.net') RETURNING id;
-- INSERT INTO users (username, password, email) VALUES ('pgim', '%0DYIqim6A', 'seongmin06@example.net') RETURNING id;
-- INSERT INTO users (username, password, email) VALUES ('jungsu33', 'm!ly1URs7!', 'lgim@example.org') RETURNING id;
-- INSERT INTO users (username, password, email) VALUES ('gangcunja', 'Z)4EjDxA@c', 'ominjun@example.net') RETURNING id;
-- INSERT INTO users (username, password, email) VALUES ('bgim', '@LI7I7MwTM', 'mu@example.net') RETURNING id;
-- INSERT INTO users (username, password, email) VALUES ('ui', '&o^!J%zH44', 'hansanghun@example.com') RETURNING id;

-- Assign roles to users

        WITH inserted_user AS (
            INSERT INTO users (username, password, email) VALUES ('ygim', bcrypt_hash('WQ5X$8WwOl'), 'seunghyeon42@example.org') RETURNING id
        )
        INSERT INTO user_roles (user_id, role_id)
        SELECT inserted_user.id, role.id
        FROM inserted_user, role
        WHERE role.name = 'ROLE_BRONZE';
        

        WITH inserted_user AS (
            INSERT INTO users (username, password, email) VALUES ('asong', bcrypt_hash('#5XpNgJa+U'), 'areum23@example.net') RETURNING id
        )
        INSERT INTO user_roles (user_id, role_id)
        SELECT inserted_user.id, role.id
        FROM inserted_user, role
        WHERE role.name = 'ROLE_BRONZE';
        

        WITH inserted_user AS (
            INSERT INTO users (username, password, email) VALUES ('jiyeonggim', bcrypt_hash('FcCSiQFW(3'), 'njo@example.net') RETURNING id
        )
        INSERT INTO user_roles (user_id, role_id)
        SELECT inserted_user.id, role.id
        FROM inserted_user, role
        WHERE role.name = 'ROLE_BRONZE';
        

        WITH inserted_user AS (
            INSERT INTO users (username, password, email) VALUES ('jaehyeoncoe', bcrypt_hash('+dMnBxW03$'), 'iujin@example.org') RETURNING id
        )
        INSERT INTO user_roles (user_id, role_id)
        SELECT inserted_user.id, role.id
        FROM inserted_user, role
        WHERE role.name = 'ROLE_BRONZE';
        

        WITH inserted_user AS (
            INSERT INTO users (username, password, email) VALUES ('ni', bcrypt_hash(')^21GtAacK'), 'ujin66@example.net') RETURNING id
        )
        INSERT INTO user_roles (user_id, role_id)
        SELECT inserted_user.id, role.id
        FROM inserted_user, role
        WHERE role.name = 'ROLE_BRONZE';
        

        WITH inserted_user AS (
            INSERT INTO users (username, password, email) VALUES ('pgim', bcrypt_hash('%0DYIqim6A'), 'seongmin06@example.net') RETURNING id
        )
        INSERT INTO user_roles (user_id, role_id)
        SELECT inserted_user.id, role.id
        FROM inserted_user, role
        WHERE role.name = 'ROLE_BRONZE';
        

        WITH inserted_user AS (
            INSERT INTO users (username, password, email) VALUES ('jungsu33', bcrypt_hash('m!ly1URs7!'), 'lgim@example.org') RETURNING id
        )
        INSERT INTO user_roles (user_id, role_id)
        SELECT inserted_user.id, role.id
        FROM inserted_user, role
        WHERE role.name = 'ROLE_BRONZE';
        

        WITH inserted_user AS (
            INSERT INTO users (username, password, email) VALUES ('gangcunja', bcrypt_hash('Z)4EjDxA@c'), 'ominjun@example.net') RETURNING id
        )
        INSERT INTO user_roles (user_id, role_id)
        SELECT inserted_user.id, role.id
        FROM inserted_user, role
        WHERE role.name = 'ROLE_BRONZE';
        

        WITH inserted_user AS (
            INSERT INTO users (username, password, email) VALUES ('bgim', bcrypt_hash('@LI7I7MwTM'), 'mu@example.net') RETURNING id
        )
        INSERT INTO user_roles (user_id, role_id)
        SELECT inserted_user.id, role.id
        FROM inserted_user, role
        WHERE role.name = 'ROLE_BRONZE';
        

        WITH inserted_user AS (
            INSERT INTO users (username, password, email) VALUES ('ui', bcrypt_hash('&o^!J%zH44'), 'hansanghun@example.com') RETURNING id
        )
        INSERT INTO user_roles (user_id, role_id)
        SELECT inserted_user.id, role.id
        FROM inserted_user, role
        WHERE role.name = 'ROLE_BRONZE';
