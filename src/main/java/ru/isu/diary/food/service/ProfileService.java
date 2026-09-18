package ru.isu.diary.food.service;

import ru.isu.diary.food.dto.ProfileDTO;
import ru.isu.diary.food.entity.BodyMetrics;
import ru.isu.diary.food.enums.ActivityLevel;

import java.util.List;
import java.util.UUID;

public interface ProfileService {
    ProfileDTO getProfile(UUID userId);
    ProfileDTO updateProfile(UUID userId, ProfileDTO profileData);
    BodyMetrics addBodyMetrics(UUID userId, double height, double weight, ActivityLevel activityLevel);
    List<BodyMetrics> getBodyMetricsHistory(UUID userId);
}
