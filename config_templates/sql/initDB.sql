DROP TABLE IF EXISTS users;
DROP TYPE IF EXISTS user_flag;

DROP TABLE IF EXISTS cities;

DROP TABLE IF EXISTS groups;
DROP TYPE IF EXISTS group_type;
DROP TABLE IF EXISTS projects;

DROP TABLE IF EXISTS users_groups;

DROP SEQUENCE IF EXISTS common_seq;

CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');
CREATE TYPE group_type AS ENUM ('CURRENT', 'FINISHED');

CREATE SEQUENCE common_seq START 100000;

CREATE TABLE cities (
  id     INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
  id_str TEXT NOT NULL UNIQUE,
  name   TEXT NOT NULL
);


CREATE TABLE projects (
  id          INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
  name        TEXT NOT NULL,
  description TEXT
);

CREATE TABLE users_groups (
  user_id  INTEGER NOT NULL,
  group_id INTEGER NOT NULL
);

CREATE TABLE groups (
  id         INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
  name       TEXT    NOT NULL,
  type       group_type NOT NULL,
  project_id INTEGER NOT NULL,
  FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);

CREATE TABLE users (
  id        INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
  full_name TEXT      NOT NULL,
  email     TEXT      NOT NULL,
  flag      user_flag NOT NULL,
  city_id   INTEGER NOT NULL,
  FOREIGN KEY (city_id) REFERENCES cities (id) ON DELETE CASCADE
);

