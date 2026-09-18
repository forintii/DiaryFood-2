package ru.isu.diary.food.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ru.isu.diary.food.enums.ActivityLevel;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "body_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime measuredAt;

    private double height;
    private double weight;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public double bmi() {
        if (height == 0) return 0;
        double heightM = height / 100.0;
        return weight / (heightM * heightM);
    }
}
