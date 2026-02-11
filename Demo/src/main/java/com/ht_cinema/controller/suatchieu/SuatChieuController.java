package com.ht_cinema.controller.suatchieu;

import com.ht_cinema.model.DetailFilms;
import com.ht_cinema.model.SuatChieu;
import com.ht_cinema.repository.DetailFilmsRepository;
import com.ht_cinema.repository.RoomsRepository;
import com.ht_cinema.repository.SuatChieuRepository;
import com.ht_cinema.service.SuatChieuService;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/suatchieu")
public class SuatChieuController {

    @Autowired
    private SuatChieuRepository suatChieuRepository;
    
    @Autowired
    private SuatChieuService suatChieuService;
    
    @Autowired
    private RoomsRepository roomsRepository;

    @Autowired
    private DetailFilmsRepository detailFilmsRepository;

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("suatChieu", new SuatChieu());
        model.addAttribute("detailFilms", detailFilmsRepository.findAll());
        model.addAttribute("rooms", roomsRepository.findAll());
        return "admin/suatchieu/suatChieu";
    }

    @PostMapping("/add")
    public String addSuatChieu(@Valid @ModelAttribute("suatChieu") SuatChieu suatChieu) {
        if (suatChieu.getDetailFilm() != null && suatChieu.getGioBatDau() != null) {
            DetailFilms df = detailFilmsRepository.findById(suatChieu.getDetailFilm().getId()).orElse(null);
            if (df != null) {
                suatChieu.setGioKetThuc(suatChieu.getGioBatDau().plusMinutes(df.getThoiLuong()));
            }
        }
        suatChieuRepository.save(suatChieu);
        return "redirect:/admin/suatchieu/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        SuatChieu suatChieu = suatChieuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy suất chiếu"));
        model.addAttribute("suatChieu", suatChieu);
        model.addAttribute("detailFilms", detailFilmsRepository.findAll());
        model.addAttribute("rooms", roomsRepository.findAll());
        return "admin/suatchieu/suatChieu_edit";
    }

    @PostMapping("/edit")
    public String updateSuatChieu(@Valid @ModelAttribute("suatChieu") SuatChieu suatChieu, BindingResult result,Model model) {
        if (result.hasErrors()) {
            model.addAttribute("detailFilms", detailFilmsRepository.findAll());
            model.addAttribute("rooms", roomsRepository.findAll());
            return "/admin/suatchieu/suatChieu_edit"; // trả về lại form
        }

        if (suatChieu.getDetailFilm() != null && suatChieu.getGioBatDau() != null) {
            DetailFilms df = detailFilmsRepository.findById(suatChieu.getDetailFilm().getId()).orElse(null);
            if (df != null) {
                suatChieu.setGioKetThuc(suatChieu.getGioBatDau().plusMinutes(df.getThoiLuong()));
            }
        }
        suatChieuRepository.save(suatChieu);
        return "redirect:/admin/suatchieu/list";
    }
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/list")
    public String listSuatChieu(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String filmName,
                                @RequestParam(required = false) String fromDate,
                                @RequestParam(required = false) String toDate) {

        Page<SuatChieu> suatChieuPage;

        try {
            if (filmName != null && !filmName.trim().isEmpty() &&
                fromDate != null && !fromDate.isEmpty() &&
                toDate != null && !toDate.isEmpty()) {

                LocalDate from = LocalDate.parse(fromDate, formatter);
                LocalDate to = LocalDate.parse(toDate, formatter);
                suatChieuPage = suatChieuService.findByFilmNameAndDateRange(filmName.trim(), from, to, PageRequest.of(page, size));

            } else if (filmName != null && !filmName.trim().isEmpty()) {

                suatChieuPage = suatChieuService.findByFilmName(filmName.trim(), PageRequest.of(page, size));

            } else if (fromDate != null && !fromDate.isEmpty() &&
                       toDate != null && !toDate.isEmpty()) {

                LocalDate from = LocalDate.parse(fromDate, formatter);
                LocalDate to = LocalDate.parse(toDate, formatter);
                suatChieuPage = suatChieuService.findByDateRange(from, to, PageRequest.of(page, size));

            } else {
                suatChieuPage = suatChieuService.getAll(PageRequest.of(page, size));
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("errorMessage", "Ngày nhập không hợp lệ!");
            suatChieuPage = suatChieuService.getAll(PageRequest.of(page, size));
        }
        model.addAttribute("suatChieuPage", suatChieuPage);
        model.addAttribute("activePage", "suatchieu");
        model.addAttribute("sort", "sort");
        model.addAttribute("filmName", filmName);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        return "admin/suatchieu/suatchieu_list";
    }

    @GetMapping("/delete/{id}")
    public String deleteSuatChieu(@PathVariable Integer id,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String filmName,
                                  @RequestParam(required = false) String fromDate,
                                  @RequestParam(required = false) String toDate) {
        suatChieuService.deleteSuatChieu(id);
        String redirectUrl = String.format("redirect:/admin/suatchieu/list?page=%d&size=%d", page, size);
        if (filmName != null && !filmName.isEmpty()) redirectUrl += "&filmName=" + filmName;
        if (fromDate != null && !fromDate.isEmpty()) redirectUrl += "&fromDate=" + fromDate;
        if (toDate != null && !toDate.isEmpty()) redirectUrl += "&toDate=" + toDate;

        return redirectUrl;
    }
}
