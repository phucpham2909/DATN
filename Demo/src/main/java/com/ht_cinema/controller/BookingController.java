package com.ht_cinema.controller;

import com.ht_cinema.config.CustomUserDetails;
import com.ht_cinema.dto.SeatMessage; // Nếu dùng WebSocket cho ghế giữ chỗ
import com.ht_cinema.model.*;
import com.ht_cinema.repository.*;
import com.ht_cinema.service.SeatHoldService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/booking")
public class BookingController {

	@Autowired
	private FilmRepository filmRepo;
	@Autowired
	private SuatChieuRepository suatChieuRepo;
	@Autowired
	private SeatsRepository seatsRepo;
	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private BookingRepository bookingRepo;
	@Autowired
	private VeRepository veRepo;
	@Autowired
	private ProductRepository productRepo;
	@Autowired
	private BookingComboRepository bookingComboRepo;
	@Autowired
	private SeatHoldService seatHoldService;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	private Account getCurrentAccount() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails user) {
			return user.getAccount();
		}
		return null;
	}
	
	@GetMapping("/films")
	public String listFilms(Model model) {
		List<Film> films = filmRepo.findAll();
		LocalDate today = LocalDate.now();
		Map<Integer, Map<LocalDate, Map<String, List<SuatChieu>>>> groupedShowTimes = new HashMap<>();

		for (Film f : films) {
			if (f.getAvatar() != null && !f.getAvatar().startsWith("http")) {
				f.setAvatar("/film-images/" + f.getAvatar());
			}

			List<SuatChieu> showTimes = suatChieuRepo.findByDetailFilm_Film_Id(f.getId());
			List<SuatChieu> validShowTimes = showTimes.stream()
					.filter(sc -> sc.getDate() != null && !sc.getDate().isBefore(today))
					.sorted(Comparator.comparing(SuatChieu::getDate).thenComparing(SuatChieu::getGioBatDau))
					.collect(Collectors.toList());

			Map<LocalDate, Map<String, List<SuatChieu>>> dateCinemaMap = new LinkedHashMap<>();
			for (SuatChieu sc : validShowTimes) {
				String cinemaName = sc.getRoom().getCinema().getName();
				dateCinemaMap.computeIfAbsent(sc.getDate(), k -> new LinkedHashMap<>())
						.computeIfAbsent(cinemaName, k -> new ArrayList<>()).add(sc);
			}

			groupedShowTimes.put(f.getId(), dateCinemaMap);
		}

		model.addAttribute("films", films);
		model.addAttribute("groupedShowTimes", groupedShowTimes);
		return "client/booking/film-list";
	}

	@GetMapping("/choose-show/{filmId}")
	public String chooseShow(@PathVariable Integer filmId, Model model, Locale locale) {
		Film film = filmRepo.findById(filmId).orElseThrow();
		List<SuatChieu> showTimes = suatChieuRepo.findByDetailFilm_Film_Id(filmId);

		DateTimeFormatter f1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter f2 = locale.getLanguage().equals("vi")
				? DateTimeFormatter.ofPattern("EEE dd/MM", new Locale("vi", "VN"))
				: DateTimeFormatter.ofPattern("EEE dd/MM", Locale.ENGLISH);

		List<String> next7Days = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			LocalDate d = LocalDate.now().plusDays(i);
			next7Days.add(d.format(f1) + ";" + d.format(f2));
		}

		Set<Rooms> roomsSet = showTimes.stream().map(SuatChieu::getRoom).collect(Collectors.toSet());
		List<Rooms> rooms = new ArrayList<>(roomsSet);

		Set<Cinemas> cinemaSet = rooms.stream().map(Rooms::getCinema).collect(Collectors.toSet());
		List<Cinemas> cinemas = new ArrayList<>(cinemaSet);

		model.addAttribute("film", film);
		model.addAttribute("showTimes", showTimes);
		model.addAttribute("rooms", rooms);
		model.addAttribute("cinemas", cinemas);
		model.addAttribute("next7Days", next7Days);

		return "client/booking/choose-show";
	}

	@GetMapping("/choose-seat")
	public String chooseSeat(@RequestParam Integer suatChieuId, Model model, HttpSession session) {
		Account account = getCurrentAccount();
		if (account == null) {
			return "redirect:/login/form";
		}

	    SuatChieu sc = suatChieuRepo.findById(suatChieuId).orElseThrow();
	    List<Seats> seats = seatsRepo.findByRoomId(sc.getRoom().getId());

	    Set<Integer> bookedSeatIds = veRepo.findBookedSeats(suatChieuId)
	            .stream()
	            .map(v -> v.getSeats().getId())
	            .collect(Collectors.toSet());

	    for (Seats seat : seats) {
	        if (bookedSeatIds.contains(seat.getId())) {
	            seat.setBooked(true);
	            seat.setStatus(0);
	        } else {
	            seat.setBooked(false);
	            seat.setStatus(1);
	        }
	    }

	    Film film = sc.getDetailFilm().getFilm();
	    if (film.getAvatar() != null && !film.getAvatar().startsWith("http")) {
	        film.setAvatar("/film-images/" + film.getAvatar());
	    }

	    model.addAttribute("film", film);
	    model.addAttribute("suatChieu", sc);
	    model.addAttribute("seats", seats);
	    model.addAttribute("accountId", account.getId());

	    return "client/booking/choose-seat";
	}

	@PostMapping("/confirm")
	public String confirmBooking(@RequestParam Integer suatChieuId, @RequestParam List<Integer> seatIds,
			@RequestParam Integer accountId, @RequestParam(required = false) String next, HttpSession session) {

		Account account = getCurrentAccount();
		if (account == null) {
			return "redirect:/login/form";
		}
		SuatChieu sc = suatChieuRepo.findById(suatChieuId).orElseThrow();
		account = accountRepo.findById(accountId).orElseThrow();

		Booking booking = Booking.builder().account(account).suatChieu(sc).trangThai(false).sum(0).build();
		bookingRepo.save(booking);

		int seatTotal = 0;
		for (Integer seatId : seatIds) {
			Seats seat = seatsRepo.findById(seatId).orElseThrow();
			Ve ve = Ve.builder().booking(booking).seats(seat).price(seat.getPrice()).gioBatDau(sc.getGioBatDau())
					.gioKetThuc(sc.getGioKetThuc()).build();
			veRepo.save(ve);
			seatTotal += seat.getPrice();
		}
		booking.setSum(seatTotal);
		bookingRepo.save(booking);

		if ("combo".equalsIgnoreCase(next))
			return "redirect:/booking/choose-combo?bookingId=" + booking.getId();
		return "redirect:/booking/confirm?bookingId=" + booking.getId();
	}

	@GetMapping("/choose-combo")
	public String chooseCombo(@RequestParam Integer bookingId, Model model, HttpSession session) {
		Account account = getCurrentAccount();
		if (account == null) {
			return "redirect:/login/form";
		}
		Booking booking = bookingRepo.findById(bookingId).orElseThrow();
		List<Product> products = productRepo.findAll();
		List<BookingProduct> comboList = bookingComboRepo.findByBooking_Id(bookingId);

		Map<Integer, Integer> comboQtyMap = new HashMap<>();
		for (BookingProduct bp : comboList)
			comboQtyMap.put(bp.getProduct().getId(), bp.getQuantity());

		model.addAttribute("booking", booking);
		model.addAttribute("products", products);
		model.addAttribute("comboQtyMap", comboQtyMap);

		return "client/booking/product";
	}

	@PostMapping("/add-combo")
	@ResponseBody
	public void addCombo(@RequestParam Integer bookingId, @RequestParam Integer productId,
			@RequestParam Integer quantity) {
		Optional<BookingProduct> old = bookingComboRepo.findByBooking_IdAndProduct_Id(bookingId, productId);
		if (quantity <= 0) {
			old.ifPresent(bookingComboRepo::delete);
			return;
		}

		BookingProduct bp = old.orElseGet(() -> {
			Booking booking = bookingRepo.findById(bookingId).orElseThrow();
			Product product = productRepo.findById(productId).orElseThrow();
			BookingProduct newBp = new BookingProduct();
			newBp.setBooking(booking);
			newBp.setProduct(product);
			return newBp;
		});
		bp.setQuantity(quantity);
		bookingComboRepo.save(bp);
	}

	@GetMapping("/confirm")
	public String confirmGet(@RequestParam Integer bookingId, HttpSession session, Model model) {
		Account account = getCurrentAccount();
		if (account == null) {
			return "redirect:/login/form";
		}

		Booking booking = bookingRepo.findById(bookingId).orElseThrow();
		List<Ve> veList = veRepo.findByBookingId(bookingId);
		List<BookingProduct> comboList = bookingComboRepo.findByBooking_Id(bookingId);

		int seatTotal = veList.stream().mapToInt(Ve::getPrice).sum();
		int comboTotal = comboList.stream().mapToInt(c -> c.getProduct().getPrice() * c.getQuantity()).sum();

		model.addAttribute("booking", booking);
		model.addAttribute("veList", veList);
		model.addAttribute("comboList", comboList);
		model.addAttribute("film", booking.getSuatChieu().getDetailFilm().getFilm());
		model.addAttribute("totalPrice", seatTotal + comboTotal);

		return "client/booking/confirm";
	}

	@PostMapping("/finish")
	public String finishBooking(@RequestParam Integer bookingId, HttpSession session) {
		Account account = getCurrentAccount();
		if (account == null) {
			return "redirect:/login/form";
		}

		Booking booking = bookingRepo.findById(bookingId).orElseThrow();
		int seatTotal = veRepo.findByBookingId(bookingId).stream().mapToInt(Ve::getPrice).sum();
		int comboTotal = bookingComboRepo.findByBooking_Id(bookingId).stream()
				.mapToInt(c -> c.getProduct().getPrice() * c.getQuantity()).sum();

		booking.setSum(seatTotal + comboTotal);
		booking.setTrangThai(true);
		bookingRepo.save(booking);
		List<Ve> veList = veRepo.findByBookingId(bookingId);
		for (Ve ve : veList) {
		    SeatMessage msg = new SeatMessage(
		        ve.getSeats().getId(),              // seatId
		        booking.getSuatChieu().getId(),    // suatChieuId
		        "booked",                           // action
		        account.getId()                     // accountId
		    );
		    messagingTemplate.convertAndSend(
		        "/topic/seat-status/" + booking.getSuatChieu().getId(), msg
		    );
		}
		return "redirect:/booking/finish?bookingId=" + bookingId;
	}

	@GetMapping("/finish")
	public String finishPage(@RequestParam Integer bookingId, HttpSession session, Model model) {
		Account account = getCurrentAccount();
		if (account == null) {
			return "redirect:/login/form";
		}

		Booking booking = bookingRepo.findById(bookingId).orElseThrow();
		List<Ve> veList = veRepo.findByBookingId(bookingId);
		List<BookingProduct> comboList = bookingComboRepo.findByBooking_Id(bookingId);

		int seatTotal = veList.stream().mapToInt(Ve::getPrice).sum();
		int comboTotal = comboList.stream().mapToInt(c -> c.getProduct().getPrice() * c.getQuantity()).sum();

		model.addAttribute("booking", booking);
		model.addAttribute("veList", veList);
		model.addAttribute("comboList", comboList);
		model.addAttribute("totalPrice", seatTotal + comboTotal);

		return "client/booking/finish";
	}

	@GetMapping("/history")
	public String bookingHistory(HttpSession session, Model model) {
		Account account = getCurrentAccount();
		if (account == null) {
			return "redirect:/login/form";
		}

		List<Booking> list = bookingRepo.findSuccessfulBookingsByUser(account.getId());
		Map<Integer, List<Ve>> veMap = new HashMap<>();
		Map<Integer, List<BookingProduct>> comboMap = new HashMap<>();

		for (Booking b : list) {
			veMap.put(b.getId(), veRepo.findByBookingId(b.getId()));
			comboMap.put(b.getId(), bookingComboRepo.findByBooking_Id(b.getId()));
		}

		model.addAttribute("list", list);
		model.addAttribute("veMap", veMap);
		model.addAttribute("comboMap", comboMap);

		return "client/booking/history";
	}
}
