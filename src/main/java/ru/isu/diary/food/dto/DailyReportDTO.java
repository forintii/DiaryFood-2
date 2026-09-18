package ru.isu.diary.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReportDTO {
    private LocalDate date;
    private double totalCalories;
    private double totalProteins;
    private double totalFats;
    private double totalCarbs;
    private double recommendedCalories;
    private double deviationPercent;
    // Разбивка по приёмам пищи
    private double breakfastCalories;
    private double lunchCalories;
    private double dinnerCalories;
    private double snackCalories;
    // Рекомендуемые нормы БЖУ (г)
    private double recommendedProteins;
    private double recommendedFats;
    private double recommendedCarbs;
}
