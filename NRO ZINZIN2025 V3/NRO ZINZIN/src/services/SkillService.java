package services;

/*
 *
 *
 * @author ZINZIN
 */
import utils.Functions;
import models.boss.BossData;
import consts.ConstPlayer;
import models.boss.BossID;
import models.boss.BossManager;
import models.Broly.Broly;
import models.Broly.SuperBroly;
import models.Commeson.NhanBan;
import models.Commeson.PhanThan;
import models.SuperRank_Boss.Rival;
import models.Yardart_Boss.Yardart;
import consts.ConstAchievement;
import models.intrinsic.Intrinsic;
import models.item.Item;
import models.mob.Mob;
import models.mob.MobMe;
import models.player.Pet;
import models.player.Player;
import models.skill.Skill;
import models.player.NewSkill;
import network.Message;

import java.io.IOException;

import utils.Logger;
import utils.SkillUtil;
import utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import lombok.NonNull;
import models.Achievement.AchievementService;
import models.Card.RadarService;
import models.npc.NonInteractiveNPC;
import server.ServerNotify;
import services.func.EffectMapService;
import utils.TimeUtil;

public class SkillService {

    private static SkillService instance;

    public static SkillService gI() {
        if (instance == null) {
            instance = new SkillService();
        }
        return instance;
    }

    public boolean useSkill(Player player, Player plTarget, Mob mobTarget, int status, Message msg) {
    // ===== CHẶN PK CÙNG BANG Ở MAP ĐẶC THÙ =====
    if (plTarget != null && player.clan != null && plTarget.clan != null && player.clan == plTarget.clan
            && MapService.gI().isMapBlackBallWar(plTarget.zone.map.mapId)) {
        Service.gI().chatJustForMe(player, plTarget, "Ê cùng bang mà");
        return false;
    }
    if (plTarget != null && (player.idNRNM != -1 || plTarget.idNRNM != -1) && player.clan != null
            && plTarget.clan != null && player.clan == plTarget.clan) {
        Service.gI().chatJustForMe(player, plTarget, "Ê cùng bang mà");
        return false;
    }
    if (plTarget != null && !Util.canDoWithTime(plTarget.lastTimeRevived, 1500)) return false;

    byte skillId = -1;
    short dx = -1, dy = -1, x = -1, y = -1;
    byte dir = -1;

    if (status == 20) { // gói "chọn skill"
        try {
            skillId = msg.reader().readByte();
            dx = msg.reader().readShort();
            dy = msg.reader().readShort();
            dir = msg.reader().readByte();
            x = msg.reader().readShort();
            y = msg.reader().readShort();
        } catch (Exception ignored) {}
    }

    // ==== TRƯỜNG HỢP 1: QUICK-CAST khi vừa chọn ô skill ====
//if (status == 20 && skillId == Skill.BIEN_HINH) {
//    // đảm bảo skillSelect đúng là Biến Hình
//    if (player.playerSkill == null || player.playerSkill.skillSelect == null
//            || player.playerSkill.skillSelect.template == null
//            || player.playerSkill.skillSelect.template.id != Skill.BIEN_HINH) {
//        selectSkill(player, skillId);
//    }
//
//    boolean isActive = player.effectSkill != null && player.effectSkill.isBienHinhBroly;
//    if (isActive) {
//        // tuỳ thích: báo nhỏ cho người chơi
//        // Service.gI().sendThongBao(player, "Đang duy trì, không thể dùng lại");
//        return false;
//    }
//
//    // chuẩn hoá trạng thái
//    if (player.effectSkill != null && player.effectSkill.useTroi) {
//        EffectSkillService.gI().removeUseTroi(player);
//    }
//    if (player.effectSkill != null && player.effectSkill.isCharging) {
//        EffectSkillService.gI().stopCharge(player);
//    }
//
//    // check mana + cooldown RIÊNG của Biến Hình
//    Skill sBienHinh = getSkillById(player, Skill.BIEN_HINH);
//    if (sBienHinh == null || !canUseSkillWithMana(player) || !canUseBienHinhWithCooldown(player, sBienHinh)) {
//        return false;
//    }
//
//    // Ép cooldown hiển thị cho client (trường hợp template để 0)
//    sBienHinh.coolDown = (int) resolveBienHinhCooldownMillis(player, sBienHinh);
//
//    // BẬT Biến Hình
//    EffectSkillService.gI().startUseSkillBienHinh(player);
//
//    // GHI COOLDOWN + ĐẨY UI thanh hồi chiêu NGAY
//    markBienHinhCooldown(player);
//    selectSkill(player, Skill.BIEN_HINH);
//    affterUseSkill(player, Skill.BIEN_HINH);
//
//    return true;
//}

// ==== TRƯỜNG HỢP 2: đang chọn sẵn Biến Hình và gửi gói cast thường ====
short tid = player.playerSkill.skillSelect.template.id;
//if (tid == Skill.BIEN_HINH) {
//    // đang duy trì -> KHÔNG cho bấm (giống skill thường)
//    boolean isActive = player.effectSkill != null && player.effectSkill.isBienHinhBroly;
//    if (isActive) {
//        return false;
//    }
//
//    if (player.effectSkill != null && player.effectSkill.useTroi) {
//        EffectSkillService.gI().removeUseTroi(player);
//    }
//    if (player.effectSkill != null && player.effectSkill.isCharging) {
//        EffectSkillService.gI().stopCharge(player);
//    }
//
//    Skill sBienHinh = getSkillById(player, Skill.BIEN_HINH);
//    if (sBienHinh == null || !canUseSkillWithMana(player) || !canUseBienHinhWithCooldown(player, sBienHinh)) {
//        return false;
//    }
//
//    sBienHinh.coolDown = (int) resolveBienHinhCooldownMillis(player, sBienHinh);
//
//    EffectSkillService.gI().startUseSkillBienHinh(player);
//
//    markBienHinhCooldown(player);
//    selectSkill(player, Skill.BIEN_HINH);
//    affterUseSkill(player, Skill.BIEN_HINH);
//
//    return true;
//}


    // các chặn chung cho skill khác
    if ((player.effectSkill != null && player.effectSkill.isHaveEffectSkill()
            && (tid != Skill.TU_SAT && tid != Skill.QUA_CAU_KENH_KHI && tid != Skill.MAKANKOSAPPO))
            || (plTarget != null && !canAttackPlayer(player, plTarget))
            || (mobTarget != null && mobTarget.isDie())
            || !canUseSkillWithMana(player) || !canUseSkillWithCooldown(player)) {
        return false;
    }

    if (player.effectSkill != null && player.effectSkill.useTroi) {
        EffectSkillService.gI().removeUseTroi(player);
    }
    if (player.effectSkill != null && player.effectSkill.isCharging) {
        EffectSkillService.gI().stopCharge(player);
    }

    if (BossManager.gI().getBossById(Util.createIdBossClone((int) player.id) + 9999) != null) {
        BossManager.gI().getBossById(Util.createIdBossClone((int) player.id) + 9999).playertarget = plTarget;
    }

    // route theo type cho skill còn lại
    switch (player.playerSkill.skillSelect.template.type) {
        case 1 -> useSkillAttack(player, plTarget, mobTarget);
        case 2 -> {
            if (canUseSkillWithMana(player) && canUseSkillWithCooldown(player)) {
                useSkillBuffToPlayer(player, plTarget);
                return true;
            }
            return false;
        }
        case 3 -> useSkillAlone(player);
        case 4 -> useNewSkillNotFocus(player, plTarget, mobTarget, status, skillId, dx, dy, dir, x, y);
        default -> { return false; }
    }
    return true;
}




    public void sendPhanThab(Player player, Player plTarget, Mob mobTarget) {
        if (BossManager.gI().getBossById((Util.createIdBossClone((int) player.id) - 9999)) != null) {
            BossManager.gI().getBossById((Util.createIdBossClone((int) player.id)
                    - 9999)).idSkillPlayer = player.playerSkill.skillSelect.skillId;
            if (plTarget != null) {
                BossManager.gI().getBossById((Util.createIdBossClone((int) player.id) - 9999)).playertarget = plTarget;
            }
            if (mobTarget != null) {
                BossManager.gI().getBossById((Util.createIdBossClone((int) player.id) - 9999)).mobTarget = mobTarget;
            }
        }
    }

    private void useNewSkillNotFocus(Player player, Player plTarget, Mob mobTarget, int status, byte skillId, Short dx,
            Short dy, byte dir, Short x, Short y) {
        try {
            // Cho đệ và boss dùng skill mới
            if (skillId == -1 && (plTarget != null || mobTarget != null)) {
                skillId = player.playerSkill.skillSelect.template.id;
                dx = (short) player.location.x;
                dy = (short) player.location.y;
                if (plTarget != null) {
                    x = (short) plTarget.location.x;
                    y = (short) plTarget.location.y;
                } else {
                    x = (short) mobTarget.location.x;
                    y = (short) mobTarget.location.y;
                }
                dir = (byte) (dx > x ? -1 : 1);
            }
            switch (skillId) {
                case Skill.SUPER_KAME, Skill.LIEN_HOAN_CHUONG, Skill.MA_PHONG_BA -> {
                    player.newSkill.setSkillSpecial(dir, dx, dy, x, y);
                    newSkillNotFocus(player, status);
                    AchievementService.gI().checkDoneTask(player, ConstAchievement.TUYET_KY_THANH_THAO);
                }
            }
            affterUseSkill(player, player.playerSkill.skillSelect.template.id);
        } catch (Exception e) {
//             e.printStackTrace();
        }
    }

    public void updateSkillSpecial(Player player) {
        try {
            if (player.newSkill == null || player.zone == null) {
                return;
            }
            if (player.isDie() || player.effectSkill.isHaveEffectSkill()) {
                player.newSkill.closeSkillSpecial();
                return;
            }
            if (player.newSkill.skillSelect.template.id == Skill.MA_PHONG_BA) {
                if (Util.canDoWithTime(player.newSkill.lastTimeSkillSpecial, NewSkill.TIME_GONG)) {
                    player.newSkill.lastTimeSkillSpecial = System.currentTimeMillis();
                    player.newSkill.closeSkillSpecial();
                    List<Player> playersMap;
                    int maxFight = (player.newSkill.skillSelect.point / 2) == 0 ? 1
                            : player.newSkill.skillSelect.point / 2;
                    int count = 0;
                    if (player.isBoss) {
                        playersMap = player.zone.getNotBosses();
                    } else {
                        playersMap = player.zone.getHumanoids();
                    }

                    for (Player playerMap : playersMap) {
                        if (count >= maxFight) {
                            break;
                        }
                        if (playerMap == null || playerMap.id == player.id) {
                            continue;
                        }
                        if (player.newSkill.dir == -1 && !playerMap.isDie()
                                && Util.getDistance(player.location.x - 24, player.location.y, playerMap.location.x,
                                        playerMap.location.y) <= player.newSkill.skillSelect.dx
                                && this.canAttackPlayer(player, playerMap)) {
                            player.newSkill.playersTaget.add(playerMap);
                            count++;
                        } else if (player.newSkill.dir == 1 && !playerMap.isDie()
                                && Util.getDistance(player.xSend + 24, player.ySend, playerMap.xSend,
                                        playerMap.ySend) <= player.newSkill.skillSelect.dx
                                && this.canAttackPlayer(player, playerMap)) {
                            player.newSkill.playersTaget.add(playerMap);
                            count++;
                        }
                    }

                    if (!player.isBoss) {
                        for (Mob mobMap : player.zone.mobs) {
                            if (mobMap == null) {
                                continue;
                            }
                            if (count >= maxFight) {
                                break;
                            }
                            if (player.newSkill.dir == -1 && !mobMap.isDie()
                                    && Util.getDistance(player.location.x - 24, player.location.y, mobMap.location.x,
                                            mobMap.location.y) <= player.newSkill.skillSelect.dx) {
                                player.newSkill.mobsTaget.add(mobMap);
                                mobMap.addTemporaryEnemies(player);
                                count++;
                            } else if (player.newSkill.dir == 1 && !mobMap.isDie()
                                    && Util.getDistance(player.location.x + 24, player.location.y, mobMap.location.x,
                                            mobMap.location.y) <= player.newSkill.skillSelect.dx) {
                                player.newSkill.mobsTaget.add(mobMap);
                                mobMap.addTemporaryEnemies(player);
                                count++;
                            }
                        }
                    }
                    newSkillNotFocus(player, 21);
                    EffectSkillService.gI().startUseMafuba(player, player.newSkill.timeEnd());
                }
            } else {
                if (player.newSkill.stepSkillSpecial == 0
                        && Util.canDoWithTime(player.newSkill.lastTimeSkillSpecial, NewSkill.TIME_GONG)) {
                    player.newSkill.lastTimeSkillSpecial = System.currentTimeMillis();
                    player.newSkill.stepSkillSpecial = 1;
                    newSkillNotFocus(player, 21);
                } else if (player.newSkill.stepSkillSpecial == 1
                        && !Util.canDoWithTime(player.newSkill.lastTimeSkillSpecial, NewSkill.TIME_GONG)) {
                    List<Player> playersMap;
                    if (player.isBoss) {
                        playersMap = player.zone.getNotBosses();
                    } else {
                        playersMap = player.zone.getHumanoids();
                    }

                    for (Player playerMap : playersMap) {
                        if (playerMap == null || playerMap.id == player.id) {
                            continue;
                        }
                        if (player.newSkill.dir == -1 && player.location.x > playerMap.location.x && !playerMap.isDie()
                                && Math.abs(
                                        playerMap.location.x - player.newSkill._xPlayer) <= player.newSkill._xObjTaget
                                && Math.abs(
                                        playerMap.location.y - player.newSkill._yPlayer) <= player.newSkill._yObjTaget
                                && this.canAttackPlayer(player, playerMap)) {
                            this.playerAttackPlayer(player, playerMap, false);
                        }
                        if (player.newSkill.dir == 1 && player.location.x < playerMap.location.x && !playerMap.isDie()
                                && Math.abs(
                                        playerMap.location.x - player.newSkill._xPlayer) <= player.newSkill._xObjTaget
                                && Math.abs(
                                        playerMap.location.y - player.newSkill._yPlayer) <= player.newSkill._yObjTaget
                                && this.canAttackPlayer(player, playerMap)) {
                            this.playerAttackPlayer(player, playerMap, false);
                        }
                    }
                    if (!player.isBoss) {
                        for (Mob mobMap : player.zone.mobs) {
                            if (mobMap == null) {
                                continue;
                            }
                            if (player.newSkill.dir == -1 && player.location.x > mobMap.location.x && !mobMap.isDie()
                                    && Math.abs(
                                            mobMap.location.x - player.newSkill._xPlayer) <= player.newSkill._xObjTaget
                                    && Math.abs(mobMap.location.y
                                            - player.newSkill._yPlayer) <= player.newSkill._yObjTaget) {
                                this.playerAttackMob(player, mobMap, false, false);
                            }
                            if (player.newSkill.dir == 1 && player.location.x < mobMap.location.x && !mobMap.isDie()
                                    && Math.abs(
                                            mobMap.location.x - player.newSkill._xPlayer) <= player.newSkill._xObjTaget
                                    && Math.abs(mobMap.location.y
                                            - player.newSkill._yPlayer) <= player.newSkill._yObjTaget) {
                                this.playerAttackMob(player, mobMap, false, false);
                            }
                        }
                    }
                } else if (player.newSkill.stepSkillSpecial == 1) {
                    player.newSkill.closeSkillSpecial();
                }
            }
        } catch (Exception e) {
//             e.printStackTrace();
        }
    }

    public void sendCurrLevelSpecial(Player player, Skill skill) {
        Message message = null;
        try {
            message = Service.gI().messageSubCommand((byte) 62);
            message.writer().writeShort(skill.skillId);
            message.writer().writeByte(0);
            message.writer().writeShort(skill.currLevel);
            player.sendMessage(message);
        } catch (final IOException ex) {
        } finally {
            if (message != null) {
                message.cleanup();
            }
        }
    }

    // _______________________________NEW_SKILL_NOT_FOCUS_______________________________
    public void newSkillNotFocus(Player player, int status) {
        Message msg = null;
        try {
            NewSkill newSkill = player.newSkill;
            msg = new Message(-45);
            msg.writer().writeByte(status);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(newSkill.skillSelect.template.id);
            if (status == 20) {
                byte typeFrame = 4;
                switch (newSkill.skillSelect.template.id) {
                    case Skill.SUPER_KAME -> typeFrame = 1;
                    case Skill.LIEN_HOAN_CHUONG -> typeFrame = 2;
                    case Skill.MA_PHONG_BA -> typeFrame = 3;
                }
                byte dir = newSkill.dir;
                short timeGong = NewSkill.TIME_GONG;
                boolean isFly = false;
                byte typePaint = newSkill.typePaint;
                byte typeItem = newSkill.typeItem;
                msg.writer().writeByte(typeFrame);
                msg.writer().writeByte(dir);
                msg.writer().writeShort(timeGong);
                msg.writer().writeByte((byte) (isFly ? 1 : 0));
                msg.writer().writeByte(typePaint);
                msg.writer().writeByte(typeItem);
            } else if (status == 21) {
                short pointX = (short) (newSkill._xPlayer + ((newSkill.dir == -1) ? (-newSkill._xObjTaget) : newSkill._xObjTaget));
                short pointY = (short) newSkill._yPlayer;
                short timeDame = NewSkill.TIME_GONG;
                short rangeDame = newSkill._yObjTaget;
                byte typePaint = newSkill.typePaint;
                byte typeItem = newSkill.typeItem;
                byte num = (byte) (player.newSkill.playersTaget.size() + player.newSkill.mobsTaget.size());
                msg.writer().writeShort(pointX);
                msg.writer().writeShort(pointY);
                msg.writer().writeShort(timeDame);
                msg.writer().writeShort(rangeDame);
                msg.writer().writeByte(typePaint);
                msg.writer().writeByte(num);
                if (num > 0) {
                    for (Player playerMap : player.newSkill.playersTaget) {
                        msg.writer().writeByte(1);
                        msg.writer().writeInt((int) playerMap.id);
                    }
                    for (Mob mobMap : player.newSkill.mobsTaget) {
                        msg.writer().writeByte(0);
                        msg.writer().writeByte(mobMap.id);
                    }
                }
                msg.writer().writeByte(typeItem);
            }
            Service.gI().sendMessAllPlayerInMap(player, msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void learSkillSpecial(Player player, byte skillID) {
        Message message = null;
        try {
            Skill curSkill = SkillUtil.createSkill(skillID, 1);
            SkillUtil.setSkill(player, curSkill);
            message = Service.gI().messageSubCommand((byte) 23);
            message.writer().writeShort(curSkill.skillId);
            player.sendMessage(message);
            message.cleanup();
        } catch (Exception e) {
//             e.printStackTrace();
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }

        }
    }

    public void learSkillSpecial(Player player, byte skillID, int level) {
        Message message = null;
        try {
            Skill curSkill = SkillUtil.createSkill(skillID, level);
            SkillUtil.setSkill(player, curSkill);
            message = Service.gI().messageSubCommand((byte) 23);
            message.writer().writeShort(curSkill.skillId);
            player.sendMessage(message);
            message.cleanup();
        } catch (Exception e) {
//             e.printStackTrace();
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }

        }
    }

    public void useSkillAttack(Player player, Player plTarget, Mob mobTarget) {
        if (player.effectSkill != null && player.effectSkill.useTroi) {
            EffectSkillService.gI().removeUseTroi(player);
        }
        sendPhanThab(player, plTarget, mobTarget);
        if (!player.isBoss) {
            if (player.isPet) {
                if (player.nPoint.stamina > 0) {
                    player.nPoint.numAttack++;
                    boolean haveCharmPet = ((Pet) player).master.charms != null
                            && ((Pet) player).master.charms.tdDeTu > System.currentTimeMillis();
                    if (haveCharmPet ? player.nPoint.numAttack >= 5 : player.nPoint.numAttack >= 2) {
                        player.nPoint.numAttack = 0;
                        player.nPoint.stamina--;
                    }
                } else {
                    ((Pet) player).askPea();
                    return;
                }
            } else {
                if (player.nPoint.stamina > 0) {
                    if (player.charms.tdDeoDai < System.currentTimeMillis()) {
                        player.nPoint.numAttack++;
                        if (player.nPoint.numAttack == 500) {
                            player.nPoint.numAttack = 0;
                            player.nPoint.stamina--;
                            PlayerService.gI().sendCurrentStamina(player);
                        }
                    }
                } else {
                    Service.gI().sendThongBao(player, "Thể lực đã cạn kiệt, hãy nghỉ ngơi để lấy lại sức");
                    return;
                }
            }
        }
        List<Mob> mobs;
        boolean miss = false;
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.KAIOKEN: // kaioken
                long hpUse = Util.maxIntValue(player.nPoint.hpMax / 100 * 10);
                if (player.setClothes.thanVuTruKaio == 4) {
                    hpUse = Util.maxIntValue(player.nPoint.hpMax / 100 * 5);
                } else if (player.setClothes.thanVuTruKaio == 5) {
                    hpUse = Util.maxIntValue(player.nPoint.hpMax / 100 * 3);
                }
                if (Util.maxIntValue(player.nPoint.hp) <= hpUse) {
                    break;
                } else {
                    Service.gI().sendEffAllPlayer(player, 1027);
                    player.nPoint.setHp(Util.maxIntValue(player.nPoint.hp - hpUse));
                    PlayerService.gI().sendInfoHpMpMoney(player);
                    Service.gI().Send_Info_NV(player);
                }
            case Skill.DRAGON:
            case Skill.DEMON:
            case Skill.GALICK:
            case Skill.LIEN_HOAN:
                // Siêu hạng 113
                if (player.zone != null && player.zone.map.mapId != 113 && plTarget != null
                        && Util.getDistance(player, plTarget) > Skill.RANGE_ATTACK_CHIEU_DAM) {
                    miss = true;
                }
                if (mobTarget != null && Util.getDistance(player, mobTarget) > Skill.RANGE_ATTACK_CHIEU_DAM) {
                    miss = true;
                }
            case Skill.KAMEJOKO:
            case Skill.MASENKO:
            case Skill.ANTOMIC:
                if (plTarget != null) {
                    playerAttackPlayer(player, plTarget, miss);
                }
                if (mobTarget != null) {
                    playerAttackMob(player, mobTarget, miss, false);
                }
                if (player.mobMe != null) {
                    player.mobMe.attack(plTarget, mobTarget, miss);
                }
                if (player.playerSkill != null) {
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                }
                break;
            // ******************************************************************
            case Skill.QUA_CAU_KENH_KHI:
                if (!player.playerSkill.prepareQCKK) {
                    // bắt đầu tụ quả cầu
                    player.playerSkill.prepareQCKK = !player.playerSkill.prepareQCKK;
                    player.playerSkill.lastTimePrepareQCKK = System.currentTimeMillis();
                    sendPlayerPrepareSkill(player, 4000);
                } else {
                    // ném cầu
                    player.playerSkill.prepareQCKK = !player.playerSkill.prepareQCKK;
                    mobs = new ArrayList<>();
                    if (plTarget != null) {
                        playerAttackPlayer(player, plTarget, false);
                        if (!player.isBoss) {
                            for (Mob mob : player.zone.mobs) {
                                if (!mob.isDie() && Util.getDistance(plTarget, mob) <= SkillUtil
                                        .getRangeQCKK(player.playerSkill.skillSelect.point)) {
                                    mobs.add(mob);
                                }
                            }
                        }
                    }
                    if (mobTarget != null) {
                        if (!player.isBoss) {
                            playerAttackMob(player, mobTarget, false, true);
                            for (Mob mob : player.zone.mobs) {
                                if (!mob.equals(mobTarget) && !mob.isDie()
                                        && Util.getDistance(mob, mobTarget) <= SkillUtil
                                                .getRangeQCKK(player.playerSkill.skillSelect.point)) {
                                    mobs.add(mob);
                                }
                            }
                        }
                    }
                    for (Mob mob : mobs) {
                        mob.injured(player, player.nPoint.getDameAttack(true), true);
                    }
                    PlayerService.gI().sendInfoHpMpMoney(player);
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                }
                break;
            case Skill.MAKANKOSAPPO:
                if (!player.playerSkill.prepareLaze) {
                    // bắt đầu nạp laze
                    player.playerSkill.prepareLaze = !player.playerSkill.prepareLaze;
                    player.playerSkill.lastTimePrepareLaze = System.currentTimeMillis();
                    sendPlayerPrepareSkill(player, 3000);
                } else {
                    // bắn laze
                    player.playerSkill.prepareLaze = !player.playerSkill.prepareLaze;
                    if (plTarget != null) {
                        playerAttackPlayer(player, plTarget, false);
                    }
                    if (mobTarget != null) {
                        playerAttackMob(player, mobTarget, false, true);
                    }
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                }
                PlayerService.gI().sendInfoHpMpMoney(player);
                break;
            case Skill.SOCOLA:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.SOCOLA);
                int timeSocola = SkillUtil.getTimeSocola();
                if (plTarget != null) {
                    EffectSkillService.gI().setSocola(plTarget, System.currentTimeMillis(), timeSocola);
                    Service.gI().Send_Caitrang(plTarget);
                    ItemTimeService.gI().sendItemTime(plTarget, 4133, timeSocola / 1000);
                }
                if (mobTarget != null) {
                    EffectSkillService.gI().sendMobToSocola(player, mobTarget, timeSocola);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.DICH_CHUYEN_TUC_THOI:
                int timeChoangDCTT = SkillUtil.getTimeDCTT(player.playerSkill.skillSelect.point);
                if (plTarget != null) {
                    if (player.isBoss) {
                        Service.gI().chat(player, "Dịch chuyển tức thời");
                    }
                    Service.gI().setPos(player, plTarget.location.x, plTarget.location.y);
                    playerAttackPlayer(player, plTarget, miss);
                    EffectSkillService.gI().setBlindDCTT(plTarget, System.currentTimeMillis(), timeChoangDCTT);
                    EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.BLIND_EFFECT);
                    PlayerService.gI().sendInfoHpMpMoney(plTarget);
                    ItemTimeService.gI().sendItemTime(plTarget, 3779, timeChoangDCTT / 1000);
                }
                if (mobTarget != null) {
                    Service.gI().setPos(player, mobTarget.location.x, mobTarget.location.y);
                    playerAttackMob(player, mobTarget, false, false);
                    mobTarget.effectSkill.setStartBlindDCTT(System.currentTimeMillis(), timeChoangDCTT);
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.BLIND_EFFECT);
                }
                player.nPoint.isCrit100 = true;
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.THOI_MIEN:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.THOI_MIEN);
                int timeSleep = SkillUtil.getTimeThoiMien(player.playerSkill.skillSelect.point);
                if (plTarget != null) {
                    EffectSkillService.gI().setThoiMien(plTarget, System.currentTimeMillis(), timeSleep);
                    EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.SLEEP_EFFECT);
                    ItemTimeService.gI().sendItemTime(plTarget, 3782, timeSleep / 1000);
                }
                if (mobTarget != null) {
                    mobTarget.effectSkill.setThoiMien(System.currentTimeMillis(), timeSleep);
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.SLEEP_EFFECT);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.TROI:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.TROI);
                int timeHold = SkillUtil.getTimeTroi(player.playerSkill.skillSelect.point);
                EffectSkillService.gI().setUseTroi(player, System.currentTimeMillis(), timeHold);
                if (plTarget != null && (!plTarget.playerSkill.prepareQCKK && !plTarget.playerSkill.prepareLaze
                        && !plTarget.playerSkill.prepareTuSat)) {
                    player.effectSkill.plAnTroi = plTarget;
                    EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.HOLD_EFFECT);
                    EffectSkillService.gI().setAnTroi(plTarget, player, System.currentTimeMillis(), timeHold);
                }
                if (mobTarget != null) {
                    player.effectSkill.mobAnTroi = mobTarget;
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT,
                            EffectSkillService.HOLD_EFFECT);
                    mobTarget.effectSkill.setTroi(System.currentTimeMillis(), timeHold);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
        }
        if (!player.isBoss) {
            switch (player.playerSkill.skillSelect.template.id) {
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                case Skill.ANTOMIC:
                case Skill.DRAGON:
                case Skill.DEMON:
                case Skill.GALICK:
                case Skill.LIEN_HOAN:
                case Skill.KAIOKEN:
                case Skill.QUA_CAU_KENH_KHI:
                case Skill.MAKANKOSAPPO:
                case Skill.DICH_CHUYEN_TUC_THOI:
                    player.effectSkin.lastTimeAttack = System.currentTimeMillis();
                    break;
            }
            AchievementService.gI().checkDoneTaskUseSkill(player);
            player.doesNotAttack = false;
            player.lastTimePlayerNotAttack = System.currentTimeMillis();
        }
    }

    private void useSkillAlone(Player player) {
        List<Mob> mobs;
        List<Player> players;
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.THAI_DUONG_HA_SAN:
                int timeStun = SkillUtil.getTimeStun(player.playerSkill.skillSelect.point);
                if (player.setClothes.thienXinHang == 5) {
                    timeStun *= 2;
                }
                mobs = new ArrayList<>();
                players = new ArrayList<>();
                if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                    List<Player> playersMap;
                    if (player.isBoss) {
                        playersMap = player.zone.getNotBosses();
                    } else {
                        playersMap = player.zone.getHumanoids();
                    }
                    for (Player pl : playersMap) {
                        if (pl != null && !player.equals(pl) && pl.nPoint != null && !pl.nPoint.khangTDHS) {
                            if (Util.getDistance(player, pl) <= SkillUtil
                                    .getRangeStun(player.playerSkill.skillSelect.point)
                                    && canAttackPlayer(player, pl)) {
                                if (player.isPet && ((Pet) player).master.equals(pl)) {
                                    continue;
                                }
                                String[] text = { "Mắt của ta", "Chói mắt quá", "Đui mắt rồi", "Mù mắt rồi" };
                                Service.gI().chat(pl, text[Util.nextInt(text.length)]);
                                EffectSkillService.gI().startStun(pl, System.currentTimeMillis(), timeStun);
                                players.add(pl);
                            }
                        }
                    }
                }
                if (!player.isBoss) {
                    for (Mob mob : player.zone.mobs) {
                        if (Util.getDistance(player, mob) <= SkillUtil
                                .getRangeStun(player.playerSkill.skillSelect.point)) {
                            mob.effectSkill.startStun(System.currentTimeMillis(), timeStun);
                            mobs.add(mob);
                        }
                    }
                }
                EffectSkillService.gI().sendEffectBlindThaiDuongHaSan(player, players, mobs, timeStun);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.DE_TRUNG:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.DE_TRUNG);
                if (player.mobMe != null) {
                    player.mobMe.mobMeDie();
                    player.mobMe.dispose();
                    player.mobMe = null;
                }
                player.mobMe = new MobMe(player);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.BIEN_KHI:
                EffectSkillService.gI().startUseSkillMonkey(player);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.KHIEN_NANG_LUONG:
                EffectSkillService.gI().setStartShield(player);
                EffectSkillService.gI().sendEffectPlayer(player, player, EffectSkillService.TURN_ON_EFFECT,
                        EffectSkillService.SHIELD_EFFECT);
                ItemTimeService.gI().sendItemTime(player, 3784, player.effectSkill.timeShield / 1000);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.HUYT_SAO:
                int tileHP = SkillUtil.getPercentHPHuytSao(player.playerSkill.skillSelect.point);
                if (player.zone != null) {
                    if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                        if (!player.isBoss) {
                            List<Player> playersMap = player.zone.getHumanoids();
                            for (Player pl : playersMap) {
                                if (pl.effectSkill.useTroi) {
                                    EffectSkillService.gI().removeUseTroi(pl);
                                }
                                if (!pl.isBoss && pl.gender != ConstPlayer.NAMEC
                                        && player.cFlag == pl.cFlag) {
                                    EffectSkillService.gI().setStartHuytSao(pl, tileHP);
                                    EffectSkillService.gI().sendEffectPlayer(pl, pl, EffectSkillService.TURN_ON_EFFECT,
                                            EffectSkillService.HUYT_SAO_EFFECT);
                                    pl.nPoint.calPoint();
                                    pl.nPoint.setHp((long) pl.nPoint.hp + ((long) pl.nPoint.hp * tileHP / 100));
                                    Service.gI().point(pl);
                                    Service.gI().Send_Info_NV(pl);
                                    ItemTimeService.gI().sendItemTime(pl, 3781, 30);
                                    PlayerService.gI().sendInfoHpMp(pl);
                                } else if (!pl.isBoss && pl.gender == ConstPlayer.NAMEC && player.cFlag == pl.cFlag) {
                                    pl.nPoint.setHP(
                                            (long) pl.nPoint.hp - (((long) pl.nPoint.hpMax * 10 / 100) < pl.nPoint.hp
                                                    ? ((long) pl.nPoint.hpMax * 10 / 100)
                                                    : 0));
                                    Service.gI().point(pl);
                                    Service.gI().Send_Info_NV(pl);
                                }
                            }
                        } else {
                            List<Player> playersMap = player.zone.getBosses();
                            for (Player pl : playersMap) {
                                if (pl.effectSkill.useTroi) {
                                    EffectSkillService.gI().removeUseTroi(pl);
                                }
                                EffectSkillService.gI().setStartHuytSao(pl, tileHP);
                                EffectSkillService.gI().sendEffectPlayer(pl, pl, EffectSkillService.TURN_ON_EFFECT,
                                        EffectSkillService.HUYT_SAO_EFFECT);
                                pl.nPoint.calPoint();
                                pl.nPoint.setHp((long) pl.nPoint.hp + ((long) pl.nPoint.hp * tileHP / 100));
                                Service.gI().point(pl);
                                Service.gI().Send_Info_NV(pl);
                                ItemTimeService.gI().sendItemTime(pl, 3781, 30);
                                PlayerService.gI().sendInfoHpMp(pl);
                            }
                        }
                    } else {
                        EffectSkillService.gI().setStartHuytSao(player, tileHP);
                        EffectSkillService.gI().sendEffectPlayer(player, player, EffectSkillService.TURN_ON_EFFECT,
                                EffectSkillService.HUYT_SAO_EFFECT);
                        player.nPoint.calPoint();
                        player.nPoint.setHp((long) player.nPoint.hp + ((long) player.nPoint.hp * tileHP / 100));
                        Service.gI().point(player);
                        Service.gI().Send_Info_NV(player);
                        ItemTimeService.gI().sendItemTime(player, 3781, 30);
                        PlayerService.gI().sendInfoHpMp(player);
                    }
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.TAI_TAO_NANG_LUONG:
                EffectSkillService.gI().startCharge(player);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.BIEN_HINH:
                Skill sBienHinh = getSkillById(player, Skill.BIEN_HINH);

                sBienHinh.coolDown = (int) resolveBienHinhCooldownMillis(player, sBienHinh);

                EffectSkillService.gI().startUseSkillBienHinh(player);

                markBienHinhCooldown(player);
                selectSkill(player, Skill.BIEN_HINH);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                ItemTimeService.gI().sendAllItemTime(player);
                break;
            case Skill.TU_SAT:
                if (!player.playerSkill.prepareTuSat) {
                    player.playerSkill.prepareTuSat = true;
                    player.playerSkill.lastTimePrepareTuSat = System.currentTimeMillis();
                    sendPlayerPrepareBom(player, 2000);
                } else {
                    if (!player.isBoss && !player.isPet
                            && !Util.canDoWithTime(player.playerSkill.lastTimePrepareTuSat, 1500)) {
                        player.playerSkill.skillSelect.lastTimeUseThisSkill = System.currentTimeMillis();
                        player.playerSkill.prepareTuSat = false;
                        return;
                    }
                    if (player.isBoss || player.isPet) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                        }
                    }
                    // nổ
                    player.playerSkill.prepareTuSat = !player.playerSkill.prepareTuSat;
                    int rangeBom = SkillUtil.getRangeBom(player.playerSkill.skillSelect.point);
                    int dame = player.nPoint.hpMax;
                    if (player.setClothes.cadicM == 2) {
                        rangeBom = SkillUtil.getRangeBom(player.playerSkill.skillSelect.point) + 200;
                    }
                    if (player.setClothes.cadicM == 4) {
                        dame += player.nPoint.hpMax * 20 / 100;
                    } else if (player.setClothes.cadicM == 5) {
                        dame += player.nPoint.hpMax * 50 / 100;
                    }
//                     System.out.println(" Nổ HP Gốc: " + player.nPoint.hpMax);
//                     System.out.println(" Nổ Damage: " + dame);
                    Item TrangBi = player.inventory.itemsBody.get(5);
                    if (TrangBi != null) {
                        for (Item.ItemOption io : TrangBi.itemOptions) {
                            if (io.optionTemplate.id == 231) {
                                dame *= 1.2;
                                break;
                            }
                        }
                    }
//                     System.out.println(" Nổ Damage Tăng: " + dame);
                    if (!player.isBoss) {
                        for (Mob mob : player.zone.mobs) {
                            if (Util.getDistance(player, mob) <= rangeBom) { // khoảng cách có tác dụng bom
                                mob.injured(player, dame, true);
                            }
                        }
                    }
                    List<Player> playersMap;
                    if (player.isBoss) {
                        playersMap = player.zone.getNotBosses();
                    } else {
                        playersMap = player.zone.getHumanoids();
                    }
                    if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                        for (Player pl : playersMap) {
                            if (!player.equals(pl) && canAttackPlayer(player, pl)
                                    && Util.getDistance(player, pl) <= rangeBom) {
                                dame = pl.isBoss ? player.effectSkill.isMonkey ? dame / 3 : dame / 2 : dame;
                                pl.injured(player, dame, MapService.gI().isMapYardart(player.zone.map.mapId), false);
                                PlayerService.gI().sendInfoHpMpMoney(pl);
                                Service.gI().Send_Info_NV(pl);
                            }
                        }
                    }
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                    if (!player.isBoss && !player.isPet) {
                        player.setDie();
                    }
                    if (player.effectSkill.tiLeHPHuytSao != 0) {
                        player.effectSkill.tiLeHPHuytSao = 0;
                        EffectSkillService.gI().removeHuytSao(player);
                    }
                }
                break;
        }
    }

    private void useSkillBuffToPlayer(Player player, Player plTarget) {
        Message msg = null;
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.TRI_THUONG -> {
                List<Player> players = new ArrayList();
                int percentTriThuong = SkillUtil.getPercentTriThuong(player.playerSkill.skillSelect.point);
                int point = player.playerSkill.skillSelect.point;
                if (canHsPlayer(player, plTarget)) {
                    players.add(plTarget);
                    List<Player> playersMap = player.zone.getNotBosses();
                    for (Player pl : playersMap) {
                        if (!pl.equals(plTarget) && point > 1) {
                            if (canHsPlayer(player, plTarget) && Util.getDistance(player, pl) <= 300) {
                                players.add(pl);
                            }
                        }
                    }
                    for (Player pl : players) {
                        try {
                            msg = new Message(-60);
                            msg.writer().writeInt((int) player.id); // id pem
                            msg.writer().writeByte(player.playerSkill.skillSelect.skillId); // skill pem
                            msg.writer().writeByte(1); // số người pem
                            msg.writer().writeInt((int) pl.id); // id ăn pem
                            msg.writer().writeByte(0); // read continue
                            Service.gI().sendMessAllPlayerInMap(pl, msg);
                            boolean isDie = pl.isDie();
                            player.nPoint.setHP(player.nPoint.getHP() + (player.nPoint.hpMax * percentTriThuong / 100));
                            pl.nPoint.setHP(pl.nPoint.getHP() + (pl.nPoint.hpMax * percentTriThuong / 100));
                            pl.nPoint.setMP(pl.nPoint.getMP() + (pl.nPoint.mpMax * percentTriThuong / 100));
                            if (isDie) {
                                AchievementService.gI().checkDoneTask(pl, ConstAchievement.CHAM_SOC_DAC_BIET);
                                Service.gI().chat(pl, "Cảm ơn " + player.name + " đã hồi sinh mình");
                                Service.gI().Send_Info_NV(player);
                                Service.gI().hsChar(pl, pl.nPoint.getHP(), pl.nPoint.getMP());
                                PlayerService.gI().sendInfoHpMpMoney(pl);
                                PlayerService.gI().sendInfoHpMp(player);
                            } else {
                                Service.gI().chat(pl, "Cảm ơn " + player.name + " đã cứu mình");
                                Service.gI().Send_Info_NV(player);
                                PlayerService.gI().sendInfoHpMp(pl);
                                PlayerService.gI().sendInfoHpMp(player);
                            }
                            Service.gI().Send_Info_NV(pl);
                        } catch (Exception e) {
                        } finally {
                            if (msg != null) {
                                msg.cleanup();
                            }
                        }
                    }
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
            }
        }
    }

    private void phanSatThuong(Player plAtt, Player plTarget, long dame) {
        if (plAtt != null) {
            int percentPST = plTarget.nPoint.tlPST;
            if (percentPST != 0) {
                int damePST = (int) (long) (dame * percentPST / 100L);
                Message msg = null;
                try {
                    msg = new Message(56);
                    msg.writer().writeInt((int) plAtt.id);
                    if (damePST >= plAtt.nPoint.hp) {
                        damePST = plAtt.nPoint.hp - 1;
                    }
                    if (plAtt.isBoss && !(plAtt instanceof Broly || plAtt instanceof SuperBroly)) {
                        if (damePST > plAtt.nPoint.hpMax / 100) {
                            int giamdame = 0;
                            if (plAtt.nPoint.hpMax / 200 > 1) {
                                giamdame = Util.nextInt(plAtt.nPoint.hpMax / 200);
                            }
                            damePST = plAtt.nPoint.hpMax / 100 - giamdame;
                        }
                    }
                    damePST = plAtt.injured(plAtt, damePST, true, false);
                    msg.writer().writeInt(plAtt.nPoint.hp);
                    msg.writer().writeInt(damePST);
                    msg.writer().writeBoolean(false);
                    msg.writer().writeByte(36);
                    Service.gI().sendMessAllPlayerInMap(plAtt, msg);
                } catch (Exception e) {
                    Logger.logException(SkillService.class, e);
                } finally {
                    if (msg != null) {
                        msg.cleanup();
                    }
                }
            }
        }
    }

    private void hutHPMP(Player player, long dame, Player pl, Mob mob) {
        int tiLeHutHp = player.nPoint.getTileHutHp(mob != null);
        int tiLeHutMp = player.nPoint.getTiLeHutMp();
        int hpHoi = (int) ((long) dame * tiLeHutHp / 100);
        int mpHoi = (int) ((long) dame * tiLeHutMp / 100);
        if (hpHoi > 0 || mpHoi > 0) {
            int x = -1;
            int y = -1;
            if (pl != null) {
                x = pl.location.x;
                y = pl.location.y;
            } else if (mob != null) {
                x = mob.location.x;
                y = mob.location.y;
            }
            EffectMapService.gI().sendEffectMapToAllInMap(player, 37, 3, 1, x, y, -1);
            PlayerService.gI().hoiPhuc(player, hpHoi, mpHoi);
        }
    }

    private void playerAttackPlayer(Player plAtt, Player plInjure, boolean miss) {
        if (plInjure.effectSkill.anTroi) {
            plAtt.nPoint.isCrit100 = true;
        }
        long dameAttack = plAtt.nPoint.getDameAttack(false);
        if (plAtt.isPl() && plAtt.effectSkin != null && plAtt.effectSkin.isXDame) {
            plAtt.effectSkin.isXDame = false;
            if (plInjure.isBoss) {
                dameAttack /= 3;
            }
        }
        int dameHit = plInjure.injured(plAtt, miss ? 0 : dameAttack, false, false);
        if (plAtt.playerSkill == null) {
            return;
        }
        Skill skillSelect = plAtt.playerSkill.skillSelect;
        if (plAtt.isPl() && dameHit >= 250_000_000) {
            ServerNotify.gI().notify(plAtt.name + ": đã đánh 1 chiêu " + skillSelect.template.name
                    + " với sát thương là " + Util.chiaNho(dameHit));
        }
        int damePST = plInjure.effectSkill != null && plInjure.effectSkill.isShielding && plInjure.iDMark != null
                ? plInjure.iDMark.getDamePST()
                : dameHit;
        phanSatThuong(plAtt, plInjure, miss ? 0 : damePST);
        hutHPMP(plAtt, dameHit, plInjure, null);
        if (plInjure instanceof Yardart) { // Fix lỗi máu trắng boss Yardart
            if (plInjure.nPoint.hp < dameHit) {
                dameHit = plInjure.nPoint.hp - 1;
                if (dameHit == 0) {
                    return;
                }
            } else if (plInjure.nPoint.hp <= plInjure.nPoint.hpMax / 10) {
                return;
            }
        }
        Message msg = null;
        try {
            msg = new Message(-60);
            msg.writer().writeInt((int) plAtt.id); // id pem
            msg.writer().writeByte(plAtt.playerSkill.skillSelect.skillId); // skill pem
            msg.writer().writeByte(1); // số người pem
            msg.writer().writeInt((int) plInjure.id); // id ăn pem
            msg.writer().writeByte(1); // read continue
            msg.writer().writeByte(0); // type skill
            msg.writer().writeInt(dameHit); // dame ăn
            msg.writer().writeBoolean(plInjure.isDie()); // is die
            msg.writer().writeBoolean(plAtt.nPoint.isCrit); // crit
            Service.gI().sendMessAllPlayerInMap(plAtt, msg);
            Service.gI().reload_HP_NV(plInjure);
            if (plAtt.isPl() && plInjure.isPl() && plAtt.typePk == ConstPlayer.PK_PVP_2
                    && plInjure.typePk == ConstPlayer.PK_PVP_2) {
                long tnsm = plAtt.nPoint.calSucManhTiemNang(dameHit / 10)
                        / (Math.abs(Service.gI().getCurrLevel(plAtt) - Service.gI().getCurrLevel(plInjure)) + 1);
                Service.gI().addSMTN(plInjure, (byte) 2, tnsm, false);
            }
            if (plInjure.isDie() && !plAtt.isBoss && !plInjure.isBoss
                    && MapService.gI().isMapMaBu(plInjure.zone.map.mapId)) {
                plAtt.fightMabu.changePoint((byte) 5);
            }
        } catch (Exception e) {
            Logger.logException(SkillService.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void playerAttackMob(Player plAtt, Mob mob, boolean miss, boolean dieWhenHpFull) {
        if (!mob.isDie() && plAtt != null && plAtt.nPoint != null && plAtt.playerSkill != null) {
            long dameHit = plAtt.nPoint.getDameAttack(true);
            if (plAtt.isPl() && plAtt.effectSkin != null && plAtt.effectSkin.isXDame) {
                plAtt.effectSkin.isXDame = false;
            }
            if ((plAtt.charms != null && plAtt.charms.tdBatTu > System.currentTimeMillis()
                    || plAtt.effectSkill != null && plAtt.effectSkill.isHalloween) && plAtt.nPoint.hp <= 1) {
                if (plAtt.nPoint.hp < 1) {
                    plAtt.nPoint.hp = 1;
                }
                if (!plAtt.isPet) {
                    dameHit = 0;
                    Service.gI().sendThongBao(plAtt, "Bạn đang được bùa bất tử bảo vệ không thể tấn công!");
                }
            }
            if (plAtt.charms != null && plAtt.charms.tdManhMe > System.currentTimeMillis()) {
                dameHit += (dameHit * 150 / 100);
            }
            if (plAtt.isPet) {
                if (((Pet) plAtt).master != null && ((Pet) plAtt).master.charms != null
                        && ((Pet) plAtt).master.charms.tdDeTu > System.currentTimeMillis()) {
                    dameHit *= 2;
                }
            }
            if (miss) {
                dameHit = 0;
            }
            if (dameHit > 2_000_000_000) {
                dameHit = 2_000_000_000;
            }
            hutHPMP(plAtt, dameHit, null, mob);
            sendPlayerAttackMob(plAtt, mob);
            mob.injured(plAtt, dameHit, dieWhenHpFull);
        }
    }

    private void sendPlayerPrepareSkill(Player player, int affterMiliseconds) {
        Message msg = null;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(4);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.playerSkill.skillSelect.skillId);
            msg.writer().writeShort(affterMiliseconds);
            Service.gI().sendMessAllPlayerInMap(player, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    

    public void sendPlayerPrepareBom(Player player, int affterMiliseconds) {
        Message msg = null;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(7);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.playerSkill.skillSelect.skillId);
            msg.writer().writeShort(affterMiliseconds);
            Service.gI().sendMessAllPlayerInMap(player, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public boolean canUseSkillWithMana(Player player) {
        if (player.playerSkill.skillSelect != null) {
            if (player.playerSkill.skillSelect.template.id == Skill.KAIOKEN) {
                long hpUse = player.nPoint.hpMax / 100 * 10;
                if (player.isBoss && player instanceof Rival) {
                    hpUse = 0;
                }
                if (player.nPoint.hp <= hpUse) {
                    return false;
                }
            }
            switch (player.playerSkill.skillSelect.template.manaUseType) {
                case 0 -> {
                    return player.nPoint.mp >= player.playerSkill.skillSelect.manaUse;
                }
                case 1 -> {
                    int mpUse = (int) (player.nPoint.mpMax * player.playerSkill.skillSelect.manaUse / 100);
                    return player.nPoint.mp >= mpUse;
                }
                case 2 -> {
                    return player.nPoint.mp > 0;
                }
                default -> {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public boolean canUseSkillWithCooldown(Player player) {
        return Util.canDoWithTime(player.playerSkill.skillSelect.lastTimeUseThisSkill,
                player.playerSkill.skillSelect.coolDown - 50);
    }

    public void affterUseSkill(Player player, int skillId) {
        
        if (player.playerSkill != null &&
       (player.playerSkill.skillSelect == null || player.playerSkill.skillSelect.template.id != skillId)) {
       selectSkill(player, skillId);
   }
    // Nội tại giữ nguyên
    Intrinsic intrinsic = player.playerIntrinsic.intrinsic;
    switch (skillId) {
        case Skill.DICH_CHUYEN_TUC_THOI -> {
            if (intrinsic.id == 6) player.nPoint.dameAfter = intrinsic.param1;
        }
        case Skill.THOI_MIEN -> {
            if (intrinsic.id == 7) player.nPoint.dameAfter = intrinsic.param1;
        }
        case Skill.SOCOLA -> {
            if (intrinsic.id == 14) player.nPoint.dameAfter = intrinsic.param1;
        }
        case Skill.TROI -> {
            if (intrinsic.id == 22) player.nPoint.dameAfter = intrinsic.param1;
        }
    }

    // --- Bổ sung: ép cooldown cho BIẾN HÌNH trước khi ghi lastTime ---
    Skill s = getSkillById(player, skillId);
    if (s != null && s.template != null) {
        if (s.template.id == Skill.BIEN_HINH) {
            int lvl = resolveBienHinhLevel(player);
            int cdMs = (int) Math.max(SkillUtil.getTimeMonkey(lvl), 1000L); // fallback >= 1s
            s.coolDown = cdMs; // để client có con số vẽ thanh hồi chiêu
            if (player.playerSkill != null && player.playerSkill.skillSelect != null
                   && player.playerSkill.skillSelect.template != null
                   && player.playerSkill.skillSelect.template.id == Skill.BIEN_HINH) {
               player.playerSkill.skillSelect.coolDown = cdMs;
           }
        }
    }

    // MP & mốc dùng (ghi lastTimeUseThisSkill)
    setMpAffterUseSkill(player);
    setLastTimeUseSkill(player, skillId);

    if (player.playerSkill != null && player.playerSkill.skillSelect != null) {
       Service.gI().sendTimeSkill(player, player.playerSkill.skillSelect);
   } else if (s != null) {
       Service.gI().sendTimeSkill(player, s);
   }
   }
/* ====== Helpers (thêm vào SkillService nếu chưa có) ====== */
private Skill getSkillById(Player p, int id) {
    if (p == null || p.playerSkill == null || p.playerSkill.skills == null) return null;
    for (Skill sk : p.playerSkill.skills) {
        if (sk != null && sk.template != null && sk.template.id == id) return sk;
    }
    return null;
}

private int resolveBienHinhLevel(Player p) {
    // Ưu tiên level đang lưu trong effect, nếu không có thì lấy từ skill point
    try {
        if (p != null && p.effectSkill != null && p.effectSkill.levelBienHinh > 0) {
            return p.effectSkill.levelBienHinh;
        }
    } catch (Exception ignore) {}
    Skill s = getSkillById(p, Skill.BIEN_HINH);
    return (s != null && s.point > 0) ? s.point : 1;
}

    private void setMpAffterUseSkill(Player player) {
        if (player.playerSkill.skillSelect != null) {
            switch (player.playerSkill.skillSelect.template.manaUseType) {
                case 0 -> {
                    if (player.nPoint.mp >= player.playerSkill.skillSelect.manaUse) {
                        player.nPoint.setMp(player.nPoint.mp - player.playerSkill.skillSelect.manaUse);
                    }
                }
                case 1 -> {
                    int mpUse = (int) (player.nPoint.mpMax * player.playerSkill.skillSelect.manaUse / 100);
                    if (player.nPoint.mp >= mpUse) {
                        player.nPoint.setMp(player.nPoint.mp - mpUse);
                    }
                }
                case 2 ->
                    player.nPoint.setMp(0);
            }
            PlayerService.gI().sendInfoHpMpMoney(player);
        }
    }

    private void setLastTimeUseSkill(Player player, int skillId) {
        Intrinsic intrinsic = player.playerIntrinsic.intrinsic;
        int subTimeParam = 0;
        int subTimeParamVip = 0;
        switch (skillId) {
            case Skill.TRI_THUONG -> {
                if (intrinsic.id == 10) {
                    subTimeParam = intrinsic.param1;
                }
            }
            case Skill.THAI_DUONG_HA_SAN -> {
                if (intrinsic.id == 3) {
                    subTimeParam = intrinsic.param1;
                }
            }
            case Skill.QUA_CAU_KENH_KHI -> {
                if (intrinsic.id == 4) {
                    subTimeParam = intrinsic.param1;
                }
            }
            case Skill.KHIEN_NANG_LUONG -> {
                if (intrinsic.id == 5 || intrinsic.id == 15 || intrinsic.id == 20) {
                    subTimeParam = intrinsic.param1;
                }
            }
            case Skill.MAKANKOSAPPO -> {
                if (intrinsic.id == 11) {
                    subTimeParam = intrinsic.param1;
                }
            }
            case Skill.DE_TRUNG -> {
                if (intrinsic.id == 12) {
                    subTimeParam = intrinsic.param1;
                }
            }
            case Skill.TU_SAT -> {
                if (intrinsic.id == 19) {
                    subTimeParam = intrinsic.param1;
                }
            }
            case Skill.HUYT_SAO -> {
                if (intrinsic.id == 21) {
                    subTimeParam = intrinsic.param1;
                }
            }
            case Skill.MASENKO -> {
                if (player.setClothes.nail == 4) {
                    subTimeParam = 20;
                } else if (player.setClothes.nail == 5) {
                    subTimeParam = 50;
                }
            }
        }
        long now = System.currentTimeMillis() - 1;
        // luôn set vào skill đang select (giữ behavior cũ)
        player.playerSkill.skillSelect.lastTimeUseThisSkill = now;
        // và set thêm vào đúng skill theo skillId (an toàn nếu select khác)
        Skill used = getSkillById(player, skillId);
        if (used != null) used.lastTimeUseThisSkill = now;
        int coolDown = player.playerSkill.skillSelect.coolDown;
        long lastTimeUseSkill = System.currentTimeMillis() - (coolDown * (subTimeParam + subTimeParamVip) / 100);
        if (subTimeParam != 0) {
            EffectSkillService.gI().setIntrinsic(player, skillId, coolDown, lastTimeUseSkill);
        }
        if (subTimeParamVip != 0) {
            EffectSkillService.gI().setIntrinsicVip(player, skillId, coolDown, lastTimeUseSkill);
        }
    }
    
    private boolean isBienHinhActive(Player p) {
    return p != null && p.effectSkill != null && p.effectSkill.isBienHinhBroly;
}

    private boolean canHsPlayer(Player player, Player plTarget) {
        if (plTarget == null) {
            return false;
        }
        if (plTarget.isBoss) {
            return false;
        }
        if (plTarget.typePk == ConstPlayer.PK_ALL) {
            return false;
        }
        if (plTarget.typePk == ConstPlayer.PK_PVP) {
            return false;
        }
        if (player.cFlag != 0) {
            if (plTarget.cFlag != 0 && plTarget.cFlag != player.cFlag) {
                return false;
            }
        } else if (plTarget.cFlag != 0) {
            return false;
        }
        return true;
    }

    public boolean canAttackPlayer(Player p1, Player p2) {
        if (p1.isDie() || p2.isDie()) {
            return false;
        }

        return canAttackPlayer2(p1, p2);
    }

    public boolean canAttackPlayer2(Player p1, Player p2) {

        if (p1.isNewPet || p2.isNewPet || (p1 instanceof NonInteractiveNPC) || (p2 instanceof NonInteractiveNPC)) {
            return false;
        }

        if (p1.typePk == ConstPlayer.PK_ALL || p2.typePk == ConstPlayer.PK_ALL) {
            return true;
        }
        if (p1.isPl() && p2.isPl() && (p1.iDMark != null && p1.iDMark.getKillCharId() == p2.id
                || p2.iDMark != null && p2.iDMark.getKillCharId() == p1.id)) {
            return true;
        }
        if ((p1.cFlag != 0 && p2.cFlag != 0)
                && (p1.cFlag == 8 || p2.cFlag == 8 || p1.cFlag != p2.cFlag)) {
            return true;
        }
        if (p1.pvp == null || p2.pvp == null) {
            return false;
        }
        return p1.pvp.isInPVP(p2) || p2.pvp.isInPVP(p1);
    }

    private void sendPlayerAttackMob(Player plAtt, Mob mob) {
        Message msg = null;
        try {
            msg = new Message(54);
            msg.writer().writeInt((int) plAtt.id);
            msg.writer().writeByte(plAtt.playerSkill.skillSelect.skillId);
            msg.writer().writeByte(mob.id);
            Service.gI().sendMessAllPlayerInMap(plAtt, msg);
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void selectSkill(Player player, int id) {
    if (player == null || player.playerSkill == null || player.playerSkill.skills == null) return;
    Skill found = null;
    // 1) Ưu tiên xem 'id' là template.id
    for (Skill s : player.playerSkill.skills) {
        if (s != null && s.skillId != -1 && s.template != null && s.template.id == id) {
            found = s; break;
        }
    }
    // 2) Nếu chưa thấy, xem 'id' là skillId (client gửi short ở case 34)
    if (found == null) {
        for (Skill s : player.playerSkill.skills) {
            if (s != null && s.skillId == id && s.skillId != -1) {
                found = s; break;
            }
        }
    }
    if (found != null) {
        player.playerSkill.skillSelect = found;
    }
}
    

/** Ép cooldown BIẾN_HÌNH giống Hóa Khỉ nếu template để 0 */
private long resolveBienHinhCooldownMillis(Player p, Skill sBienHinh) {
    long cd = (sBienHinh != null && sBienHinh.coolDown > 0)
            ? sBienHinh.coolDown
            : SkillUtil.getTimeMonkey(resolveBienHinhLevel(p));
    return Math.max(cd, 1000L);
}

private boolean canUseBienHinhWithCooldown(Player p, Skill sBienHinh) {
    long cd = resolveBienHinhCooldownMillis(p, sBienHinh);
    return Util.canDoWithTime(sBienHinh.lastTimeUseThisSkill, cd);
}

private void markBienHinhCooldown(Player p) {
    Skill s = getSkillById(p, Skill.BIEN_HINH);
    if (s != null) s.lastTimeUseThisSkill = System.currentTimeMillis();
}

    
}
