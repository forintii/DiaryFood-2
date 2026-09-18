package ru.isu.diary.food.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.isu.diary.food.entity.Recipe;
import ru.isu.diary.food.entity.User;
import ru.isu.diary.food.service.ProductService;
import ru.isu.diary.food.service.RecipeService;
import ru.isu.diary.food.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final ProductService productService;
    private final UserService userService;

    /** Страница со списком всех рецептов и формой создания */
    @GetMapping
    public String recipesPage(Model model) {
        model.addAttribute("recipes", recipeService.getAllRecipes());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("currentUserEmail", getCurrentUser().getEmail());
        return "recipes";
    }

    /** Создать рецепт */
    @PostMapping("/new")
    public String createRecipe(@RequestParam String name,
                               @RequestParam(required = false) List<UUID> productIds,
                               @RequestParam(required = false) List<Double> weights,
                               @RequestParam(required = false) MultipartFile photo) throws IOException {
        if (productIds == null || productIds.isEmpty()) {
            return "redirect:/recipes?error";
        }
        recipeService.createRecipe(getCurrentUser().getId(), name, productIds, weights, photo);
        return "redirect:/recipes?created";
    }

    /** Отдать фото рецепта */
    @GetMapping("/{id}/photo")
    @ResponseBody
    public ResponseEntity<byte[]> getPhoto(@PathVariable UUID id) {
        Recipe recipe = recipeService.getRecipeById(id);
        byte[] photo = recipe.getPhoto();
        if (photo == null || photo.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String contentType = recipe.getPhotoContentType() != null
                ? recipe.getPhotoContentType() : "image/jpeg";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(photo);
    }

    /** Удалить рецепт */
    @PostMapping("/{id}/delete")
    public String deleteRecipe(@PathVariable UUID id) {
        try {
            recipeService.deleteRecipe(id, getCurrentUser().getId());
        } catch (IllegalStateException e) {
            return "redirect:/recipes?forbidden";
        }
        return "redirect:/recipes";
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByEmail(email);
    }
}
