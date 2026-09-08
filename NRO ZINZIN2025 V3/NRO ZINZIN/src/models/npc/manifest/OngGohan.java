package models.npc.manifest;

import consts.ConstNpc;
import consts.ConstTask;
import consts.ConstTaskBadges;
import jdbc.daos.PlayerDAO;
import models.item.Item;
import models.npc.Npc;
import models.player.Player;
import models.shop.ShopService;
import models.task.Badges.BadgesTaskService;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.PetService;
import services.Service;
import services.TaskService;
import utils.Util;

import java.util.ArrayList;
import java.util.List;
import services.func.Input;

public class OngGohan extends Npc {

    public OngGohan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    // ====== TỶ LỆ ĐỔI ======
    // VND -> Ngọc xanh
    private static final int[][] DOI_NGOC_XANH = {
            { 10_000, 1_000 },   // 10k -> 1.000 ngọc xanh
            { 50_000, 6_000 },   // 50k -> 6.000
            {100_000,12_500 },   // 100k -> 12.500
            {500_000,65_000 }    // 500k -> 65.000
    };

    // VND -> Ngọc hồng (ruby)
    private static final int[][] DOI_NGOC_HONG = {
            { 20_000,   500 },   // 20k -> 500 ngọc hồng
            {100_000, 3_000 },   // 100k -> 3.000
            {500_000,16_000 },   // 500k -> 16.000
            {1_000_000,35_000 }  // 1.000k -> 35.000
    };

    // VND -> Thỏi vàng (457)
    private static final int[][] DOI_VANG = {
            { 20_000,   40 },
            { 50_000,  105 },
            {100_000,  250 },
            {500_000, 1500 },
            {1_000_000,3100 },
            {2_000_000,6500 },
            {5_000_000,17000 }
    };

    // Kiểu đổi
    private static final int TYPE_VANG       = 0;
    private static final int TYPE_NGOC_XANH  = 1;
    private static final int TYPE_NGOC_HONG  = 2;

    // ID item Thỏi vàng
    private static final short ITEM_VANG_ID = 457;

    // ============= MENU =============
    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) return;
        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Con cần ta giúp gì nào",
                    
                    "Bán Đồ Thần Linh",
                    "Hỗ trợ\nNhiệm vụ",
                    "Hộp thư",
                    "Mở Thành Viên",
                    
                    "Đổi Mật Khẩu",
                    "Đóng");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;

        // ======== BASE MENU ========
        if (player.iDMark.isBaseMenu()) {
            switch (select) {
                
                case 0 -> openBanThanLinh(player);
                case 1 -> hoTroNhiemVu(player);
                case 2 -> openMailBox(player);
                case 3 -> moThanhVien(player);
                
                case 4 -> Input.gI().createFormChangePassword(player);
                default -> { /* Đóng */ }
            }
            return;
        }

        // ======== MAIL BOX ========
        if (player.iDMark.getIndexMenu() == ConstNpc.MAIL_BOX) {
            switch (select) {
                case 0 -> ShopService.gI().opendShop(player, "ITEMS_MAIL_BOX", true);
                case 1 -> NpcService.gI().createMenuConMeo(player,
                        ConstNpc.CONFIRM_REMOVE_ALL_ITEM_MAIL_BOX, this.avartar,
                        "|3|Bạn chắc muốn xóa hết vật phẩm trong hòm thư?\n|7|Sau khi xóa sẽ không thể khôi phục!",
                        "Đồng ý", "Hủy bỏ");
                default -> { /* Đóng */ }
            }
            return;
        }

        // ======== BÁN THẦN LINH ========
        if (player.iDMark.getIndexMenu() == ConstNpc.DOI_THANHLINH) {
            if (select == 0) banToanBoThanLinh(player);
            return;
        }
    }

    /** Mở menu bán Thần Linh */
    private void openBanThanLinh(Player p) {
        int countTL = InventoryService.gI().countThanLinhItems(p);
        String text = "|7|Bạn có " + countTL + " món Thần Linh trong hành trang.\n"
                + "Bạn muốn bán hết để nhận VND?";
        createOtherMenu(p, ConstNpc.DOI_THANHLINH, text, "Bán tất cả", "Đóng");
    }

    /** Bán toàn bộ đồ Thần Linh (555..567) -> nhận VND */
    private void banToanBoThanLinh(Player p) {
        List<Item> dsTL = new ArrayList<>();
        for (Item it : p.inventory.itemsBag) {
            if (it.isNotNullItem() && it.template.id >= 555 && it.template.id <= 567) {
                dsTL.add(it);
            }
        }
        if (dsTL.isEmpty()) {
            Service.gI().sendThongBao(p, "Bạn không có món Thần Linh nào để bán.");
            return;
        }

        long tongVND = 0;
        for (Item it : dsTL) {
            switch (it.template.id) {
                case 555, 557, 559 -> tongVND += 5_000L * it.quantity;  // Áo
                case 556, 558, 560 -> tongVND += 7_000L * it.quantity;  // Quần
                case 561            -> tongVND += 9_000L * it.quantity;  // Nhẫn
                case 562, 564, 566 -> tongVND += 11_000L * it.quantity; // Găng
                case 563, 565, 567 -> tongVND += 3_000L * it.quantity;  // Giày
            }
        }

        // Xóa item
        InventoryService.gI().removeThanLinhItems(p);

        // Cộng VND (subcash âm để cộng)
        PlayerDAO.subcash(p, (int) -tongVND);

        Service.gI().sendMoney(p);
        Service.gI().sendThongBao(p, "Bạn nhận được " + Util.mumberToLouis(tongVND) + " VND.");
    }

    /** Hỗ trợ nhiệm vụ cơ bản như trước */
    private void hoTroNhiemVu(Player p) {
        if (TaskService.gI().getIdTask(p) >= ConstTask.TASK_9_0 && TaskService.gI().getIdTask(p) < ConstTask.TASK_11_0) {
            p.playerTask.taskMain.id = 10;
            p.playerTask.taskMain.index = 0;
            TaskService.gI().sendNextTaskMain(p);
            Service.gI().sendThongBao(p, "Bạn đã được hỗ trợ nhiệm vụ thành công");
        } else if (TaskService.gI().getIdTask(p) >= ConstTask.TASK_18_0 && TaskService.gI().getIdTask(p) < ConstTask.TASK_20_0) {
            p.playerTask.taskMain.id = 19;
            p.playerTask.taskMain.index = 0;
            TaskService.gI().sendNextTaskMain(p);
            Service.gI().sendThongBao(p, "Bạn đã được hỗ trợ nhiệm vụ thành công");
        } else if (TaskService.gI().getIdTask(p) == ConstTask.TASK_16_0) {
            TaskService.gI().sendNextTaskMain(p);
            Service.gI().sendThongBao(p, "Bạn đã được hỗ trợ nhiệm vụ thành công");
        } else {
            Service.gI().sendThongBao(p, "Chỉ hỗ trợ nhiệm vụ tàu pảy pảy, thách đấu 10 người, DHVT, Trung úy trắng");
        }
    }

    /** Hộp thư */
    private void openMailBox(Player p) {
        createOtherMenu(p, ConstNpc.MAIL_BOX,
                "|0|Tình yêu như một dây đàn\n"
                        + "Tình vừa được thì đàn đứt dây\n"
                        + "Đứt dây này anh thay dây khác\n"
                        + "Mất em rồi anh biết thay ai?",
                "Hòm Thư\n(" + (p.inventory.itemsMailBox.size()
                        - InventoryService.gI().getCountEmptyListItem(p.inventory.itemsMailBox)) + " món)",
                "Xóa Hết\nHòm Thư",
                "Đóng");
    }

    /** Mở thành viên */
    private void moThanhVien(Player p) {
        if (p.getSession().actived) {
            npcChat(p, "Bạn đã mở thành viên rồi!");
            return;
        }
        int cost = 10_000;
        if (p.getSession().cash >= cost) {
            p.getSession().actived = true;
            if (PlayerDAO.subcashactive(p, cost)) {
                Service.gI().sendMoney(p);
                Service.gI().sendThongBao(p, "|7|Kích hoạt thành công");
            } else {
                Service.gI().sendThongBao(p, "Có lỗi khi kích hoạt.");
            }
        } else {
            npcChat(p, "Không đủ tiền mở Thành Viên!");
        }
    }
}