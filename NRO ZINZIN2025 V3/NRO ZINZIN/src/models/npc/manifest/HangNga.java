package models.npc.manifest;

import consts.ConstNpc;
import models.npc.Npc;
import models.player.Player;
import models.Top.TopService;
import models.shop.ShopService;
import services.Service;
import services.func.ChangeMapService;
import utils.Util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class HangNga extends Npc {

    // Mốc thời gian sự kiện theo giờ VN (+07:00)
    private static final ZoneId ZONE = ZoneId.of("Asia/Bangkok");
    private static final LocalDateTime EVENT_START_LDT    = LocalDateTime.of(2025, 8, 14, 0, 0, 0);
    private static final LocalDateTime EVENT_END_LDT      = LocalDateTime.of(2025, 9, 14, 23, 59, 59);
    private static final LocalDateTime CLAIM_DEADLINE_LDT = LocalDateTime.of(2025, 9, 20, 23, 59, 59);
    private static final int MENU_NHAN_PET_THO = 260200;

    private static long toMillis(LocalDateTime ldt) {
        return ldt.atZone(ZONE).toInstant().toEpochMilli();
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    private static final long EVENT_START     = toMillis(EVENT_START_LDT);
    private static final long EVENT_END       = toMillis(EVENT_END_LDT);
    private static final long CLAIM_DEADLINE  = toMillis(CLAIM_DEADLINE_LDT);

    public HangNga(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    private boolean eventActive() {
        long t = now();
        return t >= EVENT_START && t <= EVENT_END;
    }

    private String leftUntil(long targetMillis) {
        long diff = targetMillis - now();
        if (diff <= 0) return "0 ngày";
        long days  = diff / 86_400_000L;
        long hours = (diff % 86_400_000L) / 3_600_000L;
        return days + " ngày " + hours + " giờ";
    }

    private String infoText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sự kiện đua Top Sự Kiện Trung Thu nhận quà khủng\n");
        if (eventActive()) {
            sb.append("Kết thúc và trao giải sau: ").append(leftUntil(EVENT_END)).append("\n");
        } else {
            if (now() < EVENT_START) sb.append("Sự kiện chưa mở\n");
            else sb.append("Sự kiện đã kết thúc\n");
        }
        sb.append("Hạn chót nhận giải: ").append(leftUntil(CLAIM_DEADLINE)).append("\n")
          .append("Đến gặp Chi Chi để nhận giải nhé\n")
          .append("Chi tiết xem tại diễn đàn, fanpage.");
        return sb.toString();
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) return;
        if (this.mapId == 5 && player.escortRabbit != null && player.escortRabbit.zone == player.zone) {
    createOtherMenu(player, MENU_NHAN_PET_THO,
        "|2|Ngươi đã dẫn Thỏ đến nơi rồi à?\n|1|Muốn nhận thưởng chứ?",
        "Nhận pet", "Huỷ");
    return;
}
        if (mapId == 5) {
            createOtherMenu(player, ConstNpc.BASE_MENU,
                "Bạn muốn hỏi chi?",
                "Top Sự Kiện Trung Thu",
                "Cửa hàng",
                "Map Sự Kiện",
                "Đóng");
        } else if (mapId == 180) {
            createOtherMenu(player, ConstNpc.BASE_MENU,
                "Ngươi tìm ta có việc gì?",
                "Xem Top Trung Thu",
                "Xem điểm",
                "Quay về",
                "Đóng");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;

        // MAP 5 – MENU CHÍNH
        if (mapId == 5 && player.iDMark.isBaseMenu()) {
            switch (select) {
                case 0: { // vào submenu Top
                    createOtherMenu(player, ConstNpc.OTHER_MENU,
                        infoText(),
                        "Top 100 Sự Kiện Trung Thu",
                        "Xem điểm",
                        "Đóng");
                    break;
                }
                case 1: { // Cửa hàng
                    try {
                        ShopService.gI().opendShop(player, "SU_KIEN", false);
                    } catch (Exception e) {
                        Service.gI().sendThongBao(player, "Cửa hàng sự kiện đang bảo trì!");
                    }
                    break;
                }
                case 2: { // Map Sự Kiện
                    if (!eventActive()) {
                        Service.gI().sendThongBao(player, "Sự kiện chưa mở!");
                        return;
                    }
                    ChangeMapService.gI().changeMapNonSpaceship(
                        player,
                        180,
                        110 + Util.nextInt(0, 100),
                        408
                    );
                    break;
                }
                default:
                    break;
            }
            return;
        }

        // MAP 5 – SUBMENU (OTHER_MENU)
        if (mapId == 5 && player.iDMark.getIndexMenu() == ConstNpc.OTHER_MENU) {
            switch (select) {
                case 0: { // Top 100 (yêu cầu >= 10 điểm)
                    if (player.point_trungthu >= 10) {
                        TopService.gI().showListTopTrungThu(player);
                    } else {
                        createOtherMenu(player, ConstNpc.OTHER_MENU,
                            "Chưa đủ điểm vào TOP (ít nhất 10 điểm)",
                            "Đóng");
                    }
                    break;
                }
                case 1: { // Xem điểm (không yêu cầu >=10)
                    Service.gI().sendThongBaoOK(
                        player,
                        "Bạn đang có " + player.point_trungthu + " điểm Trung Thu."
                    );
                    break;
                }
                default:
                    break;
            }
            return;
        }

        // MAP 180 – MENU CHÍNH
        if (mapId == 180 && player.iDMark.isBaseMenu()) {
            switch (select) {
                case 0: { // Xem Top (yêu cầu >= 10 điểm)
                    if (player.point_trungthu >= 10) {
                        TopService.gI().showListTopTrungThu(player);
                    } else {
                        createOtherMenu(player, ConstNpc.OTHER_MENU,
                            "Chưa đủ điểm vào TOP (ít nhất 10 điểm)",
                            "Đóng");
                    }
                    break;
                }
                case 1: { // Xem điểm
                    Service.gI().sendThongBaoOK(
                        player,
                        "Bạn đang có " + player.point_trungthu + " điểm Trung Thu."
                    );
                    break;
                }
                case 2: { // Quay về map 5
                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                    break;
                }
                default:
                    break;
            }
        }
        if (player.iDMark.getIndexMenu() == MENU_NHAN_PET_THO) {
    if (select == 0) { // Nhận pet
        if (player.escortRabbit == null || player.escortRabbit.zone != player.zone) {
            Service.gI().sendThongBao(player, "Chưa thấy Thỏ đâu!");
            return;
        }
        // +1 điểm sự kiện Trung Thu
        player.point_trungthu += 1;
        Service.gI().sendThongBaoOK(player,
            "Nhận thành công!\nđiểm sự kiện Trung Thu (tổng: " + player.point_trungthu + ")");

        // Cho Thỏ rời đi & xóa trạng thái
        player.escortRabbit.finishAndDespawn();
        player.escortRabbit = null;
    }
    return;
}

    }
}
