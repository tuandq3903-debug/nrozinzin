package models.npc.manifest;

import consts.ConstNpc;
import models.item.Item;
import models.npc.Npc;
import models.player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;

public class TiemBanhTrungThu extends Npc {

    // Menu nhận thưởng (tránh đụng ID hệ thống)
    private static final int MENU_NHAN_BANH = 972001;
    private static final int MENU_NHAN_HOP  = 972002;

    public TiemBanhTrungThu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Xin chào " + player.name + "\n"
                            + "Tôi là nhân viên làm bánh và hộp quà\n"
                            + "Tôi có thể giúp gì cho bạn ?",
                    "Làm Sự Kiện", "Từ chối");
        }
    }

    private boolean hasEmptySlot(Player p) {
        try {
            return p != null && p.inventory != null
                    && InventoryService.gI().getCountEmptyBag(p) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Null-safe khi đang chạy trong thread chờ
    private boolean isInventoryReady(Player p) {
        return p != null && !p.beforeDispose
                && p.inventory != null && p.inventory.itemsBag != null;
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;
        if (this.mapId != 180) return;

        // ---- Base menu
        if (player.iDMark.isBaseMenu()) {
            if (select == 0) {
                createOtherMenu(player, 1,
                        "Hãy tìm đủ nguyên liệu và chọn loại muốn làm",
                        "Làm Bánh\nTrung thu", "Chế Tạo\nHộp Quà", "Từ chối");
            }
            return;
        }

        // ---- Chọn loại chế tạo
        if (player.iDMark.getIndexMenu() == 1) {
            switch (select) {
                case 0: { // Làm bánh 1701
                    Item comNep   = InventoryService.gI().findItemBag(player, 1214);
                    Item botGao   = InventoryService.gI().findItemBag(player, 1547);
                    Item muoiTieu = InventoryService.gI().findItemBag(player, 1545);
                    Item thitTuoi = InventoryService.gI().findItemBag(player, 1549);
                    boolean ok = (comNep != null && comNep.quantity >= 99)
                            && (botGao != null && botGao.quantity >= 99)
                            && (muoiTieu != null && muoiTieu.quantity >= 99)
                            && (thitTuoi != null && thitTuoi.quantity >= 99)
                            && player.inventory.gold >= 1_000_000
                            && hasEmptySlot(player);
                    if (ok) {
                        createOtherMenu(player, ConstNpc.MENU_BANH_TRUNG_THU,
                                "Bạn muốn làm bánh trung thu?\n"
                                        + "Cơm nếp " + comNep.quantity + "/99\n"
                                        + "Bột gạo " + botGao.quantity + "/99\n"
                                        + "Muối tiêu " + muoiTieu.quantity + "/99\n"
                                        + "Thịt tươi " + thitTuoi.quantity + "/99\n"
                                        + "Giá vàng: 1.000.000",
                                "Đồng ý", "Từ chối");
                    } else {
                        String msg = "Bạn muốn làm bánh trung thu\n";
                        msg += (comNep == null ? "Cơm nếp 0/99\n" : "Cơm nếp " + comNep.quantity + "/99\n");
                        msg += (botGao == null ? "Bột gạo 0/99\n" : "Bột gạo " + botGao.quantity + "/99\n");
                        msg += (muoiTieu == null ? "Muối tiêu 0/99\n" : "Muối tiêu " + muoiTieu.quantity + "/99\n");
                        msg += (thitTuoi == null ? "Thịt tươi 0/99\n" : "Thịt tươi " + thitTuoi.quantity + "/99\n");
                        msg += (player.inventory.gold < 1_000_000 ? "Còn thiếu vàng\n" : "Giá vàng: 1.000.000\n");
                        if (!hasEmptySlot(player)) msg += "Hành trang cần ít nhất 1 ô trống\n";
                        createOtherMenu(player, ConstNpc.MENU_BANH_TRUNG_THU_2, msg, "Từ chối");
                    }
                    break;
                }
                case 1: { // Làm hộp 1720
                    Item giayMau = InventoryService.gI().findItemBag(player, 1032);
                    Item khungTre = InventoryService.gI().findItemBag(player, 1035);
                    Item duoiKhi  = InventoryService.gI().findItemBag(player, 1045);
                    boolean ok = (giayMau != null && giayMau.quantity >= 99)
                            && (khungTre != null && khungTre.quantity >= 99)
                            && (duoiKhi  != null && duoiKhi.quantity  >= 99)
                            && player.inventory.gold >= 5_000_000
                            && hasEmptySlot(player);
                    if (ok) {
                        createOtherMenu(player, ConstNpc.MENU_HOP_TRUNG_THU,
                                "Bạn muốn làm hộp quà trung thu?\n"
                                        + "Giấy màu " + giayMau.quantity + "/99\n"
                                        + "Khung tre " + khungTre.quantity + "/99\n"
                                        + "Đuôi khỉ " + duoiKhi.quantity + "/99\n"
                                        + "Giá vàng: 5.000.000",
                                "Đồng ý", "Từ chối");
                    } else {
                        String msg = "Bạn muốn làm hộp quà trung thu\n";
                        msg += (giayMau == null ? "Giấy màu 0/99\n" : "Giấy màu " + giayMau.quantity + "/99\n");
                        msg += (khungTre == null ? "Khung tre 0/99\n" : "Khung tre " + khungTre.quantity + "/99\n");
                        msg += (duoiKhi  == null ? "Đuôi khỉ 0/99\n" : "Đuôi khỉ "  + duoiKhi.quantity  + "/99\n");
                        msg += (player.inventory.gold < 5_000_000 ? "Còn thiếu vàng\n" : "Giá vàng: 5.000.000\n");
                        if (!hasEmptySlot(player)) msg += "Hành trang cần ít nhất 1 ô trống\n";
                        createOtherMenu(player, ConstNpc.MENU_HOP_TRUNG_THU_2, msg, "Từ chối");
                    }
                    break;
                }
            }
            return;
        }

        // ---- Đồng ý làm BÁNH (1701)
        if (player.iDMark.getIndexMenu() == ConstNpc.MENU_BANH_TRUNG_THU) {
            if (select == 0) {
                if (player.isCookingBanhTrungThu) {
                    this.npcChat(player, "Bạn đang làm bánh trung thu rồi mà!");
                    return;
                }
                if (player.isLamHopQua) {
                    this.npcChat(player, "Bạn đang làm hộp quà trung thu rồi mà!");
                    return;
                }
                final int vang = 1_000_000;
                player.isCookingBanhTrungThu = true;
                this.npcChat(player, "Bắt đầu làm bánh trung thu...\n|7|Vui lòng chờ trong giây lát!");

                new Thread(() -> {
                    try {
                        int timeWait = 60;
                        while (timeWait > 0) {
                            if (!isInventoryReady(player)) return;
                            try {
                                this.npcChat(player, "Đang làm bánh trung thu\n|7|Thời gian còn lại: " + timeWait + " giây.");
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {}
                            timeWait--;
                        }
                        if (!isInventoryReady(player)) return;

                        Item comNep   = InventoryService.gI().findItemBag(player, 1214);
                        Item botGao   = InventoryService.gI().findItemBag(player, 1547);
                        Item muoiTieu = InventoryService.gI().findItemBag(player, 1545);
                        Item thitTuoi = InventoryService.gI().findItemBag(player, 1549);

                        if (comNep == null || comNep.quantity < 99
                                || botGao == null || botGao.quantity < 99
                                || muoiTieu == null || muoiTieu.quantity < 99
                                || thitTuoi == null || thitTuoi.quantity < 99
                                || player.inventory.gold < vang) {
                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "|7|Nguyên liệu/vàng không đủ tại thời điểm chế tạo. Hủy làm bánh.", "Đóng");
                            return;
                        }

                        // Trừ vật liệu + vàng
                        InventoryService.gI().subQuantityItemsBag(player, comNep, 99);
                        InventoryService.gI().subQuantityItemsBag(player, botGao, 99);
                        InventoryService.gI().subQuantityItemsBag(player, muoiTieu, 99);
                        InventoryService.gI().subQuantityItemsBag(player, thitTuoi, 99);
                        player.inventory.gold -= vang;
                        Service.gI().sendMoney(player);
                        InventoryService.gI().sendItemBag(player);

                        // Tạo phần thưởng PENDING (chưa phát)
                        player.pendingBanhTrungThu = ItemService.gI().createNewItem((short) 1701);

                        // Mở menu Nhận
                        this.createOtherMenu(player, MENU_NHAN_BANH,
                                "|2|Đã làm xong bánh trung thu!\n|1|Nhấn “Nhận” để lấy vào hành trang.",
                                "Nhận", "Để sau");
                    } finally {
                        player.isCookingBanhTrungThu = false;
                    }
                }).start();
            }
            return;
        }

        // ---- Đồng ý làm HỘP (1720)
        if (player.iDMark.getIndexMenu() == ConstNpc.MENU_HOP_TRUNG_THU) {
            if (select == 0) {
                if (player.isLamHopQua) {
                    this.npcChat(player, "Bạn đang làm hộp quà trung thu rồi mà!");
                    return;
                }
                if (player.isCookingBanhTrungThu) {
                    this.npcChat(player, "Bạn đang làm bánh trung thu rồi mà!");
                    return;
                }
                final int vang = 5_000_000;
                player.isLamHopQua = true;
                this.npcChat(player, "Bắt đầu làm hộp quà trung thu...\n|7|Vui lòng chờ trong giây lát!");

                new Thread(() -> {
                    try {
                        int timeWait = 60;
                        while (timeWait > 0) {
                            if (!isInventoryReady(player)) return;
                            try {
                                this.npcChat(player, "Đang làm hộp quà trung thu\n|7|Thời gian còn lại: " + timeWait + " giây.");
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {}
                            timeWait--;
                        }
                        if (!isInventoryReady(player)) return;

                        Item giayMau = InventoryService.gI().findItemBag(player, 1032);
                        Item khungTre = InventoryService.gI().findItemBag(player, 1035);
                        Item duoiKhi  = InventoryService.gI().findItemBag(player, 1045);

                        if (giayMau == null || giayMau.quantity < 99
                                || khungTre == null || khungTre.quantity < 99
                                || duoiKhi  == null || duoiKhi.quantity  < 99
                                || player.inventory.gold < vang) {
                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "|7|Nguyên liệu/vàng không đủ tại thời điểm chế tạo. Hủy làm hộp quà.", "Đóng");
                            return;
                        }

                        // Trừ vật liệu + vàng
                        InventoryService.gI().subQuantityItemsBag(player, giayMau, 99);
                        InventoryService.gI().subQuantityItemsBag(player, khungTre, 99);
                        InventoryService.gI().subQuantityItemsBag(player, duoiKhi, 99);
                        player.inventory.gold -= vang;
                        Service.gI().sendMoney(player);
                        InventoryService.gI().sendItemBag(player);

                        // Tạo phần thưởng PENDING (chưa phát)
                        player.pendingHopTrungThu = ItemService.gI().createNewItem((short) 1720);

                        // Mở menu Nhận
                        this.createOtherMenu(player, MENU_NHAN_HOP,
                                "|2|Đã làm xong hộp quà trung thu!\n|1|Nhấn “Nhận” để lấy vào hành trang.",
                                "Nhận", "Để sau");
                    } finally {
                        player.isLamHopQua = false;
                    }
                }).start();
            }
            return;
        }

        // ---- Menu “thiếu nguyên liệu” (chỉ hiển thị)
        if (player.iDMark.getIndexMenu() == ConstNpc.MENU_BANH_TRUNG_THU_2
                || player.iDMark.getIndexMenu() == ConstNpc.MENU_HOP_TRUNG_THU_2) {
            return;
        }

        // ---- Nhận BÁNH (từ pending)
        if (player.iDMark.getIndexMenu() == MENU_NHAN_BANH) {
            if (select == 0) {
                if (player.pendingBanhTrungThu == null) {
                    Service.gI().sendThongBao(player, "|7|Không có phần thưởng nào để nhận!");
                    return;
                }
                if (!hasEmptySlot(player)) {
                    Service.gI().sendThongBao(player, "|7|Hành trang cần ít nhất 1 ô trống!");
                    return;
                }
                InventoryService.gI().addItemBag(player, player.pendingBanhTrungThu);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "|1|Đã nhận: " + player.pendingBanhTrungThu.template.name);
                player.pendingBanhTrungThu = null;
            }
            return;
        }

        // ---- Nhận HỘP (từ pending)
        if (player.iDMark.getIndexMenu() == MENU_NHAN_HOP) {
            if (select == 0) {
                if (player.pendingHopTrungThu == null) {
                    Service.gI().sendThongBao(player, "|7|Không có phần thưởng nào để nhận!");
                    return;
                }
                if (!hasEmptySlot(player)) {
                    Service.gI().sendThongBao(player, "|7|Hành trang cần ít nhất 1 ô trống!");
                    return;
                }
                InventoryService.gI().addItemBag(player, player.pendingHopTrungThu);
                InventoryService.gI().sendItemBag(player);
                Service.gI().sendThongBao(player, "|1|Đã nhận: " + player.pendingHopTrungThu.template.name);
                player.pendingHopTrungThu = null;
            }
        }
    }
}
