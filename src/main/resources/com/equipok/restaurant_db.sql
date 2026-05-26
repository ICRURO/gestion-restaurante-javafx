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
    capacity INT DEFAULT 4 /ñ
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

CREATE TABLE wastes (
    waste_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    waste_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    puesto VARCHAR(50) NOT NULL,
    telefono VARCHAR(15),
    turno VARCHAR(20)
);

ALTER TABLE usuarios ADD COLUMN rol ENUM('GERENTE', 'MESERO_CAJERO', 'CHEF') NOT NULL DEFAULT 'GERENTE';

INSERT INTO usuarios (usuario, contrasenia, rol) VALUES ('mesero', '1234', 'MESERO_CAJERO'), ('chef', '1234', 'CHEF');

CREATE TABLE wastes (
    waste_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    waste_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS proveedores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    empresa VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(100),
    categoria VARCHAR(50)
);

-- Datos de prueba iniciales (opcional)
INSERT INTO proveedores (nombre, empresa, telefono, correo, categoria) VALUES
('Carlos Ramos', 'Carnes del Puerto S.A.', '2299112233', 'ventas@carnesdelpuerto.com', 'Alimentos'),
('Maria Fernández', 'Bebidas de Veracruz', '2288445566', 'contacto@bebidasver.com.mx', 'Bebidas');

ALTER TABLE bill_items 
MODIFY COLUMN status ENUM('PENDING', 'IN_PREPARATION', 'READY', 'PAID') DEFAULT 'PENDING';
ALTER TABLE bill_items 
ADD COLUMN special_note VARCHAR(255) DEFAULT '';
ALTER TABLE products 
ADD COLUMN stock INT DEFAULT 50;

CREATE TABLE insumos (
    insumo_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity DOUBLE NOT NULL,
    unit VARCHAR(20) NOT NULL, -- ej. kg, litros, piezas
    min_stock DOUBLE NOT NULL  -- Nos servirá para el CU-18
);