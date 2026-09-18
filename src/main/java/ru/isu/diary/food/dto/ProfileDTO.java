package ru.isu.diary.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.isu.diary.food.enums.ActivityLevel;
import ru.isu.diary.food.enums.Gender;
import ru.isu.diary.food.enums.Goal;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private String name;
    private Gender gender;
    private LocalDate birthDate;
    private Double height;
    private Double weight;
    private ActivityLevel activityLevel;
    private Goal goal;
}
