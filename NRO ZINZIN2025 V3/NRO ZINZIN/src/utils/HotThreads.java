package utils;

import java.lang.management.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class HotThreads implements Runnable {
    private final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    private final int top;
    private final Map<Long, Long> lastCpu = new HashMap<>();
    private long lastWall;

    public HotThreads(int top) {
        this.top = top;
        if (bean.isThreadCpuTimeSupported() && !bean.isThreadCpuTimeEnabled()) {
            bean.setThreadCpuTimeEnabled(true);
        }
    }

    @Override public void run() {
        long now = System.nanoTime();
        long elapsedMs = (lastWall == 0) ? 0 : TimeUnit.NANOSECONDS.toMillis(now - lastWall);
        lastWall = now;

        long[] ids = bean.getAllThreadIds();
        List<Map.Entry<Long, Long>> deltas = new ArrayList<>();
        for (long id : ids) {
            long t = bean.getThreadCpuTime(id); // ns
            if (t <= 0) continue;
            long prev = lastCpu.getOrDefault(id, t);
            long d = t - prev;
            lastCpu.put(id, t);
            if (d > 0) deltas.add(Map.entry(id, d));
        }
        deltas.sort((a,b)->Long.compare(b.getValue(), a.getValue()));
        int n = Math.min(top, deltas.size());

        StringBuilder sb = new StringBuilder();
        sb.append("[HotThreads] top ").append(n)
          .append(" (window=").append(elapsedMs).append("ms)\n");
        for (int i = 0; i < n; i++) {
            long id = deltas.get(i).getKey();
            long d  = deltas.get(i).getValue(); // ns
            ThreadInfo info = bean.getThreadInfo(id, 30);
            sb.append(String.format("#%d tid=%d cpu=%dms name=%s state=%s\n",
                    i+1, id, TimeUnit.NANOSECONDS.toMillis(d),
                    info != null ? info.getThreadName() : "?", 
                    info != null ? info.getThreadState() : "?"));
        }
        System.out.println(sb.toString()); // hoặc Logger.info(...)
    }
}
