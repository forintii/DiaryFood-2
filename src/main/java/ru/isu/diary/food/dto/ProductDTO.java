package ru.isu.diary.food.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private UUID id;

    @NotBlank
    private String name;

    @PositiveOrZero
    private double caloriesPer100g;

    @PositiveOrZero
    private double proteinsPer100g;

    @PositiveOrZero
    private double fatsPer100g;

    @PositiveOrZero
    private double carbsPer100g;
}
