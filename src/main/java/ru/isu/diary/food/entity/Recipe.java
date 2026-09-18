package ru.isu.diary.food.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    // Суммарный вес всех ингредиентов в граммах
    @Builder.Default
    private double totalWeight = 0;

    // Питательность на 100 г готового блюда (вычисляется при создании)
    @Builder.Default
    private double caloriesPer100g = 0;
    @Builder.Default
    private double proteinsPer100g = 0;
    @Builder.Default
    private double fatsPer100g = 0;
    @Builder.Default
    private double carbsPer100g = 0;

    // Фото рецепта — хранится в БД как BLOB, доступно по /recipes/{id}/photo
    @Lob
    @Column(name = "photo", columnDefinition = "LONGBLOB")
    private byte[] photo;

    private String photoContentType; // например "image/jpeg"

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL,
               fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private List<RecipeIngredient> ingredients = new ArrayList<>();
}
