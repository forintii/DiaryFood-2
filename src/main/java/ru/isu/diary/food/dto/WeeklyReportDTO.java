package ru.isu.diary.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyReportDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyReportDTO> dailyReports;
    private double averageCalories;
}
