CREATE TABLE users (
                       user_id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       email    VARCHAR(255) NOT NULL,
                       password VARCHAR(255)
);

CREATE UNIQUE INDEX uq_users_username ON users (UPPER(username));
CREATE UNIQUE INDEX uq_users_email ON USERS (UPPER(email));