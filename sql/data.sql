-- Populate users
INSERT INTO users (username, email, first_name, last_name) VALUES ('johndoe', 'john.doe@example.com', 'John', 'Doe');
INSERT INTO users (username, email, first_name, last_name) VALUES ('janedoe', 'jane.doe@example.com', 'Jane', 'Doe');

-- Populate products
INSERT INTO products (name, description, price, stock_quantity, category) VALUES ('Laptop', 'High-performance laptop', 999.99, 50, 'Electronics');
INSERT INTO products (name, description, price, stock_quantity, category) VALUES ('Mouse', 'Wireless mouse', 29.99, 200, 'Electronics');
INSERT INTO products (name, description, price, stock_quantity, category) VALUES ('Desk Chair', 'Ergonomic office chair', 249.99, 30, 'Furniture');

-- Populate orders
-- Note: The user_id and product_ids might need to be adjusted if the IDs are not auto-incremented starting from 1
INSERT INTO orders (user_id, total_amount, status, created_at) VALUES (1, 1029.98, 'PENDING', '2026-01-04 00:00:00');

-- Populate order_product join table
INSERT INTO order_products (order_id, product_id) VALUES (1, 1);
INSERT INTO order_products (order_id, product_id) VALUES (1, 2);

