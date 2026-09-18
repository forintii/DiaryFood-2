package ru.isu.diary.food.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.isu.diary.food.dto.ProfileDTO;
import ru.isu.diary.food.entity.BodyMetrics;
import ru.isu.diary.food.entity.Profile;
import ru.isu.diary.food.entity.User;
import ru.isu.diary.food.enums.ActivityLevel;
import ru.isu.diary.food.repository.BodyMetricsRepository;
import ru.isu.diary.food.repository.ProfileRepository;
import ru.isu.diary.food.repository.UserRepository;
import ru.isu.diary.food.service.ProfileService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final BodyMetricsRepository bodyMetricsRepository;

    @Override
    public ProfileDTO getProfile(UUID userId) {
        Profile profile = profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        BodyMetrics metrics = bodyMetricsRepository
                .findFirstByUser_IdOrderByMeasuredAtDesc(userId).orElse(null);
        return toDTO(profile, metrics);
    }

    @Override
    public ProfileDTO updateProfile(UUID userId, ProfileDTO data) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Profile profile = profileRepository.findByUser_Id(userId)
                .orElse(Profile.builder().user(user).build());
        profile.setName(data.getName());
        profile.setGender(data.getGender());
        profile.setBirthDate(data.getBirthDate());
        profile.setGoal(data.getGoal());
        profileRepository.save(profile);
        return toDTO(profile, null);
    }

    @Override
    public BodyMetrics addBodyMetrics(UUID userId, double height, double weight, ActivityLevel level) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        BodyMetrics metrics = BodyMetrics.builder()
                .user(user).measuredAt(LocalDateTime.now())
                .height(height).weight(weight).activityLevel(level)
                .build();
        return bodyMetricsRepository.save(metrics);
    }

    @Override
    public List<BodyMetrics> getBodyMetricsHistory(UUID userId) {
        return bodyMetricsRepository.findByUser_IdOrderByMeasuredAtDesc(userId);
    }

    private ProfileDTO toDTO(Profile profile, BodyMetrics metrics) {
        ProfileDTO dto = new ProfileDTO();
        dto.setName(profile.getName());
        dto.setGender(profile.getGender());
        dto.setBirthDate(profile.getBirthDate());
        dto.setGoal(profile.getGoal());
        if (metrics != null) {
            dto.setHeight(metrics.getHeight());
            dto.setWeight(metrics.getWeight());
            dto.setActivityLevel(metrics.getActivityLevel());
        }
        return dto;
    }
}
