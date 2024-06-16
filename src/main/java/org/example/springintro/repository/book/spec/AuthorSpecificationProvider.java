package org.example.springintro.repository.book.spec;

import java.util.Arrays;
import org.example.springintro.model.Book;
import org.example.springintro.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private final String AUTHOR = "author";

    @Override
    public String getKey() {
        return AUTHOR;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(AUTHOR)
                .in(Arrays.stream(params).toArray());
    }
}
