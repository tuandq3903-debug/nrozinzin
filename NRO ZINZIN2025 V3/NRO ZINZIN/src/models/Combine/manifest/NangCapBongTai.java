package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import models.Combine.CombineService;
import models.item.Item;
import models.player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

public class NangCapBongTai {

    // ==== Hằng số nguyên liệu và kết quả ====
    private static final short EARRING_LV1_ID = 454;      // Bông tai cấp 1
    private static final short SHARD_ID        = 933;      // Mảnh vỡ bông tai
    private static final short EARRING_LV2_ID = 921;      // Bông tai cấp 2 (Porata +2)
    private static final int   SHARD_REQUIRED  = 9999;    // Số mảnh vỡ cần
    private static final int   GOLD_COST       = 5000000; // Vàng cần
    private static final int   GEM_COST        = 20;      // Ngọc cần
    private static final int   SUCCESS_RATE    = 50;      // Tỉ lệ thành công (%)

    /** Hiển thị menu combine nâng cấp bông tai từ +1 lên +2 */
    public static void showInfoCombine(Player player) {
    if (player.combine.itemsCombine.size() != 2) {
        Service.gI().sendDialogMessage(player,
            "Cần 1 bông tai cấp 1 (ID " + EARRING_LV1_ID + ") và " +
            SHARD_REQUIRED + " mảnh vỡ (ID " + SHARD_ID + ").");
        return;
    }
    Item earring1 = null, shard = null;
    for (Item it : player.combine.itemsCombine) {
        if (!it.isNotNullItem()) continue;
        if (it.template.id == EARRING_LV1_ID)      earring1 = it;
        else if (it.template.id == SHARD_ID)       shard    = it;
    }
    if (earring1 == null || shard == null) {
        Service.gI().sendDialogMessage(player,
            "Cần 1 bông tai cấp 1 và 9999 mảnh vỡ.");
        return;
    }

    int qtyShard = shard.quantity;
    long haveGold = player.inventory.gold;
    int haveGem  = player.inventory.getGemAndRuby();

    StringBuilder text = new StringBuilder()
        .append(ConstFont.BOLD_BLUE).append("Bông tai Porata [+2]\n\n")
        .append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: ").append(SUCCESS_RATE).append("%\n")
        .append(qtyShard >= SHARD_REQUIRED ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
        .append("Cần ").append(SHARD_REQUIRED).append(" mảnh vỡ\n")
        .append(haveGold  >= GOLD_COST      ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
        .append("Cần 5 Tr vàng\n")   // ← luôn 5 Tr
        .append(haveGem   >= GEM_COST       ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
        .append("Cần 20 ngọc\n")
        .append(ConstFont.BOLD_RED).append("Thất bại: mất 99 mảnh vỡ\n");

    // Thiếu ngọc
    if (haveGem < GEM_COST) {
        CombineService.gI().baHatMit.createOtherMenu(
            player, ConstNpc.IGNORE_MENU, text.toString(),
            "Còn thiếu\n" + (GEM_COST - haveGem) + " ngọc"
        );
        return;
    }
    // Thiếu vàng
    if (haveGold < GOLD_COST) {
        CombineService.gI().baHatMit.createOtherMenu(
            player, ConstNpc.IGNORE_MENU, text.toString(),
            "Còn thiếu\n" + Util.numberToMoney(GOLD_COST - haveGold) + " vàng"
        );
        return;
    }
    // Thiếu mảnh vỡ
    if (qtyShard < SHARD_REQUIRED) {
        CombineService.gI().baHatMit.createOtherMenu(
            player, ConstNpc.IGNORE_MENU, text.toString(),
            "Còn thiếu\n" + (SHARD_REQUIRED - qtyShard) + " mảnh vỡ"
        );
        return;
    }

    // Đủ điều kiện → show nút Nâng cấp
    CombineService.gI().baHatMit.createOtherMenu(
        player, ConstNpc.MENU_START_COMBINE, text.toString(),
        "Nâng cấp\n5 Tr vàng\n20 ngọc",  // ← 5 Tr đúng như yêu cầu
        "Từ chối"
    );
}


    /** Xử lý khi bấm "Nâng cấp" */
    public static void nangCapBongTai(Player player) {
        if (player.combine.itemsCombine.size() != 2) return;

        Item earring1 = null, shard = null;
        for (Item it : player.combine.itemsCombine) {
            if (!it.isNotNullItem()) continue;
            if (it.template.id == EARRING_LV1_ID)      earring1 = it;
            else if (it.template.id == SHARD_ID)       shard    = it;
        }
        if (earring1 == null || shard == null) return;

        int qtyShard = shard.quantity;
        long haveGold = player.inventory.gold;
        int haveGem  = player.inventory.getGemAndRuby();
        if (qtyShard < SHARD_REQUIRED || haveGold < GOLD_COST || haveGem < GEM_COST) return;

        // Trừ vàng và ngọc
        player.inventory.gold -= GOLD_COST;
        player.inventory.subGemAndRuby(GEM_COST);

        // Thực hiện combine
        if (Util.isTrue(SUCCESS_RATE, 100)) {
            // Thành công: tạo +2
            InventoryService.gI().subQuantityItemsBag(player, earring1, 1);
            InventoryService.gI().subQuantityItemsBag(player, shard, SHARD_REQUIRED);
            Item earring2 = ItemService.gI().createNewItem(EARRING_LV2_ID);
            earring2.itemOptions.add(new Item.ItemOption(72, 2));
            InventoryService.gI().addItemBag(player, earring2);
            CombineService.gI().sendEffectSuccessCombine(player);
        } else {
            // Thất bại: mất 99 mảnh
            InventoryService.gI().subQuantityItemsBag(player, shard, 99);
            CombineService.gI().sendEffectFailCombine(player);
        }

        // Cập nhật UI
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}