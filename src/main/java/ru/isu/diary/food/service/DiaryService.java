package ru.isu.diary.food.service;

import ru.isu.diary.food.dto.DailyReportDTO;
import ru.isu.diary.food.dto.DiaryEntryDTO;
import ru.isu.diary.food.enums.MealType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DiaryService {
    DiaryEntryDTO addEntry(UUID userId, UUID productId, double weight, LocalDate date, MealType mealType);
    DiaryEntryDTO addRecipeEntry(UUID userId, UUID recipeId, double weight, LocalDate date, MealType mealType);
    boolean deleteEntry(UUID entryId);
    List<DiaryEntryDTO> getEntriesByDate(UUID userId, LocalDate date);
    DailyReportDTO getDailyRation(UUID userId, LocalDate date);
}
