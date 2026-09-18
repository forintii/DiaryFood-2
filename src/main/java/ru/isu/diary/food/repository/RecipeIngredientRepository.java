package ru.isu.diary.food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.isu.diary.food.entity.RecipeIngredient;

import java.util.UUID;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, UUID> {
}
