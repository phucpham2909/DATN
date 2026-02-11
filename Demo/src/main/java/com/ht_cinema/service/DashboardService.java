package com.ht_cinema.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ht_cinema.model.Booking;
import com.ht_cinema.model.BookingProduct;
import com.ht_cinema.repository.BookingProductRepository;
import com.ht_cinema.repository.BookingRepository;

@Service
public class DashboardService {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private BookingProductRepository bookingProductRepo;



    // Phân trang toàn bộ vé
    public Page<Booking> getAllDoanhThuVe(Pageable pageable) {
        return bookingRepo.findAll(pageable);
    }

    // Phân trang toàn bộ sản phẩm
    public Page<BookingProduct> getAllDoanhThuSanPham(Pageable pageable) {
        return bookingProductRepo.findAll(pageable);
    }

    // Tổng tiền vé toàn bộ
    public Integer getTongTienVeAll() {
        return bookingRepo.findAll().stream()
                .mapToInt(Booking::getSum)
                .sum();
    }

    // Tổng tiền sản phẩm toàn bộ
    public Integer getTongTienSanPhamAll() {
        return bookingProductRepo.findAll().stream()
                .mapToInt(bp -> bp.getProduct().getPrice() * bp.getQuantity())
                .sum();
    }

    // Phân trang theo khoảng ngày (vé)
    public Page<Booking> getDoanhThuVe(LocalDate start, LocalDate end, Pageable pageable) {
        return bookingRepo.findByDateBetween(start, end, pageable);
    }

    // Tổng tiền vé theo khoảng ngày
    public Integer getTongTienVe(LocalDate start, LocalDate end) {
        return bookingRepo.findByDateBetween(start, end).stream()
                .mapToInt(Booking::getSum)
                .sum();
    }

    // Phân trang theo khoảng ngày (sản phẩm)
    public Page<BookingProduct> getDoanhThuSanPham(LocalDate start, LocalDate end, Pageable pageable) {
        return bookingProductRepo.findByDateBetween(start, end, pageable);
    }

    // Tổng tiền sản phẩm theo khoảng ngày
    public Integer getTongTienSanPham(LocalDate start, LocalDate end) {
        return bookingProductRepo.findByDateBetween(start, end).stream()
                .mapToInt(bp -> bp.getProduct().getPrice() * bp.getQuantity())
                .sum();
    }
    public Page<BookingProduct> searchDoanhThuSanPham(String keyword, Pageable pageable) {
        return bookingProductRepo.findByProductNameContaining(keyword, pageable);
    }
}