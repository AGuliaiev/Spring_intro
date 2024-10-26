DELETE FROM users_roles WHERE user_id = 1;
INSERT IGNORE INTO users_roles (user_id, role_id) VALUES
(1, 1),
(2, 2);
