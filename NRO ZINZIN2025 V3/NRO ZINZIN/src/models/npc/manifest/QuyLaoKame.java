package models.npc.manifest;

/**
 *
 * @author ZINZIN
 */
import models.clan.Clan;
import consts.ConstDailyGift;
import consts.ConstNpc;
import models.item.Item;
import java.util.ArrayList;
import jdbc.daos.PlayerDAO;
import models.TreasureUnderSea.TreasureUnderSea;
import models.TreasureUnderSea.TreasureUnderSeaService;
import models.npc.Npc;
import static models.npc.NpcFactory.PLAYERID_OBJECT;
import models.player.Player;
import models.player.dailyGift.DailyGiftService;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.PlayerService;
import services.RewardService;
import services.Service;
import services.TaskService;
import services.func.ChangeMapService;
import services.func.Input;
import models.Top.TopService;
import models.shop.ShopService;
import models.skill.Skill;
import utils.Logger;
import utils.SkillUtil;
import utils.TimeUtil;
import utils.Util;

public class QuyLaoKame extends Npc {

    public QuyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }
    @Override
    public void openBaseMenu(Player player) {
        Item ruacon = InventoryService.gI().findItemBag(player, 874);
        if (canOpenNpc(player)) {
            String[] menu;
            if (!player.canReward) {
                if (DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_QUA_VUA_HUNG)) {
                    menu = new String[] {
                        "Nhận quà",
                        "Nói\nchuyện",
                        "Đổi\nđiểm nạp [" + player.inventory.coupon + "]",
                        "Nhận quà\nKOL"};
                } else if (ruacon != null && ruacon.quantity >= 1) {
                    menu = new String[] {
                        "Nói\nchuyện",
                        "Đổi\nđiểm nạp [" + player.inventory.coupon + "]",
                        "Nhận quà\nKOL",
                        "Giao\nRùa con"
                    };
                } else {
                    menu = new String[] {
                        "Nói\nchuyện",
                        "Đổi\nđiểm nạp [" + player.inventory.coupon + "]",
                        "Nhận quà\nKOL"
                    };
                }
            } else {
                menu = new String[] {
                    "Giao\nBé Na"
                };
           }
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Con muốn hỏi gì nào?", menu);
            }
        }
    }
    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.canReward) {
                RewardService.gI().rewardLancon(player);
                return;
            }
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.BASE_MENU -> {
                    if (!DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_QUA_VUA_HUNG)) {
                        select++;
                    }
                    switch (select) {

                        case 0 -> {
                            if (DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_QUA_VUA_HUNG)) {
                                Item giaoThuyTinh = ItemService.gI().createNewItem((short) 1807);
                                Item riuSonTinh = ItemService.gI().createNewItem((short) 1806);
                                Item hacMiNuong = ItemService.gI().createNewItem((short) 1557);
                                giaoThuyTinh.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 20)));
                                giaoThuyTinh.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 15)));
                                giaoThuyTinh.itemOptions.add(new Item.ItemOption(103, Util.nextInt(10, 15)));
                                giaoThuyTinh.itemOptions.add(new Item.ItemOption(93, 3));
                                riuSonTinh.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 15)));
                                riuSonTinh.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 20)));
                                riuSonTinh.itemOptions.add(new Item.ItemOption(103, Util.nextInt(10, 15)));
                                riuSonTinh.itemOptions.add(new Item.ItemOption(93, 3));
                                hacMiNuong.itemOptions.add(new Item.ItemOption(50, 25));
                                hacMiNuong.itemOptions.add(new Item.ItemOption(77, 24));
                                hacMiNuong.itemOptions.add(new Item.ItemOption(27, 15));
                                hacMiNuong.itemOptions.add(new Item.ItemOption(108, 5));
                                hacMiNuong.itemOptions.add(new Item.ItemOption(94, 15));
                                hacMiNuong.itemOptions.add(new Item.ItemOption(148, 50));
                                hacMiNuong.itemOptions.add(new Item.ItemOption(30, 0));
                                hacMiNuong.itemOptions.add(new Item.ItemOption(93, 3));
                                InventoryService.gI().addItemBag(player, hacMiNuong);
                                InventoryService.gI().addItemBag(player, riuSonTinh);
                                InventoryService.gI().addItemBag(player, giaoThuyTinh);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn vừa nhận thưởng " + giaoThuyTinh.template.name + ", " + riuSonTinh.template.name + ", " + hacMiNuong.template.name);
                                DailyGiftService.updateDailyGift(player, ConstDailyGift.NHAN_QUA_VUA_HUNG);
                            } else {
                                Service.gI().sendThongBao(player, "Hôm nay bạn đã nhận quà rồi!!!");
                            }
                        }
                        case 1 -> {
                            if (player.HocSkill.Time != -1 && player.HocSkill.Time <= System.currentTimeMillis()) {

                                player.HocSkill.Time = -1;
                                try {
                                    var curSkill = SkillUtil.createSkill(
                                            SkillUtil.getTempSkillSkillByItemID(player.HocSkill.ItemTemplateSkillId),
                                            SkillUtil.getSkillByItemID(player,
                                                    player.HocSkill.ItemTemplateSkillId).point);
                                    player.CheckHocSkill.add((int) player.HocSkill.ItemTemplateSkillId);
                                    SkillUtil.setSkill(player, curSkill);
                                    var msg = Service.gI().messageSubCommand((byte) 62);
                                    msg.writer().writeShort(curSkill.skillId);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    PlayerService.gI().sendInfoHpMpMoney(player);
                                } catch (Exception e) {
                                    Logger.log(e.toString());
                                }
                            }
                            Clan clan = player.clan;
                        String[] baseMenu = new String[] {
                            "Nhiệm vụ",
                            "Học\nKỹ năng",
                            "Kho báu\ndưới biển"};
                        if (clan != null) {
                            if (clan.isLeader(player)) {
                                baseMenu = new String[] {
                                    "Nhiệm vụ",
                                    "Học\nKỹ năng",
                                    "Về khu\nvực bang",
                                    "Giải tán\nBang hội",
                                    "Kho báu\ndưới biển"};
                            } else {
                                baseMenu = new String[] {
                                    "Nhiệm vụ",
                                    "Học\nKỹ năng",
                                    "Về khu\nvực bang",
                                    "Kho báu\ndưới biển"};
                                }
                            }
                            this.createOtherMenu(player, 0, "Chào con, ta rất vui khi gặp con\nCon muốn làm gì nào ?", baseMenu);
                        }
                        case 2 -> {
                            ShopService.gI().opendShop(player, "ITEMS_DIEM_NAP", true);
                        }
                        case 3 -> {
                            if (player.playerTask.kolTask.template != null) {
                                String npcSay = "Nhiệm vụ " + (player.playerTask.kolTask.template.id + 1) + ":"
                                        + "\n" + player.playerTask.kolTask.getTaskInfo()
                                        + "\n" + player.playerTask.kolTask.getRewardsInfo()
                                        + "\nHoàn thành: " + player.playerTask.kolTask.count + "/" + player.playerTask.kolTask.template.max_count + " (" + player.playerTask.kolTask.getPercentProcess() + "%)";
                                this.createOtherMenu(player, ConstNpc.RECEIVE_KOL_TASK, npcSay, player.playerTask.kolTask.isDone() ? "Trả\nnhiệm vụ" : "Đóng");
                            }
                        }
                    }
                }
                case 12 -> {
                    switch (select) {
                        case 1:
                            this.createOtherMenu(player, 13,
                                    "Con có muốn huỷ học kỹ năng này và nhận lại 50% số tiềm năng không ?",
                                    "Ok", "Đóng");
                            break;
                        case 0:
                            var time = player.HocSkill.Time - System.currentTimeMillis();
                            var ngoc = 5;
                            if (time / 600_000 >= 2) {
                                ngoc += time / 600_000;
                            }
                            if (player.inventory.gem < ngoc) {
                                Service.gI().sendThongBao(player, "Bạn không có đủ ngọc");
                                return;
                            }
                            player.inventory.subGem(ngoc);
                            player.HocSkill.Time = -1;
                            try {
                                String[] subName = ItemService.gI()
                                        .getTemplate(player.HocSkill.ItemTemplateSkillId).name.split("");
                                byte level = Byte.parseByte(subName[subName.length - 1]);
                                Skill curSkill = SkillUtil.getSkillByItemID(player,
                                        player.HocSkill.ItemTemplateSkillId);
                                if (curSkill.point == 0) {
                                    player.CheckHocSkill.add((int) player.HocSkill.ItemTemplateSkillId);
                                    curSkill = SkillUtil.createSkill(
                                            SkillUtil.getTempSkillSkillByItemID(player.HocSkill.ItemTemplateSkillId),
                                            level);
                                    SkillUtil.setSkill(player, curSkill);
                                    var msg = Service.gI().messageSubCommand((byte) 23);
                                    msg.writer().writeShort(curSkill.skillId);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                } else {
                                    curSkill = SkillUtil.createSkill(
                                            SkillUtil.getTempSkillSkillByItemID(player.HocSkill.ItemTemplateSkillId),
                                            level);

                                    player.CheckHocSkill.add((int) player.HocSkill.ItemTemplateSkillId);
//                                     // System.out.println(curSkill.template.name + " - " + curSkill.point);
                                    SkillUtil.setSkill(player, curSkill);
                                    var msg = Service.gI().messageSubCommand((byte) 62);
                                    msg.writer().writeShort(curSkill.skillId);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    PlayerService.gI().sendInfoHpMpMoney(player);
                                }
                            } catch (Exception e) {
                                Logger.log(e.toString());
                            }
                            break;

                    }
                }
                case 0 -> {
                    switch (select) {
                        case 0 ->
                            NpcService.gI().createTutorial(player, tempId, avartar,
                                    player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
                        case 1 -> {
                            if (player.HocSkill.Time != -1) {
                                var ngoc = 5;
                                var time = player.HocSkill.Time - System.currentTimeMillis();
                                if (time / 600_000 >= 2) {
                                    ngoc += time / 600_000;
                                }
                                String[] subName = ItemService.gI()
                                        .getTemplate(player.HocSkill.ItemTemplateSkillId).name.split("");
                                byte level = Byte.parseByte(subName[subName.length - 1]);
                                this.createOtherMenu(player, 12,
                                        "Con đang học kỹ năng\n"
                                        + SkillUtil.findSkillTemplate(SkillUtil.getTempSkillSkillByItemID(
                                                player.HocSkill.ItemTemplateSkillId)).name
                                        + " cấp " + level + "\nThời gian còn lại " + TimeUtil.getTime(time),
                                        "Học\nCấp tốc\n" + ngoc + " ngọc", "Huỷ", "Bỏ qua");
                            } else {
                                ShopService.gI().opendShop(player, "QUY_LAO", false);
                            }
                        }
                        case 2 -> {
                            Clan clan = player.clan;
                            if (clan != null && select == 2) {
                                ChangeMapService.gI().changeMapNonSpaceship(player, 153, Util.nextInt(100, 200), 432);
                            } else {
                                if (player.clan != null && player.clan.BanDoKhoBau != null) {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                            "Bang hội con đang ở hang kho báu cấp "
                                            + player.clan.BanDoKhoBau.level + "\ncon có muốn đi cùng họ không?",
                                            "Top\nBang hội", "Thành tích\nBang", "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                            "Đây là bản đồ kho báu hải tặc tí hon\nCác con cứ yên tâm lên đường\nỞ đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                            "Top\nBang hội", "Thành tích\nBang", "Chọn\ncấp độ", "Từ chối");
                                }
                            }
                        }
                        case 3 -> {
                            boolean clanCheck = true;
                            Clan clan = player.clan;
                            if (clan != null) {
                                clanCheck = false;
                                if (clan.isLeader(player)) {
                                    createOtherMenu(player, 3, "Con có chắc muốn giải tán bang hội không?", "Đồng ý",
                                            "Từ chối");
                                } else {
                                    clanCheck = true;
                                }
                            }
                            if (clanCheck) {
                                if (player.clan != null && player.clan.BanDoKhoBau != null) {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                            "Bang hội con đang ở hang kho báu cấp "
                                            + player.clan.BanDoKhoBau.level + "\ncon có muốn đi cùng họ không?",
                                            "Top\nBang hội", "Thành tích\nBang", "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                            "Đây là bản đồ kho báu hải tặc tí hon\nCác con cứ yên tâm lên đường\nỞ đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                            "Top\nBang hội", "Thành tích\nBang", "Chọn\ncấp độ", "Từ chối");
                                }
                            }
                        }
                        case 4 -> {
                            if (player.clan != null && player.clan.BanDoKhoBau != null) {
                                this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                        "Bang hội con đang ở hang kho báu cấp "
                                        + player.clan.BanDoKhoBau.level + "\ncon có muốn đi cùng họ không?",
                                        "Top\nBang hội", "Thành tích\nBang", "Đồng ý", "Từ chối");
                            } else {
                                this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                        "Đây là bản đồ kho báu hải tặc tí hon\nCác con cứ yên tâm lên đường\nỞ đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                        "Top\nBang hội", "Thành tích\nBang", "Chọn\ncấp độ", "Từ chối");
                            }
                        }
                    }
                }
                case 3 -> {
                    Clan clan = player.clan;
                    if (clan != null) {
                        if (clan.isLeader(player)) {
                            if (select == 0) {
                                Input.gI().createFormGiaiTanBangHoi(player);
                            }
                        }
                    }
                }
                case ConstNpc.RECEIVE_KOL_TASK -> {
                    switch (select) {
                        case 0 -> {
                            if (player.playerTask.kolTask.isDone()) {
                                player.playerTask.kolTask.receive(player);
                            }
                        }
                    }
                }
                case ConstNpc.MENU_OPENED_DBKB -> {
                    switch (select) {
                        case 2 -> {
                            if (player.clan == null) {
                                Service.gI().sendThongBao(player, "Hãy vào bang hội trước");
                                return;
                            }
                            if (player.isAdmin() || player.nPoint.power >= TreasureUnderSea.POWER_CAN_GO_TO_DBKB) {
                                ChangeMapService.gI().goToDBKB(player);
                            } else {
                                this.npcChat(player, "Yêu cầu sức mạnh lớn hơn "
                                        + Util.numberToMoney(TreasureUnderSea.POWER_CAN_GO_TO_DBKB));
                            }
                        }



                    }
                }
                case ConstNpc.MENU_OPEN_DBKB -> {
                    switch (select) {
                        case 2 -> {
                            if (player.clan == null) {
                                Service.gI().sendThongBao(player, "Hãy vào bang hội trước");
                                return;
                            }
                            if (player.isAdmin() || player.nPoint.power >= TreasureUnderSea.POWER_CAN_GO_TO_DBKB) {
                                Input.gI().createFormChooseLevelBDKB(player);
                            } else {
                                this.npcChat(player, "Yêu cầu sức mạnh lớn hơn "
                                        + Util.numberToMoney(TreasureUnderSea.POWER_CAN_GO_TO_DBKB));
                            }
                        }



                    }
                }
                case ConstNpc.MENU_ACCEPT_GO_TO_BDKB -> {
                    switch (select) {
                        case 0 ->
                            TreasureUnderSeaService.gI().openBanDoKhoBau(player,
                                    Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                    }
                }
            }
        }
    }
}
