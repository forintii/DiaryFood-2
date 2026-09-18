package ru.isu.diary.food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.isu.diary.food.entity.Recipe;

import java.util.List;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    List<Recipe> findAllByOrderByNameAsc();
}
