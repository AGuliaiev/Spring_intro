package org.example.springintro.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.springintro.model.Book;
import org.example.springintro.repository.SpecificationProvider;
import org.example.springintro.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {

    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(provider -> provider.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't find correct"
                        + " specification provider for kye " + key));
    }
}
