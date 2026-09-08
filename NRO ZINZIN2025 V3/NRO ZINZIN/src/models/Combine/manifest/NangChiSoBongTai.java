package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import models.Combine.CombineService;
import models.item.Item;
import models.player.Player;
import services.InventoryService;
import services.Service;
import utils.Util;

public class NangChiSoBongTai {

    private static final short EARRING_LV2_ID      = 921;   // Bông tai cấp 2
    private static final short SHARD_ID             = 934;   // Mảnh hồn porata
    private static final short BLUE_STONE_ID        = 935;   // Đá xanh lam
    private static final int   SHARD_REQUIRED       = 99;    // Số mảnh cần
    private static final int   BLUE_STONE_REQUIRED  = 1;     // Số đá xanh lam cần
    private static final int   GEM_COST             = 250;   // 250 ngọc
    private static final int   SUCCESS_RATE         = 50;    // 50%
    private static final int[] OPTIONS              = {
        77, 103, 50, 108, 94, 14, 80, 81, 175, 5
    };

    /** Hiển thị menu “Nâng chỉ số bông tai +2” */
    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 3) {
            Service.gI().sendDialogMessage(player,
                "Cần 1 bông tai cấp 2 (ID " + EARRING_LV2_ID + "), " +
                SHARD_REQUIRED + " mảnh hồn porata (ID " + SHARD_ID + ") và " +
                BLUE_STONE_REQUIRED + " đá xanh lam (ID " + BLUE_STONE_ID + ").");
            return;
        }

        Item earring = null, shard = null, blueStone = null;
        for (Item it : player.combine.itemsCombine) {
            if (it.template.id == EARRING_LV2_ID)      earring = it;
            else if (it.template.id == SHARD_ID)       shard    = it;
            else if (it.template.id == BLUE_STONE_ID)  blueStone= it;
        }
        if (earring == null || shard == null || blueStone == null) {
            Service.gI().sendDialogMessage(player,
                "Cần 1 bông tai cấp 2, 99 mảnh hồn porata và 1 đá xanh lam.");
            return;
        }

        int qtyShard = shard.quantity;
        int qtyBlue  = blueStone.quantity;
        int haveGem  = player.inventory.getGemAndRuby();

        // Build nội dung menu
        StringBuilder text = new StringBuilder()
            .append(ConstFont.BOLD_BLUE).append("Bông tai Porata [+2]\n\n")
            .append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: ").append(SUCCESS_RATE).append("%\n")
            .append(qtyShard >= SHARD_REQUIRED ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần ").append(SHARD_REQUIRED).append(" mảnh hồn porata\n")
            .append(qtyBlue  >= BLUE_STONE_REQUIRED ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần ").append(BLUE_STONE_REQUIRED).append(" đá xanh lam\n")
            .append(haveGem  >= GEM_COST ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần ").append(GEM_COST).append(" ngọc\n")
            .append(ConstFont.BOLD_GREEN).append("+1 Chỉ số ngẫu nhiên\n");

        // Nếu thiếu nguyên liệu
        if (haveGem < GEM_COST) {
            CombineService.gI().baHatMit.createOtherMenu(
                player, ConstNpc.IGNORE_MENU, text.toString(),
                "Còn thiếu\n" + (GEM_COST - haveGem) + " ngọc");
            return;
        }
        if (qtyBlue < BLUE_STONE_REQUIRED) {
            CombineService.gI().baHatMit.createOtherMenu(
                player, ConstNpc.IGNORE_MENU, text.toString(),
                "Còn thiếu\n" + BLUE_STONE_REQUIRED + " đá xanh lam");
            return;
        }
        if (qtyShard < SHARD_REQUIRED) {
            CombineService.gI().baHatMit.createOtherMenu(
                player, ConstNpc.IGNORE_MENU, text.toString(),
                "Còn thiếu\n" + (SHARD_REQUIRED - qtyShard) + " mảnh hồn porata");
            return;
        }

        // Đủ điều kiện → show nút "Nâng cấp"
        CombineService.gI().baHatMit.createOtherMenu(
            player, ConstNpc.MENU_START_COMBINE, text.toString(),
            "Nâng cấp\n" + GEM_COST + " ngọc",
            "Từ chối"
        );
    }

    /** Xử lý khi người chơi bấm “Nâng cấp” */
    public static void nangChiSoBongTai(Player player) {
        if (player.combine.itemsCombine.size() != 3) return;

        Item earring = null, shard = null, blueStone = null;
        for (Item it : player.combine.itemsCombine) {
            if (it.template.id == EARRING_LV2_ID)      earring = it;
            else if (it.template.id == SHARD_ID)       shard    = it;
            else if (it.template.id == BLUE_STONE_ID)  blueStone= it;
        }
        if (earring == null || shard == null || blueStone == null) return;

        int qtyShard = shard.quantity;
        int qtyBlue  = blueStone.quantity;
        int haveGem  = player.inventory.getGemAndRuby();

        if (qtyShard < SHARD_REQUIRED
         || qtyBlue  < BLUE_STONE_REQUIRED
         || haveGem  < GEM_COST) {
            return;
        }

        // Trừ ngọc trước
        player.inventory.subGemAndRuby(GEM_COST);

        // Roll tỉ lệ
        if (Util.isTrue(SUCCESS_RATE, 100)) {
            // Thành công: gán chỉ số mới
            earring.itemOptions.clear();
            int opt = OPTIONS[Util.nextInt(OPTIONS.length)];
            int param = (opt == 94 || opt == 14)
                        ? Util.nextInt(3, 10)
                        : Util.nextInt(5, 15);
            earring.itemOptions.add(new Item.ItemOption(opt, param));
            // Thêm chỉ số mặc định (nếu cần)
            earring.itemOptions.add(new Item.ItemOption(72, 2));
            earring.itemOptions.add(new Item.ItemOption(38, 0));
            CombineService.gI().sendEffectSuccessCombine(player);
        } else {
            CombineService.gI().sendEffectFailCombine(player);
        }

        // Trừ mảnh và đá xanh lam
        InventoryService.gI().subQuantityItemsBag(player, shard, SHARD_REQUIRED);
        InventoryService.gI().subQuantityItemsBag(player, blueStone, BLUE_STONE_REQUIRED);

        // Cập nhật UI/balo/ngân lượng
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}
