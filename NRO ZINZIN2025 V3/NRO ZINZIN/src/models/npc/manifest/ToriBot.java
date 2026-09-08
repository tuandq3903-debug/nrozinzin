package models.npc.manifest;

import consts.ConstNpc;
import consts.ConstTaskBadges;
import jdbc.daos.PlayerDAO;
import models.item.Item;
import models.npc.Npc;
import models.player.Player;
import models.task.Badges.BadgesTaskService;
import services.InventoryService;
import services.ItemService;
import services.PetService;
import services.Service;
import services.func.QuaToriBot;
import utils.Util;

public class ToriBot extends Npc {

    // Menu nội bộ cho Đổi VNĐ
    private static final int MENU_DOI_VND  = 100;
    private static final int MENU_VANG     = 101;
    private static final int MENU_NGOC_XANH= 102;
    private static final int MENU_NGOC_HONG= 103;
    private static final int MENU_DOI_DE   = 104;

    public ToriBot(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) return;

        createOtherMenu(player, ConstNpc.BASE_MENU,
                "Trong thời gian mùa 1 diễn ra\n"
              + "Nếu mua VIP sẽ được nhận nhiều ưu đãi hơn nữa.\n"
              + "Lưu ý: Nâng cấp VIP theo bậc 1 → 2 → 3.",
                "VIP 1", "VIP 2", "VIP 3", "Đổi VNĐ", "Đóng");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;

        // Chỉ hoạt động ở 3 làng
        if (this.mapId != 0 && this.mapId != 7 && this.mapId != 14) return;

        // --- BASE MENU ---
        if (player.iDMark.isBaseMenu()) {
            switch (select) {
                case 0 -> createOtherMenu(player, 2,
                        "Nâng cấp VIP 1 bạn sẽ được"
                      + "\n- x5 đá bảo vệ"
                      + "\n- Cải trang Karin Kid Lân 15 ngày"
                      + "\n- x3 hộp mù bé ba"
                      + "\n- Ván bay mây mưa 15 ngày.",
                        "20K", "Đóng");
                case 1 -> createOtherMenu(player, 4,
                        "Nâng cấp VIP 2 bạn sẽ được"
                      + "\n- x10 đá bảo vệ"
                      + "\n- Ván bay mây mưa 30 ngày"
                      + "\n- CT Karin múa Lân 30 ngày"
                      + "\n- Pet Bí Ma Vương 30 ngày"
                      + "\n- x10 mảnh đội trưởng vàng"
                      + "\n- x5 hộp mù bé ba",
                        "100K", "Đóng");
                case 2 -> createOtherMenu(player, 6,
                        "Nâng cấp VIP 3 bạn sẽ được"
                      + "\n- x30 đá bảo vệ"
                      + "\n- Ván bay máy bay 41 vv"
                      + "\n- CT Bunma Rider vv"
                      + "\n- Cánh thiên thần ác quỷ vv"
                      + "\n- Pet cá mập vv"
                      + "\n- x20 mảnh đội trưởng vàng"
                      + "\n- x10 hộp mù bé ba",
                        "300K", "Đóng");
                case 3 -> openDoiVND(player); // Mục chính: Thỏi vàng / Ngọc xanh / Ngọc hồng / Đệ tử
                default -> { /* Đóng */ }
            }
            return;
        }

        // --- VIP 1/2/3 nhận quà ---
        if (player.iDMark.getIndexMenu() == 2) { if (select == 0) QuaToriBot.Qua_1(player, true); return; }
        if (player.iDMark.getIndexMenu() == 4) { if (select == 0) QuaToriBot.Qua_2(player, true); return; }
        if (player.iDMark.getIndexMenu() == 6) { if (select == 0) QuaToriBot.Qua_3(player, true); return; }

        // --- Nhóm Đổi VNĐ: chọn mục chính ---
        if (player.iDMark.getIndexMenu() == MENU_DOI_VND) {
            switch (select) {
                case 0 -> openDoiVang(player);
                case 1 -> openDoiNgocXanh(player);
                case 2 -> openDoiNgocHong(player);
                case 3 -> openDoiDe(player);
                default -> { /* Đóng */ }
            }
            return;
        }

        // --- Xử lý từng submenu ---
        if (player.iDMark.getIndexMenu() == MENU_VANG) {
            doiTienFromList(player, TYPE_VANG, select);
            return;
        }
        if (player.iDMark.getIndexMenu() == MENU_NGOC_XANH) {
            doiTienFromList(player, TYPE_NGOC_XANH, select);
            return;
        }
        if (player.iDMark.getIndexMenu() == MENU_NGOC_HONG) {
            doiTienFromList(player, TYPE_NGOC_HONG, select);
            return;
        }
        if (player.iDMark.getIndexMenu() == MENU_DOI_DE) {
            doiDe(player, select);
        }
    }

    // ====== TỶ LỆ ĐỔI ======
    private static final int TYPE_VANG       = 0;
    private static final int TYPE_NGOC_XANH  = 1;
    private static final int TYPE_NGOC_HONG  = 2;

    // ID item Thỏi vàng
    private static final short ITEM_VANG_ID = 457;

    // VND -> Ngọc xanh
    private static final int[][] DOI_NGOC_XANH = {
            { 10_000,  1_000 },
            { 50_000,  6_000 },
            {100_000, 12_500 },
            {500_000, 65_000 }
    };

    // VND -> Ngọc hồng (ruby)
    private static final int[][] DOI_NGOC_HONG = {
            {   20_000,   500 },
            {  100_000,  3_000 },
            {  500_000, 16_000 },
            {1_000_000, 35_000 }
    };

    // VND -> Thỏi vàng (457)
    private static final int[][] DOI_VANG = {
            {   20_000,    40 },
            {   50_000,   105 },
            {  100_000,   250 },
            {  500_000,  1500 },
            {1_000_000,  3100 },
            {2_000_000,  6500 },
            {5_000_000, 17000 }
    };

    // VND -> Đổi đệ (0=Mabư, 1=Cell, 2=Fide)
    private static final int[][] DOI_DE = {
            {100_000, 0},
            {100_000, 1},
            {100_000, 2}
    };

    // ================== UI MENUS ==================

    /** Mục chính Đổi VNĐ */
    private void openDoiVND(Player p) {
        createOtherMenu(p, MENU_DOI_VND,
                "Chọn mục muốn đổi bằng VNĐ:",
                "Thỏi vàng", "Ngọc xanh", "Ngọc hồng", "Đệ tử", "Đóng");
    }

    /** Submenu: Vàng */
    private void openDoiVang(Player p) {
        java.util.List<String> rows = new java.util.ArrayList<>();
        for (int[] row : DOI_VANG) {
            rows.add(Util.mumberToLouis(row[0]) + " VND\n→ " + Util.mumberToLouis(row[1]) + " Thỏi vàng");
        }
        rows.add("Đóng");
        createOtherMenu(p, MENU_VANG, "Chọn gói đổi Thỏi vàng:", rows.toArray(new String[0]));
    }

    /** Submenu: Ngọc xanh */
    private void openDoiNgocXanh(Player p) {
        java.util.List<String> rows = new java.util.ArrayList<>();
        for (int[] row : DOI_NGOC_XANH) {
            rows.add(Util.mumberToLouis(row[0]) + " VND\n→ " + Util.mumberToLouis(row[1]) + " Ngọc xanh");
        }
        rows.add("Đóng");
        createOtherMenu(p, MENU_NGOC_XANH, "Chọn gói đổi Ngọc xanh:", rows.toArray(new String[0]));
    }

    /** Submenu: Ngọc hồng */
    private void openDoiNgocHong(Player p) {
        java.util.List<String> rows = new java.util.ArrayList<>();
        for (int[] row : DOI_NGOC_HONG) {
            rows.add(Util.mumberToLouis(row[0]) + " VND\n→ " + Util.mumberToLouis(row[1]) + " Ngọc hồng");
        }
        rows.add("Đóng");
        createOtherMenu(p, MENU_NGOC_HONG, "Chọn gói đổi Ngọc hồng:", rows.toArray(new String[0]));
    }

    /** Submenu: Đổi đệ */
    private void openDoiDe(Player p) {
        createOtherMenu(p, MENU_DOI_DE,
                "Chọn loại đệ muốn đổi (mỗi loại 100.000 VND):",
                "Đệ Mabư Nhí", "Đệ Cell Nhí", "Đệ Fide Nhí", "Đóng");
    }

    // ================== HANDLERS ==================

    /** Xử lý đổi Vàng/Ngọc theo danh sách submenu */
    private void doiTienFromList(Player p, int type, int select) {
        int[][] table;
        String labelGet;

        switch (type) {
            case TYPE_VANG      -> { table = DOI_VANG;      labelGet = "Thỏi vàng"; }
            case TYPE_NGOC_XANH -> { table = DOI_NGOC_XANH; labelGet = "Ngọc xanh"; }
            default             -> { table = DOI_NGOC_HONG; labelGet = "Ngọc hồng"; }
        }

        if (select < 0 || select >= table.length) return; // hoặc chọn "Đóng"

        int vnd = table[select][0];
        int amount = table[select][1];

        if (p.getSession().cash < vnd) {
            Service.gI().sendThongBao(p, "Không đủ số dư VND");
            return;
        }
        if (!PlayerDAO.subcash(p, vnd)) {
            Service.gI().sendThongBao(p, "Có lỗi khi trừ VND. Thử lại sau!");
            return;
        }

        if (type == TYPE_VANG) {
            Item goldBar = ItemService.gI().createNewItemLock(ITEM_VANG_ID, amount);
            InventoryService.gI().addItemBag(p, goldBar);
            InventoryService.gI().sendItemBag(p);
        } else if (type == TYPE_NGOC_XANH) {
            p.inventory.gem += amount;
        } else {
            p.inventory.ruby += amount;
        }

        BadgesTaskService.updateCountBagesTask(p, ConstTaskBadges.DAI_GIA_MOI_NHU, vnd);
        Service.gI().sendMoney(p);
        Service.gI().sendThongBao(p, "Bạn nhận được " + Util.mumberToLouis(amount) + " " + labelGet);
    }

    /** Xử lý đổi đệ */
    private void doiDe(Player p, int select) {
        if (select < 0 || select >= DOI_DE.length) return; // hoặc "Đóng"

        int vnd = DOI_DE[select][0];
        int petType = DOI_DE[select][1];

        if (p.pet == null) {
            Service.gI().sendThongBao(p, "Bạn chưa có đệ nào để đổi!");
            return;
        }
        if (p.getSession().cash < vnd) {
            Service.gI().sendThongBao(p, "Không đủ VND để đổi đệ");
            return;
        }
        if (!PlayerDAO.subcash(p, vnd)) {
            Service.gI().sendThongBao(p, "Có lỗi khi trừ VND. Thử lại sau!");
            return;
        }

        switch (petType) {
            case 0 -> PetService.gI().changeMabu1Pet(p);
            case 1 -> PetService.gI().changeCellPet(p);
            default -> PetService.gI().changeFidePet(p);
        }

        BadgesTaskService.updateCountBagesTask(p, ConstTaskBadges.DAI_GIA_MOI_NHU, vnd);
        Service.gI().sendMoney(p);
        Service.gI().sendThongBao(p, "Đổi đệ thành công!");
    }
}
