# --- First database schema

# --- !Ups

CREATE TABLE kitten (
  id     INT PRIMARY KEY AUTO_INCREMENT,
  source VARCHAR(255) NOT NULL,
  image  BINARY NOT NULL,
  date   TIMESTAMP NOT NULL
);

# --- !Downs

DROP TABLE kitten;
