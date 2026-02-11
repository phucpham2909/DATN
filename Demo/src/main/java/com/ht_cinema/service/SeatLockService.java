package com.ht_cinema.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SeatLockService {

    // Map: suatChieuId -> Map<seatId, expireTime>
    private final Map<Integer, Map<Integer, LocalDateTime>> lockMap = new ConcurrentHashMap<>();

    private static final int LOCK_MINUTES = 5;

    // Khóa ghế tạm
    public synchronized boolean lockSeat(int suatChieuId, int seatId) {
        Map<Integer, LocalDateTime> seatLocks = lockMap.computeIfAbsent(suatChieuId, k -> new ConcurrentHashMap<>());

        // Nếu ghế đang bị khóa và chưa hết 5 phút
        if (seatLocks.containsKey(seatId) && seatLocks.get(seatId).isAfter(LocalDateTime.now())) {
            return false;
        }

        // Khóa ghế 5 phút
        seatLocks.put(seatId, LocalDateTime.now().plusMinutes(LOCK_MINUTES));
        return true;
    }

    // Mở khóa ghế
    public synchronized void unlockSeat(int suatChieuId, int seatId) {
        Map<Integer, LocalDateTime> seatLocks = lockMap.get(suatChieuId);
        if (seatLocks != null) {
            seatLocks.remove(seatId);
        }
    }

    // Lấy danh sách ghế đang khóa
    public synchronized Set<Integer> getLockedSeats(int suatChieuId) {
        Map<Integer, LocalDateTime> seatLocks = lockMap.getOrDefault(suatChieuId, Collections.emptyMap());
        LocalDateTime now = LocalDateTime.now();

        // Xóa các khóa đã hết hạn
        seatLocks.entrySet().removeIf(e -> e.getValue().isBefore(now));

        return seatLocks.keySet();
    }
}
