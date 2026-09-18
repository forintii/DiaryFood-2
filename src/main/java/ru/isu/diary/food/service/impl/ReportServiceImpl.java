package ru.isu.diary.food.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.isu.diary.food.dto.DailyReportDTO;
import ru.isu.diary.food.dto.MonthlyReportDTO;
import ru.isu.diary.food.dto.WeeklyReportDTO;
import ru.isu.diary.food.entity.BodyMetrics;
import ru.isu.diary.food.entity.Profile;
import ru.isu.diary.food.repository.BodyMetricsRepository;
import ru.isu.diary.food.repository.ProfileRepository;
import ru.isu.diary.food.service.DiaryService;
import ru.isu.diary.food.service.ReportService;
import ru.isu.diary.food.util.NutritionCalculator;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final DiaryService diaryService;
    private final ProfileRepository profileRepository;
    private final BodyMetricsRepository bodyMetricsRepository;
    private final NutritionCalculator nutritionCalculator;

    @Override
    public DailyReportDTO generateDailyReport(UUID userId, LocalDate date) {
        DailyReportDTO report = diaryService.getDailyRation(userId, date);
        double recommended = calculateRecommendedCalories(userId);
        report.setRecommendedCalories(recommended);
        report.setDeviationPercent(nutritionCalculator.calculateDeviation(report.getTotalCalories(), recommended));
        if (recommended > 0) {
            report.setRecommendedProteins(nutritionCalculator.recommendedProteins(recommended));
            report.setRecommendedFats(nutritionCalculator.recommendedFats(recommended));
            report.setRecommendedCarbs(nutritionCalculator.recommendedCarbs(recommended));
        }
        return report;
    }

    @Override
    public WeeklyReportDTO generateWeeklyReport(UUID userId, LocalDate startDate) {
        List<DailyReportDTO> days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            days.add(generateDailyReport(userId, startDate.plusDays(i)));
        }
        WeeklyReportDTO report = new WeeklyReportDTO();
        report.setStartDate(startDate);
        report.setEndDate(startDate.plusDays(6));
        report.setDailyReports(days);
        report.setAverageCalories(days.stream().mapToDouble(DailyReportDTO::getTotalCalories).average().orElse(0));
        return report;
    }

    @Override
    public MonthlyReportDTO generateMonthlyReport(UUID userId, YearMonth yearMonth) {
        List<DailyReportDTO> days = new ArrayList<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            days.add(generateDailyReport(userId, yearMonth.atDay(day)));
        }
        MonthlyReportDTO report = new MonthlyReportDTO();
        report.setYearMonth(yearMonth);
        report.setDailyReports(days);
        report.setAverageCalories(days.stream().mapToDouble(DailyReportDTO::getTotalCalories).average().orElse(0));
        return report;
    }

    @Override
    public double calculateRecommendedCalories(UUID userId) {
        BodyMetrics metrics = bodyMetricsRepository
                .findFirstByUser_IdOrderByMeasuredAtDesc(userId).orElse(null);
        Profile profile = profileRepository.findByUser_Id(userId).orElse(null);

        if (metrics == null || profile == null || profile.getBirthDate() == null || profile.getGender() == null) {
            return 0;
        }

        int age = Period.between(profile.getBirthDate(), LocalDate.now()).getYears();
        double bmr = nutritionCalculator.calculateBMR(
                metrics.getWeight(), metrics.getHeight(), age, profile.getGender()
        );
        double tdee = nutritionCalculator.calculateDailyNorm(bmr, metrics.getActivityLevel());
        return nutritionCalculator.applyGoalAdjustment(tdee, profile.getGoal(), profile.getGender());
    }
}
