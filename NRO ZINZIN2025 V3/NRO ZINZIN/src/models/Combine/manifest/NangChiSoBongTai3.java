package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import models.Combine.CombineService;
import models.item.Item;
import models.player.Player;
import services.InventoryService;
import services.Service;
import utils.Util;

public class NangChiSoBongTai3 {

    private static final short EARRING_LV3_ID       = 1860;   // Bông tai cấp 3 (Porata +3)
    private static final short SHARD_ID             = 934;    // Mảnh hồn Porata
    private static final short BLUE_STONE_ID        = 935;    // Đá xanh lam
    private static final int   SHARD_REQUIRED       = 99;     // Số mảnh hồn cần
    private static final int   BLUE_STONE_REQUIRED  = 1;      // Số đá xanh lam cần
    private static final int   GEM_COST             = 250;    // 250 ngọc
    private static final int   SUCCESS_RATE         = 10;     // Tỉ lệ thành công (%)
    private static final int[] OPTION_IDS           = {77, 103, 50, 108, 94, 14, 80, 81, 175, 5};

    /** Hiển thị menu "Nâng chỉ số bông tai +3" */
    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 3) {
            Service.gI().sendDialogMessage(player,
                "Cần 1 bông tai cấp 3, " + SHARD_REQUIRED + " mảnh hồn Porata và " +
                BLUE_STONE_REQUIRED + " đá xanh lam.");
            return;
        }

        Item earring3 = null, shard = null, blueStone = null;
        for (Item it : player.combine.itemsCombine) {
            if (!it.isNotNullItem()) continue;
            switch (it.template.id) {
                case EARRING_LV3_ID -> earring3 = it;
                case SHARD_ID        -> shard     = it;
                case BLUE_STONE_ID   -> blueStone = it;
            }
        }
        if (earring3 == null || shard == null || blueStone == null) {
            Service.gI().sendDialogMessage(player,
                "Cần 1 bông tai cấp 3, " + SHARD_REQUIRED + " mảnh hồn Porata và " +
                BLUE_STONE_REQUIRED + " đá xanh lam.");
            return;
        }

        int qtyShard = shard.quantity;
        int qtyBlue  = blueStone.quantity;
        int haveGem  = player.inventory.getGemAndRuby();

        StringBuilder text = new StringBuilder()
            .append(ConstFont.BOLD_BLUE).append("Bông tai Porata [+3]\n\n")
            .append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: ").append(SUCCESS_RATE).append("%\n")
            .append(qtyShard >= SHARD_REQUIRED ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần ").append(SHARD_REQUIRED).append(" mảnh hồn Porata\n")
            .append(qtyBlue  >= BLUE_STONE_REQUIRED ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần ").append(BLUE_STONE_REQUIRED).append(" đá xanh lam\n")
            .append(haveGem  >= GEM_COST ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần ").append(GEM_COST).append(" ngọc\n")
            .append(ConstFont.BOLD_GREEN).append("+1 Chỉ số ngẫu nhiên\n");

        // Kiểm tra từng điều kiện thiếu
        if (haveGem < GEM_COST) {
            CombineService.gI().baHatMit.createOtherMenu(
                player, ConstNpc.IGNORE_MENU, text.toString(),
                "Còn thiếu\n" + (GEM_COST - haveGem) + " ngọc"
            );
            return;
        }
        if (qtyBlue < BLUE_STONE_REQUIRED) {
            CombineService.gI().baHatMit.createOtherMenu(
                player, ConstNpc.IGNORE_MENU, text.toString(),
                "Còn thiếu\n" + BLUE_STONE_REQUIRED + " đá xanh lam"
            );
            return;
        }
        if (qtyShard < SHARD_REQUIRED) {
            CombineService.gI().baHatMit.createOtherMenu(
                player, ConstNpc.IGNORE_MENU, text.toString(),
                "Còn thiếu\n" + (SHARD_REQUIRED - qtyShard) + " mảnh hồn Porata"
            );
            return;
        }

        // Đủ điều kiện, show nút Nâng cấp
        CombineService.gI().baHatMit.createOtherMenu(
            player, ConstNpc.MENU_START_COMBINE, text.toString(),
            "Nâng cấp\n" + GEM_COST + " ngọc", "Từ chối"
        );
    }

    /** Xử lý khi bấm “Nâng cấp” */
    public static void nangChiSoBongTai3(Player player) {
        if (player.combine.itemsCombine.size() != 3) return;

        Item earring3 = null, shard = null, blueStone = null;
        for (Item it : player.combine.itemsCombine) {
            if (!it.isNotNullItem()) continue;
            switch (it.template.id) {
                case EARRING_LV3_ID -> earring3 = it;
                case SHARD_ID        -> shard     = it;
                case BLUE_STONE_ID   -> blueStone = it;
            }
        }
        if (earring3 == null || shard == null || blueStone == null) return;

        int qtyShard = shard.quantity;
        int qtyBlue  = blueStone.quantity;
        int haveGem  = player.inventory.getGemAndRuby();
        if (qtyShard < SHARD_REQUIRED || qtyBlue < BLUE_STONE_REQUIRED || haveGem < GEM_COST) return;

        // Trừ ngọc
        player.inventory.subGemAndRuby(GEM_COST);

        // Roll kết quả
        if (Util.isTrue(SUCCESS_RATE, 100)) {
            earring3.itemOptions.clear();
            int opt = OPTION_IDS[Util.nextInt(OPTION_IDS.length)];
            int param = (opt == 94 || opt == 14) ? Util.nextInt(3, 15) : Util.nextInt(5, 20);
            earring3.itemOptions.add(new Item.ItemOption(opt, param));
            earring3.itemOptions.add(new Item.ItemOption(72, 3));
            earring3.itemOptions.add(new Item.ItemOption(38, 0));
            CombineService.gI().sendEffectSuccessCombine(player);
        } else {
            CombineService.gI().sendEffectFailCombine(player);
        }

        // Trừ mảnh và đá xanh lam
        InventoryService.gI().subQuantityItemsBag(player, shard, SHARD_REQUIRED);
        InventoryService.gI().subQuantityItemsBag(player, blueStone, BLUE_STONE_REQUIRED);

        // Cập nhật UI
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}