package org.example.spring_intro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    id (Long, PK)
//title (String, not null)
//author (String, not null)
//isbn (String, not null, unique)
//price (BigDecimal, not null)
//description (String)
//coverImage (String)
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String price;
    private String description;
    private String coverImage;

}
