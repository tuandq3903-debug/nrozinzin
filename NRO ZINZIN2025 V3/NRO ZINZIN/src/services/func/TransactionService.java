package services.func;

/*
 *
 * @author ZINZIN
 */

import server.ThreadPoolManager;
import jdbc.DatabaseManager;
import jdbc.daos.PlayerDAO;
import models.player.Player;
import network.Message;
import server.Client;
import server.Maintenance;
import services.Service;
import utils.Logger;
import utils.TimeUtil;
import utils.Util;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import models.Bot.Bot;

public class TransactionService implements Runnable {

    private static final int TIME_DELAY_TRADE = 30_000;   // 30s
    private static final long TICK_MS = 300;

    /** Map trade an toàn cho đa luồng (network thread + scheduler thread). */
    static final Map<Player, Trade> PLAYER_TRADE = new ConcurrentHashMap<>();

    private static final byte SEND_INVITE_TRADE = 0;
    private static final byte ACCEPT_TRADE     = 1;
    private static final byte ADD_ITEM_TRADE   = 2;
    private static final byte CANCEL_TRADE     = 3;
    private static final byte LOCK_TRADE       = 5;
    private static final byte ACCEPT           = 7;

    private static volatile TransactionService i;

    /** Future để dừng vòng lặp khi cần (maintenance/shutdown). */
    private volatile ScheduledFuture<?> loopFuture;

    private TransactionService() {}

    public static TransactionService gI() {
        TransactionService ref = i;
        if (ref == null) {
            synchronized (TransactionService.class) {
                if (i == null) {
                    i = new TransactionService();
                    i.start();
                }
                ref = i;
            }
        }
        return ref;
    }

    /** Khởi động tick update định kỳ. */
    private void start() {
        if (loopFuture == null || loopFuture.isCancelled()) {
            loopFuture = ThreadPoolManager.SCHEDULER
                    .scheduleWithFixedDelay(this, 0, TICK_MS, TimeUnit.MILLISECONDS);
        }
    }

    /** Dừng tick update. */
    public void stop() {
        ScheduledFuture<?> f = loopFuture;
        if (f != null) {
            f.cancel(false);
        }
    }

    public void controller(Player pl, Message msg) {
        try {
            byte action = msg.reader().readByte();
            int playerId = -1;
            Player plMap = null;
            Trade trade = PLAYER_TRADE.get(pl);

            if (pl.baovetaikhoan) {
                Service.gI().sendThongBao(pl, "Chức năng bảo vệ đã được bật. Bạn vui lòng kiểm tra lại");
                return;
            }
            if (!pl.getSession().actived) {
                Service.gI().sendThongBao(pl, "Vui lòng kích hoạt thành viên");
                return;
            }

            if (action == SEND_INVITE_TRADE) {
                pl.iDMark.setTransactionWP(false);
                pl.iDMark.setTransactionWVP(false);
            }

            switch (action) {
                case SEND_INVITE_TRADE:
                case ACCEPT_TRADE: {
                    playerId = msg.reader().readInt();
                    plMap = pl.zone.getPlayerInMap(playerId);
                    if (plMap != null && plMap.isPl()) {
                        if (plMap.tradeWVP) {
                            return;
                        }
                        trade = PLAYER_TRADE.get(pl);
                        if (trade == null) {
                            trade = PLAYER_TRADE.get(plMap);
                        }
                        if (trade == null) {
                            if (action == SEND_INVITE_TRADE) {
                                if (Util.canDoWithTime(pl.iDMark.getLastTimeTrade(), TIME_DELAY_TRADE)
                                        && Util.canDoWithTime(plMap.iDMark.getLastTimeTrade(), TIME_DELAY_TRADE)) {
                                    boolean checkLogout1 = false;
                                    boolean checkLogout2 = false;
                                    try (Connection con = DatabaseManager.getConnection()) {
                                        checkLogout1 = PlayerDAO.checkLogout(con, pl);
                                        checkLogout2 = PlayerDAO.checkLogout(con, plMap);
                                    } catch (Exception e) {
                                        // tránh làm gián đoạn flow vì lỗi DB tạm thời
                                        Logger.logException(getClass(), e);
                                    }
                                    if (checkLogout1) {
                                        Client.gI().kickSession(pl.getSession());
                                        break;
                                    }
                                    if (checkLogout2) {
                                        Client.gI().kickSession(plMap.getSession());
                                        break;
                                    }
                                    pl.iDMark.setLastTimeTrade(System.currentTimeMillis());
                                    pl.iDMark.setPlayerTradeId((int) plMap.id);
                                    sendInviteTrade(pl, plMap);
                                } else {
                                    Service.gI().sendThongBao(pl, "Thử lại sau " +
                                            TimeUtil.getTimeLeft(
                                                    Math.max(pl.iDMark.getLastTimeTrade(), plMap.iDMark.getLastTimeTrade()),
                                                    TIME_DELAY_TRADE / 1000));
                                }
                            } else { // ACCEPT_TRADE
                                if (plMap.iDMark.getPlayerTradeId() == pl.id) {
                                    trade = new Trade(pl, plMap);
                                    trade.openTabTrade();
                                }
                            }
                        } else {
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                        }
                    }
                    break;
                }
                case ADD_ITEM_TRADE: {
                    trade = PLAYER_TRADE.get(pl);
                    if (trade != null) {
                        byte index = msg.reader().readByte();
                        int quantity = msg.reader().readInt();
                        if (quantity < 0) {
                            Service.gI().sendThongBao(pl, "Không thể thực hiện");
                            trade.cancelTrade();
                            break;
                        }
                        if (quantity == 0) {
                            quantity = 1;
                        }
                        if (index != -1 && quantity > Trade.QUANLITY_MAX) {
                            Service.gI().sendThongBao(pl, "Đã quá giới hạn giao dịch...");
                            trade.cancelTrade();
                            break;
                        }
                        trade.addItemTrade(pl, index, quantity);
                    }
                    break;
                }
                case CANCEL_TRADE: {
                    trade = PLAYER_TRADE.get(pl);
                    if (trade != null) {
                        trade.cancelTrade();
                    }
                    break;
                }
                case LOCK_TRADE: {
                    trade = PLAYER_TRADE.get(pl);
                    if (Maintenance.isRunning) {
                        if (trade != null) trade.cancelTrade();
                        break;
                    }
                    if (trade != null) {
                        trade.lockTran(pl);
                    }
                    break;
                }
                case ACCEPT: {
                    trade = PLAYER_TRADE.get(pl);
                    if (Maintenance.isRunning) {
                        if (trade != null) trade.cancelTrade();
                        break;
                    }
                    if (trade != null) {
                        trade.acceptTrade();
                        if (trade.accept == 1) {
                            Service.gI().sendThongBao(pl, "Xin chờ đối phương đồng ý");
                        } else if (trade.accept == 2) {
                            trade.dispose();
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            Logger.logException(this.getClass(), e);
        }
    }

    /** Mời giao dịch */
    private void sendInviteTrade(Player plInvite, Player plReceive) {
        if (plReceive.isBot) {
            ((Bot) plReceive).shop.activeTraDe(plInvite);
        }
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(0);
            msg.writer().writeInt((int) plInvite.id);
            plReceive.sendMessage(msg);
        } catch (Exception e) {
            Logger.logException(this.getClass(), e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    /** Hủy giao dịch theo player */
    public void cancelTrade(Player player) {
        Trade trade = PLAYER_TRADE.get(player);
        if (trade != null) {
            trade.cancelTrade();
        }
    }

    public boolean check(Player player) {
        return PLAYER_TRADE.get(player) != null;
    }

    /** Tick update: chạy 1 lần mỗi 300ms bởi scheduler. */
    @Override
    public void run() {
        // Khi maintenance bắt đầu, dừng vòng lặp để giải phóng tài nguyên
        if (Maintenance.isRunning) {
            stop();
            return;
        }
        try {
            // Duyệt snapshot entrySet của ConcurrentHashMap an toàn khi thêm/xóa
            Set<Map.Entry<Player, Trade>> entrySet = PLAYER_TRADE.entrySet();
            for (Map.Entry<Player, Trade> entry : entrySet) {
                Trade t = entry.getValue();
                if (t == null) continue;
                try {
                    t.update();
                } catch (Throwable perTrade) {
                    // Không để một trade lỗi làm hỏng cả vòng tick
//                    Logger.logException(this.getClass(), perTrade);
                }
            }
            // Có thể dọn trade kết thúc (nếu Trade có cờ trạng thái), ví dụ:
            // PLAYER_TRADE.entrySet().removeIf(e -> e.getValue() == null || e.getValue().isDone());
        } catch (Throwable ex) {
//            Logger.logException(this.getClass(), ex);
        }
    }
}
