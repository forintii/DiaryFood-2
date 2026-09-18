package ru.isu.diary.food.service;

import ru.isu.diary.food.dto.DailyReportDTO;
import ru.isu.diary.food.dto.MonthlyReportDTO;
import ru.isu.diary.food.dto.WeeklyReportDTO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

public interface ReportService {
    DailyReportDTO generateDailyReport(UUID userId, LocalDate date);
    WeeklyReportDTO generateWeeklyReport(UUID userId, LocalDate startDate);
    MonthlyReportDTO generateMonthlyReport(UUID userId, YearMonth yearMonth);
    double calculateRecommendedCalories(UUID userId);
}
