CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    contrasenia VARCHAR(50) NOT NULL
);

CREATE TABLE sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT,
    table_id INT,
    subtotal DOUBLE,
    tip DOUBLE,
    total DOUBLE,
    payment_method VARCHAR(50),
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tables (
    table_id INT PRIMARY KEY,
    status_table VARCHAR(20) DEFAULT 'AVAILABLE'
);

CREATE TABLE bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_id INT,
    items TEXT,
    total DOUBLE,
    status_bill VARCHAR(20) DEFAULT 'PENDING',
    discount DOUBLE DEFAULT 0,
    paid_amount DOUBLE default 0,
    FOREIGN KEY (table_id) REFERENCES tables(table_id)
);

CREATE TABLE bill_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT,
    product_name VARCHAR(100),
    price DOUBLE,
    status ENUM('PENDING', 'PAID') DEFAULT 'PENDING',
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE
);

CREATE TABLE waiters (
    waiter_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL
);