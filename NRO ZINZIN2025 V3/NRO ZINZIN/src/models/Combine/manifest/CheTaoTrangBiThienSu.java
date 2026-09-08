package models.Combine.manifest;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import consts.ConstFont;
import consts.ConstNpc;
import models.item.Item;
import models.Combine.CombineService;
import models.player.Player;
import server.ServerNotify;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

public class CheTaoTrangBiThienSu {

    // Số lượng mảnh Thiên Sứ cần thiết để chế tạo
    private static final int SO_MANH_CAN = 999;
    // Phí vàng để nâng cấp (200 triệu)
    private static final long PHI_NANG_CAP = 200_000_000L;
    // Tỉ lệ cơ bản và tỉ lệ VIP
    private static final int TI_LE_CO_BAN = 25;
    private static final int TI_LE_VIP = 35;

    /**
     * Hiển thị thông tin chế tạo cho người chơi
     */
    public static void showInfoCombine(Player player) {
        // Kiểm tra còn ô trống trong hành trang
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendServerMessage(player, "Hành trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        // Lọc các item hợp lệ trong khung combine
        List<Item> items = player.combine.itemsCombine.stream()
            .filter(Item::isNotNullItem)
            .collect(Collectors.toList());

        // Đếm số lượng loại nguyên liệu
        long soCongThuc = dem(items, i -> i.isCongThuc() || i.isCongThucVip());
        long soManh = dem(items, Item::isManhThienSu);
        long soDaNangCap = dem(items, Item::isDaNangCapTS);
        long soDaMayMan = dem(items, Item::isDaMayMan);

        // Kiểm tra điều kiện số lượng nguyên liệu
        String thongBao = kiemTraSoLuong(soCongThuc, soManh, soDaNangCap, soDaMayMan);
        if (thongBao != null) {
            Service.gI().sendDialogMessage(player, thongBao);
            return;
        }

        // Lấy từng item cần thiết
        Item congThuc = lay(items, i -> i.isCongThuc() || i.isCongThucVip());
        Item manh = lay(items, Item::isManhThienSu);
        Item daNangCap = lay(items, Item::isDaNangCapTS);
        Item daMayMan = lay(items, Item::isDaMayMan);

        // Tính tỉ lệ thành công
        int tiLeCoBan = congThuc.isCongThucVip() ? TI_LE_VIP : TI_LE_CO_BAN;
        int bonusNangCap = tinhBonus(daNangCap, 1073);
        int bonusMayMan = tinhBonus(daMayMan, 1078);
        int tiLeThanhCong = tiLeCoBan + bonusNangCap;

        // Tạo item kết quả tạm theo giới tính công thức
        int recipeGender = genderOfRecipe(congThuc);
        Item ketQua = ItemService.gI().getAngelItem(recipeGender, manh.typeManh());
        // Xây dựng nội dung thông tin hiển thị
        String info = buildInfo(ketQua, manh, daNangCap, daMayMan, tiLeCoBan, bonusNangCap, bonusMayMan, tiLeThanhCong, player);

        // Xác định có đủ điều kiện để bấm Đồng ý
        boolean coTheChay = (manh.quantity >= SO_MANH_CAN) && (player.inventory.gold >= PHI_NANG_CAP);
        int menuId = coTheChay ? ConstNpc.MENU_START_COMBINE : ConstNpc.IGNORE_MENU;
        String[] luaChon = coTheChay ? new String[]{"Đồng ý", "Từ chối"} : new String[]{"Từ chối"};

        CombineService.gI().whis.createOtherMenu(player, menuId, info, luaChon);
    }

    /**
     * Thực hiện chế tạo trang bị Thiên Sứ
     */
    public static void cheTaoTrangBiThienSu(Player player) {
        // Kiểm tra ô trống
        if (InventoryService.gI().getCountEmptyBag(player) == 0) return;

        List<Item> items = player.combine.itemsCombine.stream()
            .filter(Item::isNotNullItem)
            .collect(Collectors.toList());
        // Chỉ chạy nếu đủ nguyên liệu
        if (!sanSan(items)) return;

        Item congThuc = lay(items, i -> i.isCongThuc() || i.isCongThucVip());
        Item manh = lay(items, Item::isManhThienSu);
        Item daNangCap = lay(items, Item::isDaNangCapTS);
        Item daMayMan = lay(items, Item::isDaMayMan);

        int tiLeCoBan = congThuc.isCongThucVip() ? TI_LE_VIP : TI_LE_CO_BAN;
        int bonusNangCap = tinhBonus(daNangCap, 1073);
        int tiLeThanhCong = tiLeCoBan + bonusNangCap;

        if (manh.quantity >= SO_MANH_CAN && player.inventory.gold >= PHI_NANG_CAP) {
            // Kiểm tra có thành công
            if (Util.isTrue(tiLeThanhCong, 100)) {
                xuLyThanhCong(player, congThuc, manh, daNangCap, daMayMan);
            } else {
                xuLyThatBai(player, manh);
            }
            // Trừ tài nguyên sau khi chạy
            truTaiNguyen(player, congThuc, manh, daNangCap, daMayMan);
        }
    }

    /* ======== Phương thức hỗ trợ ======== */

    /**
     * Xây dựng nội dung hiển thị thông tin chế tạo
     */
    private static String buildInfo(Item result, Item frag, Item daNangCap, Item daMayMan,
            int baseRate, int bonusUp, int bonusLuck, int totalRate, Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append(ConstFont.BOLD_GREEN)
          .append("Chế tạo ")
          .append(result.template.name).append(" ")
          .append(result.getGenderName())
          .append("\nMạnh hơn trang bị Hủy Diệt từ 20% đến 35%\n");
        sb.append(frag.quantity >= SO_MANH_CAN ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
          .append("Mảnh ghép ").append(frag.quantity).append("/" + SO_MANH_CAN + " (Thất bại -99 mảnh ghép)\n");
        sb.append(ConstFont.BOLD_BLUE)
          .append(daNangCap != null ? daNangCap.template.name : "Không dùng đá nâng cấp")
          .append(" (thêm ").append(bonusUp).append("% tỉ lệ thành công)\n");
        sb.append(ConstFont.BOLD_BLUE)
          .append(daMayMan != null ? daMayMan.template.name : "Không dùng đá may mắn")
          .append(" (thêm ").append(bonusLuck).append("% tỉ lệ tối đa các chỉ số)\n");
        sb.append(ConstFont.BOLD_BLUE)
          .append("Tỉ lệ thành công: ").append(totalRate).append("%\n");
        sb.append(player.inventory.gold >= PHI_NANG_CAP ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
          .append("Phí nâng cấp: ").append(PHI_NANG_CAP / 1_000_000).append(" triệu vàng");
        return sb.toString();
    }

    // Đếm số item thỏa predicate
    private static long dem(List<Item> items, Predicate<Item> p) {
        return items.stream().filter(p).count();
    }

    // Lấy item đầu tiên thỏa predicate
    private static Item lay(List<Item> items, Predicate<Item> p) {
        return items.stream().filter(p).findFirst().orElse(null);
    }

    // Tính bonus từ đá (nâng cấp hoặc may mắn)
    private static int tinhBonus(Item da, int baseId) {
        return da != null ? (da.template.id - baseId) * 10 : 0;
    }

    // Kiểm tra số lượng nguyên liệu hợp lệ
    private static String kiemTraSoLuong(long r, long m, long u, long l) {
        if (r == 0) return "Cần 1 công thức";
        if (r > 1) return "Chỉ cần 1 công thức";
        if (m == 0) return "Cần 1 loại mảnh";
        if (m > 1) return "Chỉ cần 1 loại mảnh";
        if (u > 1) return "Chỉ cần 1 đá nâng cấp";
        if (l > 1) return "Chỉ cần 1 đá may mắn";
        return null;
    }

    // Kiểm tra điều kiện sẵn sàng
    private static boolean sanSan(List<Item> items) {
        return dem(items, i -> i.isCongThuc() || i.isCongThucVip()) == 1
            && dem(items, Item::isManhThienSu) == 1
            && dem(items, Item::isDaNangCapTS) <= 1
            && dem(items, Item::isDaMayMan) <= 1;
    }

    // Xử lý khi chế tạo thành công
    private static void xuLyThanhCong(Player player, Item congThuc, Item manh, Item daNangCap, Item daMayMan) {
        int bonus = tinhBonus(daMayMan, 1078) > 0
            ? Util.nextInt(0, 15 - Util.nextInt(6))
            : Util.nextInt(0, 35 - Util.nextInt(11));
        int dongChiSo = Util.isTrue(tinhBonus(daMayMan, 1078), 100)
            ? Util.nextInt(0, 3 - Util.nextInt(4))
            : Util.nextInt(0, 1 - Util.nextInt(2));
        // Tạo item kết quả theo giới tính công thức
        int recipeGender = genderOfRecipe(congThuc);
        Item ketQua = ItemService.gI().getAngelItem(recipeGender, manh.typeManh());
        // Tăng chỉ số cơ bản
        ketQua.itemOptions.stream()
            .filter(Item.ItemOption::isOptionCanUpgrade)
            .forEach(opt -> opt.param += opt.param * bonus / 100);
        // Thêm các dòng chỉ số thưởng
        if (Util.isTrue(95, 100) && dongChiSo > 0) {
            ketQua.itemOptions.add(new Item.ItemOption(41, dongChiSo));
            int[] ops = {50,77,103,197,198,199,200,201,202,203,204};
            Util.shuffleArray(ops);
            for (int i = 0; i < dongChiSo; i++) {
                ketQua.itemOptions.add(new Item.ItemOption(ops[i], Util.nextInt(1, 5)));
            }
            ServerNotify.gI().notify(
                "Whis: " + player.name + " đã chế tạo thành công " + ketQua.template.name + " với "
                + dongChiSo + " dòng chỉ số thưởng, mọi người đều kinh ngạc.");
        } else {
            ServerNotify.gI().notify(
                "Whis: " + player.name + " đã chế tạo thành công " + ketQua.template.name + ", mọi người đều trầm trồ.");
        }
        // Trừ mảnh
        InventoryService.gI().subQuantityItemsBag(player, manh, SO_MANH_CAN);
        CombineService.gI().sendEffectCombineItem(player, (byte)7, (short)ketQua.template.iconID, (short)-1);
        InventoryService.gI().addItemBag(player, ketQua);
    }

    // Xử lý khi chế tạo thất bại
    private static void xuLyThatBai(Player player, Item manh) {
        InventoryService.gI().subQuantityItemsBag(player, manh, 99);
        CombineService.gI().sendEffectCombineItem(player, (byte)8, (short)-1, (short)-1);
    }

    // Trừ tài nguyên sau chế tạo (thành công hoặc thất bại)
    private static void truTaiNguyen(Player player, Item congThuc, Item manh, Item daNangCap, Item daMayMan) {
        InventoryService.gI().subQuantityItemsBag(player, congThuc, 1);
        if (daNangCap != null) InventoryService.gI().subQuantityItemsBag(player, daNangCap, 1);
        if (daMayMan != null) InventoryService.gI().subQuantityItemsBag(player, daMayMan, 1);
        player.inventory.gold -= PHI_NANG_CAP;
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }
    
    private static int genderOfRecipe(Item congThuc) {
    int id = congThuc.template.id;
    if (congThuc.isCongThucVip()) {
        return id - 1084;    // 1084→0, 1085→1, 1086→2
    } else {
        return id - 1071;    // 1071→0, 1072→1, 1073→2
    }
}
}
