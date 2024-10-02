package org.example.springintro.repository.book;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import org.example.springintro.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books by category ID")
    @Sql(scripts = {
            "classpath:database/books/add-books-and-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/remove-books-and-categories.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategories_Id_ReturnsBooksForCategory() {
        Long categoryId = 1L;
        List<Book> books = bookRepository.findAllByCategories_Id(categoryId);

        assertThat(books).isNotEmpty();
        assertThat(books.size()).isEqualTo(3);

        Book book1 = books.get(0);
        Book book2 = books.get(1);
        Book book3 = books.get(2);

        assertThat(book1.getTitle()).isEqualTo("Book Title 1");
        assertThat(book1.getAuthor()).isEqualTo("Author 1");
        assertThat(book1.getIsbn()).isEqualTo("ISBN1234567890");
        assertThat(book1.getPrice()).isEqualByComparingTo("19.99");
        assertThat(book1.getDescription()).isEqualTo("Description 1");
        assertThat(book1.getCoverImage()).isEqualTo("cover1.jpg");
        assertThat(book1.isDeleted()).isFalse();
        assertThat(book1.getCategories()).extracting("id").containsExactly(categoryId);

        assertThat(book2.getTitle()).isEqualTo("Book Title 2");
        assertThat(book2.getAuthor()).isEqualTo("Author 2");
        assertThat(book2.getIsbn()).isEqualTo("ISBN0987654321");
        assertThat(book2.getPrice()).isEqualByComparingTo("29.99");
        assertThat(book2.getDescription()).isEqualTo("Description 2");
        assertThat(book2.getCoverImage()).isEqualTo("cover2.jpg");
        assertThat(book2.isDeleted()).isFalse();
        assertThat(book2.getCategories()).extracting("id").containsExactly(categoryId);

        assertThat(book3.getTitle()).isEqualTo("Book Title 3");
        assertThat(book3.getAuthor()).isEqualTo("Author 3");
        assertThat(book3.getIsbn()).isEqualTo("ISBN4546774667");
        assertThat(book3.getPrice()).isEqualByComparingTo("39.99");
        assertThat(book3.getDescription()).isEqualTo("Description 3");
        assertThat(book3.getCoverImage()).isEqualTo("cover3.jpg");
        assertThat(book3.isDeleted()).isFalse();
        assertThat(book3.getCategories()).extracting("id").containsExactly(categoryId);
    }
}
