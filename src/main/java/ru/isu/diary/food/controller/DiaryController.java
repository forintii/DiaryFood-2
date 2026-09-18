package ru.isu.diary.food.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.isu.diary.food.dto.DiaryEntryDTO;
import ru.isu.diary.food.entity.User;
import ru.isu.diary.food.enums.MealType;
import ru.isu.diary.food.service.DiaryService;
import ru.isu.diary.food.service.ProductService;
import ru.isu.diary.food.service.RecipeService;
import ru.isu.diary.food.service.ReportService;
import ru.isu.diary.food.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;
    private final ProductService productService;
    private final RecipeService recipeService;
    private final UserService userService;
    private final ReportService reportService;

    @GetMapping("/diary")
    public String diaryPage(Model model,
                             @RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null || date.isAfter(LocalDate.now())) date = LocalDate.now();
        UUID userId = getCurrentUser().getId();

        List<DiaryEntryDTO> entries = diaryService.getEntriesByDate(userId, date);

        double recommended = 0;
        try { recommended = reportService.calculateRecommendedCalories(userId); } catch (Exception ignored) {}

        model.addAttribute("date",        date);
        model.addAttribute("breakfast",   byMeal(entries, MealType.BREAKFAST));
        model.addAttribute("lunch",       byMeal(entries, MealType.LUNCH));
        model.addAttribute("dinner",      byMeal(entries, MealType.DINNER));
        model.addAttribute("snack",       byMeal(entries, MealType.SNACK));
        model.addAttribute("dailyReport", diaryService.getDailyRation(userId, date));
        model.addAttribute("recommended", recommended);
        model.addAttribute("products",    productService.getAllProducts());
        model.addAttribute("recipes",     recipeService.getAllRecipes());

        return "diary";
    }

    /** Добавить продукт */
    @PostMapping("/diary/entry")
    public String addEntry(@RequestParam UUID productId,
                           @RequestParam double weight,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           @RequestParam MealType mealType) {
        if (date.isAfter(LocalDate.now())) return "redirect:/diary?date=" + LocalDate.now();
        diaryService.addEntry(getCurrentUser().getId(), productId, weight, date, mealType);
        return "redirect:/diary?date=" + date;
    }

    /** Добавить рецепт */
    @PostMapping("/diary/entry/recipe")
    public String addRecipeEntry(@RequestParam UUID recipeId,
                                  @RequestParam double weight,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                  @RequestParam MealType mealType) {
        if (date.isAfter(LocalDate.now())) return "redirect:/diary?date=" + LocalDate.now();
        diaryService.addRecipeEntry(getCurrentUser().getId(), recipeId, weight, date, mealType);
        return "redirect:/diary?date=" + date;
    }

    /** Удалить запись */
    @PostMapping("/diary/entry/{id}/delete")
    public String deleteEntry(@PathVariable UUID id,
                               @RequestParam(required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        diaryService.deleteEntry(id);
        return "redirect:/diary?date=" + (date != null ? date : LocalDate.now());
    }

    private List<DiaryEntryDTO> byMeal(List<DiaryEntryDTO> entries, MealType type) {
        return entries.stream().filter(e -> e.getMealType() == type).toList();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByEmail(email);
    }
}
