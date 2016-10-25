DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS seq;

CREATE SEQUENCE seq START 10000;

CREATE TABLE users
(
  id  INTEGER PRIMARY KEY DEFAULT nextval('seq'),
  full_name VARCHAR(50) NOT NULL,
  email VARCHAR(50) NOT NULL
);