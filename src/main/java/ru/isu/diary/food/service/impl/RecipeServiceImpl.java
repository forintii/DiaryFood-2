package ru.isu.diary.food.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.isu.diary.food.dto.RecipeDTO;
import ru.isu.diary.food.entity.Product;
import ru.isu.diary.food.entity.Recipe;
import ru.isu.diary.food.entity.RecipeIngredient;
import ru.isu.diary.food.entity.User;
import ru.isu.diary.food.repository.DiaryEntryRepository;
import ru.isu.diary.food.repository.ProductRepository;
import ru.isu.diary.food.repository.RecipeIngredientRepository;
import ru.isu.diary.food.repository.RecipeRepository;
import ru.isu.diary.food.repository.UserRepository;
import ru.isu.diary.food.service.RecipeService;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final DiaryEntryRepository diaryEntryRepository;

    @Override
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAllByOrderByNameAsc()
                .stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional
    public void createRecipe(UUID userId, String name, List<UUID> productIds,
                             List<Double> weights, MultipartFile photo) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Сохраняем рецепт без ингредиентов (нужен id для связи)
        Recipe recipe = Recipe.builder().name(name).createdBy(user).build();

        // Фото (BLOB)
        if (photo != null && !photo.isEmpty()) {
            recipe.setPhoto(photo.getBytes());
            recipe.setPhotoContentType(photo.getContentType());
        }

        recipe = recipeRepository.save(recipe);

        double totalWeight = 0, totalCal = 0, totalProt = 0, totalFat = 0, totalCarb = 0;

        for (int i = 0; i < productIds.size(); i++) {
            if (i >= weights.size()) break;
            double w = weights.get(i);
            if (w <= 0) continue;

            Product p = productRepository.findById(productIds.get(i)).orElse(null);
            if (p == null) continue;

            RecipeIngredient ri = RecipeIngredient.builder()
                    .recipe(recipe).product(p).weight(w).build();
            recipeIngredientRepository.save(ri);

            totalWeight += w;
            totalCal   += p.getCaloriesPer100g() * w / 100;
            totalProt  += p.getProteinsPer100g()  * w / 100;
            totalFat   += p.getFatsPer100g()       * w / 100;
            totalCarb  += p.getCarbsPer100g()      * w / 100;
        }

        // Обновляем питательность на 100 г
        recipe.setTotalWeight(totalWeight);
        if (totalWeight > 0) {
            recipe.setCaloriesPer100g(totalCal / totalWeight * 100);
            recipe.setProteinsPer100g(totalProt / totalWeight * 100);
            recipe.setFatsPer100g(totalFat     / totalWeight * 100);
            recipe.setCarbsPer100g(totalCarb   / totalWeight * 100);
        }
        recipeRepository.save(recipe);
    }

    @Override
    @Transactional
    public void deleteRecipe(UUID recipeId, UUID currentUserId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + recipeId));
        if (recipe.getCreatedBy() == null || !recipe.getCreatedBy().getId().equals(currentUserId)) {
            throw new IllegalStateException("Нет прав для удаления этого рецепта");
        }
        // Сначала удаляем записи дневника, ссылающиеся на этот рецепт
        diaryEntryRepository.deleteByRecipe_Id(recipeId);
        // Затем удаляем сам рецепт (ингредиенты удалятся каскадно)
        recipeRepository.deleteById(recipeId);
    }

    @Override
    public Recipe getRecipeById(UUID id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: " + id));
    }

    // ── Конвертация в DTO ────────────────────────────────────────
    private RecipeDTO toDTO(Recipe r) {
        RecipeDTO dto = new RecipeDTO();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setTotalWeight(r.getTotalWeight());
        dto.setCaloriesPer100g(r.getCaloriesPer100g());
        dto.setProteinsPer100g(r.getProteinsPer100g());
        dto.setFatsPer100g(r.getFatsPer100g());
        dto.setCarbsPer100g(r.getCarbsPer100g());
        dto.setTotalCalories(r.getCaloriesPer100g() * r.getTotalWeight() / 100);
        dto.setCreatedBy(r.getCreatedBy() != null ? r.getCreatedBy().getEmail() : "");
        dto.setIngredients(
                r.getIngredients().stream()
                        .map(ri -> ri.getProduct().getName()
                                + " — " + (int) ri.getWeight() + " г"
                                + " — " + (int) (ri.getProduct().getCaloriesPer100g() * ri.getWeight() / 100) + " ккал")
                        .toList()
        );
        dto.setHasPhoto(r.getPhoto() != null && r.getPhoto().length > 0);
        return dto;
    }
}
