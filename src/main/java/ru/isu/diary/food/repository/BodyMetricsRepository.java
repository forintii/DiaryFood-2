package ru.isu.diary.food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.isu.diary.food.entity.BodyMetrics;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BodyMetricsRepository extends JpaRepository<BodyMetrics, UUID> {
    List<BodyMetrics> findByUser_IdOrderByMeasuredAtDesc(UUID userId);
    Optional<BodyMetrics> findFirstByUser_IdOrderByMeasuredAtDesc(UUID userId);
}
