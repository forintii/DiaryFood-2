package ru.isu.diary.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private UUID id;
    private String name;
    private double totalWeight;       // сумма граммов всех ингредиентов
    private double caloriesPer100g;   // ккал на 100 г готового блюда
    private double proteinsPer100g;
    private double fatsPer100g;
    private double carbsPer100g;
    private double totalCalories;     // общие ккал всего рецепта
    private String createdBy;         // email того, кто создал
    private List<String> ingredients; // строки вида "Гречка — 150 г — 173 ккал"
    private boolean hasPhoto;         // есть ли загруженное фото
}
