package ru.isu.diary.food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.isu.diary.food.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByNameContainingIgnoreCase(String name);
}
