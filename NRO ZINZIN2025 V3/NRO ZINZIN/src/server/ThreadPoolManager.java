package server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ThreadPoolManager {
    private static final Logger LOGGER = Logger.getLogger("ThreadPoolManager");

    // ThreadPool chính, dùng cho các task bất đồng bộ
    public static final ThreadPoolExecutor EXECUTOR;

    // Scheduler dùng cho các tác vụ định kỳ
    public static final ScheduledExecutorService SCHEDULER;

    static {
        // Khởi tạo executor với corePoolSize = 0, max = ∞, keepAlive = 60s
        EXECUTOR = new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory()
        );
        // Cho phép core thread timeout
        EXECUTOR.allowCoreThreadTimeOut(true);

        // Khởi tạo scheduler 1 thread
        ScheduledThreadPoolExecutor sch = new ScheduledThreadPoolExecutor(
                1,
                runnable -> {
                    Thread t = new Thread(runnable, "Server-Scheduler");
                    t.setDaemon(true);
                    return t;
                }
        );
        // Cho phép core thread của scheduler timeout sau 60s
        sch.setKeepAliveTime(60L, TimeUnit.SECONDS);
        sch.allowCoreThreadTimeOut(true);
        // Khi hủy 1 ScheduledFuture, tự động remove khỏi hàng đợi
        sch.setRemoveOnCancelPolicy(true);

        SCHEDULER = sch;

        // Lên lịch purge—dọn các task đã bị cancel—mỗi 60s
        SCHEDULER.scheduleAtFixedRate(() -> {
            // Dọn cancel tasks của EXECUTOR
            EXECUTOR.purge();
            // Dọn cancel tasks của scheduler
            sch.purge();
        }, 60L, 60L, TimeUnit.SECONDS);
    }

    private ThreadPoolManager() {
        throw new UnsupportedOperationException("Không tạo được ThreadPoolManager");
    }

    /**
     * Tắt executor một cách an toàn.
     */
    public static void shutdownExecutor() {
        LOGGER.log(Level.INFO, "Shutting down ThreadPoolExecutor...");
        EXECUTOR.shutdown();
        try {
            if (!EXECUTOR.awaitTermination(30, TimeUnit.SECONDS)) {
                LOGGER.log(Level.WARNING, "Executor did not terminate in the specified time.");
                EXECUTOR.shutdownNow();
            }
            LOGGER.log(Level.INFO, "ThreadPoolExecutor shut down successfully.");
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Interrupted during shutdown, forcing shutdown now.", e);
            EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Tắt cả executor và scheduler khi server dừng.
     */
    public static void shutdownAll() {
        shutdownExecutor();
        LOGGER.log(Level.INFO, "Shutting down Scheduler...");
        SCHEDULER.shutdownNow();
    }
}
