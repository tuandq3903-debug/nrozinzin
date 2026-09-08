package models.Bot;

import server.ServerManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Quản lý vòng lặp cập nhật Bot theo kiểu scheduled với fixed delay.
 * - Không dùng Thread.sleep(..) => tránh sleep âm gây spin CPU.
 * - Không chồng chéo tick (mỗi tick chỉ chạy 1 lần, tuần tự).
 * - Danh sách bot thread-safe bằng CopyOnWriteArrayList.
 */
public class BotManager implements Runnable {

    /** Khoảng thời gian giữa 2 tick cập nhật (ms). */
    private static final int TICK_MS = 150;

    /** Executor 1 luồng cho vòng lặp update, đảm bảo không chồng chéo tick. */
    private static final ScheduledThreadPoolExecutor LOOP_EXECUTOR =
            new ScheduledThreadPoolExecutor(1, named("bot-loop"));

    /**
     * Pool cho các tác vụ phụ/trọng tải riêng của Bot (nếu cần chạy song song).
     * Dùng ScheduledThreadPoolExecutor để có thể hẹn giờ từng bot khi cần.
     */
    public static final ScheduledThreadPoolExecutor BOT_SERVICE =
            new ScheduledThreadPoolExecutor(
                    Math.max(2, Runtime.getRuntime().availableProcessors()),
                    named("bot-service")
            );

    static {
        // Dọn task đã cancel để không bị giữ tham chiếu -> giảm rò rỉ RAM
        LOOP_EXECUTOR.setRemoveOnCancelPolicy(true);
        BOT_SERVICE.setRemoveOnCancelPolicy(true);
        // Không để scheduler cố chạy lại task delay khi shutdown
        LOOP_EXECUTOR.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        BOT_SERVICE.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
    }

    public static volatile BotManager i;

    /** Danh sách bot an toàn khi duyệt/ghi từ nhiều thread. */
    public final List<Bot> bot = new CopyOnWriteArrayList<>();

    /** Future của vòng lặp chính để có thể dừng gọn. */
    private volatile ScheduledFuture<?> loopFuture;

    public static BotManager gI() {
        BotManager ref = i;
        if (ref == null) {
            synchronized (BotManager.class) {
                if (i == null) i = new BotManager();
                ref = i;
            }
        }
        return ref;
    }

    /** Khởi động vòng lặp cập nhật bot. Gọi một lần khi server start. */
    public void start() {
        if (loopFuture == null || loopFuture.isCancelled()) {
            loopFuture = LOOP_EXECUTOR.scheduleWithFixedDelay(this, 0, TICK_MS, TimeUnit.MILLISECONDS);
        }
    }

    /** Dừng vòng lặp khi server tắt. */
    public void stop() {
        ScheduledFuture<?> f = loopFuture;
        if (f != null) {
            f.cancel(false);
        }
    }

    /** Thêm bot (thread-safe). */
    public void add(Bot b) {
        if (b != null) bot.add(b);
    }

    /** Xóa bot (thread-safe). */
    public void remove(Bot b) {
        if (b != null) bot.remove(b);
    }

    @Override
    public void run() {
        // Nếu server tắt -> dừng vòng lặp gọn
        if (!ServerManager.isRunning) {
            stop();
            return;
        }

        // Tick cập nhật: chạy tuần tự từng bot để tránh race-condition nội bộ bot
        for (Bot b : bot) {
            try {
                b.update();
            } catch (Throwable t) {
                // TODO: thay bằng logger của bạn, tránh printStackTrace trong loop
                // Logger.logException(BotManager.class, t);
            }
        }
        // Không cần sleep: scheduleWithFixedDelay đảm nhiệm nhịp tick và tránh chồng chéo
    }

    /** ThreadFactory đặt tên dễ debug và có UncaughtExceptionHandler. */
    private static ThreadFactory named(String prefix) {
        return r -> {
            Thread t = new Thread(r, prefix);
            t.setUncaughtExceptionHandler((thr, ex) -> {
                // TODO: thay bằng logger của bạn
                // Logger.logSevere("Uncaught in " + thr.getName(), ex);
            });
            return t;
        };
    }
}
