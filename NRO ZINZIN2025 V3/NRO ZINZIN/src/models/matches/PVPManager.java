package models.matches;

import models.player.Player;
import server.ServerManager;
import server.ThreadPoolManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Quản lý vòng lặp PVP an toàn CPU/RAM.
 * - Tick mỗi 1000ms bằng scheduler (không dùng sleep âm).
 * - Không chồng chéo tick.
 * - Danh sách PVP thread-safe.
 */
public class PVPManager implements Runnable {

    private static final long TICK_MS = 1000;

    private static volatile PVPManager i;
    public static PVPManager gI() {
        PVPManager ref = i;
        if (ref == null) {
            synchronized (PVPManager.class) {
                if (i == null) i = new PVPManager();
                ref = i;
            }
        }
        return ref;
    }

    private final List<PVP> pvps = new CopyOnWriteArrayList<>();
    private volatile ScheduledFuture<?> loopFuture;

    private PVPManager() {
        // Khởi động vòng lặp ngay khi tạo để giữ hành vi cũ
        start();
    }

    /** Bắt đầu vòng lặp tick PVP (gọi một lần). */
    public void start() {
        if (loopFuture == null || loopFuture.isCancelled()) {
            // DÙNG INSTANCE SCHEDULER, KHÔNG DÙNG HÀM TĨNH
            loopFuture = ThreadPoolManager.SCHEDULER
                    .scheduleWithFixedDelay(this, 0, TICK_MS, TimeUnit.MILLISECONDS);
        }
    }

    /** Dừng vòng lặp tick (gọi khi server shutdown). */
    public void stop() {
        ScheduledFuture<?> f = loopFuture;
        if (f != null) {
            f.cancel(false);
        }
    }

    public void removePVP(PVP pvp) {
        if (pvp != null) {
            pvps.remove(pvp);
        }
    }

    public void addPVP(PVP pvp) {
        if (pvp != null) {
            pvps.add(pvp);
        }
    }

    public PVP getPVP(Player player) {
        if (player == null) return null;
        for (PVP pvp : pvps) {
            try {
                if (pvp != null && (player.equals(pvp.p1) || player.equals(pvp.p2))) {
                    return pvp;
                }
            } catch (Throwable ignored) {
                // tránh vỡ vòng khi pvp/p1/p2 null bất ngờ
            }
        }
        return null;
    }

    @Override
    public void run() {
        if (!ServerManager.isRunning) {
            stop();
            return;
        }
        tick();
    }

    /** Thực hiện 1 tick cập nhật PVP. */
    private void tick() {
        for (PVP pvp : pvps) {
            if (pvp == null) continue;
            try {
                pvp.update();
            } catch (Throwable t) {
                // TODO: thay bằng logger của bạn, tránh printStackTrace trong loop
                // Logger.logException(PVPManager.class, t);
            }
        }
        // Có thể dọn pvp kết thúc ở đây nếu muốn:
        // pvps.removeIf(p -> p == null || p.isDone());
    }
}
