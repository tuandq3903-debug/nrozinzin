package utils;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class SystemMetrics {

    // Khởi tạo một lần để tái sử dụng
    private static final OperatingSystemMXBean osBean =
        ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private static final MemoryMXBean memoryBean =
        ManagementFactory.getMemoryMXBean();

    // Chuyển bytes -> GB và định dạng
    private static String formatMemory(long bytes) {
        double gb = bytes / (1024.0 * 1024.0 * 1024.0);
        return String.format("%.2f GB", gb);
    }

    // Định dạng số phần trăm
    private static String formatPercentage(double value) {
        return String.format("%.1f%%", value);
    }

    /**
     * Lấy chuỗi chứa các thông số hệ thống:
     * - Bộ nhớ vật lý (Physical Memory)
     * - Tải CPU (System CPU Load)
     * - Bộ nhớ Heap của JVM
     */
    public static String getMetrics() {
        // Physical memory
        long totalPhysical = osBean.getTotalPhysicalMemorySize();
        long freePhysical  = osBean.getFreePhysicalMemorySize();
        long usedPhysical  = totalPhysical - freePhysical;
        double physUsagePct = usedPhysical * 100.0 / totalPhysical;

        // CPU load (giá trị trả về từ 0.0 đến 1.0)
        double cpuLoadPct = osBean.getSystemCpuLoad() * 100.0;

        // Heap memory của JVM
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long heapUsed = heapUsage.getUsed();
        long heapMax  = heapUsage.getMax();
        double heapUsagePct = heapUsed * 100.0 / heapMax;

        // Xây dựng chuỗi kết quả
        StringBuilder sb = new StringBuilder();
        sb.append("CPU Load: ")
          .append(formatPercentage(cpuLoadPct))
          .append("\n");
        sb.append("Physical Memory: ")
          .append(formatMemory(usedPhysical))
          .append(" / ")
          .append(formatMemory(totalPhysical))
          .append(" (")
          .append(formatPercentage(physUsagePct))
          .append(")\n");
        sb.append("Heap Memory: ")
          .append(formatMemory(heapUsed))
          .append(" / ")
          .append(formatMemory(heapMax))
          .append(" (")
          .append(formatPercentage(heapUsagePct))
          .append(")\n");

        return sb.toString();
    }
}
