INSERT INTO orders (id, user_id, status, total, order_date, shipping_address, is_deleted)
VALUES
 (1, 1, 'PENDING', 100.00, '2024-10-11T12:00:00', 'Kiev', FALSE),
 (2, 1, 'COMPLETED', 400.00, '2024-10-11T12:00:10', 'Kiev', FALSE),
 (3, 1, 'DELIVERED', 300.95, '2024-10-11T12:00:20', 'Kiev', FALSE);
