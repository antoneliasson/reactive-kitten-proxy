# --- First database schema

# --- !Ups

CREATE TABLE kitten (
  id     VARCHAR(255) PRIMARY KEY,
  source VARCHAR(255) NOT NULL,
  image  BINARY NOT NULL,
  date   TIMESTAMP NOT NULL
);

# --- !Downs

DROP TABLE kitten;
