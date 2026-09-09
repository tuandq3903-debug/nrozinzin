package services;

/*
 *
 *
 * @author ZINZIN
 */

import java.time.Instant;
import java.util.Date;

import jdbc.DatabaseManager;
import jdbc.daos.PlayerDAO;
import models.Template;
import models.player.Player;
import network.Message;
import models.player.dailyGift.DailyGiftService;
import models.skill.Skill;
import server.Client;
import services.func.ChangeMapService;
import models.task.Badges.BadgesTaskService;
import utils.Logger;
import utils.Util;
import services.ClanWarService;
import utils.SkillUtil;

public class PlayerService {

    private static PlayerService i;

    public PlayerService() {
    }

    public static PlayerService gI() {
        if (i == null) {
            i = new PlayerService();
        }
        return i;
    }

    public void sendTNSM(Player player, byte type, long param) {
        if (param > 0) {
            Message msg;
            try {
                msg = new Message(-3);
                msg.writer().writeByte(type);// 0 là cộng sm, 1 cộng tn, 2 là cộng cả 2
                msg.writer().writeInt((int) param);// số tn cần cộng
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void sendMessageAllPlayer(Message msg) {
        for (Player pl : Client.gI().getPlayers()) {
            if (pl != null) {
                pl.sendMessage(msg);
            }
        }
        msg.cleanup();
    }

    public void sendMessageIgnore(Player plIgnore, Message msg) {
        for (Player pl : Client.gI().getPlayers()) {
            if (pl != null && !pl.equals(plIgnore)) {
                pl.sendMessage(msg);
            }
        }
        msg.cleanup();
    }

    public void sendInfoHp(Player player) {
        Message msg;
        try {
            msg = Service.gI().messageSubCommand((byte) 5);
            msg.writer().writeInt(player.nPoint.hp);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void sendInfoMp(Player player) {
        Message msg;
        try {
            msg = Service.gI().messageSubCommand((byte) 6);
            msg.writer().writeInt(player.nPoint.mp);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void sendInfoHpMp(Player player) {
        if (player.nPoint == null) {
            return;
        }
        sendInfoHp(player);
        sendInfoMp(player);
    }

    public void hoiPhuc(Player player, long hp, long mp) {
        if (!player.isDie()) {
            player.nPoint.addHp(hp);
            player.nPoint.addMp(mp);
            Service.gI().Send_Info_NV(player);
            if (!player.isPet && !player.isNewPet) {
                PlayerService.gI().sendInfoHpMp(player);
            }
        }
    }

    public void sendInfoHpMpMoney(Player player) {
        if (player == null || !player.isPl()) {
            return;
        }
        Message msg;
        try {
            msg = Service.gI().messageSubCommand((byte) 4);
            try {
                if (player.getSession().version >= 214) {
                    msg.writer().writeLong(player.inventory.gold);
                } else {
                    msg.writer().writeInt((int) player.inventory.gold);
                }
            } catch (Exception e) {
                msg.writer().writeInt((int) player.inventory.gold);
            }
            msg.writer().writeInt(player.inventory.gem);//luong
            msg.writer().writeInt(player.nPoint.hp);//chp
            msg.writer().writeInt(player.nPoint.mp);//cmp
            msg.writer().writeInt(player.inventory.ruby);//ruby
            player.sendMessage(msg);
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void playerMove(Player player, int x, int y) {
        if (player.zone == null) {
            return;
        }
        if (!player.isDie()) {
            if (player.effectSkill.isCharging) {
                EffectSkillService.gI().stopCharge(player);
            }
            if (player.effectSkill.useTroi) {
                EffectSkillService.gI().removeUseTroi(player);
            }
            player.location.x = x;
            player.location.y = y;
            player.location.lastTimeplayerMove = System.currentTimeMillis();
            switch (player.zone.map.mapId) {
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                    if (!player.isBoss && !player.isPet) {
                        if (x < 24 || x > player.zone.map.mapWidth - 24 || y < 0 || y > player.zone.map.mapHeight - 24) {
                            if (MapService.gI().getWaypointPlayerIn(player) == null) {
                                ChangeMapService.gI().changeMap(player, 21 + player.gender, 0, 200, 336);
                                return;
                            }
                        }
                        int yTop = player.zone.map.yPhysicInTop(player.location.x, player.location.y);
                        if (yTop >= player.zone.map.mapHeight - 24) {
                            ChangeMapService.gI().changeMap(player, 21 + player.gender, 0, 200, 336);
                            return;
                        }
                    }
                    break;
            }
            if (player.pet != null) {
                player.pet.followMaster();
            }
            if (player.newPet != null) {
                player.newPet.followMaster();
            }
            if (player.isPl()) {
                try {
                    int type = player.zone.map.tileMap[player.location.y / 24][player.location.x / 24];
                    player.isFly = type == 0;
                } catch (Exception e) {
                }
                if (player.isFly && player.getMount() == -1) {
                    int mp = player.nPoint.mpg / (100 * (player.effectSkill.isMonkey ? 2 : 1));
                    hoiPhuc(player, 0, -mp);
                }
            }
            MapService.gI().sendPlayerMove(player);
            TaskService.gI().checkDoneTaskGoToMap(player, player.zone);
        }
    }

    public void sendCurrentStamina(Player player) {
        Message msg;
        try {
            msg = new Message(-68);
            msg.writer().writeShort(player.nPoint.stamina);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }
    
    public static void checkCompleteHocSkill(Player pl) {
        long now = System.currentTimeMillis();
        if (pl.HocSkill.Time > 0 && now >= pl.HocSkill.Time) {
            int tplId = pl.HocSkill.ItemTemplateSkillId;
            // reset trước khi cấp
            pl.HocSkill.Time = -1;
            pl.HocSkill.ItemTemplateSkillId = -1;
            // Bảo vệ: nếu template không tồn tại trong DB thì bỏ qua (không cấp skill, vẫn lưu DB)
            Template.ItemTemplate tpl = ItemService.gI().getTemplate(tplId);
            if (tpl == null) {
                Logger.warning(PlayerService.class, "checkCompleteHocSkill: ItemTemplate id=" + tplId + " không tồn tại cho player id=" + pl.id + ", bỏ qua cấp skill");
                PlayerDAO.updatePlayer(pl);
                return;
            }
            // parse level từ tên item
            byte level = Byte.parseByte(
                tpl.name.replaceAll("\\D+", "")
            );
            try {
                // tạo và gán skill mới
                Skill newSkill = SkillUtil.createSkill(
                    SkillUtil.getTempSkillSkillByItemID(tplId),
                    level
                );
                pl.CheckHocSkill.add(tplId);
                SkillUtil.setSkill(pl, newSkill);
                // gửi sub-command 62 về client
                var msg = Service.gI().messageSubCommand((byte)62);
                msg.writer().writeShort(newSkill.skillId);
                pl.sendMessage(msg);
                msg.cleanup();
                // cập nhật UI HP/MP/Money
                PlayerService.gI().sendInfoHpMpMoney(pl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // lưu lại DB
            PlayerDAO.updatePlayer(pl);
        }
    }


    public void sendMaxStamina(Player player) {
        Message msg;
        try {
            msg = new Message(-69);
            msg.writer().writeShort(player.nPoint.maxStamina);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void changeAndSendTypePK(Player player, int type) {
        changeTypePK(player, type);
        sendTypePk(player);
    }

    public void changeTypePK(Player player, int type) {
        player.typePk = (byte) type;
    }

    public void sendTypePk(Player player) {
        Message msg;
        try {
            msg = Service.gI().messageSubCommand((byte) 35);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeByte(player.typePk);
            Service.gI().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void banPlayer(Player playerBaned) {
        try {
            DatabaseManager.executeUpdate("update account set ban = 1 where id = ? and username = ?",
                    playerBaned.getSession().userId, playerBaned.getSession().uu);
        } catch (Exception e) {
        }
        Service.gI().sendThongBao(playerBaned,
                "Tài khoản của bạn đã bị khóa\nGame sẽ mất kết nối sau 5 giây...");
        playerBaned.iDMark.setLastTimeBan(System.currentTimeMillis());
        playerBaned.iDMark.setBan(true);
    }

    private static final int COST_GOLD_HOI_SINH = 10000000;
    private static final int COST_GEM_HOI_SINH = 1;
    private static final int COST_GOLD_HOI_SINH_NRSD = 20000000;

    public void hoiSinh(Player player) {
    // 1) Chặn revive nếu người chơi đã bị loại
    if (ClanWarService.gI().isDisqualified(player)) {
        Service.gI().sendThongBao(player,
            "Bạn đã bị loại khỏi Đại Chiến Bang Hội, không thể hồi sinh");
        return;
    }
    // 2) Chặn revive nếu đang ở map chiến trường
    if (player.zone != null && player.zone.map.mapId == ClanWarService.WAR_MAP )  {
        Service.gI().sendThongBao(player,
            "Không thể hồi sinh trong Đại Chiến Bang Hội");
        return;
    }

    // 3) Gọi onPlayerDeath ngay khi chết để đánh dấu và teleport
    if (player.isDie()) {
        ClanWarService.gI().onPlayerDeath(player);
        // Nếu bị loại, onPlayerDeath đã teleport và đánh dấu, dừng luôn
        if (ClanWarService.gI().isDisqualified(player)) {
            return;
        }
    }

    // 4) Logic revive cũ (giữ nguyên)
    if (player.isDie() && player.zone != null && player.zone.map.mapId != 51) {
        if (Util.canDoWithTime(player.lastTimeRevived, 1500)) {
            boolean canHs;
            if (MapService.gI().isMapBlackBallWar(player.zone.map.mapId)) {
                if (player.inventory.gold >= COST_GOLD_HOI_SINH_NRSD) {
                    player.inventory.gold -= COST_GOLD_HOI_SINH_NRSD;
                    canHs = true;
                } else {
                    Service.gI().sendThongBao(player,
                        "Không đủ vàng để thực hiện, còn thiếu "
                        + Util.numberToMoney(COST_GOLD_HOI_SINH_NRSD
                        - player.inventory.gold) + " vàng");
                    return;
                }
            } else {
                if (player.inventory.gem >= COST_GEM_HOI_SINH) {
                    player.inventory.gem -= COST_GEM_HOI_SINH;
                    canHs = true;
                } else {
                    Service.gI().sendThongBao(player,
                        "Không đủ ngọc để thực hiện, còn thiếu "
                        + Util.numberToMoney(COST_GEM_HOI_SINH
                        - player.inventory.gem) + " ngọc");
                    return;
                }
            }
            if (canHs) {
                Service.gI().sendMoney(player);
                Service.gI().hsChar(player,
                    player.nPoint.hpMax,
                    player.nPoint.mpMax);
            }
        }
    }
}

    public void hoiSinhMaBu(Player player) {
        if (player.isDie()) {
            boolean canHs = false;
            if (MapService.gI().isMapMaBu(player.zone.map.mapId)) {
                if (player.inventory.gold >= COST_GOLD_HOI_SINH_NRSD) {
                    player.inventory.gold -= COST_GOLD_HOI_SINH_NRSD;
                    canHs = true;
                } else {
                    Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện, còn thiếu " + Util.numberToMoney(COST_GOLD_HOI_SINH_NRSD
                            - player.inventory.gold) + " vàng");
                    return;
                }
            } else {
                if (player.inventory.gold >= COST_GOLD_HOI_SINH) {
                    player.inventory.gold -= COST_GOLD_HOI_SINH;
                    canHs = true;
                } else {
                    Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện, còn thiếu " + Util.numberToMoney(COST_GOLD_HOI_SINH
                            - player.inventory.gold) + " vàng");
                    return;
                }
            }
            if (canHs) {
                Service.gI().sendMoney(player);
                Service.gI().hsChar(player, player.nPoint.hpMax, player.nPoint.mpMax);
            }
        }
    }

    public void dailyLogin(Player player) {
        if (Util.compareDay(Date.from(Instant.now()), player.firstTimeLogin)) {
            player.firstTimeLogin = Date.from(Instant.now());
            BadgesTaskService.createAndResetTask(player);
            DailyGiftService.addAndReset(player);
        }
    }

}
