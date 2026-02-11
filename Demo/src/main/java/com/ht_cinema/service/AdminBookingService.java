package com.ht_cinema.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ht_cinema.dto.BookingAdminDTO;
import com.ht_cinema.model.Booking;
import com.ht_cinema.model.SuatChieu;
import com.ht_cinema.model.Ve;
import com.ht_cinema.repository.BookingRepository;

@Service
public class AdminBookingService {

    @Autowired
    private BookingRepository bookingRepo;

    public List<BookingAdminDTO> getAllBookingAdmin() {

        return bookingRepo.findAllWithDetails().stream().map(b -> {

            String filmName = "Chưa có phim";
            String showTime = "N/A";
            String roomName = "N/A";
            String cinemaName = "N/A";

            if (b.getSuatChieu() != null) {
                SuatChieu sc = b.getSuatChieu();

                if (sc.getFilm() != null && sc.getFilm().getName() != null) {
                    filmName = sc.getFilm().getName();
                } else {
                    filmName = "[Không có phim cho suất chiếu ID " + sc.getId() + "]";
                }

                if (sc.getGioBatDau() != null && sc.getGioKetThuc() != null) {
                    showTime = sc.getGioBatDau() + " - " + sc.getGioKetThuc();
                }

                if (sc.getRoom() != null) {
                    roomName = sc.getRoom().getName();
                    if (sc.getRoom().getCinema() != null) {
                        cinemaName = sc.getRoom().getCinema().getName();
                    }
                }
            }

            String fullName = b.getAccount() != null
                    ? b.getAccount().getFullname()
                    : "N/A";

            String seatNames = b.getVe() == null ? "" :
                    b.getVe().stream()
                            .map(v -> v.getSeats().getName())
                            .collect(Collectors.joining(", "));

            int seatTotal = b.getVe() == null ? 0 :
                    b.getVe().stream()
                            .mapToInt(Ve::getPrice)
                            .sum();

            String comboNames = b.getBookingProducts() == null ? "" :
                    b.getBookingProducts().stream()
                            .map(bp -> bp.getProduct().getName() + " x" + bp.getQuantity())
                            .collect(Collectors.joining(", "));

            int comboTotal = b.getBookingProducts() == null ? 0 :
                    b.getBookingProducts().stream()
                            .mapToInt(bp -> bp.getQuantity() * bp.getProduct().getPrice())
                            .sum();

            System.out.println("Booking ID: " + b.getId() +
                    " | SuatChieu: " + (b.getSuatChieu() != null ? b.getSuatChieu().getId() : "null") +
                    " | Film: " + filmName);

            return new BookingAdminDTO(
                    b.getId(),
                    fullName,
                    filmName,
                    showTime,
                    cinemaName,
                    roomName,
                    seatNames,
                    seatTotal,
                    comboNames,
                    comboTotal,
                    seatTotal + comboTotal
            );

        }).toList();
    }
}
