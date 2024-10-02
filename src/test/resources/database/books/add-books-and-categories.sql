INSERT INTO categories (id, name, description, is_deleted) VALUES
    (1, 'Fiction', 'Fictional books', FALSE);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted) VALUES
                                                                                             (1, 'Book Title 1', 'Author 1', 'ISBN1234567890', 19.99, 'Description 1', 'cover1.jpg', FALSE),
                                                                                             (2, 'Book Title 2', 'Author 2', 'ISBN0987654321', 29.99, 'Description 2', 'cover2.jpg', FALSE),
                                                                                             (3, 'Book Title 3', 'Author 3', 'ISBN4546774667', 39.99, 'Description 3', 'cover3.jpg', FALSE);

INSERT INTO books_categories (book_id, category_id) VALUES
                                                        (1, 1),
                                                        (2, 1),
                                                        (3, 1);