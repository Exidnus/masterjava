DROP TABLE IF EXISTS send_email_results;
DROP TABLE IF EXISTS emails;

CREATE TABLE emails (
  id      INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
  subject TEXT NOT NULL,
  message TEXT NOT NULL
);

CREATE TABLE send_email_results (
  id            INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
  email_address TEXT    NOT NULL,
  succeed    BOOLEAN NOT NULL,
  fail_cause    TEXT,
  --date_time     TIMESTAMP DEFAULT now(),
  id_email      INTEGER NOT NULL REFERENCES emails (id)
);
