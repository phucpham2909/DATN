package com.ht_cinema.controller.suatchieu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.ht_cinema.model.Cinemas;
import com.ht_cinema.repository.CinemasRepository;
import com.ht_cinema.service.CinemaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/cinema")
public class CinemaController {

    @Autowired
    private CinemaService cinemaService;

    @Autowired
    private CinemasRepository cinemasRepository;

    @GetMapping("/list")
    public String listCinemas(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "sort", defaultValue = "asc") String sort
    ) {

        Sort sortOrder = sort.equalsIgnoreCase("desc")
                ? Sort.by("name").descending()
                : Sort.by("name").ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<Cinemas> cinemas;
        if (keyword == null || keyword.isEmpty()) {
            cinemas = cinemaService.findAll(pageable);
        } else {
            cinemas = cinemaService.searchByName(keyword.trim(), pageable);
        }

        model.addAttribute("cinemas", cinemas);
        model.addAttribute("list", cinemas.getContent());
        model.addAttribute("cinema", new Cinemas());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        model.addAttribute("activeParent", "cinema");
        model.addAttribute("activePage", "cinemas");

        return "admin/suatchieu/cinema_list";
    }

    @PostMapping("/add")
    public String addCinema(
            @Valid @ModelAttribute("cinema") Cinemas cinema,
            BindingResult result,
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "sort", defaultValue = "asc") String sort
    ) {

        // Kiểm tra trùng tên
        if (cinemasRepository.existsByName(cinema.getName())) {
            result.rejectValue("name", "error.cinema", "Tên rạp đã tồn tại");
        }

        // Nếu có lỗi → load lại danh sách + trả về trang
        if (result.hasErrors()) {

            Sort sortOrder = sort.equalsIgnoreCase("desc")
                    ? Sort.by("name").descending()
                    : Sort.by("name").ascending();

            Pageable pageable = PageRequest.of(page, size, sortOrder);

            Page<Cinemas> cinemas;
            if (keyword == null || keyword.isEmpty()) {
                cinemas = cinemaService.findAll(pageable);
            } else {
                cinemas = cinemaService.searchByName(keyword.trim(), pageable);
            }

            model.addAttribute("cinemas", cinemas);
            model.addAttribute("list", cinemas.getContent());
            model.addAttribute("keyword", keyword);
            model.addAttribute("sort", sort);

            model.addAttribute("activeParent", "cinema");
            model.addAttribute("activePage", "cinemas");

            return "admin/suatchieu/cinema_list";
        }

        cinemasRepository.save(cinema);
        return "redirect:/admin/cinema/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable("id") Integer id,
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "sort", defaultValue = "asc") String sort
    ) {

        Cinemas cinema = cinemasRepository.findById(id)
                .orElse(new Cinemas());

        Sort sortOrder = sort.equalsIgnoreCase("desc")
                ? Sort.by("name").descending()
                : Sort.by("name").ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<Cinemas> cinemas;
        if (keyword == null || keyword.isEmpty()) {
            cinemas = cinemaService.findAll(pageable);
        } else {
            cinemas = cinemaService.searchByName(keyword.trim(), pageable);
        }

        model.addAttribute("cinemas", cinemas);
        model.addAttribute("list", cinemas.getContent());
        model.addAttribute("cinema", cinema);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);

        model.addAttribute("activeParent", "cinema");
        model.addAttribute("activePage", "cinemas");

        return "admin/suatchieu/cinema_list";
    }

    @PostMapping("/edit")
    public String updateCinema(
            @Valid @ModelAttribute("cinema") Cinemas cinema,
            BindingResult result,
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "sort", defaultValue = "asc") String sort
    ) {

        // Kiểm tra trùng tên nhưng bỏ qua chính nó
        if (cinemasRepository.existsByNameAndIdNot(cinema.getName(), cinema.getId())) {
            result.rejectValue("name", "error.cinema", "Tên rạp đã tồn tại");
        }

        // Nếu có lỗi → load lại danh sách + trả về trang
        if (result.hasErrors()) {

            Sort sortOrder = sort.equalsIgnoreCase("desc")
                    ? Sort.by("name").descending()
                    : Sort.by("name").ascending();

            Pageable pageable = PageRequest.of(page, size, sortOrder);

            Page<Cinemas> cinemas;
            if (keyword == null || keyword.isEmpty()) {
                cinemas = cinemaService.findAll(pageable);
            } else {
                cinemas = cinemaService.searchByName(keyword.trim(), pageable);
            }

            model.addAttribute("cinemas", cinemas);
            model.addAttribute("list", cinemas.getContent());
            model.addAttribute("keyword", keyword);
            model.addAttribute("sort", sort);

            model.addAttribute("activeParent", "cinema");
            model.addAttribute("activePage", "cinemas");

            return "admin/suatchieu/cinema_list";
        }

        cinemasRepository.save(cinema);
        return "redirect:/admin/cinema/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCinema(
            @PathVariable("id") Integer id,
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "sort", defaultValue = "asc") String sort
    ) {

        try {
            cinemasRepository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            model.addAttribute("errorMessage",
                    "Không thể xóa rạp này vì có phòng đang thuộc rạp.");
        }

        return "redirect:/admin/cinema/list?page=" + page + "&keyword=" + keyword + "&sort=" + sort;
    }
}
