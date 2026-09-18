package ru.isu.diary.food.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.isu.diary.food.dto.BodyMetricsRequest;
import ru.isu.diary.food.dto.ProfileDTO;
import ru.isu.diary.food.entity.BodyMetrics;
import ru.isu.diary.food.entity.User;
import ru.isu.diary.food.service.ProfileService;
import ru.isu.diary.food.service.UserService;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    @GetMapping
    public String profilePage(Model model) {
        UUID userId = getCurrentUser().getId();

        ProfileDTO profile;
        try {
            profile = profileService.getProfile(userId);
        } catch (IllegalArgumentException e) {
            profile = new ProfileDTO();
        }

        List<BodyMetrics> allHistory = profileService.getBodyMetricsHistory(userId);
        model.addAttribute("profile", profile);
        model.addAttribute("metricsRequest", new BodyMetricsRequest());
        model.addAttribute("metricsHistory", allHistory.subList(0, Math.min(10, allHistory.size())));
        model.addAttribute("metricsTotal", allHistory.size());
        return "profile";
    }

    @PostMapping
    public String updateProfile(@ModelAttribute ProfileDTO profile) {
        profileService.updateProfile(getCurrentUser().getId(), profile);
        return "redirect:/profile?saved";
    }

    @PostMapping("/metrics")
    public String addMetrics(@ModelAttribute BodyMetricsRequest request) {
        if (request.getHeight() != null && request.getWeight() != null
                && request.getHeight() > 0 && request.getWeight() > 0
                && request.getActivityLevel() != null) {
            profileService.addBodyMetrics(
                    getCurrentUser().getId(),
                    request.getHeight(),
                    request.getWeight(),
                    request.getActivityLevel()
            );
        }
        return "redirect:/profile?metrics";
    }

    @GetMapping("/metrics/history")
    public String fullHistory(Model model) {
        UUID userId = getCurrentUser().getId();
        model.addAttribute("metricsHistory", profileService.getBodyMetricsHistory(userId));
        return "metrics-history";
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByEmail(email);
    }
}
