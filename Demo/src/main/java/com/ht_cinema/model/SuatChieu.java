package com.ht_cinema.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "suat_chieu")
public class SuatChieu {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull(message = "Ngày chiếu không được để trống")
	@FutureOrPresent(message = "Ngày chiếu không được nhỏ hơn ngày hiện tại")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

    @DateTimeFormat(pattern = "h:mm a")
    private LocalTime gioBatDau;

    @DateTimeFormat(pattern = "h:mm a")
    private LocalTime gioKetThuc;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Rooms room;

    @ManyToOne
    @JoinColumn(name = "detail_film_id")
    private DetailFilms detailFilm;
}
