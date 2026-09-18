package ru.isu.diary.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.isu.diary.food.enums.MealType;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryEntryDTO {
    private UUID id;
    private UUID productId;
    private String productName;
    private double weight;
    private LocalDate date;
    private MealType mealType;
    private double calories;
}
