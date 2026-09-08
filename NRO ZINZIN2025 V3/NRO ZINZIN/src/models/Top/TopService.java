package models.Top;

import models.Top.TopHelloween;
import models.Top.TopHungvuong;
import models.Top.TopPowerManager;
import models.Top.TopTaskManager;
import models.Top.TopTrungthu;
import models.Top.TopVnd;

import java.io.IOException;

import jdbc.DatabaseManager;
import models.player.Player;
import models.Top.RealTop;
import network.Message;
import utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import jdbc.daos.ZINZINSqlFetcher;

import models.Top.TOP;
import services.Service;
import services.TaskService;
import utils.Util;

public class TopService {

    // ---------------- Cấu hình Sự kiện Trung Thu ----------------
    private static final ZoneId ZONE = ZoneId.of("Asia/Bangkok");
    // 10/08/2025 00:00:00 -> 10/09/2025 23:59:59 (giống HangNga)
    private static final long EVENT_END = LocalDateTime.of(2025, 9, 10, 23, 59, 59)
            .atZone(ZONE).toInstant().toEpochMilli();

    // ID hộp quà – thay theo item template của bạn
    private static final short TT_BOX_TOP1   = 5001; // Top 1
    private static final short TT_BOX_TOP10  = 5002; // Top 2–10
    private static final short TT_BOX_TOP100 = 5003; // Top 11–100

    // Bảng/KEY lưu trạng thái – chỉnh theo DB của bạn
    private static final String TBL_EVENT_STATE  = "event_state";   // key,value
    private static final String TBL_EVENT_REWARD = "event_reward";  // event_key,character_id,rewarded
    private static final String TBL_MAIL_BOX     = "mail_box";      // character_id, title, body, item_id, quantity, created_at
    private static final String EVENT_KEY_BATCH  = "trungthu_2025_batch_sent";
    private static final String EVENT_KEY_PERCHR = "trungthu_2025";

    // ------------------------------------------------------------

    private static TopService instance;

    public static TopService gI() {
        if (instance == null) {
            instance = new TopService();
        }
        return instance;
    }

    /* ========================= RealTop update ========================= */
    public void updateTop() {
        if (RealTop.timeRealTop + (10 * 60 * 1000) < System.currentTimeMillis()) {
            RealTop.timeRealTop = System.currentTimeMillis();
            try (Connection con = DatabaseManager.getConnection()) {
                RealTop.topNV   = RealTop.realTop(RealTop.TOP_NV, con);
                RealTop.topDC   = RealTop.realTop(RealTop.TOP_DC, con);
                RealTop.topVDST = RealTop.realTop(RealTop.TOP_VDST, con);
                RealTop.topWHIS = RealTop.realTop(RealTop.TOP_WHIS, con);
            } catch (Exception ignored) {
                Logger.error("Lỗi đọc top");
            }
        }
    }

    /* ========================= Hiển thị TOP ========================= */
    public static void showListTopPower(Player player) {
        TopPowerManager.getInstance().load();
        List<Player> list = TopPowerManager.getInstance().getList();
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100");
            int count = Math.min(100, list.size());
            msg.writer().writeByte(count);
            for (int i = 0; i < count; i++) {
                Player top = list.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.name);
                msg.writer().writeUTF("Sức mạnh: " + Util.numberFormatLouis(top.nPoint.power));
                msg.writer().writeUTF("...");
            }
            player.sendMessage(msg);
        } catch (IOException e) {
            // ignore
        } finally {
            if (msg != null) msg.cleanup();
        }
    }

    public static void showListTopTrungThu(Player player) {
        TopTrungthu.getInstance().load();
        List<Player> list = TopTrungthu.getInstance().getList();
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100");
            int count = Math.min(100, list.size());
            msg.writer().writeByte(count);
            for (int i = 0; i < count; i++) {
                Player top = list.get(i);
                msg.writer().writeInt(i + 1); // top
                msg.writer().writeInt(i + 1); // rank
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.name);
                msg.writer().writeUTF("Top Sự Kiện Trung Thu: " + Util.numberFormatLouis(top.point_trungthu));
                msg.writer().writeUTF("...");
            }
            player.sendMessage(msg);
        } catch (IOException e) {
            // ignore
        } finally {
            if (msg != null) msg.cleanup();
        }
    }

    public static void showListTopHungVuong(Player player) {
        TopHungvuong.getInstance().load();
        List<Player> list = TopHungvuong.getInstance().getList();
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100");
            int count = Math.min(100, list.size());
            msg.writer().writeByte(count);
            for (int i = 0; i < count; i++) {
                Player top = list.get(i);
                msg.writer().writeInt(i + 1); // top
                msg.writer().writeInt(i + 1); // rank
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.name);
                msg.writer().writeUTF("Top Sự Kiện Hùng Vương: " + Util.numberFormatLouis(top.point_hungvuong));
                msg.writer().writeUTF("...");
            }
            player.sendMessage(msg);
        } catch (IOException e) {
            // ignore
        } finally {
            if (msg != null) msg.cleanup();
        }
    }

    public static void showListTopHelloween(Player player) {
        TopHelloween.getInstance().load();
        List<Player> list = TopHelloween.getInstance().getList();
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100");
            int count = Math.min(100, list.size());
            msg.writer().writeByte(count);
            for (int i = 0; i < count; i++) {
                Player top = list.get(i);
                msg.writer().writeInt(i + 1); // top
                msg.writer().writeInt(i + 1); // rank
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.name);
                msg.writer().writeUTF("Top Sự Kiện Helloween: " + Util.numberFormatLouis(top.point_helloween));
                msg.writer().writeUTF("...");
            }
            player.sendMessage(msg);
        } catch (IOException e) {
            // ignore
        } finally {
            if (msg != null) msg.cleanup();
        }
    }

    public static void showListTopVnd(Player player) {
        TopVnd.getInstance().load();
        List<Player> list = TopVnd.getInstance().getList();
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100");
            int count = Math.min(100, list.size());
            msg.writer().writeByte(count);
            for (int i = 0; i < count; i++) {
                Player top = list.get(i);
                msg.writer().writeInt(i + 1); // top
                msg.writer().writeInt(i + 1); // rank
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.name);
                msg.writer().writeUTF("Top Nạp: " + Util.numberFormatLouis(top.danap));
                msg.writer().writeUTF("...");
            }
            player.sendMessage(msg);
        } catch (IOException e) {
            // ignore
        } finally {
            if (msg != null) msg.cleanup();
        }
    }

    public static void showListTopTask(Player player) {
        TopTaskManager.getInstance().load();
        List<Player> list = TopTaskManager.getInstance().getList();
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100");
            int count = Math.min(100, list.size());
            msg.writer().writeByte(count);
            for (int i = 0; i < count; i++) {
                Player top = list.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());

                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.name);
                msg.writer().writeUTF(ZINZINSqlFetcher.loadById(top.id).playerTask.taskMain.name);
                msg.writer().writeUTF("...");
            }
            player.sendMessage(msg);
        } catch (IOException e) {
            // ignore
        } finally {
            if (msg != null) msg.cleanup();
        }
    }

    public static void showListTop(Player player, int select) {
        List<TOP> tops = RealTop.topNV;
        switch (select) {
            case 0 -> tops = RealTop.topNV;
            case 1 -> tops = RealTop.topDC;
            case 2 -> tops = RealTop.topSM;
            case 3 -> tops = RealTop.topWHIS;
        }
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100");
            int count = Math.min(100, tops.size());
            msg.writer().writeByte(count);
            for (int i = 0; i < count; i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                switch (select) {
                    case 0 -> {
                        msg.writer().writeUTF(TaskService.gI().getTaskMainById(player, top.getNv()).name.substring(
                                0,
                                Math.min(TaskService.gI().getTaskMainById(player, top.getNv()).name.length(), 20)
                        ) + "...");
                        msg.writer().writeUTF(
                                TaskService.gI().getTaskMainById(player, top.getNv()).subTasks.get(top.getSubnv()).name
                                        + " - " + getTimeLeft(top.getLasttime()));
                    }
                    case 1 -> {
                        msg.writer().writeUTF("Chơi đồ " + top.getDicanh() + " lần");
                        msg.writer().writeUTF("Gia nhập juventus " + top.getJuventus() + " lần");
                    }
                    case 2 -> {
                        msg.writer().writeUTF(getTimeLeft(top.getLasttime()));
                        msg.writer().writeUTF("...");
                    }
                    case 3 -> {
                        msg.writer().writeUTF("LV:" + top.getLevel() + " với "
                                + Util.roundToTwoDecimals(top.getTime() / 1000d) + " giây");
                        msg.writer().writeUTF(getTimeLeft(top.getLasttime()));
                    }
                }
            }
            player.sendMessage(msg);
        } catch (IOException e) {
            // ignore
        } finally {
            if (msg != null) msg.cleanup();
        }
    }

    public static String getTimeLeft(long lastTime) {
        int secondPassed = (int) ((System.currentTimeMillis() - lastTime) / 1000);
        return secondPassed > 86400 ? (secondPassed / 86400) + " ngày trước"
                : secondPassed > 3600 ? (secondPassed / 3600) + " giờ trước"
                : secondPassed > 60 ? (secondPassed / 60) + " phút trước" : secondPassed + " giây trước";
    }

    /* ====================== TRUNG THU – API CHO CHICHI ====================== */

    /** Lấy hạng Trung Thu hiện tại từ bộ nhớ TopTrungthu (1..n), -1 nếu không có trong BXH. */
    public int getRankTrungThu(Player p) {
        TopTrungthu.getInstance().load();
        List<Player> list = TopTrungthu.getInstance().getList();
        int count = Math.min(100, list.size());
        for (int i = 0; i < count; i++) {
            Player top = list.get(i);
            if (top != null && top.id == p.id) { // so theo character id
                return i + 1;
            }
        }
        return -1;
    }

    /** Kiểm tra nhân vật đã nhận thưởng Trung Thu chưa. */
    public boolean isTrungThuRewarded(Player p) {
        return isCharacterRewarded(EVENT_KEY_PERCHR, (int) p.id);
    }

    /** Đánh dấu nhân vật đã nhận thưởng Trung Thu. */
    public void markTrungThuRewarded(Player p) {
        markCharacterRewarded(EVENT_KEY_PERCHR, (int) p.id);
    }

    /* ====================== TRUNG THU – TỰ GỬI HỘP THƯ SAU KHI KẾT THÚC ====================== */

    /** Chạy bởi scheduler sau khi sự kiện kết thúc. Gửi quà Top 1/2–10/11–100 vào hộp thư. */
    public synchronized void autoSendTrungThuRewardsAll() {
        try {
            long now = System.currentTimeMillis();
            if (now <= EVENT_END) return;                 // chưa hết sự kiện
            if (isBatchRewarded(EVENT_KEY_BATCH)) return; // đã chạy batch trước đó

            // Lấy Top từ bộ nhớ
            TopTrungthu.getInstance().load();
            List<Player> list = TopTrungthu.getInstance().getList();
            if (list == null || list.isEmpty()) {
                markBatchRewarded(EVENT_KEY_BATCH);
                return;
            }

            int limit = Math.min(100, list.size());
            for (int i = 0; i < limit; i++) {
                Player top = list.get(i);
                if (top == null) continue;
                int charId = (int) top.id;
                String name = top.name != null ? top.name : "Chiến binh";
                int rank = i + 1;

                short box = boxByRank(rank);
                if (box <= 0) continue;

                // Nếu đã nhận (qua ChiChi) thì bỏ qua
                if (isCharacterRewarded(EVENT_KEY_PERCHR, charId)) continue;

                boolean mailed = sendRewardMailByChar(charId, box, 1,
                        "[Trung Thu 2025] Thưởng TOP hạng " + rank,
                        "Xin chúc mừng " + name + " đạt hạng " + rank + " sự kiện Trung Thu 2025!");
                if (mailed) {
                    markCharacterRewarded(EVENT_KEY_PERCHR, charId);
                }
            }

            // Đánh dấu batch
            markBatchRewarded(EVENT_KEY_BATCH);
        } catch (Exception e) {
            Logger.logException(TopService.class, e);
        }
    }

    private short boxByRank(int rank) {
        if (rank == 1) return TT_BOX_TOP1;
        if (rank >= 2 && rank <= 10) return TT_BOX_TOP10;
        if (rank >= 11 && rank <= 100) return TT_BOX_TOP100;
        return -1;
    }
    
    public boolean sendTrungThuRewardViaMail(models.player.Player p, short itemId, int quantity, int rank) {
    String title = "[Trung Thu 2025] Thưởng TOP hạng " + rank;
    String body  = "Xin chúc mừng " + p.name + " đạt hạng " + rank + " sự kiện Trung Thu 2025!";
    return sendRewardMailByChar((int) p.id, itemId, Math.max(1, quantity), title, body);
}

    /* ====================== Hộp thư & Cờ DB ====================== */

    /** Gửi vào hộp thư theo character_id. Thay SQL nếu schema của bạn khác. */
    private boolean sendRewardMailByChar(int characterId, short itemId, int quantity, String title, String body) {
        String sql = "INSERT INTO " + TBL_MAIL_BOX
                + " (character_id, title, body, item_id, quantity, created_at)"
                + " VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, characterId);
            ps.setString(2, title);
            ps.setString(3, body);
            ps.setShort(4, itemId);
            ps.setInt(5, Math.max(1, quantity));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            Logger.logException(TopService.class, e);
            return false;
        }
    }

    private boolean isBatchRewarded(String key) {
        String sql = "SELECT value FROM " + TBL_EVENT_STATE + " WHERE `key`=? LIMIT 1";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return "1".equals(rs.getString(1));
            }
        } catch (Exception e) {
            Logger.logException(TopService.class, e);
        }
        return false;
    }

    private void markBatchRewarded(String key) {
        String sql = "INSERT INTO " + TBL_EVENT_STATE + "(`key`,`value`) VALUES(?, '1') " +
                     "ON DUPLICATE KEY UPDATE `value`=VALUES(`value`)";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.executeUpdate();
        } catch (Exception e) {
            Logger.logException(TopService.class, e);
        }
    }

    private boolean isCharacterRewarded(String eventKey, int characterId) {
        String sql = "SELECT rewarded FROM " + TBL_EVENT_REWARD +
                     " WHERE event_key=? AND character_id=? LIMIT 1";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, eventKey);
            ps.setInt(2, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) == 1;
            }
        } catch (Exception e) {
            Logger.logException(TopService.class, e);
        }
        return false;
    }

    private void markCharacterRewarded(String eventKey, int characterId) {
        String sql = "INSERT INTO " + TBL_EVENT_REWARD + " (event_key, character_id, rewarded) " +
                     "VALUES (?, ?, 1) " +
                     "ON DUPLICATE KEY UPDATE rewarded=VALUES(rewarded)";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, eventKey);
            ps.setInt(2, characterId);
            ps.executeUpdate();
        } catch (Exception e) {
            Logger.logException(TopService.class, e);
        }
    }
}
