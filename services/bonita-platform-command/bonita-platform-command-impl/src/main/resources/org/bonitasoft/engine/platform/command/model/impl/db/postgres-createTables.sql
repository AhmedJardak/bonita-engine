CREATE TABLE platformCommand (
  id INT8 PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  description TEXT,
  IMPLEMENTATION VARCHAR(100) NOT NULL
);