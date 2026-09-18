package ru.isu.diary.food.service;

import org.springframework.web.multipart.MultipartFile;
import ru.isu.diary.food.dto.RecipeDTO;
import ru.isu.diary.food.entity.Recipe;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface RecipeService {
    List<RecipeDTO> getAllRecipes();
    void createRecipe(UUID userId, String name, List<UUID> productIds,
                      List<Double> weights, MultipartFile photo) throws IOException;
    void deleteRecipe(UUID recipeId, UUID currentUserId);
    Recipe getRecipeById(UUID id);
}
