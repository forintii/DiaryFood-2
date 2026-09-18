package ru.isu.diary.food.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.isu.diary.food.dto.ProductDTO;
import ru.isu.diary.food.entity.Product;
import ru.isu.diary.food.repository.ProductRepository;
import ru.isu.diary.food.service.ProductService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public List<ProductDTO> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query).stream().map(this::toDTO).toList();
    }

    @Override
    public ProductDTO createProduct(ProductDTO data) {
        Product product = Product.builder()
                .name(data.getName())
                .caloriesPer100g(data.getCaloriesPer100g())
                .proteinsPer100g(data.getProteinsPer100g())
                .fatsPer100g(data.getFatsPer100g())
                .carbsPer100g(data.getCarbsPer100g())
                .custom(false)
                .build();
        return toDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO getProductById(UUID id) {
        return productRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCaloriesPer100g(product.getCaloriesPer100g());
        dto.setProteinsPer100g(product.getProteinsPer100g());
        dto.setFatsPer100g(product.getFatsPer100g());
        dto.setCarbsPer100g(product.getCarbsPer100g());
        return dto;
    }
}
