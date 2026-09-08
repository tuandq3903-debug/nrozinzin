package models.npc.manifest;

import consts.ConstNpc;
import models.event.EventManager;
import models.event.event_manifest.*;
import models.item.Item;
import models.item.Item.ItemOption;
import network.Message;
import models.npc.Npc;
import models.player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NPC Hùng Vương tích hợp dâng lễ dưới submenu sự kiện
 */
public class HungVuong extends Npc {

    public HungVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) return;
        List<String> options = new ArrayList<>();
        // mục Hùng Vương
        if (EventManager.HUNG_VUONG) {
            options.add("Sự kiện\nHùng Vương");
        } else {
            options.add("Sự kiện\nHùng Vương\n|7|[TẮT]");
        } 
        // admin xem tất cả event
        if (player.isAdmin()) {
            options.add("Quản lý\nsự kiện");
        }
        createOtherMenu(player, ConstNpc.BASE_MENU,
            "Ngươi tìm ta có việc gì ?",
            options.toArray(new String[0]));
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || this.mapId != 47) return;

        // 1) Player chọn Sự kiện Hùng Vương
        if (player.iDMark.isBaseMenu() && select == 0) {
            if (!EventManager.HUNG_VUONG) {
                Service.gI().sendThongBao(player, "Đợi sự kiện chưa diễn ra");
            } else {
                // submenu 4 lựa chọn dâng lễ
                createOtherMenu(player, ConstNpc.MENU_HV_PLAYER,
                    "|2|Chọn hình thức dâng lễ:",
                    "Dâng\nsính lễ",
                    "Dâng\nsính lễ\nxịn",
                    "Dâng\nBánh dầy",
                    "Dâng\nBánh chưng\nLang Liêu");
            }
            return;
        }

        // 2) Xử lý submenu dâng lễ
        if (player.iDMark.getIndexMenu() == ConstNpc.MENU_HV_PLAYER) {
            switch (select) {
                case 0: doDangSinhLe(player, true);  break;
                case 1: doDangSinhLe(player, true);  break;
                case 2: doBanhDay(player);     break;
                case 3: doBanhChung(player);   break;
            }
            return;
        }

        // 3) Admin quản lý toàn bộ event
        if (player.isAdmin() && player.iDMark.isBaseMenu() && select == 1) {
            String[] names = {"Tết Nguyên Đán","Quốc tế Phụ nữ 8/3","Halloween",
                              "Giáng Sinh","Hùng Vương","Trung Thu","Nạp KNB"};
            createOtherMenu(player, ConstNpc.MENU_HV_EVENT,
                "|2|Chọn event để xem/bật/tắt:", names);
            return;
        }

        // 4) Admin xử lý xem/bật/tắt event
        if (player.iDMark.getIndexMenu() == ConstNpc.MENU_HV_EVENT) {
            int idx = select;
            // toggle flag
            switch (idx) {
                case 0: EventManager.LUNNAR_NEW_YEAR          = !EventManager.LUNNAR_NEW_YEAR; break;
                case 1: EventManager.INTERNATIONAL_WOMANS_DAY = !EventManager.INTERNATIONAL_WOMANS_DAY; break;
                case 2: EventManager.HALLOWEEN                = !EventManager.HALLOWEEN; break;
                case 3: EventManager.CHRISTMAS                = !EventManager.CHRISTMAS; break;
                case 4: EventManager.HUNG_VUONG               = !EventManager.HUNG_VUONG; break;
                case 5: EventManager.TRUNG_THU                = !EventManager.TRUNG_THU; break;
                case 6: EventManager.TOP_UP                   = !EventManager.TOP_UP; break;
            }
            EventManager.gI().init();
            String[] names = {"Tết Nguyên Đán","Quốc tế Phụ nữ 8/3","Halloween",
                              "Giáng Sinh","Hùng Vương","Trung Thu","Nạp KNB"};
            boolean state = new boolean[]{
                EventManager.LUNNAR_NEW_YEAR,
                EventManager.INTERNATIONAL_WOMANS_DAY,
                EventManager.HALLOWEEN,
                EventManager.CHRISTMAS,
                EventManager.HUNG_VUONG,
                EventManager.TRUNG_THU,
                EventManager.TOP_UP
            }[idx];
            Service.gI().sendThongBao(player,
                "Event " + names[idx] + " đã " + (state ? "BẬT" : "TẮT"));
            openBaseMenu(player);
            return;
        }
    }

    private void doDangSinhLe(Player player, boolean vip) {
        int needed = 9;
        Item iv = InventoryService.gI().findItemBag(player, 1220);
        Item ig = InventoryService.gI().findItemBag(player, 1221);
        Item im = InventoryService.gI().findItemBag(player, 1222);
        int cost = vip ? 10 : 1000000;
        boolean has = (iv != null && iv.quantity >= needed)
                   && (ig != null && ig.quantity >= needed)
                   && (im != null && im.quantity >= needed)
                   && (vip
                   ? player.inventory.gem >= cost   
                   : player.inventory.gold >= cost);
        if (has) {
            createOtherMenu(player, vip ? ConstNpc.MENU_SINHLE_XIN : ConstNpc.MENU_SINHLE,
                "|2|Con muốn dâng sính lễ ?\n"
                + "|1|Ngà voi " + iv.quantity + "/9\n"
                + "Cựa gà " + ig.quantity + "/9\n"
                + "Hồng mao " + im.quantity + "/9\n"
                + "Giá " + (vip ? "ngọc: " : "vàng: ") + cost,
                "Đồng ý", "Từ chối");
        } else {
            String msg = "|2|Con muốn dâng sính lễ\n";
            msg += (iv == null ? "|7|Ngà voi 0/9\n" : "|1|Ngà voi " + iv.quantity + "/9\n");
            msg += (ig == null ? "|7|Cựa gà 0/9\n" : "|1|Cựa gà " + ig.quantity + "/9\n");
            msg += (im == null ? "|7|Hồng mao 0/9\n" : "|1|Hồng mao " + im.quantity + "/9\n");
            msg += (vip
                 ? (player.inventory.gem < cost ? "|7|Còn thiếu ngọc" : "|1|Giá ngọc: " + cost + "\n")
                 : (player.inventory.gold < cost ? "|7|Còn thiếu vàng" : "|1|Giá vàng: " + cost + "\n"));
            createOtherMenu(player, vip ? ConstNpc.MENU_SINHLE_XIN_2 : ConstNpc.MENU_SINHLE_2,
                msg, "Từ chối");
        }
    }

    private void doBanhDay(Player player) {
        Item bd = InventoryService.gI().findItemBag(player, 1542);
        if (bd != null && bd.quantity >= 1) {
            createOtherMenu(player, ConstNpc.MENU_SINHLE_BANH_DAY,
                "|2|Con muốn dâng sính lễ ?\n" + "|1|Bánh dầy " + bd.quantity + "/1\n",
                "Đồng ý", "Từ chối");
        } else {
            String msg = "|2|Con muốn dâng sính lễ\n"
                       + (bd == null ? "|7|Bánh dầy 0/1\n" : "|1|Bánh dầy " + bd.quantity + "/1\n");
            createOtherMenu(player, ConstNpc.MENU_SINHLE_BANH_DAY_2, msg, "Từ chối");
        }
    }

    private void doBanhChung(Player player) {
        Item bc = InventoryService.gI().findItemBag(player, 1556);
        if (bc != null && bc.quantity >= 1) {
            createOtherMenu(player, ConstNpc.MENU_SINHLE_BANH_CHUNG,
                "|2|Con muốn dâng sính lễ ?\n"
                + "|1|Bánh chưng Lang Liêu " + bc.quantity + "/1\n",
                "Đồng ý", "Từ chối");
        } else {
            String msg = "|2|Con muốn dâng sính lễ\n"
                       + (bc == null ? "|7|Bánh chưng Lang Liêu 0/1\n"
                                    : "|1|Bánh chưng Lang Liêu " + bc.quantity + "/1\n");
            createOtherMenu(player, ConstNpc.MENU_SINHLE_BANH_CHUNG_2, msg, "Từ chối");
        }
    }

    private void handleSinhLeConfirm(Player player, int select) {
        if (select != 0) return;
        Item iv = InventoryService.gI().findItemBag(player, 1220);
        Item ig = InventoryService.gI().findItemBag(player, 1221);
        Item im = InventoryService.gI().findItemBag(player, 1222);
        Item reward = ItemService.gI().createNewItem((short)1823);
        int vang = 1000000;
        try {
            Message m1 = new Message(-81);
            m1.writer().writeByte(0);
            m1.writer().writeUTF("test"); m1.writer().writeUTF("test");
            m1.writer().writeShort(tempId); player.sendMessage(m1); m1.cleanup();
            Message m2 = new Message(-81);
            m2.writer().writeByte(1); m2.writer().writeByte(2);
            m2.writer().writeByte(InventoryService.gI().getIndexBag(player, iv));
            m2.writer().writeByte(InventoryService.gI().getIndexBag(player, ig));
            m2.writer().writeByte(InventoryService.gI().getIndexBag(player, im));
            player.sendMessage(m2); m2.cleanup();
            Message m3 = new Message(-81);
            m3.writer().writeByte(7);
            m3.writer().writeShort(reward.template.iconID);
            m3.writer().writeShort(-1); m3.writer().writeShort(-1); m3.writer().writeShort(-1);
            player.sendMessage(m3); m3.cleanup();
        } catch (Exception ignored) {}
        InventoryService.gI().addItemList(player.inventory.itemsBag, reward);
        InventoryService.gI().subQuantityItemsBag(player, iv, 9);
        InventoryService.gI().subQuantityItemsBag(player, ig, 9);
        InventoryService.gI().subQuantityItemsBag(player, im, 9);
        player.inventory.gold -= vang;
        Service.gI().sendThongBao(player, "Dâng sính lễ thành công");
        InventoryService.gI().sendItemBag(player);
    }

    private void handleSinhLeXinConfirm(Player player, int select) {
        if (select != 0) return;
        Item iv = InventoryService.gI().findItemBag(player, 1220);
        Item ig = InventoryService.gI().findItemBag(player, 1221);
        Item im = InventoryService.gI().findItemBag(player, 1222);
        Item reward = ItemService.gI().createNewItem((short)1824);
        int gem = 10;
        try {
            Message m1 = new Message(-81);
            m1.writer().writeByte(0);
            m1.writer().writeUTF("test"); m1.writer().writeUTF("test");
            m1.writer().writeShort(tempId); player.sendMessage(m1); m1.cleanup();
            Message m2 = new Message(-81);
            m2.writer().writeByte(1); m2.writer().writeByte(2);
            m2.writer().writeByte(InventoryService.gI().getIndexBag(player, iv));
            m2.writer().writeByte(InventoryService.gI().getIndexBag(player, ig));
            m2.writer().writeByte(InventoryService.gI().getIndexBag(player, im));
            player.sendMessage(m2); m2.cleanup();
            Message m3 = new Message(-81);
            m3.writer().writeByte(7);
            m3.writer().writeShort(reward.template.iconID);
            m3.writer().writeShort(-1); m3.writer().writeShort(-1); m3.writer().writeShort(-1);
            player.sendMessage(m3); m3.cleanup();
        } catch (Exception ignored) {}
        InventoryService.gI().addItemList(player.inventory.itemsBag, reward);
        InventoryService.gI().subQuantityItemsBag(player, iv, 9);
        InventoryService.gI().subQuantityItemsBag(player, ig, 9);
        InventoryService.gI().subQuantityItemsBag(player, im, 9);
        player.inventory.gem -= gem;
        Service.gI().sendThongBao(player, "Dâng sính lễ thành công");
        InventoryService.gI().sendItemBag(player);
    }

    private void handleBanhDayConfirm(Player player, int select) {
        if (select != 0) return;
        Item bd = InventoryService.gI().findItemBag(player, 1542);
        Item reward = ItemService.gI().createNewItem((short)1767);
        Item random = ItemService.gI().randomRac();
        reward.itemOptions.add(new ItemOption(85,0));
        reward.itemOptions.add(new ItemOption(50,7));
        reward.itemOptions.add(new ItemOption(77,5));
        reward.itemOptions.add(new ItemOption(77,5));
        if (Util.isTrue(95, 100)) reward.itemOptions.add(new ItemOption(1,7));
        reward.itemOptions.add(new ItemOption(174,2025));
        InventoryService.gI().subQuantityItemsBag(player, bd, 1);
        if (Util.isTrue(1, 100)) InventoryService.gI().addItemBag(player, reward);
        else InventoryService.gI().addItemBag(player, random);
        player.point_hungvuong++;
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        Service.gI().sendThongBao(player, "Dâng sính lễ thành công");
    }

    private void handleBanhChungConfirm(Player player, int select) {
        if (select != 0) return;
        Item bc = InventoryService.gI().findItemBag(player, 1556);
        Item reward = ItemService.gI().createNewItem((short)1795);
        Item random = ItemService.gI().randomRac();
        reward.itemOptions.add(new ItemOption(50,17));
        reward.itemOptions.add(new ItemOption(77,15));
        reward.itemOptions.add(new ItemOption(77,15));
        reward.itemOptions.add(new ItemOption(14,11));
        if (Util.isTrue(95,100)) reward.itemOptions.add(new ItemOption(1,7));
        reward.itemOptions.add(new ItemOption(174,2025));
        InventoryService.gI().subQuantityItemsBag(player, bc, 1);
        if (Util.isTrue(1,100)) InventoryService.gI().addItemBag(player, reward);
        else InventoryService.gI().addItemBag(player, random);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        Service.gI().sendThongBao(player, "Dâng sính lễ thành công");
    }
}
