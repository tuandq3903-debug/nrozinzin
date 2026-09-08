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

public class NhapNgocRong {

    /**
     * Hiển thị dialog và menu “Làm phép” (combine 1 lần) hoặc “Nhập nhanh” (batch combine)
     * cho Ngọc Rồng.
     */
    public static void showInfoCombine(Player player) {
        // 1. Kiểm tra còn ô trống
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendDialogMessage(player, "Hành trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        // 2. Chỉ được phép combine khi chỉ có đúng 1 loại item trong itemsCombine
        if (player.combine.itemsCombine.size() != 1) {
            Service.gI().sendDialogMessage(player, "Cần 7 viên Ngọc Rồng");
            return;
        }

        Item item = player.combine.itemsCombine.get(0);
        if (item == null || !item.isNotNullItem() ||
            item.template.id < 14 || item.template.id > 20 ||
            item.quantity < 7) {
            Service.gI().sendDialogMessage(player, "Cần 7 viên Ngọc Rồng");
            return;
        }

        // Tính số lần tối đa có thể combine: mỗi lần dùng 7 viên
        int maxCombine = item.quantity / 7;

        // ----------------------------
        // Trường hợp id từ 15–20 (Ngọc Rồng 2 sao trở lên)
        //    → chắc chắn thành công, không rủi ro, không dùng đá bảo vệ
        // ----------------------------
        if (item.template.id > 14 && item.template.id <= 20) {
            // Nếu chỉ combine được 1 lần thì hiển thị menu “Làm phép”
            if (maxCombine <= 1) {
                StringBuilder text = new StringBuilder();
                text.append(ConstFont.BOLD_BLUE).append("Con có muốn biến 7 ")
                    .append(item.template.name).append(" thành\n");
                text.append("1 viên ")
                    .append(ItemService.gI().getTemplate((short) (item.template.id - 1)).name)
                    .append("\n");
                text.append(ConstFont.BOLD_GREEN).append("Cần 7 ").append(item.template.name);

                CombineService.gI().baHatMit.createOtherMenu(
                    player,
                    ConstNpc.MENU_START_COMBINE,
                    text.toString(),
                    "Làm phép", "Từ chối"
                );
            }
            // Nếu combine được >1 lần thì hiển thị 2 nút: “Nhập nhanh” & “Từ chối”
            else {
                StringBuilder text = new StringBuilder();
                text.append(ConstFont.BOLD_BLUE)
                    .append("Con có muốn nhập nhanh ")
                    .append(maxCombine * 7)
                    .append(" ")
                    .append(item.template.name)
                    .append(" (tương đương ")
                    .append(maxCombine)
                    .append(" lần) thành\n");
                text.append("1 viên ")
                    .append(ItemService.gI().getTemplate((short) (item.template.id - 1)).name)
                    .append(" x ")
                    .append(maxCombine)
                    .append("\n");
                text.append(ConstFont.BOLD_GREEN)
                    .append("Cần ")
                    .append(maxCombine * 7)
                    .append(" ")
                    .append(item.template.name);

                CombineService.gI().baHatMit.createOtherMenu(
                    player,
                    ConstNpc.MENU_QUICK_COMBINE,
                    text.toString(),
                    "Nhập nhanh", "Từ chối"
                );
            }
        }
        // ----------------------------
        // Trường hợp id == 14 (Ngọc Rồng 1 sao) → 50% rủi ro, có thể dùng đá bảo vệ
        // ----------------------------
        else { // item.template.id == 14
            Item loNuocPhep = InventoryService.gI().findItemBag(player, (short) 1029);

            // Nếu chỉ combine được 1 lần thì hiển thị menu “Làm phép” (với tuỳ chọn “dùng đá bảo vệ”)
            if (maxCombine <= 1) {
                StringBuilder text = new StringBuilder();
                text.append(ConstFont.BOLD_BLUE).append("Con có muốn biến 7 Ngọc Rồng 1 sao thành\n");
                text.append("1 viên Ngọc Rồng Siêu Cấp\n");
                text.append(ConstFont.BOLD_GREEN).append("Cần 7 Ngọc Rồng 1 sao\n");
                text.append(player.inventory.gold >= 150_000_000 ? ConstFont.BOLD_GREEN : ConstFont.BOLD_RED)
                    .append("Cần 150.000.000 vàng\n");
                text.append(loNuocPhep == null ? ConstFont.BOLD_RED : ConstFont.BOLD_GREEN)
                    .append("Cần 1 Lọ nước phép\n");
                text.append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: 50%\n");
                text.append(ConstFont.BOLD_RED)
                    .append("Nếu dùng đá bảo vệ sẽ không bị mất 1 viên Ngọc Rồng 1 sao khi thất bại.");

                if (loNuocPhep == null) {
                    CombineService.gI().baHatMit.createOtherMenu(
                        player,
                        ConstNpc.IGNORE_MENU,
                        text.toString(),
                        "Còn thiếu\nLọ nước phép"
                    );
                    return;
                }
                if (player.inventory.gold < 150_000_000) {
                    CombineService.gI().baHatMit.createOtherMenu(
                        player,
                        ConstNpc.IGNORE_MENU,
                        text.toString(),
                        "Còn thiếu\n" + Util.numberToMoney(150_000_000 - player.inventory.gold) + " vàng"
                    );
                    return;
                }
                CombineService.gI().baHatMit.createOtherMenu(
                    player,
                    ConstNpc.MENU_START_COMBINE,
                    text.toString(),
                    "Làm phép", "Nâng cấp\ndùng đá\nbảo vệ", "Từ chối"
                );
            }
            // Nếu combine được >1 lần thì hiển thị 2 nút: “Nhập nhanh” & “Từ chối”
            else {
                StringBuilder text = new StringBuilder();
                text.append(ConstFont.BOLD_BLUE)
                    .append("Con có muốn nhập nhanh ")
                    .append(maxCombine * 7)
                    .append(" Ngọc Rồng 1 sao (")
                    .append(maxCombine)
                    .append(" lần) thành\n");
                text.append("Ngọc Rồng Siêu Cấp x ")
                    .append(maxCombine)
                    .append("\n");
                text.append(ConstFont.BOLD_GREEN)
                    .append("Cần ")
                    .append(maxCombine * 7)
                    .append(" Ngọc Rồng 1 sao\n");

                // Tổng vàng cần = 150.000.000 * maxCombine
                if (player.inventory.gold >= 150_000_000L * maxCombine) {
                    text.append(ConstFont.BOLD_GREEN);
                } else {
                    text.append(ConstFont.BOLD_RED);
                }
                text.append("Cần ")
                    .append(Util.numberToMoney(150_000_000L * maxCombine))
                    .append(" vàng\n");

                // Lọ nước phép cần = maxCombine
                int countLo = loNuocPhep == null ? 0 : loNuocPhep.quantity;
                if (countLo >= maxCombine) {
                    text.append(ConstFont.BOLD_GREEN);
                } else {
                    text.append(ConstFont.BOLD_RED);
                }
                text.append("Cần ")
                    .append(maxCombine)
                    .append(" Lọ nước phép\n");

                text.append(ConstFont.BOLD_BLUE)
                    .append("Tỉ lệ thành công mỗi lần: 50%\n");
                text.append(ConstFont.BOLD_RED)
                    .append("Nhập nhanh sẽ không dùng đá bảo vệ.\n")
                    .append("Nếu thất bại sẽ mất 1 viên Ngọc Rồng 1 sao.");

                CombineService.gI().baHatMit.createOtherMenu(
                    player,
                    ConstNpc.MENU_QUICK_COMBINE,
                    text.toString(),
                    "Nhập nhanh", "Từ chối"
                );
            }
        }
    }

    /**
     * Overload dùng cho “Nhập nhanh”: chỉ cần truyền useDBV, server tự động lấy combineCount = item.quantity/7
     */
    public static void nhapNgocRong(Player player, boolean useDBV) {
        // Kiểm tra điều kiện cơ bản
        if (InventoryService.gI().getCountEmptyBag(player) == 0
                || player.combine.itemsCombine.size() != 1) {
            return;
        }
        Item item = player.combine.itemsCombine.get(0);
        if (item == null || !item.isNotNullItem()
            || item.template.id < 14 || item.template.id > 20) {
            return;
        }
        int maxCombine = item.quantity / 7;
        if (maxCombine <= 0) {
            return;
        }
        // Gọi vào hàm chính với combineCount = maxCombine
        nhapNgocRong(player, useDBV, maxCombine);
    }

    /**
     * Hàm chính: Combine đúng combineCount lần—
     * mỗi lần trừ 7 item/lần, tạo item thành phẩm/lần.
     */
    public static void nhapNgocRong(Player player, boolean useDBV, int combineCount) {
        // Kiểm tra điều kiện cơ bản
        if (InventoryService.gI().getCountEmptyBag(player) == 0
                || player.combine.itemsCombine.size() != 1) {
            return;
        }
        Item item = player.combine.itemsCombine.get(0);
        if (item == null || !item.isNotNullItem()
            || item.template.id < 14 || item.template.id > 20) {
            return;
        }
        // Giới hạn combineCount ≤ item.quantity/7
        int maxPossible = item.quantity / 7;
        if (combineCount > maxPossible) {
            combineCount = maxPossible;
        }

        // ----------------------------
        // Trường hợp id từ 15–20 (Ngọc Rồng 2 sao trở lên)
        //    → chắc chắn thành công, không dùng đá bảo vệ
        // ----------------------------
        if (item.template.id > 14 && item.template.id <= 20 && !useDBV) {
            short newTemplateId = (short) (item.template.id - 1);
            for (int i = 0; i < combineCount; i++) {
                // Nếu hết ô trống, dừng luôn
                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                    Service.gI().sendServerMessage(player, "Hành trang đã đầy, cần một ô trống để nhận vật phẩm");
                    break;
                }
                Item nr = ItemService.gI().createNewItem(newTemplateId);
                CombineService.gI().sendEffectCombineDB(player, nr.template.iconID);
                InventoryService.gI().addItemBag(player, nr);
                InventoryService.gI().subQuantityItemsBag(player, item, 7);
            }
            // Cập nhật inventory và mở lại dialog combine (nếu vẫn còn item)
            InventoryService.gI().sendItemBag(player);
            CombineService.gI().reOpenItemCombine(player);
        }
        // ----------------------------
        // Trường hợp id == 14 (Ngọc Rồng 1 sao) → 50% rủi ro, có thể dùng đá bảo vệ
        // ----------------------------
        else if (item.template.id == 14) {
            for (int i = 0; i < combineCount; i++) {
                Item loNuocPhep = InventoryService.gI().findItemBag(player, (short) 1029);
                // Kiểm tra vàng và lọ nước phép
                if (player.inventory.gold < 150_000_000 || loNuocPhep == null) {
                    Service.gI().sendServerMessage(player, "Thiếu Lọ nước phép hoặc vàng để tiếp tục combine");
                    break;
                }
                // Kiểm tra đá bảo vệ nếu useDBV == true
                Item daBaoVe = InventoryService.gI().findItemBag(player, (short) 987);
                Item daBaoVeKhoa = InventoryService.gI().findItemBag(player, (short) 1143);
                if (useDBV && daBaoVe == null && daBaoVeKhoa == null) {
                    Service.gI().sendServerMessage(player, "Cần 1 Đá bảo vệ");
                    break;
                }

                boolean success = Util.isTrue(50, 100);
                int nrSub; // số Ngọc Rồng 1 sao bị trừ
                if (success) {
                    nrSub = 7;
                    Item nr = ItemService.gI().createNewItem((short) 1015); // id 1015 = Ngọc Rồng Siêu Cấp
                    // Thiết lập options (có thể tuỳ chỉnh nếu cần)
                    nr.itemOptions.add(new Item.ItemOption(30, 0));
                    nr.itemOptions.add(new Item.ItemOption(87, 0));
                    CombineService.gI().sendEffectCombineDB(player, nr.template.iconID);
                    InventoryService.gI().addItemBag(player, nr);
                } else {
                    nrSub = useDBV ? 0 : 1;
                    CombineService.gI().sendEffectFailCombine(player);
                }

                // Nếu dùng đá bảo vệ, trừ 1 viên đá
                if (useDBV) {
                    InventoryService.gI().subQuantityItemsBag(player,
                        (daBaoVe == null ? daBaoVeKhoa : daBaoVe), 1);
                }
                // Trừ vàng 150M, trừ ngọc (nrSub) và trừ 1 lọ nước phép
                player.inventory.gold -= 150_000_000;
                InventoryService.gI().subQuantityItemsBag(player, item, nrSub);
                InventoryService.gI().subQuantityItemsBag(player, loNuocPhep, 1);

                // Cập nhật đồ & tiền mỗi vòng
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendMoney(player);
            }
            // Mở lại tab combine sau khi batch xong (hoặc break)
            CombineService.gI().reOpenItemCombine(player);
        }
    }
}
