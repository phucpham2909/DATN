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
import com.ht_cinema.model.BookingProduct;
import com.ht_cinema.service.DashboardService;

@Controller
@RequestMapping("/admin/dashboard/product")
public class AdminDTProduct {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("")
    public String viewDoanhThu(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        Page<BookingProduct> spPage;

        if (keyword.isEmpty()) {
            spPage = dashboardService.getAllDoanhThuSanPham(pageable);
        } else {
            spPage = dashboardService.searchDoanhThuSanPham(keyword, pageable);
        }

        model.addAttribute("doanhThuSanPham", spPage.getContent());
        model.addAttribute("pageData", spPage); 
        model.addAttribute("tongTienSp", dashboardService.getTongTienSanPhamAll());     
        model.addAttribute("keyword", keyword);

        model.addAttribute("activeParent", "thongKe");
        model.addAttribute("activePage", "sp");

        return "admin/doanhThu/sanPham";
    }
    @GetMapping("/filter")
    public String doanhThuTheoKhoangNgay(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10);

        Page<Booking> vePage = dashboardService.getDoanhThuVe(start, end, pageable);
        Page<BookingProduct> spPage = dashboardService.getDoanhThuSanPham(start, end, pageable);

        model.addAttribute("doanhThuSanPham", spPage.getContent());
        model.addAttribute("tongTienSanPham", dashboardService.getTongTienSanPham(start, end));

        model.addAttribute("currentPage", page);
        model.addAttribute("doanhThuSanPham", spPage.getContent());
        model.addAttribute("pageData", spPage);  
        model.addAttribute("totalPagesSp", spPage.getTotalPages());

        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "admin/doanhThu/sanPhan";
    }
}