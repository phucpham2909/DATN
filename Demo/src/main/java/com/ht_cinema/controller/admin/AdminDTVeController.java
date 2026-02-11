package com.ht_cinema.controller.admin;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ht_cinema.model.Booking;
import com.ht_cinema.service.DashboardService;

@Controller
@RequestMapping("/admin/dashboard/ve")
public class AdminDTVeController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("")
    public String viewDoanhThu(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Booking> vePage = dashboardService.getAllDoanhThuVe(pageable);

        model.addAttribute("doanhThuVe", vePage.getContent());

        model.addAttribute("pageData", vePage);

        model.addAttribute("tongTienVe", dashboardService.getTongTienVeAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPagesVe", vePage.getTotalPages());

        model.addAttribute("activeParent", "thongKe");
        model.addAttribute("activePage", "ve");

        return "admin/doanhThu/ve";
    }
    @GetMapping("/filter")
    public String doanhThuTheoKhoangNgay(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Booking> vePage = dashboardService.getDoanhThuVe(start, end, pageable);

        model.addAttribute("doanhThuVe", vePage.getContent());

        model.addAttribute("pageData", vePage);

        model.addAttribute("tongTienVe", dashboardService.getTongTienVe(start, end));

        model.addAttribute("start", start);
        model.addAttribute("end", end);

        model.addAttribute("currentPage", page);

        return "admin/doanhThu/ve";
    }
}