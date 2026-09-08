package server;
/*
 * Lớp ServerManager chịu trách nhiệm khởi tạo và quản lý toàn bộ server game:
 * - Thiết lập cấu hình, load database, NPC, bản đồ
 * - Giám sát bộ nhớ, tự động gọi GC khi vượt ngưỡng
 * - Mở socket để chấp nhận kết nối client, hạn chế kết nối theo IP
 * - Khởi chạy các dịch vụ game (boss, sự kiện, bot…)
 * - Cung cấp API để tạm dừng hoặc đóng server
 * 
 * @author ZINZIN
 */

import models.BlackBallWar.BlackBallManager;
import jdbc.daos.HistoryTransactionDAO;
import models.boss.BossManager;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import network.inetwork.ISession;
import network.Network;
import network.MyKeyHandler;
import network.MySession;
import network.MessageSendCollect;
import network.inetwork.ISessionAcceptHandler;

import services.ClanService;
import services.NgocRongNamecService;
import services.Service;

import utils.Logger;
import utils.TimeUtil;
import utils.SystemMetrics;

import jdbc.Config;
import jdbc.daos.EventDAO;

import models.The23rdMartialArtCongress.The23rdMartialArtCongressManager;
import models.DeathOrAliveArena.DeathOrAliveArenaManager;
import models.WorldMartialArtsTournament.WorldMartialArtsTournamentManager;
import models.ShenronEvent.ShenronEventManager;
import models.SuperRank.SuperRankManager;
import models.kygui.ConsignShopManager;
import models.Bot.BotManager;
import models.Bot.NewBot;
import models.map.Map;
import models.npc.NpcFactory;
import utils.HotThreads;
import java.util.concurrent.TimeUnit;

/** 
 * Singleton quản lý server 
 */
public class ServerManager {

    /** Thời điểm server khởi động (dd/MM/yyyy HH:mm:ss) */
    public static String timeStart;

    /** Bản đồ đếm số client theo IP để giới hạn kết nối */
    public static final ConcurrentMap<String, AtomicInteger> CLIENTS = new ConcurrentHashMap<>();

    /** Thể hiện duy nhất của ServerManager */
    private static ServerManager instance;

    /** Cờ báo server đang chạy */
    public static boolean isRunning;


    /** Constructor ẩn, chỉ gọi khi khởi tạo singleton */
    private ServerManager() {
        // Nạp cấu hình server (cổng, max kết nối, DB)
        Config.Server();
        // Load toàn bộ dữ liệu từ database
        Load_Database.loadDatabase();
        // Khởi tạo NPC
        NpcFactory.createNpcConMeo();
        NpcFactory.createNpcRongThieng();
        // Khởi tạo bản đồ
        models.map.Map.initMap();
    }

    /** Khởi tạo sau khi tạo instance */
    public void init() {
        // Xóa lịch sử giao dịch cũ
        HistoryTransactionDAO.deleteHistory();
    }

    /** Lấy instance singleton, khởi tạo nếu chưa có */
    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    /** Hàm main: ghi nhận thời gian, chạy server và menu console */
    public static void main(String[] args) {
        timeStart = TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss");
//        ServerManager.gI().run();
//        Menu.main(args);
        new server.MenuAuto().setVisible(true);
    }

    /** Chạy server */
    public void run() {
        isRunning = true;

        // Mở socket để chấp nhận kết nối client
        activeServerSocket();
//        NewBot.gI().runBot(0, null, 100);

        // Khởi chạy các dịch vụ game trên ThreadPool
//        ThreadPoolManager.SCHEDULER.scheduleWithFixedDelay(new HotThreads(5), 10, 10, TimeUnit.SECONDS);
        ThreadPoolManager.EXECUTOR.execute(NgocRongNamecService.gI());
        ThreadPoolManager.EXECUTOR.execute(SuperRankManager.gI());
        ThreadPoolManager.EXECUTOR.execute(The23rdMartialArtCongressManager.gI());
        ThreadPoolManager.EXECUTOR.execute(DeathOrAliveArenaManager.gI());
        ThreadPoolManager.EXECUTOR.execute(WorldMartialArtsTournamentManager.gI());
        ThreadPoolManager.EXECUTOR.execute(ShenronEventManager.gI());

        // Load thông tin boss, khởi tạo boss lên bản đồ
        BossManager.gI().loadBoss();
        Load_Database.MAPS.forEach(Map::initBoss);

        // Khởi chạy các manager khác
        ThreadPoolManager.EXECUTOR.execute(BlackBallManager.gI());
        ThreadPoolManager.EXECUTOR.execute(BossManager.gI());
        ThreadPoolManager.EXECUTOR.execute(BotManager.gI());

    }

    /** Tạm dừng server (shutdown scheduler và thread pool) */
    public void stop() {
        isRunning = false;
        ThreadPoolManager.EXECUTOR.shutdown();
    }

    /** Đóng toàn bộ server, lưu dữ liệu và exit JVM */
    public void close() {
    // 1. Tắt các service
    isRunning = false;
    try {
        ClanService.gI().close();
    } catch (Exception e) {
        Logger.error("Lỗi khi đóng ClanService!\n");
    }
    try {
        ConsignShopManager.gI().save();
    } catch (Exception e) {
        Logger.error("Lỗi khi lưu Shop ký gửi!\n");
    }
    try {
        Client.gI().close();
    } catch (Exception e) {
        Logger.error("Lỗi khi đóng Client!\n");
    }
    try {
        EventDAO.save();
    } catch (Exception e) {
        Logger.error("Lỗi khi lưu EventDAO!\n");
    }

    Logger.success("[ ZINZIN ] Đã chuyển sang bảo trì!\n");

    if (MenuAuto.isRunning) {
    MenuAuto.isRunning = false;
    try {
        // Chờ thêm chút socket/thread; nếu bạn đã delay bên trong .bat thì có thể để 2s
        Thread.sleep(2_000);
    } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
    }
    String workingDir = "C:\\Users\\Administrator\\Downloads\\game\\NRO\\NRO ZINZIN";
    Logger.log(">> Spawning restart.bat in: " + workingDir);
    try {
        MenuAuto.runBatchFile("restart.bat", workingDir);
    } catch (IOException e) {
        Logger.logException(ServerManager.class, e, "Lỗi spawn restart.bat");
    }
}
    System.exit(0);
}


    /** Thiết lập và khởi động server socket để nhận kết nối client */
    private void activeServerSocket() {
        try {
            Network.gI().init()
                .setAcceptHandler(new ISessionAcceptHandler() {
                    // Khi có session mới
                    @Override
                    public void sessionInit(ISession is) {
                        if (!canConnectWithIp(is.getIP())) {
                            is.disconnect(); // vượt quá giới hạn IP
                            return;
                        }
                        // Cấu hình handler cho session
                        is.setMessageHandler(Controller.gI())
                          .setSendCollect(new MessageSendCollect())
                          .setKeyHandler(new MyKeyHandler())
                          .startCollect();
                    }
                    // Khi client ngắt kết nối
                    @Override
                    public void sessionDisconnect(ISession session) {
                        Client.gI().kickSession((MySession) session);
                    }
                })
                .setTypeSessioClone(MySession.class)
                .setDoSomeThingWhenClose(() -> {
                    Logger.error("LỖI: SERVER ĐÓNG BẤT NGỜ\n");
                    System.exit(0);
                })
                .start(Config.PORT);
        } catch (Exception e) {
            Logger.error("Lỗi khi khởi động ServerSocket!\n");
        }
    }

    /** Kiểm tra và giới hạn số kết nối theo mỗi IP */
    private boolean canConnectWithIp(String ip) {
        AtomicInteger count = CLIENTS.computeIfAbsent(ip, k -> new AtomicInteger(0));
        int current = count.incrementAndGet();
        if (current <= Config.MAX_PER_IP) {
            return true;
        } else {
            count.decrementAndGet();
            return false;
        }
    }

    /** Cập nhật khi client ngắt kết nối, giảm bộ đếm IP */
    public void disconnect(MySession session) {
        String ip = session.getIP();
        AtomicInteger count = CLIENTS.get(ip);
        if (count != null) {
            int remain = count.decrementAndGet();
            if (remain <= 0) {
                CLIENTS.remove(ip, count);
            }
        }
    }
}
