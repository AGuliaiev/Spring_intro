package org.example.springintro.repository.book.spec;

import java.util.Arrays;
import org.example.springintro.model.Book;
import org.example.springintro.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    private static final String TITLE = "title";

    @Override
    public String getKey() {
        return TITLE;
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(TITLE)
                .in(Arrays.stream(params).toArray());
    }
}
