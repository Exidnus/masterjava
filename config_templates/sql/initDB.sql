DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS user_seq;
DROP TYPE IF EXISTS user_flag;

DROP TABLE IF EXISTS cities;
DROP SEQUENCE IF EXISTS city_seq;

DROP TABLE IF EXISTS groups;
DROP SEQUENCE IF EXISTS group_seq;
DROP TYPE IF EXISTS group_type;

DROP TABLE IF EXISTS projects;
DROP SEQUENCE IF EXISTS project_seq;

DROP TABLE IF EXISTS groups_users;

CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');
CREATE TYPE group_type AS ENUM ('CURRENT', 'FINISHED');

CREATE SEQUENCE city_seq START 100000;
CREATE SEQUENCE project_seq START 100000;
CREATE SEQUENCE group_seq START 100000;
CREATE SEQUENCE user_seq START 100000;

CREATE TABLE cities (
  id     INTEGER PRIMARY KEY DEFAULT nextval('city_seq'),
  id_str TEXT NOT NULL UNIQUE,
  name   TEXT NOT NULL
);


CREATE TABLE projects (
  id          INTEGER PRIMARY KEY DEFAULT nextval('project_seq'),
  name        TEXT NOT NULL,
  description TEXT
);

CREATE TABLE groups_users (
  user_id  INTEGER NOT NULL,
  group_id INTEGER NOT NULL
);

CREATE TABLE groups (
  id         INTEGER PRIMARY KEY DEFAULT nextval('group_seq'),
  name       TEXT    NOT NULL,
  type       group_type NOT NULL--,
  --project_id INTEGER NOT NULL,
  --FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);

CREATE TABLE users (
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  full_name TEXT      NOT NULL,
  email     TEXT      NOT NULL,
  flag      user_flag NOT NULL,
  city_id   INTEGER NOT NULL--,
  --group_id  INTEGER NOT NULL--,
  --FOREIGN KEY (city_id) REFERENCES cities (id) ON DELETE CASCADE
);

