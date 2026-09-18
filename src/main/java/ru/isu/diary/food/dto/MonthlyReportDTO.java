package ru.isu.diary.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportDTO {
    private YearMonth yearMonth;
    private List<DailyReportDTO> dailyReports;
    private double averageCalories;
}
