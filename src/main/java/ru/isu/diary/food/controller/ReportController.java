package ru.isu.diary.food.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.isu.diary.food.dto.DailyReportDTO;
import ru.isu.diary.food.entity.User;
import ru.isu.diary.food.service.ReportService;
import ru.isu.diary.food.service.UserService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @GetMapping
    public String reportPage(Model model,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null || date.isAfter(LocalDate.now())) date = LocalDate.now();
        User user = getCurrentUser();

        DailyReportDTO report = reportService.generateDailyReport(user.getId(), date);
        LocalDate weekStart = date.with(DayOfWeek.MONDAY);
        var weeklyReport = reportService.generateWeeklyReport(user.getId(), weekStart);

        // Данные для графиков
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EE dd.MM", new Locale("ru"));
        List<String> weekLabels = weeklyReport.getDailyReports().stream()
                .map(d -> d.getDate().format(fmt)).toList();
        List<Double> weekCalories = weeklyReport.getDailyReports().stream()
                .map(DailyReportDTO::getTotalCalories).toList();

        model.addAttribute("report", report);
        model.addAttribute("weeklyReport", weeklyReport);
        model.addAttribute("weekLabels", weekLabels);
        model.addAttribute("weekCalories", weekCalories);
        return "report";
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByEmail(email);
    }
}
