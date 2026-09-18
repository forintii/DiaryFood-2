package ru.isu.diary.food.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.isu.diary.food.enums.ActivityLevel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyMetricsRequest {
    private Double height;
    private Double weight;
    private ActivityLevel activityLevel;
}
