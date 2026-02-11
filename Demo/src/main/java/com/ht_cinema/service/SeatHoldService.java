package com.ht_cinema.service;

import com.ht_cinema.config.CustomUserDetails;
import com.ht_cinema.dto.SeatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
@Slf4j
public class SeatHoldService {

    private final long HOLD_DURATION = 30 * 1000;

    private final Map<Integer, Map<Integer, HoldInfo>> holdMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ Lấy accountId từ Spring Security
    public Integer getAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails user) {
            return user.getAccount().getId();
        }

        return null; // ✅ Không trả về 1 nữa
    }

    public boolean tryHoldSeat(Integer seatId, Integer suatChieuId) {
        Integer accountId = getAccountId();
        if (accountId == null) return false;

        holdMap.putIfAbsent(suatChieuId, new ConcurrentHashMap<>());
        Map<Integer, HoldInfo> seats = holdMap.get(suatChieuId);

        synchronized (seats) {
            HoldInfo current = seats.get(seatId);

            if (current != null && current.isActive() && !current.getAccountId().equals(accountId)) {
                return false;
            }

            if (current != null && current.getScheduledFuture() != null) {
                current.getScheduledFuture().cancel(false);
            }

            HoldInfo info = new HoldInfo(accountId, System.currentTimeMillis());
            seats.put(seatId, info);

            ScheduledFuture<?> future = scheduler.schedule(
                    () -> autoRelease(seatId, suatChieuId, accountId),
                    HOLD_DURATION,
                    TimeUnit.MILLISECONDS
            );

            info.setScheduledFuture(future);
        }

        return true;
    }

    public void release(Integer seatId, Integer suatChieuId) {
        Integer accountId = getAccountId();
        if (accountId == null) return;

        Map<Integer, HoldInfo> seats = holdMap.get(suatChieuId);
        if (seats == null) return;

        synchronized (seats) {
            HoldInfo info = seats.get(seatId);
            if (info != null && info.getAccountId().equals(accountId)) {
                seats.remove(seatId);
                if (info.getScheduledFuture() != null) info.getScheduledFuture().cancel(false);
            }
        }
    }

    private void autoRelease(Integer seatId, Integer suatChieuId, Integer accountId) {
        Map<Integer, HoldInfo> seats = holdMap.get(suatChieuId);
        if (seats == null) return;

        synchronized (seats) {
            HoldInfo info = seats.get(seatId);
            if (info != null && info.getAccountId().equals(accountId)) {
                seats.remove(seatId);

                log.info("AUTO RELEASE: SuatChieuId={} SeatId={} by AccountId={}",
                        suatChieuId, seatId, accountId);

                SeatMessage msg = new SeatMessage(seatId, suatChieuId, "release", accountId);
                messagingTemplate.convertAndSend("/topic/seat-status/" + suatChieuId, msg);
            }
        }
    }

    public boolean isSeatCurrentlyHeld(Integer seatId, Integer suatChieuId) {
        Map<Integer, HoldInfo> seats = holdMap.get(suatChieuId);
        if (seats == null) return false;

        HoldInfo info = seats.get(seatId);
        return info != null && info.isActive();
    }

    private static class HoldInfo {
        private final Integer accountId;
        private final long timestamp;
        private ScheduledFuture<?> scheduledFuture;

        public HoldInfo(Integer accountId, long timestamp) {
            this.accountId = accountId;
            this.timestamp = timestamp;
        }

        public Integer getAccountId() { return accountId; }

        public boolean isActive() {
            return System.currentTimeMillis() - timestamp <= 30 * 1000;
        }

        public ScheduledFuture<?> getScheduledFuture() { return scheduledFuture; }

        public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }
    }
}
