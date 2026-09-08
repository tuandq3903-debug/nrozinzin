package models.npc.manifest;

import consts.ConstNpc;
import models.npc.Npc;
import models.player.Player;
import models.Top.TopService;
import services.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import services.InventoryService;
import utils.Util;

public class ChiChi extends Npc {

    
    private static final ZoneId ZONE = ZoneId.of("Asia/Bangkok");
    private static final long EVENT_END = LocalDateTime.of(2025, 9, 14, 23, 59, 59)
            .atZone(ZONE).toInstant().toEpochMilli();
    private static final short TT_BOX_TOP1   = 2003; // Top 1
    private static final short TT_BOX_TOP10  = 2004; // Top 2–10
    private static final short TT_BOX_TOP100 = 2005; // Top 11–100
    private static final int  MENU_TIM_PET_THO_CONFIRM = 260100;
    private static final int  MENU_TIM_PET_THO_MISSING = 260101;
    private static final short ITEM_BANH_TRUNG_THU    = 1701; 
    private static final short ITEM_DEN_LONG          = 1305; 
    private static final int  GOLD_TIM_PET            = 1_000_000; // 1 triệu vàng

    public ChiChi(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    private boolean eventEnded() {
        return System.currentTimeMillis() > EVENT_END;
    }

    private short boxByRank(int rank) {
        if (rank == 1) return TT_BOX_TOP1;
        if (rank >= 2 && rank <= 10) return TT_BOX_TOP10;
        if (rank >= 11 && rank <= 100) return TT_BOX_TOP100;
        return -1;
    }

    private void claim(Player p) {
        if (!eventEnded()) {
            Service.gI().sendThongBao(p, "Chưa đến thời gian trao thưởng. Quay lại sau khi sự kiện kết thúc!");
            return;
        }

        if (TopService.gI().isTrungThuRewarded(p)) {
            Service.gI().sendThongBaoOK(p, "Bạn đã nhận thưởng Trung Thu rồi!");
            return;
        }

        int rank = TopService.gI().getRankTrungThu(p); // 1..100, -1 nếu không trong BXH
        if (rank <= 0 || rank > 100) {
            Service.gI().sendThongBao(p, "Rất tiếc, bạn không nằm trong TOP nhận thưởng!");
            return;
        }

        short boxId = boxByRank(rank);
        if (boxId <= 0) {
            Service.gI().sendThongBao(p, "Không tìm thấy phần thưởng phù hợp!");
            return;
        }

        boolean ok = TopService.gI().sendTrungThuRewardViaMail(p, boxId, 1, rank);
        if (ok) {
            TopService.gI().markTrungThuRewarded(p);
            Service.gI().sendThongBaoOK(p,
                    "Đã gửi phần thưởng TOP Trung Thu (hạng " + rank + ") vào Hộp thư!");
        } else {
            Service.gI().sendThongBao(p, "Có lỗi khi gửi phần thưởng, vui lòng thử lại!");
        }
    }
    
    private void showTimPetMenu(Player p) {
    if (p.escortRabbit != null) {
        Service.gI().sendThongBaoOK(p, "|2|Bạn đang dắt Thỏ.\nHãy đưa tới Hang Nga (Map 5) và chọn 'Nhận pet'.");
        return;
    }
    int haveBanh = 0, haveDen = 0;
    var banh = InventoryService.gI().findItemBag(p, ITEM_BANH_TRUNG_THU);
    var den  = InventoryService.gI().findItemBag(p, ITEM_DEN_LONG);
    if (banh != null) haveBanh = banh.quantity;
    if (den  != null) haveDen  = den.quantity;

    boolean ok = haveBanh >= 1 && haveDen >= 1 && p.inventory.gold >= GOLD_TIM_PET;

    StringBuilder sb = new StringBuilder("Bắt đầu 'Tìm Pet Thỏ'?\n");
    sb.append((haveBanh>=1 ? "|1|" : "|7|")).append("Bánh Trung Thu (").append(ITEM_BANH_TRUNG_THU).append("): ")
      .append(Math.min(haveBanh, 1)).append("/1\n");
    sb.append((haveDen>=1 ? "|1|" : "|7|")).append("Ánh Trăng (").append(ITEM_DEN_LONG).append("): ")
      .append(Math.min(haveDen, 1)).append("/1\n");
    sb.append((p.inventory.gold>=GOLD_TIM_PET ? "|1|" : "|7|"))
      .append("Vàng: ").append(Util.numberToMoney(p.inventory.gold)).append("/")
      .append(Util.numberToMoney(GOLD_TIM_PET)).append("\n");

    if (ok) {
        createOtherMenu(p, MENU_TIM_PET_THO_CONFIRM, sb.toString(), "Bắt đầu", "Huỷ");
    } else {
        createOtherMenu(p, MENU_TIM_PET_THO_MISSING, sb.toString(), "Đóng");
    }
}


    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) return;

        createOtherMenu(player, ConstNpc.BASE_MENU,
            "Ta phụ trách **trao thưởng TOP Trung Thu**.",
            "Nhận thưởng Top Trung Thu",
            "Xem xếp hạng của tôi",
            "Tìm Pet Thỏ",
            "Đóng");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;

        if (player.iDMark.isBaseMenu()) {
            switch (select) {
                case 0: // Nhận thưởng → gửi vào Hộp thư
                    claim(player);
                    break;
                case 1: { // Xem hạng hiện tại
                    int rank = TopService.gI().getRankTrungThu(player);
                    if (rank > 0) {
                        Service.gI().sendThongBaoOK(player,
                            "Hạng Trung Thu hiện tại của bạn: " + rank
                          + "\nĐiểm: " + player.point_trungthu);
                    } else {
                        Service.gI().sendThongBaoOK(player,
                            "Bạn chưa có trong BXH Trung Thu.\nĐiểm hiện tại: " + player.point_trungthu);
                    }
                    break;
                }
                case 2: // Tìm Pet Thỏ
                    showTimPetMenu(player);
                    break;
                default:
                    break;
            }
        }
        if (player.iDMark.getIndexMenu() == MENU_TIM_PET_THO_CONFIRM) {
    if (select == 0) { // Bắt đầu
        if (player.escortRabbit != null) {
            Service.gI().sendThongBaoOK(player, "Bạn đang dắt Thỏ rồi!");
            return;
        }
        var banh = InventoryService.gI().findItemBag(player, ITEM_BANH_TRUNG_THU);
        var den  = InventoryService.gI().findItemBag(player, ITEM_DEN_LONG);
        if (banh == null || banh.quantity < 1 || den == null || den.quantity < 1 || player.inventory.gold < GOLD_TIM_PET) {
            Service.gI().sendThongBao(player, "Thiếu nguyên liệu hoặc vàng!");
            return;
        }
        // Trừ nguyên liệu + vàng
        InventoryService.gI().subQuantityItemsBag(player, banh, 1);
        InventoryService.gI().subQuantityItemsBag(player, den, 1);
        player.inventory.gold -= GOLD_TIM_PET;
        Service.gI().sendMoney(player);
        InventoryService.gI().sendItemBag(player);

        // Gọi Pet Thỏ
        EscortPetRabbit pet = new EscortPetRabbit(player);
        pet.spawnNearOwner();
        player.escortRabbit = pet;

        Service.gI().sendThongBaoOK(player,
            "Đã bắt được Thỏ!\nHãy dắt Thỏ tới gặp Hằng Nga để nhận thưởng.");
    }
    return;
}

    }
}
