package ru.isu.diary.food.util;

import org.springframework.stereotype.Component;
import ru.isu.diary.food.entity.Product;
import ru.isu.diary.food.enums.ActivityLevel;
import ru.isu.diary.food.enums.Gender;
import ru.isu.diary.food.enums.Goal;

@Component
public class NutritionCalculator {

    public double calculateCalories(Product product, double weight) {
        return product.getCaloriesPer100g() * weight / 100.0;
    }

    // Формула Миффлина-Сан-Жеора
    public double calculateBMR(double weight, double height, int age, Gender gender) {
        double bmr = 10 * weight + 6.25 * height - 5 * age;
        return gender == Gender.MALE ? bmr + 5 : bmr - 161;
    }

    public double calculateDailyNorm(double bmr, ActivityLevel level) {
        double multiplier = switch (level) {
            case SEDENTARY          -> 1.2;
            case LIGHTLY_ACTIVE     -> 1.375;
            case MODERATELY_ACTIVE  -> 1.55;
            case VERY_ACTIVE        -> 1.725;
            case EXTRA_ACTIVE       -> 1.9;
        };
        return bmr * multiplier;
    }

    // Корректировка под цель
    public double applyGoalAdjustment(double tdee, Goal goal, Gender gender) {
        if (goal == null) return tdee;
        double result = switch (goal) {
            case WEIGHT_LOSS -> {
                // -20%, но не более 500 ккал дефицита
                double deficit = Math.min(tdee * 0.20, 500.0);
                yield tdee - deficit;
            }
            case WEIGHT_GAIN  -> tdee * 1.20;   // +20%
            case MAINTENANCE  -> tdee;
        };
        // Минимальный порог безопасности
        double minCalories = (gender == Gender.FEMALE) ? 1200.0 : 1500.0;
        return Math.max(result, minCalories);
    }

    public double calculateBMI(double weight, double height) {
        if (height == 0) return 0;
        double heightM = height / 100.0;
        return weight / (heightM * heightM);
    }

    public double calculateDeviation(double actual, double recommended) {
        if (recommended == 0) return 0;
        return (actual - recommended) / recommended * 100.0;
    }

    // Рекомендуемые нормы БЖУ в граммах:
    // Белки 30% от ккал / 4 ккал на г, Жиры 30% / 9 ккал на г, Углеводы 40% / 4 ккал на г
    public double recommendedProteins(double calories) { return calories * 0.30 / 4.0; }
    public double recommendedFats(double calories)     { return calories * 0.30 / 9.0; }
    public double recommendedCarbs(double calories)    { return calories * 0.40 / 4.0; }
}
