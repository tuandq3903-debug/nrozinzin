package services.func;

import java.util.ArrayList;
import java.util.List;
import jdbc.daos.PlayerDAO;
import models.item.Item;
import models.item.Item.ItemOption;
import models.player.Player;
import services.InventoryService;
import services.ItemService;
import services.Service;
import utils.Util;

public class QuaToriBot {

    public static void Qua_1(Player player, boolean receivedPet) {
        purchaseVip(player, 1, 20000, 6);
    }

    public static void Qua_2(Player player, boolean receivedPet) {
        purchaseVip(player, 2, 100000, 6);
    }

    public static void Qua_3(Player player, boolean receivedPet) {
        purchaseVip(player, 3, 300000, 11);
    }

    public static void Qua_4(Player player, boolean receivedPet) {
        purchaseVip(player, 4, 400000, 6);
    }

    private static void purchaseVip(Player player, int level, int cost, int requiredEmptySlots) {
        int currentVip = player.getSession().vip;
        // Kiểm tra thứ tự cấp VIP
        if (currentVip < level - 1) {
            Service.gI().sendThongBao(player,
                "Bạn phải mua VIP " + (level - 1) + " trước khi lên VIP " + level + ".");
            return;
        }
        if (currentVip >= level) {
            Service.gI().sendThongBao(player,
                "Bạn đã đạt VIP cấp " + level + " hoặc cao hơn.");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) < requiredEmptySlots) {
            Service.gI().sendThongBao(player,
                "Bạn phải có ít nhất " + requiredEmptySlots + " ô trống hành trang");
            return;
        }
        if (player.getSession().cash < cost) {
            Service.gI().sendThongBao(player,
                "Bạn chưa đủ " + cost + " cash để nhận VIP " + level + "!");
            return;
        }
        try {
            int time = 5;
            Service.gI().sendThongBao(player,
                "Tiến Hành Nhận\nVIP " + level + " \nSau " + time + " Giây!");
            while (time > 0) {
                time--;
                Thread.sleep(1000);
                Service.gI().sendThongBao(player, "|7|" + time);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        PlayerDAO.subVIP(player);
        PlayerDAO.subcash(player, cost);
        player.getSession().vip = level;
        Service.gI().sendMoney(player);
        grantRewards(player, level);
    }

    private static void grantRewards(Player player, int level) {
        List<Item> rewards = new ArrayList<>();
        switch (level) {
            case 1:
                // VIP 1
                Item i1 = ItemService.gI().createNewItem((short) 987, 5);
                i1.itemOptions.add(new ItemOption(30, 0));
                rewards.add(i1);
                Item i2 = ItemService.gI().createNewItem((short) 1785, 1);
                i2.itemOptions.add(new ItemOption(50, 25));
                i2.itemOptions.add(new ItemOption(101, 70));
                i2.itemOptions.add(new ItemOption(95, 15));
                i2.itemOptions.add(new ItemOption(30, 0));
                i2.itemOptions.add(new ItemOption(93, 15));
                rewards.add(i2);
                Item i3 = ItemService.gI().createNewItem((short) 1796, 3);
                i3.itemOptions.add(new ItemOption(30, 0));
                rewards.add(i3);
                Item i4 = ItemService.gI().createNewItem((short) 1578, 1);
                i4.itemOptions.add(new ItemOption(77, 8));
                i4.itemOptions.add(new ItemOption(103, 8));
                i4.itemOptions.add(new ItemOption(84, 20));
                i4.itemOptions.add(new ItemOption(30, 0));
                i4.itemOptions.add(new ItemOption(93, 15));
                rewards.add(i4);
                break;

            case 2:
                // VIP 2
                Item j1 = ItemService.gI().createNewItem((short) 987, 10);
                j1.itemOptions.add(new ItemOption(30, 0));
                rewards.add(j1);
                Item j2 = ItemService.gI().createNewItem((short) 1578, 1);
                j2.itemOptions.add(new ItemOption(50, 15));
                j2.itemOptions.add(new ItemOption(77, 10));
                j2.itemOptions.add(new ItemOption(103, 10));
                j2.itemOptions.add(new ItemOption(93, 30));
                j2.itemOptions.add(new ItemOption(30, 0));
                rewards.add(j2);
                Item j3 = ItemService.gI().createNewItem((short) 1785, 1);
                j3.itemOptions.add(new ItemOption(50, 30));
                j3.itemOptions.add(new ItemOption(77, 28));
                j3.itemOptions.add(new ItemOption(103, 28));
                j3.itemOptions.add(new ItemOption(93, 30));
                j3.itemOptions.add(new ItemOption(30, 0));
                rewards.add(j3);
                Item j4 = ItemService.gI().createNewItem((short) 1107, 1);
                j4.itemOptions.add(new ItemOption(50, 15));
                j4.itemOptions.add(new ItemOption(77, 13));
                j4.itemOptions.add(new ItemOption(103, 13));
                j4.itemOptions.add(new ItemOption(5, 10));
                j4.itemOptions.add(new ItemOption(93, 30));
                j4.itemOptions.add(new ItemOption(30, 0));
                rewards.add(j4);
                Item j5 = ItemService.gI().createNewItem((short) 956, 10);
                j5.itemOptions.add(new ItemOption(30, 0));
                rewards.add(j5);
                Item j6 = ItemService.gI().createNewItem((short) 1796, 5);
                j6.itemOptions.add(new ItemOption(30, 0));
                rewards.add(j6);
                break;

            case 3:
                // VIP 3
                Item k1 = ItemService.gI().createNewItem((short) 987, 30);
                k1.itemOptions.add(new ItemOption(30, 1));
                rewards.add(k1);
                Item k2 = ItemService.gI().createNewItem((short) 1798, 1);
                k2.itemOptions.add(new ItemOption(50, 15));
                k2.itemOptions.add(new ItemOption(77, 10));
                k2.itemOptions.add(new ItemOption(103, 10));
                k2.itemOptions.add(new ItemOption(30, 1));
                rewards.add(k2);
                Item k3 = ItemService.gI().createNewItem((short) 1797, 1);
                k3.itemOptions.add(new ItemOption(50, 30));
                k3.itemOptions.add(new ItemOption(77, 28));
                k3.itemOptions.add(new ItemOption(103, 28));
                k3.itemOptions.add(new ItemOption(101, 50));
                k3.itemOptions.add(new ItemOption(106, 0));
                k3.itemOptions.add(new ItemOption(30, 1));
                rewards.add(k3);
                Item k4 = ItemService.gI().createNewItem((short) 1722, 1);
                k4.itemOptions.add(new ItemOption(50, 15));
                k4.itemOptions.add(new ItemOption(77, 13));
                k4.itemOptions.add(new ItemOption(103, 13));
                k4.itemOptions.add(new ItemOption(14, 5));
                k4.itemOptions.add(new ItemOption(5, 10));
                k4.itemOptions.add(new ItemOption(30, 1));
                rewards.add(k4);
                Item k5 = ItemService.gI().createNewItem((short) 1786, 1);
                k5.itemOptions.add(new ItemOption(50, 12));
                k5.itemOptions.add(new ItemOption(77, 16));
                k5.itemOptions.add(new ItemOption(103, 16));
                k5.itemOptions.add(new ItemOption(30, 1));
                rewards.add(k5);
                Item k6 = ItemService.gI().createNewItem((short) 956, 20);
                k6.itemOptions.add(new ItemOption(30, 1));
                rewards.add(k6);
                Item k7 = ItemService.gI().createNewItem((short) 1796, 10);
                k7.itemOptions.add(new ItemOption(30, 1));
                rewards.add(k7);
                break;

            case 4:
                // VIP 4
                Item l1 = ItemService.gI().createNewItem((short) 457, 30);
                l1.itemOptions.add(new ItemOption(100, 1));
                l1.itemOptions.add(new ItemOption(86, 0));
                rewards.add(l1);
                Item l2 = ItemService.gI().createNewItem((short) 956, 15);
                rewards.add(l2);
                int randomDa = new int[]{1079,1080,1081,1082,1083,220,221,222,223,224}[Util.nextInt(10)];
                Item l3 = ItemService.gI().createNewItem((short) randomDa, 15);
                rewards.add(l3);
                Item l4 = ItemService.gI().createNewItem((short) 987, 50);
                rewards.add(l4);
                Item l5 = ItemService.gI().createNewItem((short) 1187, 50);
                rewards.add(l5);
                Item l6;
                switch (player.gender) {
                    case 0: l6 = ItemService.gI().createNewItem((short)1227,2); break;
                    case 1: l6 = ItemService.gI().createNewItem((short)1228,2); break;
                    default: l6 = ItemService.gI().createNewItem((short)1229,2); break;
                }
                rewards.add(l6);
                Item l7 = ItemService.gI().createNewItem((short)1283,1);
                l7.itemOptions.add(new ItemOption(50,27));
                l7.itemOptions.add(new ItemOption(77,25));
                l7.itemOptions.add(new ItemOption(103,25));
                l7.itemOptions.add(new ItemOption(5,15));
                l7.itemOptions.add(new ItemOption(101,100));
                l7.itemOptions.add(new ItemOption(95,10));
                l7.itemOptions.add(new ItemOption(30,1));
                rewards.add(l7);
                Item l8 = ItemService.gI().createNewItem((short)1284,1);
                l8.itemOptions.add(new ItemOption(77,15));
                l8.itemOptions.add(new ItemOption(103,15));
                l8.itemOptions.add(new ItemOption(14,9));
                l8.itemOptions.add(new ItemOption(84,0));
                l8.itemOptions.add(new ItemOption(30,1));
                rewards.add(l8);
                Item l9 = ItemService.gI().createNewItem((short)1199,20);
                rewards.add(l9);
                Item l10 = ItemService.gI().createNewItem((short)1115,1);
                l10.itemOptions.add(new ItemOption(50,28));
                l10.itemOptions.add(new ItemOption(77,27));
                l10.itemOptions.add(new ItemOption(103,27));
                l10.itemOptions.add(new ItemOption(14,15));
                l10.itemOptions.add(new ItemOption(95,10));
                l10.itemOptions.add(new ItemOption(93,365));
                l10.itemOptions.add(new ItemOption(30,1));
                rewards.add(l10);
                Item l11 = ItemService.gI().createNewItem((short)1127,1);
                l11.itemOptions.add(new ItemOption(50,15));
                l11.itemOptions.add(new ItemOption(77,13));
                l11.itemOptions.add(new ItemOption(103,13));
                l11.itemOptions.add(new ItemOption(14,5));
                l11.itemOptions.add(new ItemOption(5,10));
                l11.itemOptions.add(new ItemOption(30,1));
                rewards.add(l11);
                break;

            default:
                return;
        }
        // Gửi và thêm tất cả phần thưởng
        for (Item reward : rewards) {
            Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + reward.template.name);
            InventoryService.gI().addItemBag(player, reward);
        }
        InventoryService.gI().sendItemBag(player);
    }
}
