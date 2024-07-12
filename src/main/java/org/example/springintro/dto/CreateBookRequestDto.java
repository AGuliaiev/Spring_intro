package org.example.springintro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBookRequestDto {
    private String title;
    private String author;
    private String isbn;
    @Min(0)
    private String price;
    @NotNull
    private String description;
    @NotNull
    private String coverImage;
}
