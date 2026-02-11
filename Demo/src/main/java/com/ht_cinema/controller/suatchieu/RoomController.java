package com.ht_cinema.controller.suatchieu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ht_cinema.model.Cinemas;
import com.ht_cinema.model.Rooms;
import com.ht_cinema.repository.CinemasRepository;
import com.ht_cinema.repository.RoomsRepository;
import com.ht_cinema.service.RoomService;

@Controller
@RequestMapping("/admin/room")
public class RoomController {

	@Autowired
	private RoomService roomService;

	@Autowired
	private RoomsRepository roomsRepository;

	@Autowired
	private CinemasRepository cinemasRepository;

	@GetMapping("/list")
	public String listRooms(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String keyword,
			@RequestParam(value = "sort", defaultValue = "asc") String sort,
			@RequestParam(required = false) Integer id) {

		Sort sortOrder = sort.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();

		Pageable pageable = PageRequest.of(page, size, sortOrder);

		Page<Rooms> rooms = (keyword != null && !keyword.isEmpty()) ? roomService.searchByName(keyword, pageable)
				: roomService.findAll(pageable);

		Rooms room = (id != null) ? roomsRepository.findById(id).orElse(new Rooms()) : new Rooms();

		model.addAttribute("room", room);
		model.addAttribute("rooms", rooms);
		model.addAttribute("cinemas", cinemasRepository.findAll());

		model.addAttribute("keyword", keyword);
		model.addAttribute("sort", sort);

		model.addAttribute("activeParent", "cinema");
		model.addAttribute("activePage", "room");

		return "admin/suatchieu/room_list";
	}

	@PostMapping("/add")
	public String addRoom(@ModelAttribute("room") Rooms room, BindingResult result, Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) {

		String name = room.getName();
		Integer cinemaId = (room.getCinema() != null) ? room.getCinema().getId() : null;

		if (name == null || name.trim().isEmpty()) {
			result.rejectValue("name", null, "Tên phòng không được để trống");
		}

		if (cinemaId == null) {
			result.rejectValue("cinema.id", null, "Vui lòng chọn chi nhánh");
		}

		if (!result.hasErrors() && roomsRepository.existsByNameAndCinemaId(name.trim(), cinemaId)) {

			result.rejectValue("name", null, "Tên phòng đã tồn tại trong rạp này");
		}

		if (result.hasErrors()) {

			Pageable pageable = PageRequest.of(page, size);
			Page<Rooms> rooms = roomService.findAll(pageable);

			model.addAttribute("rooms", rooms);
			model.addAttribute("list", rooms.getContent());
			model.addAttribute("cinemas", cinemasRepository.findAll());
			model.addAttribute("keyword", keyword);
			model.addAttribute("activeParent", "cinema");
			model.addAttribute("activePage", "room");

			return "admin/suatchieu/room_list";
		}

		Cinemas cinema = cinemasRepository.findById(cinemaId).orElse(null);
		room.setCinema(cinema);
		room.setName(name.trim());

		roomsRepository.save(room);

		return "redirect:/admin/room/list";
	}

	@PostMapping("/edit")
	public String editRoom(@ModelAttribute("room") Rooms room, BindingResult result, Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) {

		String name = room.getName();
		Integer cinemaId = (room.getCinema() != null) ? room.getCinema().getId() : null;

		if (name == null || name.trim().isEmpty()) {
			result.rejectValue("name", null, "Tên phòng không được để trống");
		}

		if (cinemaId == null) {
			result.rejectValue("cinema.id", null, "Vui lòng chọn chi nhánh");
		}

		if (!result.hasErrors()
				&& roomsRepository.existsByNameAndCinemaIdAndIdNot(name.trim(), cinemaId, room.getId())) {

			result.rejectValue("name", null, "Tên phòng đã tồn tại trong rạp này");
		}

		if (result.hasErrors()) {

			Pageable pageable = PageRequest.of(page, size);
			Page<Rooms> rooms = roomService.findAll(pageable);

			model.addAttribute("rooms", rooms);
			model.addAttribute("list", rooms.getContent());
			model.addAttribute("cinemas", cinemasRepository.findAll());
			model.addAttribute("keyword", keyword);

			return "admin/suatchieu/room_list";
		}

		Cinemas cinema = cinemasRepository.findById(cinemaId).orElse(null);
		room.setCinema(cinema);
		room.setName(name.trim());

		roomsRepository.save(room);

		return "redirect:/admin/room/list";
	}

	@PostMapping("/delete/{id}")
	public String deleteRoom(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {

		try {
			roomsRepository.deleteById(id);
			redirectAttributes.addFlashAttribute("success", "Xóa phòng thành công!");

		} catch (DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("error", "Không thể xóa phòng này vì có ghế thuộc phòng!");
		}

		return "redirect:/admin/room/list";
	}
}
