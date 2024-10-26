INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'john.doe@example.com', '$2a$10$eUSaZyTY8GwFNeDMNPn.1.2YyGDN7tx2wsCgyy4mlZK9yNtovW5I6', 'John', 'Doe', 'Zabolotnogo 13', FALSE),
       (2, 'jane.doe@example.com', '$2a$10$hexogMMYbbZy.aXV2hSnIuYj8K9221rpf4yVMXF/wbPymdkECwhWC', 'Jane', 'Doe', 'Zabolotnogo 13', FALSE)
ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    password = VALUES(password),
    first_name = VALUES(first_name),
    last_name = VALUES(last_name),
    shipping_address = VALUES(shipping_address),
    is_deleted = VALUES(is_deleted);
