package models.mob;

/*
 *
 *
 * @author ZINZIN
 */
import services.InventoryService;
import services.Service;
import services.TaskService;
import services.ItemMapService;
import consts.ConstMap;
import consts.ConstMob;
import consts.ConstTask;
import models.event.EventManager;
import models.item.Item;
import models.map.ItemMap;

import java.util.List;

import models.map.Zone;
import models.player.Location;
import models.player.Pet;
import models.player.Player;
import network.Message;

import java.io.IOException;

import server.Maintenance;
import models.Top.RealTop;
import utils.Util;

import java.util.ArrayList;
import jdbc.Config;

import models.Achievement.AchievementService;
import models.Training.TrainingService;
import server.ServerNotify;
import services.ChatGlobalService;
import services.ItemService;
import services.MapService;
import models.skill.Skill;
import utils.TimeUtil;

public class Mob {

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public List<Player> temporaryEnemies = new ArrayList<>();

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int lvMob = 0;
    public int status = 5;
    public int type = 1;

    private long lastTimeAttackPlayer;
    private long timeAttack = 2000;
    public long lastTimePhucHoi = System.currentTimeMillis();
    public long lastTimeSendEffect = System.currentTimeMillis();
    private static double globalDropRate = 100.0;

    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.sethp(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.type = mob.type;
        this.setTiemNang();
    }
    
    public static void setGlobalDropRate(double rate) {
        globalDropRate = rate;
    }

    public static double getGlobalDropRate() {
        return globalDropRate;
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }
    
    private boolean canDrop(int baseChance, int outOf) {
    double globalRate = Config.RATE_DROP_PERCENT / 100.0;
    // tính lại baseChance rồi gọi Util
    int scaledChance = (int) Math.round(baseChance * globalRate);
    return Util.isTrue(scaledChance, outOf);
}

    public void setTiemNang() {
        this.maxTiemNang = (long) this.point.getHpFull() * (long) (this.pTiemNang + Util.nextInt(-2, 2)) / 100L;
    }

    public boolean isDie() {
        return this.point.gethp() <= 0;
    }

    public void setDie() {
        this.lastTimePhucHoi = System.currentTimeMillis();
        this.lastTimeDie = System.currentTimeMillis();
    }

    public void addTemporaryEnemies(Player pl) {
        if (pl != null && !temporaryEnemies.contains(pl)) {
            temporaryEnemies.add(pl);
        }
    }

    public void injured(Player plAtt, long damage, boolean dieWhenHpFull) {
        if (!this.isDie()) {
            if (damage >= this.point.hp) {
                damage = this.point.hp;
            }
            if (!dieWhenHpFull) {
                if (damage >= this.point.hp) {
                    damage = Math.max(1, this.point.hp - 1); // Đảm bảo còn lại ít nhất 1 HP
                }
                if ((this.tempId == ConstMob.MOC_NHAN || this.tempId == ConstMob.BU_NHIN_MA_QUAI)
                        && damage > this.point.maxHp / 10) {
                    damage = this.point.maxHp / 10;
                }
            }
            if (MapService.gI().isMapKhiGasHuyDiet(this.zone.map.mapId)) {
                boolean mob76Die = true;
                for (Mob mob : this.zone.mobs) {
                    if (!mob.isDie() && mob.tempId == ConstMob.CO_MAY_HUY_DIET) {
                        mob76Die = false;
                        break;
                    }
                }
                if (!mob76Die && plAtt != null && plAtt.playerSkill != null && plAtt.playerSkill.skillSelect != null) {
                    switch (plAtt.playerSkill.skillSelect.template.id) {
                        case Skill.LIEN_HOAN, Skill.ANTOMIC, Skill.MASENKO, Skill.KAMEJOKO ->
                            damage = 1;
                    }
                }
            }
            if (!dieWhenHpFull && !isBigBoss() && !MapService.gI().isMapPhoBan(this.zone.map.mapId) && this.lvMob > 0
                    && plAtt != null && plAtt.charms.tdOaiHung < System.currentTimeMillis()) {
                damage = (int) ((this.point.maxHp <= 20000000 ? this.point.maxHp * 10 : 2000000000) * (10.0 / 100));
                this.mobAttackPlayer(plAtt);
            }
            if (plAtt != null && plAtt.isBoss && this.tempId > 0 && canDrop(1, 2)
                    && Util.canDoWithTime(lastTimeAttackPlayer, 2500)) {
                this.mobAttackPlayer(plAtt);
                lastTimeAttackPlayer = System.currentTimeMillis();
            }

            if (damage > 2_000_000_000) {
                damage = 2_000_000_000;
            }

            this.point.hp -= damage;
            addTemporaryEnemies(plAtt);
            if (this.isDie()) {
                this.status = 0;
                this.setDie();
                this.temporaryEnemies.clear();
                if (plAtt != null) {
                    this.sendMobDieAffterAttacked(plAtt, (int) damage);
                    TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                    TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                    TaskService.gI().checkDoneClanTaskKillMob(plAtt, this);
                    AchievementService.gI().checkDoneTaskKillMob(plAtt, this);
                }
                if (this.id == 13) {
                    this.zone.isbulon1Alive = false;
                }
                if (this.id == 14) {
                    this.zone.isbulon2Alive = false;
                }
            } else {
                this.sendMobStillAliveAffterAttacked((int) damage,
                        plAtt != null ? (plAtt.nPoint != null && plAtt.nPoint.isCrit) : false);
            }
            if (plAtt != null) {
                if (plAtt.isPl() && plAtt.satellite != null && plAtt.satellite.isDefend) {
                    plAtt.satellite.isDefend = false;
                }
                Service.gI().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
                TrainingService.gI().tangTnsmLuyenTap(plAtt, getTiemNangForPlayer(plAtt, damage));
            }
        }
    }

    public long getTiemNangForPlayer(Player pl, long dame) {
        int levelPlayer = Service.gI().getCurrLevel(pl);
        int n = levelPlayer - this.level;
        if (pl.zone != null && MapService.gI().isMapBanDoKhoBau(pl.zone.map.mapId)) {
            n = 0;
        }
        if (pl.nPoint != null && pl.nPoint.power < 40_000_000_000L) {
            n = 0;
        }
        long pDameHit = dame * 100 / point.getHpFull();
        long tiemNang = pDameHit * maxTiemNang / 100;
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        if (n >= 0) {
            for (int i = 0; i < n; i++) {
                long sub = tiemNang * 10 / 100;
                if (sub <= 0) {
                    sub = 1;
                }
                tiemNang -= sub;
            }
        } else {
            for (int i = 0; i < -n; i++) {
                long add = tiemNang * 10 / 100;
                if (add <= 0) {
                    add = 1;
                }
                tiemNang += add;
            }
        }
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        if (pl.zone != null && pl.nPoint != null) {
            tiemNang = (int) pl.nPoint.calSucManhTiemNang(tiemNang);
        } else {
            return 0;
        }
        if (pl.zone.map.mapId == 122 || pl.zone.map.mapId == 123 || pl.zone.map.mapId == 124) {
            tiemNang *= 2;
        }
        return tiemNang;
    }

    public void update() {
        if (zone.isGoldenFriezaAlive && TimeUtil.is21H()) {
            if (!isDie()) {
                startDie();
                return;
            }
        }
        if (!this.isDie() && this.tempId == ConstMob.CO_MAY_HUY_DIET && Util.canDoWithTime(lastTimeSendEffect, 1000)) {
            sendEffect(55);
            lastTimeSendEffect = System.currentTimeMillis();
        }

        if (this.isDie() && !Maintenance.isRunning && !isBigBoss()) {
            switch (zone.map.type) {
                case ConstMap.MAP_DOANH_TRAI:
                    if (this.tempId == ConstMob.BULON && this.zone.isTUTAlive
                            && Util.canDoWithTime(lastTimeDie, 10000)) {
                        this.hoiSinh();
                        this.hoiSinhMobPhoBan();
                        if (this.id == 13) {
                            this.zone.isbulon1Alive = true;
                        }
                        if (this.id == 14) {
                            this.zone.isbulon2Alive = true;
                        }
                    }
                    break;
                case ConstMap.MAP_BAN_DO_KHO_BAU:
                    break;
                case ConstMap.MAP_CON_DUONG_RAN_DOC:
                    break;
                case ConstMap.MAP_KHI_GAS_HUY_DIET:
                    break;
                case ConstMap.MAP_TAY_KARIN:
                    break;
                default:
                    if (this.zone.isGoldenFriezaAlive && TimeUtil.is21H()) {
                        return;
                    }
                    if (Util.canDoWithTime(lastTimeDie, 5000)) {
                        this.hoiSinh();
                        this.sendMobHoiSinh();
                    }
                    if (Util.canDoWithTime(lastTimePhucHoi, 30000) && !isDie()) {
                        lastTimePhucHoi = System.currentTimeMillis();
                        int hpMax = this.point.maxHp;
                        if (this.point.hp < hpMax) {
                            hoi_hp(hpMax / 10);
                        } else {
                            this.sendMobHoiSinh();
                        }
                    }
            }
        }

        effectSkill.update();
        attack();
    }

    public boolean isBigBoss() {
        return (this.tempId == ConstMob.HIRUDEGARN
                || this.tempId == ConstMob.VUA_BACH_TUOC
                || this.tempId == ConstMob.ROBOT_BAO_VE
                || this.tempId == ConstMob.GAU_TUONG_CUOP
                || this.tempId == ConstMob.VOI_CHIN_NGA
                || this.tempId == ConstMob.GA_CHIN_CUA
                || this.tempId == ConstMob.NGUA_CHIN_LMAO
                || this.tempId == ConstMob.PIANO);
    }

    public void attack() {
        Player player = getPlayerCanAttack();
        if (!isDie() && !effectSkill.isHaveEffectSkill() && tempId != ConstMob.MOC_NHAN
                && tempId != ConstMob.BU_NHIN_MA_QUAI && tempId != ConstMob.CO_MAY_HUY_DIET && !this.isBigBoss()
                && (this.lvMob < 1 || MapService.gI().isMapPhoBan(this.zone.map.mapId))
                && Util.canDoWithTime(lastTimeAttackPlayer, timeAttack)) {
            if (player != null) {
                this.mobAttackPlayer(player);
            }
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

    public Player getPlayerCanAttack() {
        Player plAttack = getFirstPlayerCanAttack();
        if (plAttack != null) {
            return plAttack;
        }
        int distance = 100;
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.isNewPet && (pl.satellite == null || !pl.satellite.isDefend)
                        && (pl.effectSkin == null || !pl.effectSkin.isVoHinh)
                        && (this.tempId > 18 || (this.tempId > 9 && this.type == 4)) || isBigBoss()) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance || isBigBoss()) {
                        plAttack = pl;
                        distance = dis;
                    }
                }
            }
            this.timeAttack = 2000;
        } catch (Exception e) {

        }
        return plAttack;
    }

    private Player getFirstPlayerCanAttack() {
        Player plAtt = null;
        try {
            List<Player> playersMap = zone.getHumanoids();
            int dis = 300;
            if (playersMap != null) {
                for (Player plAttt : playersMap) {
                    if (plAttt.isDie() || plAttt.isBoss || (plAttt.satellite != null && plAttt.satellite.isDefend)
                            || (plAttt.effectSkin != null && plAttt.effectSkin.isVoHinh)
                            || !this.temporaryEnemies.contains(plAttt)) {
                        continue;
                    }
                    int d = Util.getDistance(plAttt, this);
                    if (d <= dis) {
                        dis = d;
                        plAtt = plAttt;
                    }
                }
            }
            this.timeAttack = 1000;
        } catch (Exception e) {

        }
        return plAtt;
    }

    private void mobAttackPlayer(Player player) {
        int dameMob = this.point.getDameAttack();
        if (player.charms != null && player.charms.tdDaTrau > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        if (player.isPet && ((Pet) player).master.charms != null
                && ((Pet) player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        if (this.lvMob > 0 && !MapService.gI().isMapPhoBan(this.zone.map.mapId)) {
            dameMob = (int) (player.nPoint.hpMax * (10.0 / 100));
        }
        if (player.satellite != null && player.satellite.isDefend) {
            dameMob -= dameMob / 5;
        }
        if (player.itemTime != null && player.itemTime.isUseCMS) {
            dameMob = (int) Math.round(dameMob * 0.1);
        }
        if (this.lvMob > 0 && player.charms.tdOaiHung > System.currentTimeMillis()) {
            dameMob = 0;
        }
        int dame = player.injured(null, dameMob, false, true);

        this.sendMobAttackMe(player, dame);
        this.sendMobAttackPlayer(player);
        this.phanSatThuong(player, dame);
    }

    private void sendMobAttackMe(Player player, int dame) {
        if (!player.isPet && !player.isNewPet && !player.isBot) {
            Message msg;
            try {
                msg = new Message(-11);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(dame); // dame
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    private void sendMobAttackPlayer(Player player) {
        Message msg;
        try {
            msg = new Message(-10);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeInt(player.nPoint.hp);
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hoiSinh() {
        this.status = 5;
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
    }

    public int lvMob() {
        for (Mob mobMap : this.zone.mobs) {
            if (mobMap.lvMob > 0) {
                return 0;
            }
        }
        this.lvMob = this.tempId > 18 && !isBigBoss() ? canDrop(10, 100) ? 1 : 0 : 0;
        this.point.hp = this.lvMob > 0 ? this.point.maxHp <= 20000000 ? this.point.maxHp * 10 : 2000000000
                : this.point.maxHp;
        return this.lvMob;
    }

    public void sendMobHoiSinh() {
        Message msg = null;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(lvMob());
            msg.writer().writeInt(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            this.sendMobMaxHp(this.point.hp);
            this.sendSieuQuai(this.lvMob > 0 ? 1 : 0);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void hoi_hp(int hp) {
        Message msg = null;
        try {
            this.point.sethp(this.point.gethp() + hp);
            int HP = hp > 0 ? 1 : Math.abs(hp);
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(HP);
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendEffect(int Effect) {
        Message msg = null;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(Effect);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void sendMobDieAffterAttacked(Player plKill, int dameHit) {
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(plKill.nPoint.isCrit); // crit
            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            this.sendSieuQuai(0);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (Exception e) {
        }
    }

    private void hutItem(Player player, List<ItemMap> items) {
        if (!player.isPet && !player.isNewPet) {
            if (player.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(player, item.itemMapId, true);
                }
            }
        } else {
            if (((Pet) player).master.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(((Pet) player).master, item.itemMapId, true);
                }
            }
        }
    }

    private List<ItemMap> mobReward(Player player, ItemMap itemTask, Message msg) {
        List<ItemMap> itemReward = new ArrayList<>();
        try {
            itemReward = this.getItemMobReward(player, this.location.x + Util.nextInt(-10, 10),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y));
            if (itemTask != null) {
                itemReward.add(itemTask);
            }
            msg.writer().writeByte(itemReward.size()); // sl item roi
            for (ItemMap itemMap : itemReward) {
                msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                msg.writer().writeShort(itemMap.x); // xend item
                msg.writer().writeShort(itemMap.y); // yend item
                msg.writer().writeInt((int) itemMap.playerId); // id nhan vat
            }
        } catch (Exception e) {
//             e.printStackTrace();
        }
        return itemReward;
    }

    private boolean isHeoNe() {
        return this.tempId == ConstMob.HEO_DA_XANH || this.tempId == ConstMob.HEO_RUNG
                || this.tempId == ConstMob.HEO_RUNG_ME
                || this.tempId == ConstMob.HEO_XANH_ME || this.tempId == ConstMob.HEO_XAYDA
                || this.tempId == ConstMob.HEO_XAYDA_ME;
    }

    private boolean isMocNhan() {
        return this.tempId == ConstMob.MOC_NHAN;
    }

    public List<ItemMap> getItemMobReward(Player player, int x, int yEnd) {
        List<ItemMap> list = new ArrayList<>();
        if (player.isBoss || player.isBot) {
            return list;
        }

        // if (player.isPl() && Util.isTrue(1, 10000) && this.tempId == 0) {
        // short itTemp = (short) ItemService.gI().randTempItemKichHoat(player.gender);
        // ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
        // List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
        // if (!ops.isEmpty()) {
        // it.options = ops;
        // }
        // it.options.add(new Item.ItemOption(210, 0));
        // it.options.add(new Item.ItemOption(216, 0));
        // it.options.add(new Item.ItemOption(30, 0));
        // list.add(it);
        // }
        if (this.tempId == 0) {
            return list;
        }
        int mapid = player.zone.map.mapId;
        int dropRateModifier = 1;
        if (player != null) {
            if (player.itemTime.isCoBonLa) {
                dropRateModifier *= 2; // Tăng 50% (x1.5)
            }
        }

        if (EventManager.CHRISTMAS) {
            Player pl = player;
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (canDrop(1, 50)) {
                if (pl.itemEvent != null && pl.itemEvent.canDropTatVoGiangSinh(100)) {
                    list.add(new ItemMap(zone, 649, 1, x, yEnd, player.id));
                }
            }
        }
        if (mapid == 5 || mapid == 13) {
            Player pl = player;
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (canDrop(1, 500)) {
                if (pl.itemEvent != null && pl.itemEvent.canDropBinhNuoc(100)) {
                    list.add(new ItemMap(zone, 456, 1, x, yEnd, pl.id));
                }
            }
        }
        if (EventManager.INTERNATIONAL_WOMANS_DAY) {
            Player pl = player;
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (canDrop(1, 50)) {
                if (pl.itemEvent != null && pl.itemEvent.canDropHoaHong(100)) {
                    list.add(new ItemMap(zone, 610, 1, x, yEnd, player.id));
                }
            }
        }
        if (EventManager.HALLOWEEN) {
            if (MapService.gI().isMapEventHalloween(mapid)) {
                if (canDrop(1, 50)) {
                    list.add(new ItemMap(zone, 707, 1, x, yEnd, player.id));
                } else if (canDrop(1, 50)) {
                    list.add(new ItemMap(zone, 708, 1, x, yEnd, player.id));
                }
            }
        }
//        if (EventManager.TRUNG_THU) {
//            if (MapService.gI().isMapEventTrungthu(mapid)) {
//                if (canDrop(1, 50)) {
//                    list.add(new ItemMap(zone, 707, 1, x, yEnd, player.id));
//                } else if (canDrop(1, 50)) {
//                    list.add(new ItemMap(zone, 708, 1, x, yEnd, player.id));
//                }
//            }
//        }
        if (canDrop(1, 5000)) {
            list.add(new ItemMap(zone, 18, 1, x, yEnd, player.id));
        }
        if (canDrop(1, 5000)) {
            list.add(new ItemMap(zone, 19, 1, x, yEnd, player.id));
        }
        if (canDrop(1, 5000)) {
            list.add(new ItemMap(zone, 20, 1, x, yEnd, player.id));
        }

        if (player.itemTime.isUseMayDo && (canDrop(15, 100) || (player.actived() && canDrop(1, 10)))
                && this.tempId > 57 && this.tempId < 66) {
            list.add(new ItemMap(zone, 380, 1, x, yEnd, player.id));
        }
        if (player.itemTime.isUseMayDo2 && canDrop(15, 100) && this.tempId > 80 && this.tempId < 81) {
            list.add(new ItemMap(zone, 1110, 1, x, yEnd, player.id));
        }

//        if (player.isPl() && TaskService.gI().getIdTask(player) == ConstTask.TASK_8_1) {
//            if (player.gender == 0 && this.tempId == 11 || player.gender == 1 && this.tempId == 12
//                    || player.gender == 2 && this.tempId == 10) {
//                list.add(new ItemMap(zone, 20, 1, x, yEnd, player.id));
//            }
//        }
        if (MapService.gI().isMapDoanhTrai(mapid)) {
            if (canDrop(1, 500)) {
                list.add(new ItemMap(zone, 1836, 1, x, yEnd, player.id));
            }
        }
        if (MapService.gI().isMapNgucTu(mapid)) {
            if (canDrop(10, 100)) {
                list.add(new ItemMap(zone, 1229, 1, x, yEnd, player.id));
            }
        }
        if (MapService.gI().isMapPhoBan(mapid) && this.tempId != 22) {
            if (canDrop(50, 100) || (player.actived() && canDrop(10, 50))) {
                list.add(new ItemMap(zone, 1229, 1, x, yEnd, player.id));
            }
        }
        if (canDrop(1, 1000)) {
            list.add(new ItemMap(zone, 77, Util.nextInt(50, 70), x, yEnd, player.id));
        }
        if (MapService.gI().isMapUpPorata(mapid)) {
            if (canDrop(70, 100)) {
                ItemMap it = new ItemMap(zone, 934, Util.nextInt(1, 10), x, yEnd, player.id);
                it.options.add(new Item.ItemOption(30, 0));
                list.add(it);
            } else if (canDrop(150, 200)) {
                ItemMap it = new ItemMap(zone, 935, Util.nextInt(1, 10), x, yEnd, player.id);
                it.options.add(new Item.ItemOption(30, 0));
                list.add(it);
            } else if (canDrop(8, 10)) {
                ItemMap it = new ItemMap(zone, 933, 1, x, yEnd, player.id);
                it.options.add(new Item.ItemOption(30, 0));
                list.add(it);
            } else if (canDrop(8, 10)) {
                ItemMap it = new ItemMap(zone, 1861, Util.nextInt(1, 10), x, yEnd, player.id);
                it.options.add(new Item.ItemOption(30, 0));
                list.add(it);
            }
            
        }
        // sự kiện trung thu ( map )
        if (MapService.gI().isMapEventTrungthu(mapid)) {
            if (canDrop(10, 100)) {
                ItemMap it = new ItemMap(zone, 1214, 1, x, yEnd, player.id);
                list.add(it);
            } else if (canDrop(10, 100)) {
                ItemMap it = new ItemMap(zone, 1547, 1, x, yEnd, player.id);
                list.add(it);
            } else if (canDrop(10, 100)) {
                ItemMap it = new ItemMap(zone, 1545, 1, x, yEnd, player.id);
                list.add(it);
            } else if (canDrop(10, 100)) {
                ItemMap it = new ItemMap(zone, 1549, Util.nextInt(1, 10), x, yEnd, player.id);
                list.add(it);
            } else if (canDrop(10, 100)) {
                ItemMap it = new ItemMap(zone, 1032, Util.nextInt(1, 10), x, yEnd, player.id);
                list.add(it);
            } else if (canDrop(10, 100)) {
                ItemMap it = new ItemMap(zone, 1035, Util.nextInt(1, 10), x, yEnd, player.id);
                list.add(it);
            }
            
        }

        // Vang roi
        //======================== Vàng Ngọc ========================
        // Kiểm tra xem bản đồ có phải là bản đồ 3 hành tinh hay không
        if (MapService.gI().isMap3Planets(mapid)) {

            // Phân chia tỷ lệ rơi vàng dựa trên các điều kiện
            if (canDrop(1, 20) // Xác suất chung là 1/100
                    || (Config.TEST && canDrop(1, 5))
                    || (player.actived() && canDrop(1, 20))
                    || (player.isAdmin() && canDrop(1, 20))) {

                // Tính số lượng vàng rơi ra ngẫu nhiên trong khoảng từ 500 đến 3000
                int vang = Util.nextInt(500, 3000);

                // Phân chia các mức vàng rơi dựa trên giá trị vàng đã tính
                if (vang < 1000) {
                    // Nếu vàng dưới 1000, rơi item vàng cấp 76 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 76, vang, x, yEnd, player.id));
                } else if (vang < 2000) {
                    // Nếu vàng từ 1000 đến 2000, rơi item vàng cấp 188 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 188, vang, x, yEnd, player.id));
                } else {
                    // Nếu vàng trên 2000, rơi item vàng cấp 189 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 189, vang, x, yEnd, player.id));
                }
            }
        }

        // Kiểm tra nếu bản đồ hiện tại là bản đồ Nappa
        if (MapService.gI().isMapNappa(mapid)) {

            // Kiểm tra điều kiện rơi vàng (bỏ qua tỷ lệ rơi vàng, chỉ xét điều kiện khác)
            if (canDrop(1, 100) // Xác suất chung là 1/100
                    || (Config.TEST && canDrop(1, 5)) // Trong chế độ test, tỷ lệ rơi vàng là 1/5
                    || (player.actived() && canDrop(1, 20)) // Nếu người chơi đang hoạt động, tỷ lệ rơi vàng là 1/20
                    || (player.isAdmin() && canDrop(1, 10)) // Nếu người chơi là admin, tỷ lệ rơi vàng là 10%
                    ) {

                // Tính số lượng vàng rơi ra ngẫu nhiên trong khoảng từ 2000 đến 6000
                int vang = Util.nextInt(2000, 6000);

                // Phân chia các mức vàng rơi dựa trên giá trị vàng đã tính
                if (vang < 3000) {
                    // Nếu vàng dưới 3000, rơi item vàng cấp 188 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 188, vang, x, yEnd, player.id));
                } else if (vang < 5000) {
                    // Nếu vàng từ 3000 đến 5000, rơi item vàng cấp 189 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 189, vang, x, yEnd, player.id));
                } else {
                    // Nếu vàng trên 5000, rơi item vàng cấp 190 với số lượng vàng tương ứng
                    list.add(new ItemMap(zone, 190, vang, x, yEnd, player.id));
                }
            }
        }

        // Vàng cold
        if (MapService.gI().isMapCold(mapid)) {

            // Kiểm tra điều kiện rơi vàng với 4 trường hợp tài khoản khác nhau
            if (canDrop(1, 100) // Xác suất chung là 1/100
                    || (Config.TEST && canDrop(1, 5)) // Trong chế độ test, tỷ lệ rơi vàng là 1/5
                    || (player.actived() && canDrop(1, 20)) // Nếu người chơi đang hoạt động, tỷ lệ rơi vàng là 1/20
                    || (player.isAdmin() && canDrop(1, 10)) // Nếu người chơi là admin, tỷ lệ rơi vàng là 10%
                    ) {

                // Tính số lượng vàng rơi ra ngẫu nhiên trong khoảng từ 7000 đến 15000
                int vang = Util.nextInt(8000, 18000);

                // Phân chia các mức vàng rơi dựa trên giá trị vàng đã tính
                if (vang < 10000) {
                    list.add(new ItemMap(zone, 189, vang, x, yEnd, player.id)); // Rơi vàng cấp 189
                } else if (vang < 14000) {
                    list.add(new ItemMap(zone, 190, vang, x, yEnd, player.id)); // Rơi vàng cấp 190
                } else {
                    list.add(new ItemMap(zone, 190, vang, x, yEnd, player.id)); // Rơi vàng cấp 190
                }
            }
        }

        // Vàng tương lai
        if (MapService.gI().isMapTuongLai(mapid)) {

            // Kiểm tra điều kiện rơi vàng với 4 trường hợp tài khoản khác nhau
            if (canDrop(1, 100) // Xác suất chung là 1/100
                    || (Config.TEST && canDrop(1, 5)) // Trong chế độ test, tỷ lệ rơi vàng là 1/5
                    || (player.actived() && canDrop(1, 20)) // Nếu người chơi đang hoạt động, tỷ lệ rơi vàng là 1/20
                    || (player.isAdmin() && canDrop(1, 10)) // Nếu người chơi là admin, tỷ lệ rơi vàng là 10%
                    ) {

                // Tính số lượng vàng rơi ra ngẫu nhiên trong khoảng từ 5000 đến 10000
                int vang = Util.nextInt(5000, 12000);

                // Phân chia các mức vàng rơi dựa trên giá trị vàng đã tính
                if (vang < 6000) {
                    list.add(new ItemMap(zone, 188, vang, x, yEnd, player.id)); // Rơi vàng cấp 188
                } else if (vang < 10000) {
                    list.add(new ItemMap(zone, 189, vang, x, yEnd, player.id)); // Rơi vàng cấp 189
                } else {
                    list.add(new ItemMap(zone, 190, vang, x, yEnd, player.id)); // Rơi vàng cấp 190
                }
            }
        }

        // Vàng phó bản
        if (MapService.gI().isMapPhoBan(mapid)) {

            // Kiểm tra điều kiện rơi vàng với 4 trường hợp tài khoản khác nhau
            if (canDrop(1, 100) // Xác suất chung là 1/100
                    || (Config.TEST && canDrop(1, 5)) // Trong chế độ test, tỷ lệ rơi vàng là 1/5
                    || (player.actived() && canDrop(1, 10)) // Nếu người chơi đang hoạt động, tỷ lệ rơi vàng là 1/10
                    || (player.isAdmin() && canDrop(1, 10)) // Nếu người chơi là admin, tỷ lệ rơi vàng là 10%
                    ) {

                // Tính số lượng vàng rơi ra ngẫu nhiên trong khoảng từ 8000 đến 20000
                int vang = Util.nextInt(8000, 20000);

                // Phân chia các mức vàng rơi dựa trên giá trị vàng đã tính
                if (vang < 6000) {
                    list.add(new ItemMap(zone, 188, vang, x, yEnd, player.id)); // Rơi vàng cấp 188
                } else if (vang < 10000) {
                    list.add(new ItemMap(zone, 189, vang, x, yEnd, player.id)); // Rơi vàng cấp 189
                } else {
                    list.add(new ItemMap(zone, 190, vang, x, yEnd, player.id)); // Rơi vàng cấp 190
                }
            }
        }

        // Ngọc
        if (canDrop(1, 1000000) // Xác suất chung là 1/100000
                || (Config.TEST && canDrop(1, 10)) // Trong chế độ test, tỷ lệ rơi ngọc là 1/10
                || (player.actived() && canDrop(5, 100000)) // Nếu người chơi đang hoạt động, tỷ lệ rơi ngọc là 1/200
                || (player.isAdmin() && canDrop(1, 10)) // Nếu người chơi là admin, tỷ lệ rơi ngọc là 10%
                ) {
            // Thay đổi ngọc muốn rơi ở đây
            int ngoc = Util.nextInt(1, 1);
            list.add(new ItemMap(zone, 77, ngoc, x, yEnd, player.id));  // Thêm ngọc vào danh sách item
        }

        // Set kich hoat
        if (player.itemTime.isCoBonLa) {
            if (canDrop(1, 3000) && MapService.gI().isMapUpSKH(mapid)) {
                short itTemp = (short) ItemService.gI().randTempItemKichHoat(player.gender);
                ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
                List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
                if (!ops.isEmpty()) {
                    it.options = ops;
                }
                int[] opsrand = ItemService.gI().randOptionItemKichHoat(player.gender);
                it.options.add(new Item.ItemOption(opsrand[0], 0));
                it.options.add(new Item.ItemOption(opsrand[1], 0));
                it.options.add(new Item.ItemOption(30, 0));
                list.add(it);
                //ChatGlobalService.gI().autoChatGlobal(player, "[ Hệ Thống ] " + player.name + " vừa nhặt được " + it.itemTemplate.name + " Sét Kích Hoạt");
            }
        } else {
            if (canDrop(1, 7000) && MapService.gI().isMapUpSKH(mapid)) {
                short itTemp = (short) ItemService.gI().randTempItemKichHoat(player.gender);
                ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
                List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
                if (!ops.isEmpty()) {
                    it.options = ops;
                }

                int[] opsrand = ItemService.gI().randOptionItemKichHoat(player.gender);
                it.options.add(new Item.ItemOption(opsrand[0], 0));
                it.options.add(new Item.ItemOption(opsrand[1], 0));
                list.add(it);
                //ChatGlobalService.gI().autoChatGlobal(player, "[ Hệ Thống ] " + player.name + " vừa nhặt được " + it.itemTemplate.name + " Sét Kích Hoạt");
            }
        }
        if (((canDrop(1, 10000)) || (Config.TEST && canDrop(1, 10000)) || canDrop(1, 10000)) && MapService.gI().isMapUpSKH(mapid)) {
            short itTemp = (short) ItemService.gI().randTempItemKichHoat(player.gender);
            ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
            List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
            if (!ops.isEmpty()) {
                it.options = ops;
            }

            int[] opsrand = ItemService.gI().randOptionItemKichHoatNew(player.gender);
            it.options.add(new Item.ItemOption(opsrand[0], 0));
            it.options.add(new Item.ItemOption(opsrand[1], 0));
            it.options.add(new Item.ItemOption(opsrand[2], 0));
            it.options.add(new Item.ItemOption(opsrand[3], 0));
            it.options.add(new Item.ItemOption(30, 0));
            list.add(it);
            //ChatGlobalService.gI().autoChatGlobal(player, "[ Hệ Thống ] " + player.name + " vừa nhặt được " + it.itemTemplate.name + " Sét Kích Hoạt");
        }
        
        //========================Đồ Sao Khác Vải Thô========================
        if (((player.actived() && canDrop(0, 3000)) // Nếu người chơi đang hoạt động và có xác suất 1/10000
                || (Config.TEST && canDrop(1, 4000))) // Nếu trong chế độ TEST và có xác suất 1/10000
                && MapService.gI().isMapNappa(mapid) // Kiểm tra nếu là bản đồ Nappa
                ) {
            short itTemp = (short) ItemService.gI().randTempItemDoSao(player.gender);  // Lấy item sao ngẫu nhiên cho người chơi
            ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);  // Tạo đối tượng item
            List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);  // Lấy danh sách option cho item

            if (!ops.isEmpty()) {
                it.options = ops;  // Nếu có options thì gán vào item
            }

            // Thêm option dựa trên xác suất
            int randOption = Util.nextInt(100);  // Lấy số ngẫu nhiên từ 0 đến 99
            boolean hasOption = false;  // Biến kiểm tra có thêm option hay không

            // 50% xác suất thêm option
            if (randOption < 50) {
                int randAddOption = Util.nextInt(100);  // Lấy số ngẫu nhiên để quyết định sao nào
                if (randAddOption < 50) {  // 45% cho option 1 (sao 1)
                    it.options.add(new Item.ItemOption(107, 1));  // Thêm option sao 1
                    hasOption = true;
                } else if (randAddOption < 90) {  // 30% cho option 2 (sao 2)
                    it.options.add(new Item.ItemOption(107, 2));  // Thêm option sao 2
                    hasOption = true;
                } else {  // 25% cho option 3 (sao 3)
                    it.options.add(new Item.ItemOption(107, 3));  // Thêm option sao 3
                    hasOption = true;
                }
            }

            // Nếu có option (sao) thì mới thêm vào list
            if (hasOption) {
                list.add(it);  // Thêm item vào danh sách item rơi
            }
        }
      
        // END
        //========================Đồ Sao 3 Map Đầu========================
        if (((canDrop(1, 3000))
                || (Config.TEST && canDrop(1, 300))
                || (player.isAdmin() && canDrop(15, 150)))
                && MapService.gI().isMapUpSKH(mapid)) {

            // Tính toán tỷ lệ dựa trên sức mạnh
            int baseRate = 50; // Tỷ lệ cơ bản 50%
            int powerReduction = (int) Math.min(player.nPoint.power / 100000, 5) * 20; // Giảm 20% mỗi 100k sức mạnh, tối đa 5 lần
            int finalRate = Math.max(baseRate - powerReduction, 0); // Tỷ lệ không âm

            // Nếu tỷ lệ > 0, kiểm tra xác suất
            if (finalRate > 0 && Util.nextInt(100) < finalRate) {
                short itTemp = (short) ItemService.gI().randDoSao(player.gender);
                ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
                List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);

                if (!ops.isEmpty()) {
                    it.options = ops;
                }

                // Thêm option dựa trên xác suất
                int randOption = Util.nextInt(100); // Lấy số ngẫu nhiên từ 0 đến 99
                boolean hasOption = false; // Biến để kiểm tra có thêm option hay không

                // 50% xác suất thêm option 
                if (randOption < 50) {
                    int randAddOption = Util.nextInt(100); // Lấy số ngẫu nhiên để quyết định thêm sao nào
                    if (randAddOption < 60) { // 60% cho option 1 (sao 1)
                        it.options.add(new Item.ItemOption(107, 1)); // Option 1 (sao 1)
                        hasOption = true;
                    } else if (randAddOption < 90) { // 30% cho option 2 (sao 2)
                        it.options.add(new Item.ItemOption(107, 2)); // Option 2 (sao 2)
                        hasOption = true;
                    } else { // 10% cho option 3 (sao 3)
                        it.options.add(new Item.ItemOption(107, 3)); // Option 3 (sao 3)
                        hasOption = true;
                    }
                }

                // Nếu có option (sao) thì mới thêm vào list
                if (hasOption) {
                    list.add(it);
                }
            }
        }

        // Sao pha le
        if (player.itemTime.isCoBonLa) {
            if (canDrop(1, 3000)) {
                int rand = Util.nextInt(0, 6);
                ItemMap it = new ItemMap(zone, 441 + rand, 1, x, yEnd, player.id);
                it.options.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));
                list.add(it);
            }
        } else {
            if (canDrop(1, 7000)) {
                int rand = Util.nextInt(0, 6);
                ItemMap it = new ItemMap(zone, 441 + rand, 1, x, yEnd, player.id);
                it.options.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));
                list.add(it);
            }
        }

        // Da nang cap
//        if (player.itemTime.isCoBonLa) {
//            if (canDrop(1, 5000)) {
//                int rand = Util.nextInt(0, 4);
//                ItemMap it = new ItemMap(zone, 220 + rand, 1, x, yEnd, player.id);
//                it.options.add(new Item.ItemOption(71 - rand, 0));
//                list.add(it);
//            }
//        } else {
//            if (canDrop(1, 1000)) {
//                int rand = Util.nextInt(0, 4);
//                ItemMap it = new ItemMap(zone, 220 + rand, 1, x, yEnd, player.id);
//                it.options.add(new Item.ItemOption(71 - rand, 0));
//                list.add(it);
//            }
//        }

        if (MapService.gI().isMapCold(mapid)) {
            if (player.isPet) {
                player = ((Pet) player).master;
            }
            if (player.isPet) {
                player = ((Pet) player).master;
            }
            if (player.itemTime.isCoBonLa) {
                if (canDrop(1, 50000)) {
                    ItemMap it = ItemService.gI().randDoTL(this.zone, 1, x, yEnd, player.id);
                    list.add(it);
                    ServerNotify.gI().notify(player.name + " vừa nhặt được " + it.itemTemplate.name + " tại "
                            + this.zone.map.mapName + " khu " + this.zone.zoneId);
                }
                if (canDrop(1, 500) && InventoryService.gI().fullSetThan(player)) {
                    ItemMap it = new ItemMap(zone, Util.nextInt(663, 667), 1, x, yEnd, player.id);
                    it.options.add(new Item.ItemOption(30, 0));
                    list.add(it);
                }
            } else {
                if (canDrop(1, 100000)) {
                    ItemMap it = ItemService.gI().randDoTL(this.zone, 1, x, yEnd, player.id);
                    list.add(it);
                    ServerNotify.gI().notify(player.name + " vừa nhặt được " + it.itemTemplate.name + " tại "
                            + this.zone.map.mapName + " khu " + this.zone.zoneId);
                }
                if (canDrop(1, 1000) && InventoryService.gI().fullSetThan(player)) {
                    ItemMap it = new ItemMap(zone, Util.nextInt(663, 667), 1, x, yEnd, player.id);
                    it.options.add(new Item.ItemOption(30, 0));
                    list.add(it);
                }
            }
        }
        if (MapService.gI().isMapBiaRung(mapid)) {
            if (TaskService.gI().getIdTask(player) == ConstTask.TASK_31_7) {
                if (canDrop(1, 3000)) {
                    ItemMap it = new ItemMap(zone, Util.nextInt(663, 667), 1, x, yEnd, player.id);
                    it.options.add(new Item.ItemOption(30, 0));
                    list.add(it);
                }
            }
        }
        if ((canDrop(10, 1000) || (player.actived() && player.setClothes.checkSetDes() && canDrop(20, 100))) && MapService.gI().isMapNgucTu(mapid)) {
            list.add(new ItemMap(zone, Util.nextInt(1066, 1070), 1, x, yEnd, player.id));
        }
        if ((canDrop(10, 1000) || (player.actived() && player.setClothes.checkSetDes() && canDrop(20, 100))) && MapService.gI().isMapNgucTu(mapid)) {
            list.add(new ItemMap(zone, 1229, 1, x, yEnd, player.id));
        }
        if (canDrop(1, 1000)) {
            if (this.isHeoNe()) {
                ItemMap it = new ItemMap(zone, 1549, 1, x, yEnd, player.id);
                it.options.add(new Item.ItemOption(86, 0));
                it.options.add(new Item.ItemOption(93, 30));
                it.options.add(new Item.ItemOption(174, 2025));
                it.options.add(new Item.ItemOption(30, 0));
                list.add(it);
            }
        }
        if (canDrop(1, 1000)) {
            if (this.isMocNhan()) {
                ItemMap it = new ItemMap(zone, 751, 1, x, yEnd, player.id);
                it.options.add(new Item.ItemOption(86, 0));
                it.options.add(new Item.ItemOption(174, 2025));
                list.add(it);
            }
        }
        if (canDrop(1, 1000) && MapService.gI().isMapBanDoKhoBau(mapid) && MapService.gI().isMapDoanhTrai(mapid)) {
            ItemMap it = new ItemMap(zone, 750, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(86, 0));
            it.options.add(new Item.ItemOption(174, 2025));
            list.add(it);
        }

        if (MapService.gI().isMapTuongLai(mapid)
                && ((canDrop(1, 1000) || (player.actived() && canDrop(1, 150))))
                && InventoryService.gI().fullSetThan(player)) {
            ItemMap it = new ItemMap(zone, Util.nextInt(663, 667), 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(30, 0));
            list.add(it);
        }
        
        //========================Mảnh đá Vụn========================

        // Mảnh đá vụn cho bản đồ Doanh Trại
        if (MapService.gI().isMapDoanhTrai(mapid)
                && (canDrop(1, 500)
                || (Config.TEST && canDrop(1, 5))
                || (player.actived() && canDrop(1, 10)))) {
            ItemMap it = new ItemMap(zone, 225, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(74, 0));
            list.add(it);
        }

        // Mảnh đá vụn cho bản đồ 3 Planets (tỷ lệ khác)
        if (MapService.gI().isMap3Planets(mapid)
                && (canDrop(1, 500)
                || (Config.TEST && canDrop(1, 10))
                || (player.actived() && canDrop(1, 250)))) {
            ItemMap it = new ItemMap(zone, 225, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(74, 0));
            list.add(it);
        }

        // Kiểm tra nếu map nằm trong danh sách các map cần áp dụng xác suất
        if (MapService.gI().isMap3Planets(mapid)
                || MapService.gI().isMapNappa(mapid)
                || MapService.gI().isMapTuongLai(mapid)
                || MapService.gI().isMapCold(mapid)) {
            if (canDrop(1, 500)
                    || (player.actived() && canDrop(1, 500))) {
                int rand = Util.nextInt(0, 1);
                ItemMap it = new ItemMap(zone, 19 + rand, 1, x, yEnd, player.id);
                list.add(it);
            }
        }

        // Manh thien su
        if ((canDrop(1, 10000) || (player.actived() && canDrop(1, 1000)))
                && MapService.gI().isMapHanhTinhThucVat(mapid) && InventoryService.gI().findItemNTK(player)) {
            list.add(new ItemMap(zone, Util.nextInt(1066, 1070), 1, x, yEnd, player.id));
        }
        if (canDrop(1, 1000) && MapService.gI().isMapHanhTinhThucVat(mapid) && InventoryService.gI().findItemNTK(player)) {
            list.add(new ItemMap(zone, 1281, 1, x, yEnd, player.id));
        }
        if (canDrop(10, 1000) && MapService.gI().isMapNgucTu(mapid)) {
            list.add(new ItemMap(zone, Util.nextInt(1066, 1070), 1, x, yEnd, player.id));
        }
        if (canDrop(1, 10000) || (player.actived() && canDrop(1, 5000))) {
            list.add(new ItemMap(zone, 861, 1, x, yEnd, player.id));
        }
        // if (player.nPoint.power >= 80000000000L) {
        // if (player.zone.map.mapId == 155) {
        // list.add(new ItemMap(zone, 2055, 1, x, yEnd, player.id));
        // } else {
        // if (canDrop(1, 500) || (player.actived() && canDrop(1, 100))) {
        // list.add(new ItemMap(zone, 2051, 1, x, yEnd, player.id));
        // }
        // if (canDrop(1, 1000) || (player.actived() && canDrop(1, 200))) {
        // list.add(new ItemMap(zone, 2052, 1, x, yEnd, player.id));
        // }
        // }
        // }
        return list;
    }

    private ItemMap dropItemTask(Player player) {
        ItemMap itemMap = null;
        switch (tempId) {
            case ConstMob.KHUNG_LONG:
            case ConstMob.LON_LOI:
            case ConstMob.QUY_DAT:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_2_0) {
                    itemMap = new ItemMap(zone, 73, 1, location.x, location.y, player.id);
                }
                break;
            case ConstMob.THAN_LAN_ME:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_8_1) {
                    if (canDrop(1, 50)) {
                        itemMap = new ItemMap(zone, 20, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player,
                                "Con thằn lằn mẹ này không giữ ngọc, hãy tìm con thằn lằn mẹ khác");
                    }
                }
            case ConstMob.QUY_BAY_ME:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_8_1) {
                    if (canDrop(1, 50)) {
                        itemMap = new ItemMap(zone, 20, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player,
                                "Con thằn lằn mẹ này không giữ ngọc, hãy tìm con thằn lằn mẹ khác");
                    }
                }
            case ConstMob.PHI_LONG_ME:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_8_1) {
                    if (canDrop(1, 50)) {
                        itemMap = new ItemMap(zone, 20, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player,
                                "Con thằn lằn mẹ này không giữ ngọc, hãy tìm con thằn lằn mẹ khác");
                    }
                }    
            case ConstMob.OC_MUON_HON:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_14_1) {
                    if (canDrop(1, 100)) {
                        itemMap = new ItemMap(zone, 85, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player,
                                "Con ốc mượn hồn này không giữ truyện tranh, hãy thử tìm con ốc mượn hồn khác");
                    }
                }
            case ConstMob.HEO_XAYDA_ME:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_14_1) {
                    if (canDrop(1, 100)) {
                        itemMap = new ItemMap(zone, 85, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player,
                                "Con heo xayda mẹ này không giữ truyện tranh, hãy thử tìm con heo xayda mẹ khác");
                    }
                }
            case ConstMob.OC_SEN:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_14_1) {
                    if (canDrop(1, 100)) {
                        itemMap = new ItemMap(zone, 85, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player,
                                "Con ốc xên này không giữ truyện tranh, hãy thử tìm con ốc xên khác");
                    }
                }
        }
        if (itemMap != null) {
            return itemMap;
        }
        return null;
    }

    private void sendMobStillAliveAffterAttacked(int dameHit, boolean crit) {
        Message msg;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(crit); // chí mạng
            msg.writer().writeInt(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hoiSinhMobPhoBan() {
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(this.lvMob); // level mob
            msg.writer().writeInt(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hoiSinhMobTayKarin() {
        this.point.hp = this.point.maxHp;
        this.maxTiemNang = 1;
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(this.lvMob); // level mob
            msg.writer().writeInt(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            this.sendSieuQuai(this.lvMob > 0 ? 1 : 0);
            this.sendMobMaxHp(this.point.hp);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendSieuQuai(int type) {
        Message msg;
        try {
            msg = new Message(-75);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(type);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendDisable(boolean bool) {
        Message msg;
        try {
            msg = new Message(81);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendDoneMove(boolean bool) {
        Message msg;
        try {
            msg = new Message(82);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendFire(boolean bool) {
        Message msg;
        try {
            msg = new Message(85);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendIce(boolean bool) {
        Message msg;
        try {
            msg = new Message(86);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendWind(boolean bool) {
        Message msg;
        try {
            msg = new Message(88);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendMobMaxHp(int maxHp) {
        Message msg;
        try {
            msg = new Message(87);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(maxHp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    private void phanSatThuong(Player plTarget, long dame) {
        if (plTarget.nPoint == null) {
            return;
        }
        int percentPST = plTarget.nPoint.tlPST;
        if (percentPST != 0) {
            int damePST = (int) (long) (dame * percentPST / 100L);
            Message msg;
            try {
                msg = new Message(-9);
                msg.writer().writeByte(this.id);
                if (damePST >= this.point.hp) {
                    damePST = this.point.hp - 1;
                }
                int hpMob = this.point.hp;
                injured(null, damePST, true);
                damePST = hpMob - this.point.hp;
                msg.writer().writeInt(this.point.hp);
                msg.writer().writeInt(damePST);
                msg.writer().writeBoolean(false);
                msg.writer().writeByte(36);
                Service.gI().sendMessAllPlayerInMap(this.zone, msg);
                msg.cleanup();
            } catch (IOException e) {
            }
        }
    }

    public void startDie() {
        Message msg;
        try {
            setDie();
            this.point.hp = -1;
            this.status = 0;
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            this.sendSieuQuai(0);
            msg.cleanup();
        } catch (IOException e) {
        }
    }
}
