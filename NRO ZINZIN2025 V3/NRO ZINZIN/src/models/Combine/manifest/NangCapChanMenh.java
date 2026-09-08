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


public class NangCapChanMenh {
    private static final int DA_THIEN_TU_ID = 1848;
    private static final int REQUIRED_DA = 99;
    private static final double SUCCESS_RATE = 10;

    private static boolean isChanMenh(Item item) {
        return item.template.id >= 1839 && item.template.id <= 1847;
    }

    public static void showInfoCombine(Player player) {
    if (player.combine.itemsCombine.size() != 2) {
        // Nếu không đúng 2 item, hiển thị menu yêu cầu
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                "Cần 1 Chân Mệnh và 99 Đá Thiên Tử", "Đóng");
        return;
    }
    // Tìm Chân Mệnh và Đá Thiên Tử trong itemsCombine
    Item chanMenh = null, daThienTu = null;
    for (Item item : player.combine.itemsCombine) {
        if (isChanMenh(item)) chanMenh = item;
        else if (item.template.id == DA_THIEN_TU_ID) daThienTu = item;
    }
    // Kiểm tra thiếu Chân Mệnh
    if (chanMenh == null) {
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                "Thiếu Chân Mệnh", "Đóng");
        return;
    }
    // Kiểm tra đã lên cấp tối đa
    if (chanMenh.template.id >= 1847) {
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                "Chân Mệnh đã đạt cấp tối đa", "Đóng");
        return;
    }
    // Chuẩn bị hiển thị chi tiết: số Đá, vàng, tỉ lệ
    long vang = 500_000_000L;
    StringBuilder text = new StringBuilder("|0|Nâng cấp Chân Mệnh\n");
    text.append(chanMenh.template.name).append("\n");
    text.append(daThienTu != null && daThienTu.quantity >= REQUIRED_DA ? ConstFont.BOLD_GREEN : ConstFont.BOLD_RED)
        .append("Cần: 99 Đá Thiên Tử\n");
    text.append(player.inventory.gold >= vang ? ConstFont.BOLD_GREEN : ConstFont.BOLD_RED)
        .append("Cần: ").append(Util.numberFormat(vang)).append(" vàng\n");
    text.append(ConstFont.BOLD_BLUE)
        .append("Tỉ lệ thành công: ").append((int) SUCCESS_RATE).append("%");
    // Hiển thị menu “Nâng cấp”
    CombineService.gI().baHatMit.createOtherMenu(
        player,
        ConstNpc.MENU_START_COMBINE,
        text.toString(),
        "Nâng cấp"
    );
}


    public static void startCombine(Player player) {
    if (player.combine.itemsCombine.size() != 2) return;
    // Tương tự tìm Chân Mệnh và Đá
    Item chanMenh = null, daThienTu = null;
    for (Item item : player.combine.itemsCombine) {
        if (isChanMenh(item)) chanMenh = item;
        else if (item.template.id == DA_THIEN_TU_ID) daThienTu = item;
    }
    long vang = 500_000_000L;
    // Nếu thiếu nguyên liệu hoặc vàng, thoát
    if (chanMenh == null || daThienTu == null
        || daThienTu.quantity < REQUIRED_DA
        || player.inventory.gold < vang) return;
    if (chanMenh.template.id >= 1847) return;  // đã max cấp

    // Trừ nguyên liệu
    InventoryService.gI().subQuantityItemsBag(player, daThienTu, REQUIRED_DA);
    player.inventory.gold -= vang;

    // Xác suất thành công
    boolean isSuccess = Util.isTrue((int) SUCCESS_RATE, 100);
    if (isSuccess) {
        // Tạo Chân Mệnh mới: id tăng 1
        int nextId = chanMenh.template.id + 1;
        Item chanMenhMoi = ItemService.gI().createNewItem((short) nextId);
        // Giữ lại các Option quan trọng (50,77,103), tăng tham số ngẫu nhiên 1–3%
        for (Item.ItemOption oldOpt : chanMenh.itemOptions) {
            int optId = oldOpt.optionTemplate.id;
            if (optId == 50 || optId == 77 || optId == 103) {
                int percent = Util.nextInt(1, 3);
                int newParam = (int) Math.ceil(oldOpt.param * (1 + percent / 100.0));
                chanMenhMoi.itemOptions.add(new Item.ItemOption(optId, newParam));
            }
        }
        // Xóa Chân Mệnh cũ, thêm Chân Mệnh mới vào túi
        InventoryService.gI().subQuantityItemsBag(player, chanMenh, 1);
        InventoryService.gI().addItemBag(player, chanMenhMoi);
        CombineService.gI().sendEffectSuccessCombine(player);
    } else {
        // Hiệu ứng thất bại, giữ item nguyên
        CombineService.gI().sendEffectFailCombine(player);
    }
    // Cập nhật UI túi đồ và tiền vàng, mở lại menu combine
    InventoryService.gI().sendItemBag(player);
    Service.gI().sendMoney(player);
    CombineService.gI().reOpenItemCombine(player);
}
}

