package ru.isu.diary.food.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.isu.diary.food.dto.DailyReportDTO;
import ru.isu.diary.food.dto.DiaryEntryDTO;
import ru.isu.diary.food.entity.DiaryEntry;
import ru.isu.diary.food.entity.Product;
import ru.isu.diary.food.entity.Recipe;
import ru.isu.diary.food.entity.User;
import ru.isu.diary.food.enums.MealType;
import ru.isu.diary.food.repository.DiaryEntryRepository;
import ru.isu.diary.food.repository.ProductRepository;
import ru.isu.diary.food.repository.RecipeRepository;
import ru.isu.diary.food.repository.UserRepository;
import ru.isu.diary.food.service.DiaryService;
import ru.isu.diary.food.util.NutritionCalculator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryEntryRepository diaryEntryRepository;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final NutritionCalculator nutritionCalculator;

    // ── Добавить продукт ─────────────────────────────────────────
    @Override
    public DiaryEntryDTO addEntry(UUID userId, UUID productId, double weight,
                                   LocalDate date, MealType mealType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        DiaryEntry entry = DiaryEntry.builder()
                .user(user).product(product)
                .weight(weight).consumedAt(date).mealType(mealType)
                .build();
        return toDTO(diaryEntryRepository.save(entry));
    }

    // ── Добавить рецепт ──────────────────────────────────────────
    @Override
    public DiaryEntryDTO addRecipeEntry(UUID userId, UUID recipeId, double weight,
                                         LocalDate date, MealType mealType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
        DiaryEntry entry = DiaryEntry.builder()
                .user(user).recipe(recipe)
                .weight(weight).consumedAt(date).mealType(mealType)
                .build();
        return toDTO(diaryEntryRepository.save(entry));
    }

    @Override
    public boolean deleteEntry(UUID entryId) {
        if (!diaryEntryRepository.existsById(entryId)) return false;
        diaryEntryRepository.deleteById(entryId);
        return true;
    }

    @Override
    public List<DiaryEntryDTO> getEntriesByDate(UUID userId, LocalDate date) {
        return diaryEntryRepository.findByUser_IdAndConsumedAt(userId, date)
                .stream().map(this::toDTO).toList();
    }

    // ── Дневной итог ─────────────────────────────────────────────
    @Override
    public DailyReportDTO getDailyRation(UUID userId, LocalDate date) {
        List<DiaryEntry> entries = diaryEntryRepository.findByUser_IdAndConsumedAt(userId, date);

        double calories = sumCal(entries, null);
        double proteins = entries.stream()
                .mapToDouble(e -> getPer100g(e, "prot") * e.getWeight() / 100).sum();
        double fats     = entries.stream()
                .mapToDouble(e -> getPer100g(e, "fat")  * e.getWeight() / 100).sum();
        double carbs    = entries.stream()
                .mapToDouble(e -> getPer100g(e, "carb") * e.getWeight() / 100).sum();

        DailyReportDTO dto = new DailyReportDTO();
        dto.setDate(date);
        dto.setTotalCalories(calories);
        dto.setTotalProteins(proteins);
        dto.setTotalFats(fats);
        dto.setTotalCarbs(carbs);
        dto.setBreakfastCalories(sumCal(entries, MealType.BREAKFAST));
        dto.setLunchCalories(sumCal(entries, MealType.LUNCH));
        dto.setDinnerCalories(sumCal(entries, MealType.DINNER));
        dto.setSnackCalories(sumCal(entries, MealType.SNACK));
        return dto;
    }

    // ── Вспомогательные методы ───────────────────────────────────

    /** Суммирует калории записей (type=null → все типы). */
    private double sumCal(List<DiaryEntry> entries, MealType type) {
        return entries.stream()
                .filter(e -> type == null || e.getMealType() == type)
                .mapToDouble(e -> getPer100g(e, "cal") * e.getWeight() / 100)
                .sum();
    }

    /**
     * Возвращает питательность на 100 г для записи —
     * берёт из продукта или рецепта в зависимости от того, что заполнено.
     * nutrient: "cal", "prot", "fat", "carb"
     */
    private double getPer100g(DiaryEntry e, String nutrient) {
        if (e.getProduct() != null) {
            return switch (nutrient) {
                case "cal"  -> e.getProduct().getCaloriesPer100g();
                case "prot" -> e.getProduct().getProteinsPer100g();
                case "fat"  -> e.getProduct().getFatsPer100g();
                case "carb" -> e.getProduct().getCarbsPer100g();
                default     -> 0;
            };
        }
        if (e.getRecipe() != null) {
            return switch (nutrient) {
                case "cal"  -> e.getRecipe().getCaloriesPer100g();
                case "prot" -> e.getRecipe().getProteinsPer100g();
                case "fat"  -> e.getRecipe().getFatsPer100g();
                case "carb" -> e.getRecipe().getCarbsPer100g();
                default     -> 0;
            };
        }
        return 0;
    }

    /** Конвертирует DiaryEntry в DTO (работает и для продуктов, и для рецептов). */
    private DiaryEntryDTO toDTO(DiaryEntry entry) {
        DiaryEntryDTO dto = new DiaryEntryDTO();
        dto.setId(entry.getId());
        dto.setWeight(entry.getWeight());
        dto.setDate(entry.getConsumedAt());
        dto.setMealType(entry.getMealType());

        double calories = getPer100g(entry, "cal") * entry.getWeight() / 100;
        dto.setCalories(calories);

        if (entry.getProduct() != null) {
            dto.setProductId(entry.getProduct().getId());
            dto.setProductName(entry.getProduct().getName());
        } else if (entry.getRecipe() != null) {
            dto.setProductName(entry.getRecipe().getName());
        }
        return dto;
    }
}
