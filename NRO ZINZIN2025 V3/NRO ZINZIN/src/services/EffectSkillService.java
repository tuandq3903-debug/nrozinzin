package services;

/*
 *
 *
 * @author ZINZIN
 */

import models.mob.Mob;
import models.player.Player;
import models.skill.Skill;
import network.Message;
import consts.ConstPlayer;
import utils.SkillUtil;
import java.util.List;
import models.item.Item;
import utils.Util;
import models.map.MaBuHold;

public class EffectSkillService {

    public static final byte TURN_ON_EFFECT = 1;
    public static final byte TURN_OFF_EFFECT = 0;
    public static final byte TURN_OFF_ALL_EFFECT = 2;

    public static final byte HOLD_EFFECT = 32;
    public static final byte SHIELD_EFFECT = 33;
    public static final byte HUYT_SAO_EFFECT = 39;
    public static final byte BLIND_EFFECT = 40;
    public static final byte SLEEP_EFFECT = 41;
    public static final byte STONE_EFFECT = 42;

    private static EffectSkillService instance;

    private EffectSkillService() {

    }

    public static EffectSkillService gI() {
        if (instance == null) {
            instance = new EffectSkillService();
        }
        return instance;
    }

    //hiệu ứng player dùng skill
    public void sendEffectUseSkill(Player player, byte skillId) {
        Skill skill = SkillUtil.getSkillbyId(player, skillId);
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(8);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skill.skillId);
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    public void sendEffectPlayer(Player plUseSkill, Player plTarget, byte toggle, byte effect) {
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(toggle); //0: hủy hiệu ứng, 1: bắt đầu hiệu ứng
            msg.writer().writeByte(0); //0: vào phần phayer, 1: vào phần mob
            if (toggle == TURN_OFF_ALL_EFFECT) {
                msg.writer().writeInt((int) plTarget.id);
            } else {
                msg.writer().writeByte(effect); //loại hiệu ứng
                msg.writer().writeInt((int) plTarget.id); //id player dính effect
                msg.writer().writeInt((int) plUseSkill.id); //id player dùng skill
            }
            Service.gI().sendMessAllPlayerInMap(plUseSkill, msg);
            msg.cleanup();
        } catch (Exception e) {
            utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    public void sendEffectMob(Player plUseSkill, Mob mobTarget, byte toggle, byte effect) {
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(toggle); //0: hủy hiệu ứng, 1: bắt đầu hiệu ứng
            msg.writer().writeByte(1); //0: vào phần phayer, 1: vào phần mob
            msg.writer().writeByte(effect); //loại hiệu ứng
            msg.writer().writeByte(mobTarget.id); //id mob dính effect
            msg.writer().writeInt((int) plUseSkill.id); //id player dùng skill
            Service.gI().sendMessAllPlayerInMap(mobTarget.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    //Trói *********************************************************************
    //dừng sử dụng trói
    public void removeUseTroi(Player player) {
        if (player.effectSkill.mobAnTroi != null) {
            player.effectSkill.mobAnTroi.effectSkill.removeAnTroi();
        }
        if (player.effectSkill.plAnTroi != null) {
            removeAnTroi(player.effectSkill.plAnTroi);
        }
        player.effectSkill.useTroi = false;
        player.effectSkill.mobAnTroi = null;
        player.effectSkill.plAnTroi = null;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, HOLD_EFFECT);
    }

    //hết thời gian bị trói
    public void removeAnTroi(Player player) {
        if (player != null && player.effectSkill != null) {
            player.effectSkill.anTroi = false;
            player.effectSkill.plTroi = null;
            sendEffectPlayer(player, player, TURN_OFF_EFFECT, HOLD_EFFECT);
        }
    }

    public void setAnTroi(Player player, Player plTroi, long lastTimeAnTroi, int timeAnTroi) {
        player.effectSkill.anTroi = true;
        player.effectSkill.plTroi = plTroi;
    }

    public void setUseTroi(Player player, long lastTimeTroi, int timeTroi) {
        player.effectSkill.useTroi = true;
        player.effectSkill.lastTimeTroi = lastTimeTroi;
        player.effectSkill.timeTroi = timeTroi;
    }
    //**************************************************************************

    //Thôi miên ****************************************************************
    //thiết lập thời gian bắt đầu bị thôi miên
    public void setThoiMien(Player player, long lastTimeThoiMien, int timeThoiMien) {
        player.effectSkill.isThoiMien = true;
        player.effectSkill.lastTimeThoiMien = lastTimeThoiMien;
        player.effectSkill.timeThoiMien = timeThoiMien;
    }

    //hết hiệu ứng thôi miên
    public void removeThoiMien(Player player) {
        player.effectSkill.isThoiMien = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, SLEEP_EFFECT);
    }

    //**************************************************************************
    //Thái dương hạ san &&&&****************************************************
    //player ăn choáng thái dương hạ san
    public void startStun(Player player, long lastTimeStartBlind, int timeBlind) {
        player.effectSkill.lastTimeStartStun = lastTimeStartBlind;
        player.effectSkill.timeStun = timeBlind;
        player.effectSkill.isStun = true;
        sendEffectPlayer(player, player, TURN_ON_EFFECT, BLIND_EFFECT);
    }

    //kết thúc choáng thái dương hạ san
    public void removeStun(Player player) {
        player.effectSkill.isStun = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, BLIND_EFFECT);
    }
    //**************************************************************************

    //Socola *******************************************************************
    //player biến thành socola
    public void setSocola(Player player, long lastTimeSocola, int timeSocola) {
        player.effectSkill.lastTimeSocola = lastTimeSocola;
        player.effectSkill.timeSocola = timeSocola;
        player.effectSkill.isSocola = true;
        player.effectSkill.countPem1hp = 0;
    }

    //player trở lại thành người
    public void removeSocola(Player player) {
        player.effectSkill.isSocola = false;
        Service.gI().Send_Caitrang(player);
    }

    //quái biến thành socola
    public void sendMobToSocola(Player player, Mob mob, int timeSocola) {
        Message msg;
        try {
            msg = new Message(-112);
            msg.writer().writeByte(1);
            msg.writer().writeByte(mob.id); //mob id
            msg.writer().writeShort(4133); //icon socola
            Service.gI().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
            mob.effectSkill.setSocola(System.currentTimeMillis(), timeSocola);
        } catch (Exception e) {
            utils.Logger.logException(EffectSkillService.class, e);
        }
    }
    //**************************************************************************

    //Dịch chuyển tức thời *****************************************************
    public void setBlindDCTT(Player player, long lastTimeDCTT, int timeBlindDCTT) {
        player.effectSkill.isBlindDCTT = true;
        player.effectSkill.lastTimeBlindDCTT = lastTimeDCTT;
        player.effectSkill.timeBlindDCTT = timeBlindDCTT;
    }

    public void removeBlindDCTT(Player player) {
        player.effectSkill.isBlindDCTT = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, BLIND_EFFECT);
    }
    //**************************************************************************

    //Huýt sáo *****************************************************************
    //Hưởng huýt sáo
    public void setStartHuytSao(Player player, int tiLeHP) {
        player.effectSkill.tiLeHPHuytSao = tiLeHP;
        player.effectSkill.lastTimeHuytSao = System.currentTimeMillis();
    }

    //Hết hiệu ứng huýt sáo
    public void removeHuytSao(Player player) {
        player.effectSkill.tiLeHPHuytSao = 0;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, HUYT_SAO_EFFECT);
        Service.gI().point(player);
        Service.gI().Send_Info_NV(player);
    }

    //**************************************************************************
    //Biến khỉ *****************************************************************
    //Bắt đầu biến khỉ
    public void startUseSkillMonkey(Player player) {
    sendEffectMonkey(player);
    if (player.isBoss) {
        setIsMonkey(player);
        return;
    }
    Service.gI().sendSpeedPlayer(player, 0);
    player.effectSkill.isUseSkillMonkey = true;
    player.effectSkill.timeUseSkillMonkey = 1500;
    player.effectSkill.lastTimeUseSkillMonkey = System.currentTimeMillis();
}

    
    public void startUseSkillBienHinh(Player player) {
    if (player == null || player.effectSkill == null || player.playerSkill == null) return;

    // 1) Kiểm tra CT Broly SSJ4 (id 1775) đang mặc
    Item ct = null;
    try {
        if (player.inventory != null && player.inventory.itemsBody != null && player.inventory.itemsBody.size() > 5) {
            ct = player.inventory.itemsBody.get(5);
        }
    } catch (Exception ignore) {}

    if (ct == null || !ct.isNotNullItem() || ct.template == null || ct.template.id != 1775) {
        Service.gI().sendThongBao(player, "Cần mặc CT Broly SSJ4 để dùng Biến Hình");
        return;
    }

    // 2) Nếu ĐÃ đang biến -> KHÔNG toggle-off, bỏ qua lần bấm (giống skill thường)
    if (player.effectSkill.isBienHinhBroly) {
        // Service.gI().sendThongBao(player, "Đang duy trì, không thể dùng lại");
        return;
    }

    // 3) Tìm skill BIẾN_HÌNH của player
    Skill sBienHinh = null;
    if (player.playerSkill.skills != null) {
        for (Skill s : player.playerSkill.skills) {
            if (s != null && s.template != null && s.template.id == Skill.BIEN_HINH) {
                sBienHinh = s;
                break;
            }
        }
    }
    if (sBienHinh == null) return;

    // 4) Tính cooldown giống Hóa Khỉ nếu template để 0
    int level = 1;
    try {
        if (player.effectSkill.levelBienHinh > 0) level = player.effectSkill.levelBienHinh;
    } catch (Exception ignore) {}
    long cd = (sBienHinh.coolDown > 0) ? sBienHinh.coolDown : SkillUtil.getTimeMonkey(level);
    if (cd <= 0) cd = 1000; // fallback an toàn (1s)

    // 5) Nếu CHƯA hồi chiêu -> không cho dùng (y hệt skill khác)
    if (!Util.canDoWithTime(sBienHinh.lastTimeUseThisSkill, cd)) {
        return;
    }

    // 6) BẬT Biến Hình
    setIsBienHinhBroly(player);

    // 7) Ghi mốc hồi chiêu NGAY lúc bật
    sBienHinh.lastTimeUseThisSkill = System.currentTimeMillis();

    // 8) (tuỳ UI) ép cooldown để client vẽ đồng hồ nếu template=0
    try { sBienHinh.coolDown = (int) cd; } catch (Exception ignore) {}

    // Lưu ý: gói đẩy thanh cooldown thường được gọi ở SkillService.affterUseSkill(...)
    // Nếu ở source của bạn affterUseSkill(...) đã được gọi sau khi startUseSkillBienHinh(),
    // thì UI sẽ hiển thị đúng thanh hồi chiêu như skill khác.
}
    
    public void finishUseMonkey(Player player) {
        if (player.effectSkill != null) {
            player.effectSkill.isUseSkillMonkey = false;
            Service.gI().sendSpeedPlayer(player, -1);
            if (!player.isDie()) {
                setIsMonkey(player);
            }
        }
    }

    public void setIsMonkey(Player player) {
        EffectSkillService.gI().sendEffectMonkey(player);
        int timeMonkey = SkillUtil.getTimeMonkey(player.playerSkill.skillSelect.point);
//        int timeMonkey = SkillUtil.getTimeMonkey(resolveMonkeyLevel(player));
        player.effectSkill.levelMonkey = (byte) resolveMonkeyLevel(player);

        if (player.setClothes.cadic == 5) {
            timeMonkey *= 5;
        }
        player.effectSkill.isMonkey = true;
        player.effectSkill.timeMonkey = timeMonkey;
        player.effectSkill.lastTimeUpMonkey = System.currentTimeMillis();
        player.effectSkill.levelMonkey = (byte) player.playerSkill.skillSelect.point;
        player.nPoint.setHp(((long) player.nPoint.hp * 2));
        Service.gI().Send_Caitrang(player);
        if (!player.isPet) {
            PlayerService.gI().sendInfoHpMp(player);
        }
        Service.gI().point(player);
        Service.gI().Send_Info_NV(player);
        Service.gI().sendInfoPlayerEatPea(player);
    }

    public void monkeyDown(Player player) {
        player.effectSkill.isMonkey = false;
        player.effectSkill.levelMonkey = 0;
        if (player.nPoint.hp > player.nPoint.hpMax) {
            player.nPoint.setHp(player.nPoint.hpMax);
        }
        sendEffectEndCharge(player);
        sendEffectMonkey(player);
        Service.gI().setNotMonkey(player);
        Service.gI().Send_Caitrang(player);
        Service.gI().point(player);
        PlayerService.gI().sendInfoHpMp(player);
        Service.gI().Send_Info_NV(player);
        Service.gI().sendInfoPlayerEatPea(player);
    }

    public void setIntrinsic(Player player, int skillId, int cooldown, long lastTimeUseSkill) {
        player.effectSkill.isIntrinsic = true;
        player.effectSkill.skillID = skillId;
        player.effectSkill.cooldown = cooldown;
        player.effectSkill.lastTimeUseSkill = lastTimeUseSkill;
    }

    public void releaseCooldownSkillByIntrinsic(Player player) {
        player.effectSkill.isIntrinsic = false;
        Skill skill = SkillUtil.getSkillbyId(player, player.effectSkill.skillID);
        Service.gI().releaseCooldownSkill(player, skill);
    }

    public void setIntrinsicVip(Player player, int skillId, int cooldown, long lastTimeUseSkill) {
        player.effectSkill.isIntrinsicVip = true;
        player.effectSkill.skillIDVip = skillId;
        player.effectSkill.cooldownVip = cooldown;
        player.effectSkill.lastTimeUseSkillVip = lastTimeUseSkill;
    }

    public void releaseCooldownSkillByIntrinsicVip(Player player) {
        player.effectSkill.isIntrinsicVip = false;
        Skill skill = SkillUtil.getSkillbyId(player, player.effectSkill.skillIDVip);
        Service.gI().releaseCooldownSkill(player, skill);
    }

    public void setIsBinh(Player plAtt, Player player, int time) {
        if (player.effectSkill != null) {
            int typeBinh = plAtt.newSkill.typeItem;
            player.effectSkill.isBinh = true;
            player.effectSkill.typeBinh = typeBinh;
            player.effectSkill.timeBinh = time;
            player.effectSkill.playerUseMafuba = plAtt;
            player.effectSkill.lastTimeUpBinh = System.currentTimeMillis();
            ItemTimeService.gI().sendItemTime(player, typeBinh == 0 ? 11175 : 11166, time / 1000);
            Service.gI().Send_Caitrang(player);
        }
    }

    public void BinhDown(Player player) {
        if (player.effectSkill != null) {
            player.effectSkill.isBinh = false;
            Service.gI().Send_Caitrang(player);
        }
    }

    public void setIsHalloween(Player player, int outFit, int time) {
        if (player.effectSkill != null) {
            player.effectSkill.isHalloween = true;
            player.effectSkill.idOutfitHalloween = outFit != -1 ? outFit : Util.nextInt(5);
            player.effectSkill.timeHalloween = time;
            player.effectSkill.lastTimeHalloween = System.currentTimeMillis();
            ItemTimeService.gI().sendItemTime(player, 5101, time / 1000);
            Service.gI().Send_Caitrang(player);
        }
    }

    public void removeHalloween(Player player) {
        if (player.effectSkill != null) {
            player.effectSkill.isHalloween = false;
            Service.gI().Send_Caitrang(player);
        }
    }

    public void startUseMafuba(Player player, int time) {
        if (player.effectSkill != null) {
            player.effectSkill.isUseMafuba = true;
            player.effectSkill.timeUseMafuba = time;
            player.effectSkill.lastTimeUseMafuba = System.currentTimeMillis();
        }
    }

    public void finishUseMafuba(Player player) {
        if (player.effectSkill != null && player.newSkill != null && player.location != null) {
            player.effectSkill.isUseMafuba = false;
            for (Player playerMap : player.newSkill.playersTaget) {
                try {
                    if (player.location != null && playerMap.location != null) {
                        EffectSkillService.gI().setIsBinh(player, playerMap, 11000 * (player.newSkill.typeItem == 0 ? 1 : 2));
                        int x = player.location.x + ((player.newSkill.dir == -1) ? (-50) : 50);
                        int y = player.location.y;
                        if (player.zone != null && !MapService.gI().isMapBlackBallWar(player.zone.map.mapId)) {
                            Service.gI().setPos(playerMap, x, y);
                        }
                    }
                } catch (Exception e) {
//                     e.printStackTrace();
                }
            }
            for (Mob mobMap : player.newSkill.mobsTaget) {
                mobMap.effectSkill.setBinh(player, System.currentTimeMillis(), 11000 * (player.effectSkill.typeBinh == 0 ? 1 : 2));
            }
        }
    }

    public void setIsStone(Player player, int time) {
        if (MapService.gI().isMapMaBu(player.zone.map.mapId)) {
            player.nPoint.hp /= 2;
            PlayerService.gI().sendInfoHp(player);
            Service.gI().Send_Info_NV(player);
        }
        player.effectSkill.isStone = true;
        player.effectSkill.timeStone = time;
        player.effectSkill.lastTimeStone = System.currentTimeMillis();
        ItemTimeService.gI().sendItemTime(player, 4392, time / 1000);
        Service.gI().Send_Caitrang(player);
        EffectSkillService.gI().sendEffectPlayer(player, player, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.STONE_EFFECT);
    }

    public void removeStone(Player player) {
        player.effectSkill.isStone = false;
        Service.gI().Send_Caitrang(player);
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, STONE_EFFECT);
    }

    public void setIsLamCham(Player player, int time) {
        player.nPoint.speed = 1;
        Service.gI().point(player);
        Service.gI().sendSpeedPlayer(player, -1);
        player.effectSkill.isLamCham = true;
        player.effectSkill.timeLamCham = time;
        player.effectSkill.lastTimeLamCham = System.currentTimeMillis();
    }

    public void removeLamCham(Player player) {
        player.nPoint.speed = 8;
        Service.gI().point(player);
        Service.gI().sendSpeedPlayer(player, -1);
        Service.gI().chat(player, "Nhẹ lại rồi!");
        player.effectSkill.isLamCham = false;
    }

    public void setIsTanHinh(Player player, int time) {
        Service.gI().setPos2(player, player.location.x, 10000);
        player.effectSkill.isTanHinh = true;
        player.effectSkill.timeTanHinh = time;
        player.effectSkill.lastTimeTanHinh = System.currentTimeMillis();
    }

    public void removeTanHinh(Player player) {
        Service.gI().setPos2(player, player.location.x, player.location.y);
        player.effectSkill.isTanHinh = false;
    }

    public void setDameBuff(Player player, int time, int tiLe) {
        player.effectSkill.isDameBuff = true;
        player.effectSkill.timeDameBuff = time;
        player.effectSkill.tileDameBuff = tiLe;
        player.effectSkill.lastTimeDameBuff = System.currentTimeMillis();
    }

    public void removeDameBuff(Player player) {
        player.effectSkill.isDameBuff = false;
    }

    public void setMabuHold(Player player, MaBuHold MabuHold) {
        short x = (short) MabuHold.x;
        short y = (short) MabuHold.y;
        player.maBuHold = MabuHold;
        player.precentMabuHold = 0;
        PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.PK_ALL);
        Service.gI().sendMabuHold(player, 1, x, y);
        player.effectSkill.isMabuHold = true;
    }

    public void removeMabuHold(Player player) {
        player.maBuHold = null;
        player.precentMabuHold = 0;
        PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
        Service.gI().sendMabuHold(player, 0, (short) player.location.x, (short) 312);
        player.effectSkill.isMabuHold = false;
    }

    public void setPKCommeson(Player player, int time) {
        player.effectSkill.isPKCommeson = true;
        player.effectSkill.timePKCommeson = time;
        player.effectSkill.lastTimePKCommeson = System.currentTimeMillis();
        ItemTimeService.gI().sendItemTime(player, 2295, time / 1000);
    }

    public void removePKCommeson(Player player) {
        player.effectSkill.isPKCommeson = false;
        ItemTimeService.gI().sendItemTime(player, 2295, 0);
    }

    public void setPKSTT(Player player, int time) {
        player.effectSkill.isPKSTT = true;
        player.effectSkill.timePKSTT = time;
        player.effectSkill.lastTimePKSTT = System.currentTimeMillis();
        ItemTimeService.gI().sendItemTime(player, 2295, time / 1000);
    }

    public void removePKSTT(Player player) {
        player.effectSkill.isPKSTT = false;
        ItemTimeService.gI().sendItemTime(player, 2295, 0);
    }

    public void setChibi(Player player, int time) {
        player.effectSkill.isChibi = true;
        player.effectSkill.timeChibi = time;
        player.typeChibi = Util.nextInt(0, 3);
        if (player.typeChibi == 3) {
            player.nPoint.calPoint();
            player.nPoint.setHp((long) player.nPoint.hpMax);
            Service.gI().point(player);
            Service.gI().Send_Info_NV(player);
        }
        player.effectSkill.lastTimeChibi = System.currentTimeMillis();
        ItemTimeService.gI().sendItemTime(player, 433, time / 1000);
        Service.gI().sendChibi(player);
        Service.gI().sendHaveChibiFollowToAllMap(player);
    }

    public void removeChibi(Player player) {
        player.effectSkill.isChibi = false;
        player.typeChibi = -1;
        ItemTimeService.gI().sendItemTime(player, 433, 0);
        Service.gI().sendChibi(player);
        Service.gI().sendHaveChibiFollowToAllMap(player);
    }

    //**************************************************************************
    //Tái tạo năng lượng *******************************************************
    public void startCharge(Player player) {
        if (!player.effectSkill.isCharging) {
            player.effectSkill.isCharging = true;
            sendEffectCharge(player);
        }
    }

    public void stopCharge(Player player) {
        player.effectSkill.countCharging = 0;
        player.effectSkill.isCharging = false;;
        sendEffectStopCharge(player);

    }

    //**************************************************************************
    //Khiên năng lượng *********************************************************
    public void setStartShield(Player player) {
        player.effectSkill.isShielding = true;
        player.effectSkill.lastTimeShieldUp = System.currentTimeMillis();
        player.effectSkill.timeShield = SkillUtil.getTimeShield(player.playerSkill.skillSelect.point);
    }

    public void removeShield(Player player) {
        player.effectSkill.isShielding = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, SHIELD_EFFECT);
    }

    public void breakShield(Player player) {
        removeShield(player);
        Service.gI().sendThongBao(player, "Khiên năng lượng đã bị vỡ!");
        ItemTimeService.gI().removeItemTime(player, 3784);
    }

    //**************************************************************************
    public void sendEffectBlindThaiDuongHaSan(Player plUseSkill, List<Player> players, List<Mob> mobs, int timeStun) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(0);
            msg.writer().writeInt((int) plUseSkill.id);
            msg.writer().writeShort(plUseSkill.playerSkill.skillSelect.skillId);
            msg.writer().writeByte(mobs.size());
            for (Mob mob : mobs) {
                msg.writer().writeByte(mob.id);
                msg.writer().writeByte(timeStun / 1000);
            }
            msg.writer().writeByte(players.size());
            for (Player pl : players) {
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeByte(timeStun / 1000);
            }
            Service.gI().sendMessAllPlayerInMap(plUseSkill, msg);
            msg.cleanup();
        } catch (Exception e) {
            utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    //hiệu ứng bắt đầu gồng
    public void sendEffectStartCharge(Player player) {
        Skill skill = SkillUtil.getSkillbyId(player, Skill.TAI_TAO_NANG_LUONG);
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(6);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skill.skillId);
            Service.gI().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    //hiệu ứng đang gồng
    public void sendEffectCharge(Player player) {
        Skill skill = SkillUtil.getSkillbyId(player, Skill.TAI_TAO_NANG_LUONG);
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skill.skillId);
            Service.gI().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    //dừng gồng
    public void sendEffectStopCharge(Player player) {
        try {
            Message msg = new Message(-45);
            msg.writer().writeByte(3);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(-1);
            Service.gI().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    // hiệu ứng nổ kết thúc gồng
public void sendEffectEndCharge(Player player) {
    try {
        short skillId = 0;
        // lấy skill đang chọn nếu có
        models.skill.Skill sel = (player != null && player.playerSkill != null)
                ? player.playerSkill.skillSelect : null;
        if (sel != null) skillId = (short) sel.skillId;

        // fallback: BIẾN_HÌNH (1732) -> BIẾN_KHỈ
        if (skillId == 0) skillId = resolveBienHinhSkillId(player);
        if (skillId == 0) {
            models.skill.Skill monkey = SkillUtil.getSkillbyId(player, Skill.BIEN_KHI);
            if (monkey != null) skillId = (short) monkey.skillId;
        }

        Message msg = new Message(-45);
        msg.writer().writeByte(5);
        msg.writer().writeInt((int) player.id);
        // -1 nếu vẫn không xác định được (an toàn client)
        msg.writer().writeShort(skillId == 0 ? (short) -1 : skillId);
        Service.gI().sendMessAllPlayerInMap(player, msg);
        msg.cleanup();
    } catch (Exception e) {
        utils.Logger.logException(EffectSkillService.class, e);
    }
}

    private int resolveMonkeyLevel(Player p) {
    try {
        models.skill.Skill mk = SkillUtil.getSkillbyId(p, Skill.BIEN_KHI);
        if (mk != null) return mk.point;
        models.skill.Skill bh = SkillUtil.getSkillbyId(p, Skill.BIEN_HINH);
        if (bh != null) return bh.point;
    } catch (Exception ignore) {}
    return 1; // mặc định
}


    // hiệu ứng biến khỉ
public void sendEffectMonkey(Player player) {
    short skillId = 0;
    try {
        // Ưu tiên BIẾN_KHỈ nếu có
        models.skill.Skill monkey = SkillUtil.getSkillbyId(player, Skill.BIEN_KHI);
        if (monkey != null) {
            skillId = (short) monkey.skillId;
        } else {
            // Nếu không có, fallback sang BIẾN_HÌNH (1732)
            skillId = resolveBienHinhSkillId(player);
        }
        if (skillId == 0) {
            // Không xác định được -> bỏ qua để tránh NPE
            return;
        }
        Message msg = new Message(-45);
        msg.writer().writeByte(6);
        msg.writer().writeInt((int) player.id);
        msg.writer().writeShort(skillId);
        Service.gI().sendMessAllPlayerInMap(player, msg);
        msg.cleanup();
    } catch (Exception e) {
        utils.Logger.logException(EffectSkillService.class, e);
    }
}

    private short resolveBienHinhSkillId(Player p) {
    try {
        if (p != null && p.playerSkill != null) {
            // Ưu tiên skill đang chọn nếu chính là Biến Hình
            models.skill.Skill s = p.playerSkill.skillSelect;
            if (s != null && s.template != null && s.template.id == Skill.BIEN_HINH) {
                return (short) s.skillId;
            }
            // Không thì tìm skill Biến Hình trong list skills
            s = SkillUtil.getSkillbyId(p, Skill.BIEN_HINH);
            if (s != null) {
                return (short) s.skillId;
            }
        }
    } catch (Exception ignore) {}
    return 0; // fallback an toàn, tránh NPE
}
    public void setIsBodyChangeTechnique(Player player) {
        player.effectSkill.isBodyChangeTechnique = true;
        Service.gI().sendThongBao(player, "Bạn đã bị biến thành Tiểu Đội Trưởng");
        PlayerService.gI().changeAndSendTypePK(player, 5);
        for (int i = player.zone.getPlayers().size() - 1; i >= 0; i--) {
            Player pl = player.zone.getPlayers().get(i);
            Service.gI().playerInfoUpdate(player, pl, "!Tiểu đội trưởng", 180, 181, 182);
        }
    }

    public void removeBodyChangeTechnique(Player player) {
        PlayerService.gI().changeAndSendTypePK(player, 0);
        player.effectSkill.isTanHinh = false;
    }
    
    public void sendEffectSuper(Player player) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(6);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(97);
            Service.gI().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
//             e.printStackTrace();
        }
    }
    public void setIsBienHinhBroly(Player player) {
    sendEffectMonkey(player);

    int time = SkillUtil.getTimeMonkey(resolveMonkeyLevel(player));
    if (player.setClothes.cadic == 5) time *= 5;

    player.effectSkill.isBienHinhBroly = true;
    player.effectSkill.levelBienHinh   = (byte) resolveMonkeyLevel(player); // NEW
    player.effectSkill.timeBienHinh    = time;
    player.effectSkill.lastTimeBienHinh = System.currentTimeMillis();

    Service.gI().point(player);

    player.nPoint.setHp(player.nPoint.hpMax);
    player.nPoint.setMp(player.nPoint.mpMax);
    player.nPoint.setDame(player.nPoint.dame);
    PlayerService.gI().sendInfoHpMp(player);

    // Gửi ngoại hình/info
    Service.gI().Send_Caitrang(player);
    Service.gI().Send_Info_NV(player);
}

public void bienHinhDown(Player player) {
    player.effectSkill.isBienHinhBroly = false;

    Service.gI().point(player);

    if (player.nPoint.hp > player.nPoint.hpMax) player.nPoint.setHp(player.nPoint.hpMax);
    if (player.nPoint.mp > player.nPoint.mpMax) player.nPoint.setMp(player.nPoint.mpMax);
    if (player.nPoint.dame > player.nPoint.dame) player.nPoint.setDame(player.nPoint.dame);
    PlayerService.gI().sendInfoHpMp(player);

    // Hiệu ứng & ngoại hình
    sendEffectEndCharge(player);
    sendEffectMonkey(player);
    Service.gI().Send_Caitrang(player);
    Service.gI().Send_Info_NV(player);
}

    public void setIsBienhinh(Player player) {      
        player.effectSkill.isBienHinhBroly = true;
        ItemTimeService.gI().sendAllItemTime(player);
    }
}
