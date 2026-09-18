package ru.isu.diary.food.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.isu.diary.food.dto.ProductDTO;
import ru.isu.diary.food.service.ProductService;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/new")
    public String newProductPage(Model model) {
        model.addAttribute("product", new ProductDTO());
        return "product-form";
    }

    @PostMapping("/new")
    public String createProduct(@Valid @ModelAttribute("product") ProductDTO product,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "product-form";
        }
        productService.createProduct(product);
        return "redirect:/diary";
    }
}
