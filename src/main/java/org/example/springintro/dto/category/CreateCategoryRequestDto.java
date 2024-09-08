package org.example.springintro.dto.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateCategoryRequestDto {
    @NotBlank
    private String name;
    private String description;
}
