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

public class Lamphepnhapda {

    private static final short FRAG_ID    = 225;      // Đá vụn
    private static final short KEY_ID     = 226;      // bình nước phép
    private static final int   FRAGUE     = 99;       // Số lượng đá vụn cần
    private static final int   GOLD_COST  = 1_000_000; // 1 Triệu vàng

    // 1) Hiển thị menu “Làm phép Nhập đá”
    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            Service.gI().sendDialogMessage(player,
                "Cần 99 đá vụn  và 1 bình nước phép");
            return;
        }
        Item frag = null, key = null;
        for (Item it : player.combine.itemsCombine) {
            if (it.template.id == FRAG_ID) frag = it;
            else if (it.template.id == KEY_ID) key = it;
        }
        if (frag == null || key == null) {
            Service.gI().sendDialogMessage(player,
                "Cần 99 đá vụn  và 1 bình nước phép ");
            return;
        }

        int qtyFrag = frag.quantity;
        int qtyKey  = key.quantity;

        StringBuilder text = new StringBuilder()
            .append(ConstFont.BOLD_BLUE)
            .append("Làm phép Nhập đá\n\n")
            .append(ConstFont.BOLD_BLUE)
            .append("Bạn cần:\n")
            .append(qtyFrag >= FRAGUE ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append(FRAGUE + " đá vụn (ID " + FRAG_ID + ")\n")
            .append(qtyKey  >= 1     ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("1 bình nước phép (ID " + KEY_ID + ")\n")
            .append(player.inventory.gold >= GOLD_COST ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("1 000 000 vàng\n");

        // Nếu thiếu vàng
        if (player.inventory.gold < GOLD_COST) {
            CombineService.gI().baHatMit.createOtherMenu(
                player,
                ConstNpc.IGNORE_MENU,
                text.toString(),
                "Còn thiếu\n" + Util.numberToMoney(GOLD_COST - player.inventory.gold) + " vàng"
            );
            return;
        }
        // Nếu thiếu đá vụn
        if (qtyFrag < FRAGUE) {
            CombineService.gI().baHatMit.createOtherMenu(
                player,
                ConstNpc.IGNORE_MENU,
                text.toString(),
                "Còn thiếu\n" + (FRAGUE - qtyFrag) + " đá vụn"
            );
            return;
        }
        // Nếu thiếu đá chủ
        if (qtyKey < 1) {
            CombineService.gI().baHatMit.createOtherMenu(
                player,
                ConstNpc.IGNORE_MENU,
                text.toString(),
                "Còn thiếu\n1 bình nước phép"
            );
            return;
        }

        // Đủ điều kiện → show nút “Nhập đá”
        CombineService.gI().baHatMit.createOtherMenu(
            player,
            ConstNpc.MENU_START_COMBINE,
            text.toString(),
            "Nhập đá\n",
            "Từ chối"
        );
    }

    // 2) Xử lý khi bấm “Nhập đá”
    public static void lamPhepNhapDa(Player player) {
        if (player.combine.itemsCombine.size() != 2) return;

        Item frag = null, key = null;
        for (Item it : player.combine.itemsCombine) {
            if (it.template.id == FRAG_ID) frag = it;
            else if (it.template.id == KEY_ID) key = it;
        }
        if (frag == null || key == null) return;

        int qtyFrag = frag.quantity;
        if (qtyFrag < FRAGUE || key.quantity < 1 || player.inventory.gold < GOLD_COST) {
            return;
        }

        // Trừ vàng và nguyên liệu
        player.inventory.gold -= GOLD_COST;
        InventoryService.gI().subQuantityItemsBag(player, frag, FRAGUE);
        InventoryService.gI().subQuantityItemsBag(player, key, 1);

        // Tạo ngẫu nhiên 1 item từ 220 đến 224
        short outId = (short) Util.nextInt(220, 224);
        Item result = ItemService.gI().createNewItem(outId);
        InventoryService.gI().addItemBag(player, result);

        // Hiệu ứng thành công
        CombineService.gI().sendEffectSuccessCombine(player);

        // Cập nhật lại UI
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }

}
