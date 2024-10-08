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

        Book bookFirst = books.get(0);
        Book bookSecond = books.get(1);
        Book bookThird = books.get(2);

        assertThat(bookFirst.getTitle()).isEqualTo("Book Title 1");
        assertThat(bookFirst.getAuthor()).isEqualTo("Author 1");
        assertThat(bookFirst.getIsbn()).isEqualTo("ISBN1234567890");
        assertThat(bookFirst.getPrice()).isEqualByComparingTo("19.99");
        assertThat(bookFirst.getDescription()).isEqualTo("Description 1");
        assertThat(bookFirst.getCoverImage()).isEqualTo("cover1.jpg");
        assertThat(bookFirst.isDeleted()).isFalse();
        assertThat(bookFirst.getCategories()).extracting("id").containsExactly(categoryId);

        assertThat(bookSecond.getTitle()).isEqualTo("Book Title 2");
        assertThat(bookSecond.getAuthor()).isEqualTo("Author 2");
        assertThat(bookSecond.getIsbn()).isEqualTo("ISBN0987654321");
        assertThat(bookSecond.getPrice()).isEqualByComparingTo("29.99");
        assertThat(bookSecond.getDescription()).isEqualTo("Description 2");
        assertThat(bookSecond.getCoverImage()).isEqualTo("cover2.jpg");
        assertThat(bookSecond.isDeleted()).isFalse();
        assertThat(bookSecond.getCategories()).extracting("id").containsExactly(categoryId);

        assertThat(bookThird.getTitle()).isEqualTo("Book Title 3");
        assertThat(bookThird.getAuthor()).isEqualTo("Author 3");
        assertThat(bookThird.getIsbn()).isEqualTo("ISBN4546774667");
        assertThat(bookThird.getPrice()).isEqualByComparingTo("39.99");
        assertThat(bookThird.getDescription()).isEqualTo("Description 3");
        assertThat(bookThird.getCoverImage()).isEqualTo("cover3.jpg");
        assertThat(bookThird.isDeleted()).isFalse();
        assertThat(bookThird.getCategories()).extracting("id").containsExactly(categoryId);
    }
}
