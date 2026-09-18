package ru.isu.diary.food.service;

import ru.isu.diary.food.dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    List<ProductDTO> searchProducts(String query);
    ProductDTO createProduct(ProductDTO productData);
    ProductDTO getProductById(UUID id);
}
