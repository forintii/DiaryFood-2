package ru.isu.diary.food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.isu.diary.food.entity.DiaryEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, UUID> {
    List<DiaryEntry> findByUser_IdAndConsumedAt(UUID userId, LocalDate date);
    List<DiaryEntry> findByUser_IdAndConsumedAtBetween(UUID userId, LocalDate startDate, LocalDate endDate);
    void deleteByRecipe_Id(UUID recipeId);
}
