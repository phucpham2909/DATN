package com.ht_cinema.service;

import com.ht_cinema.model.DetailFilms;
import com.ht_cinema.model.SuatChieu;
import com.ht_cinema.repository.DetailFilmsRepository;
import com.ht_cinema.repository.SuatChieuRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SuatChieuService {

    @Autowired
    private SuatChieuRepository suatChieuRepository;

    @Autowired
    private DetailFilmsRepository detailFilmsRepository;

    private LocalTime calculateGioKetThuc(LocalTime gioBatDau, int thoiLuong) {
        if (gioBatDau == null) return null;
        return gioBatDau.plusMinutes(thoiLuong);
    }

    public List<SuatChieu> getAll() {
        return suatChieuRepository.findAll();
    }

    public Page<SuatChieu> getAll(Pageable pageable) {
        return suatChieuRepository.findAllByOrderByDateAscGioBatDauAsc(pageable);
    }

    public Page<SuatChieu> findByFilmName(String filmName, Pageable pageable) {
        return suatChieuRepository.findByDetailFilm_Film_NameContainingIgnoreCaseOrderByDateAscGioBatDauAsc(filmName, pageable);
    }

    public Page<SuatChieu> findByFilmNameAndDateRange(String filmName, LocalDate from, LocalDate to, Pageable pageable) {
        return suatChieuRepository.findByDetailFilm_Film_NameContainingIgnoreCaseAndDateBetweenOrderByDateAscGioBatDauAsc(filmName, from, to, pageable);
    }

    public Page<SuatChieu> findByDateRange(LocalDate from, LocalDate to, Pageable pageable) {
        return suatChieuRepository.findByDateBetweenOrderByDateAscGioBatDauAsc(from, to, pageable);
    }

    public Optional<SuatChieu> getById(Integer id) {
        return suatChieuRepository.findById(id);
    }

    public SuatChieu createSuatChieu(SuatChieu suatChieu) {
        if (suatChieu.getDetailFilm() != null && suatChieu.getGioBatDau() != null) {
            Optional<DetailFilms> detail = detailFilmsRepository.findById(suatChieu.getDetailFilm().getId());
            detail.ifPresent(df -> suatChieu.setGioKetThuc(calculateGioKetThuc(suatChieu.getGioBatDau(), df.getThoiLuong())));
        }
        return suatChieuRepository.save(suatChieu);
    }
    public SuatChieu updateSuatChieu(Integer id, SuatChieu suatChieu) {
        return suatChieuRepository.findById(id).map(existing -> {
            existing.setDate(suatChieu.getDate());
            existing.setGioBatDau(suatChieu.getGioBatDau());
            existing.setRoom(suatChieu.getRoom());
            existing.setDetailFilm(suatChieu.getDetailFilm());

            if (suatChieu.getDetailFilm() != null && suatChieu.getGioBatDau() != null) {
                Optional<DetailFilms> detail = detailFilmsRepository.findById(suatChieu.getDetailFilm().getId());
                detail.ifPresent(df -> existing.setGioKetThuc(calculateGioKetThuc(suatChieu.getGioBatDau(), df.getThoiLuong())));
            }

            return suatChieuRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SuatChieu not found with id " + id));
    }

    public void deleteSuatChieu(Integer id) {
        suatChieuRepository.deleteById(id);
    }
}
