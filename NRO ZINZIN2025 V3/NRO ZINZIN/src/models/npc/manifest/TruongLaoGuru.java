package models.npc.manifest;

/**
 * @author ZINZIN
 */

import models.clan.Clan;
import consts.ConstNpc;
import consts.ConstPlayer;
import models.npc.Npc;
import models.player.Player;
import models.shop.ShopService;
import models.skill.Skill;
import services.ItemService;
import services.NpcService;
import services.PlayerService;
import services.RewardService;
import services.Service;
import services.TaskService;
import services.func.ChangeMapService;
import services.func.Input;
import utils.SkillUtil;
import utils.TimeUtil;
import utils.Util;

public class TruongLaoGuru extends Npc {

    public TruongLaoGuru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) return;
        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            if (player.gender != ConstPlayer.NAMEC) {
                NpcService.gI().createTutorial(player, tempId, avartar,
                        "Con hãy về hành tinh của mình mà thể hiện");
                return;
            }
            String[] menus;
            if (!player.canReward) {
                Clan clan = player.clan;
                if (clan != null) {
                    if (clan.isLeader(player)) {
                        menus = new String[]{
                            "Nhiệm vụ",
                            "Học\nKỹ năng",
                            "Về khu\nvực bang",
                            "Giải tán\nBang hội"
                        };
                    } else {
                        menus = new String[]{
                            "Nhiệm vụ",
                            "Học\nKỹ năng",
                            "Về khu\nvực bang"
                        };
                    }
                } else {
                    menus = new String[]{
                        "Nhiệm vụ",
                        "Học\nKỹ năng"
                    };
                }
            } else {
                menus = new String[]{"Giao\nLân con"};
            }
            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Chào con, ta rất vui khi gặp được con\nCon muốn làm gì nào ?", menus);
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;
        if (player.canReward) {
            RewardService.gI().rewardLancon(player);
            return;
        }
        int index = player.iDMark.getIndexMenu();
        switch (index) {
            case ConstNpc.BASE_MENU -> {
                switch (select) {
                    case 0 -> // Nhiệm vụ
                        NpcService.gI().createTutorial(player, tempId, avartar,
                                player.playerTask.taskMain.subTasks.get(
                                        player.playerTask.taskMain.index).name);

                    case 1 -> { // Học Kỹ năng
                        if (player.HocSkill.Time != -1) {
                            long timeLeft = player.HocSkill.Time - System.currentTimeMillis();
                            if (timeLeft <= 0) {
                                // Hoàn thành tự động khi đã hết thời gian
                                player.HocSkill.Time = -1;
                                int tplId = player.HocSkill.ItemTemplateSkillId;
                                // Lấy level từ tên item
                                String[] parts = ItemService.gI().getTemplate(tplId).name.split("");
                                byte level = Byte.parseByte(parts[parts.length - 1]);
                                try {
                                    // Tạo skill và gán
                                    Skill newSkill = SkillUtil.createSkill(
                                            SkillUtil.getTempSkillSkillByItemID(tplId), level);
                                    player.CheckHocSkill.add(tplId);
                                    SkillUtil.setSkill(player, newSkill);
                                    // Thông báo client
                                    var msg = Service.gI().messageSubCommand((byte) 62);
                                    msg.writer().writeShort(newSkill.skillId);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    PlayerService.gI().sendInfoHpMpMoney(player);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // Chưa xong, hiển thị menu tăng tốc
                                int ngoc = 5;
                                if (timeLeft / 600_000 >= 2) ngoc += timeLeft / 600_000;
                                String[] subName = ItemService.gI()
                                        .getTemplate(player.HocSkill.ItemTemplateSkillId).name.split("");
                                byte level = Byte.parseByte(subName[subName.length - 1]);
                                createOtherMenu(player, 12,
                                        "Con đang học kỹ năng\n"
                                                + SkillUtil.findSkillTemplate(
                                                        SkillUtil.getTempSkillSkillByItemID(
                                                                player.HocSkill.ItemTemplateSkillId))
                                                        .name
                                                + " cấp " + level + "\nThời gian còn lại "
                                                + TimeUtil.getTime(timeLeft),
                                        "Học Cấp tốc " + ngoc + " ngọc", "Huỷ", "Bỏ qua");
                            }
                        } else {
                            // Mở shop mua sách học kỹ năng
                            ShopService.gI().opendShop(player, "QUY_LAO", false);
                        }
                    }

                    case 2 -> // Về khu vực bang
                        ChangeMapService.gI().changeMapNonSpaceship(player, 153,
                                Util.nextInt(100, 200), 432);

                    case 3 -> // Giải tán Bang hội
                        createOtherMenu(player, 3,
                                "Con có chắc muốn giải tán bang hội không?", "Đồng ý", "Từ chối");
                }
            }

            case 3 -> { // Xác nhận giải tán
                if (select == 0) {
                    Input.gI().createFormGiaiTanBangHoi(player);
                }
            }

            case 12 -> { // Menu tăng tốc học
                switch (select) {
                    case 0 -> {
                        long time = player.HocSkill.Time - System.currentTimeMillis();
                        int ngoc = 5;
                        if (time / 600_000 >= 2) ngoc += time / 600_000;
                        if (player.inventory.gem < ngoc) {
                            Service.gI().sendThongBao(player, "Bạn không có đủ ngọc");
                            return;
                        }
                        player.inventory.subGem(ngoc);
                        player.HocSkill.Time = -1;
                        try {
                            String[] subName = ItemService.gI()
                                    .getTemplate(player.HocSkill.ItemTemplateSkillId).name.split("");
                            byte level = Byte.parseByte(
                                    subName[subName.length - 1]);
                            Skill curSkill = SkillUtil.getSkillByItemID(
                                    player, player.HocSkill.ItemTemplateSkillId);
                            curSkill = SkillUtil.createSkill(
                                    SkillUtil.getTempSkillSkillByItemID(
                                            player.HocSkill.ItemTemplateSkillId), level);
                            player.CheckHocSkill.add((int) player.HocSkill.ItemTemplateSkillId);
                            SkillUtil.setSkill(player, curSkill);
                            var msg = Service.gI().messageSubCommand((byte) 62);
                            msg.writer().writeShort(curSkill.skillId);
                            player.sendMessage(msg);
                            msg.cleanup();
                            PlayerService.gI().sendInfoHpMpMoney(player);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    case 1 -> // Huỷ học, nhận lại 50% tiềm năng
                        createOtherMenu(player, 13,
                                "Con có muốn huỷ học kỹ năng này và nhận lại 50% số tiềm năng không ?",
                                "Ok", "Đóng");
                }
            }
        }
    }
}
