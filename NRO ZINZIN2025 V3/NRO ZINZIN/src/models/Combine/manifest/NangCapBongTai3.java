package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import models.item.Item;
import models.Combine.CombineService;
import models.player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

public class NangCapBongTai3 {

    // ==== Thay đổi nếu bạn có ID khác cho bông tai cấp 3 ====
    private static final short EARRING_LV2_ID = 921;    // bông tai cấp 2
    private static final short SHARD_ID        = 1861;  // mảnh vỡ bông tai cấp 3
    private static final short EARRING_LV3_ID = 1860;    // bông tai cấp 3 (thay ID thật ở đây)

    // Hiển thị menu combine
    public static void showInfoCombine(Player player) {
        // 1) phải bỏ đúng 2 món
        if (player.combine.itemsCombine.size() != 2) {
            Service.gI().sendDialogMessage(player,
                "Cần 1 bông tai cấp 2 và 9999 mảnh vỡ bông tai cấp 3.");
            return;
        }

        // 2) tìm item
        Item lv2   = null;
        Item shard = null;
        for (Item it : player.combine.itemsCombine) {
            if (it.template.id == EARRING_LV2_ID) lv2   = it;
            else if (it.template.id == SHARD_ID)        shard = it;
        }
        if (lv2 == null || shard == null) {
            Service.gI().sendDialogMessage(player,
                "Cần 1 bông tai cấp 2 và 9999 mảnh vỡ bông tai cấp 3.");
            return;
        }

        // 3) đọc số lượng mảnh vỡ từ trường quantity
        int quantityShard = shard.quantity;

        // 4) build nội dung menu
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE)
            .append("Bông tai Porata [+3]\n\n")
            .append(ConstFont.BOLD_BLUE)
            .append("Tỉ lệ thành công: 10%\n")
            .append(quantityShard >= 9999 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần 9999 mảnh vỡ bông tai cấp 3\n")
            .append(player.inventory.gold >= 5_000_000 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần 5 000 000 vàng\n")
            .append(player.inventory.getGemAndRuby() >= 20 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần 20 ngọc\n")
            .append(ConstFont.BOLD_RED)
            .append("Thất bại: mất 99 mảnh vỡ\n");

        // 5) nếu thiếu thì show nút “Còn thiếu…” để ignore menu
        if (player.inventory.getGemAndRuby() < 20) {
            CombineService.gI().baHatMit.createOtherMenu(
                player,
                ConstNpc.IGNORE_MENU,
                text.toString(),
                "Còn thiếu\n" + (20 - player.inventory.getGemAndRuby()) + " ngọc"
            );
            return;
        }
        if (player.inventory.gold < 5_000_000) {
            CombineService.gI().baHatMit.createOtherMenu(
                player,
                ConstNpc.IGNORE_MENU,
                text.toString(),
                "Còn thiếu\n" + Util.numberToMoney(5_000_000 - player.inventory.gold) + " vàng"
            );
            return;
        }
        if (quantityShard < 9999) {
            CombineService.gI().baHatMit.createOtherMenu(
                player,
                ConstNpc.IGNORE_MENU,
                text.toString(),
                "Còn thiếu\n" + (9999 - quantityShard) + " mảnh vỡ"
            );
            return;
        }

        // 6) đủ điều kiện, show nút Nâng cấp
        CombineService.gI().baHatMit.createOtherMenu(
            player,
            ConstNpc.MENU_START_COMBINE,
            text.toString(),
            "Nâng cấp",
            "Từ chối"
        );
    }

    // Xử lý sự kiện khi bấm “Nâng cấp”
    public static void nangCapBongTai3(Player player) {
        // 1) kiểm tra lại số món
        if (player.combine.itemsCombine.size() != 2) return;

        // 2) tìm lại item
        Item lv2   = null;
        Item shard = null;
        for (Item it : player.combine.itemsCombine) {
            if (it.template.id == EARRING_LV2_ID) lv2   = it;
            else if (it.template.id == SHARD_ID)        shard = it;
        }
        if (lv2 == null || shard == null) return;

        // 3) kiểm tra đủ nguyên liệu
        int quantityShard = shard.quantity;
        if (quantityShard < 9999
         || player.inventory.gold < 5_000_000
         || player.inventory.getGemAndRuby() < 20) {
            return;
        }

        // 4) trừ vàng và ngọc
        player.inventory.gold -= 5_000_000;
        player.inventory.subGemAndRuby(20);

        // 5) roll tỉ lệ 10%
        if (Util.isTrue(10, 100)) {
            // ✅ Thành công: 
            //    - trừ 1 bông tai lv2
            InventoryService.gI().subQuantityItemsBag(player, lv2, 1);
            //    - trừ 9999 mảnh
            InventoryService.gI().subQuantityItemsBag(player, shard, 9999);
            //    - cấp ra bông tai lv3
            Item newE = ItemService.gI().createNewItem(EARRING_LV3_ID);
            //    (tuỳ ý gán thêm chỉ số)
            newE.itemOptions.add(new Item.ItemOption(72, 3));
            InventoryService.gI().addItemBag(player, newE);
            //    - hiệu ứng thành công
            CombineService.gI().sendEffectSuccessCombine(player);

        } else {
            // ❌ Thất bại:
            //    - trừ 99 mảnh
            InventoryService.gI().subQuantityItemsBag(player, shard, 99);
            //    - hiệu ứng thất bại
            CombineService.gI().sendEffectFailCombine(player);
        }

        // 6) cập nhật lại UI
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}
