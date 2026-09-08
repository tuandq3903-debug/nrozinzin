package models.Baby;

import models.boss.Boss;
import models.boss.BossID;
import models.boss.BossStatus;
import models.boss.BossesData;
import models.item.Item;
import models.map.ItemMap;
import models.player.Player;
import services.Service;
import services.func.ChangeMapService;
import services.EffectSkillService;
import utils.Util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Boss Baby Monkey Yellow - xử lý logic xuất hiện, rời bản đồ và thưởng khi bị hạ.
 */
public class BabyMonKeyYellow extends Boss {

    // Thời điểm boss tham gia bản đồ hoặc được reset thời gian
    private long st;

    // Các tùy chọn mặc định cho ItemMap khi rơi vật phẩm đặc biệt
    private static final List<Item.ItemOption> DEFAULT_OPTIONS = Arrays.asList(
        new Item.ItemOption(50, 28),
        new Item.ItemOption(77, 28),
        new Item.ItemOption(103, 28),
        new Item.ItemOption(94, 12),
        new Item.ItemOption(5, 12),
        new Item.ItemOption(204, 17)
    );

    public BabyMonKeyYellow() throws Exception {
        super(BossID.BABY_MONKEY, BossesData.BABY_MONKEY);
    }

    /**
     * Gọi khi boss bị hạ bởi người chơi.
     */
    @Override
    public void reward(Player killer) {
        if (killer == null) {
            return;
        }
        // Cộng điểm thành tích săn boss
        killer.effect.addPointTrumSanBoss();

        // Rơi vật phẩm theo tỉ lệ xác suất
        if (Util.isTrue(8, 30)) {
            int itemId = Item.itemIds_tl_GN[ThreadLocalRandom.current()
                .nextInt(Item.itemIds_tl_GN.length)];
            Service.gI().dropItemMap(
                zone,
                Util.ratiDTL(zone, itemId, 1, location.x, location.y, killer.id)
            );
        } else if (Util.isTrue(15, 50)) {
            int itemId = Item.itemIds_tl_AWJ[ThreadLocalRandom.current()
                .nextInt(Item.itemIds_tl_AWJ.length)];
            Service.gI().dropItemMap(
                zone,
                Util.ratiDTL(zone, itemId, 1, location.x, location.y, killer.id)
            );
        } else if (Util.isTrue(15, 100)) {
            int dropX = location.x;
            int dropY = zone.map.yPhysicInTop(dropX, location.y - 24);
            ItemMap specialItem = new ItemMap(zone, 1763, 1, dropX, dropY, killer.id);
            DEFAULT_OPTIONS.forEach(specialItem.options::add);
            Service.gI().dropItemMap(zone, specialItem);
        }
    }

    /**
     * Khi boss gia nhập bản đồ cùng parentBoss.
     */
    @Override
    public void joinMap() {
        this.st = System.currentTimeMillis();
        if (parentBoss != null) {
            this.zone = parentBoss.zone;
            int x = parentBoss.location.x + ThreadLocalRandom.current().nextInt(-100, 101);
            int y = parentBoss.location.y;
            ChangeMapService.gI().changeMap(this, zone, x, y);
            Service.gI().sendFlagBag(this);
            notifyJoinMap();
            changeStatus(BossStatus.CHAT_S);
        }
    }

    /**
     * Kết thúc giai đoạn chat khởi tạo, chuyển sang active.
     */
    @Override
    public void doneChatE() {
        if (parentBoss != null
                && parentBoss.bossAppearTogether != null
                && parentBoss.bossAppearTogether[parentBoss.currentLevel] != null) {
            parentBoss.changeStatus(BossStatus.ACTIVE);
        }
    }

    /**
     * Boss tự rời bản đồ (khi nghỉ hoặc hết thời gian).
     */
    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        lastZone = null;
        lastTimeRest = System.currentTimeMillis();
        changeStatus(BossStatus.REST);
    }

    /**
     * Thực hiện tự động rời bản đồ nếu quá thời gian hoặc không có player.
     */
    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 900_000)) {
            leaveMapNew();
        } else if (zone != null && zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    /**
     * Xử lý khi boss bị tấn công.
     */
    @Override
    public synchronized int injured(Player attacker, long damage, boolean piercing, boolean isMobAttack) {
        if (isDie()) {
            return 0;
        }
        // Khả năng né đòn
        if (!piercing && Util.isTrue(nPoint.tlNeDon, 1000)) {
            chat("Xí hụt");
            return 0;
        }
        // Tính damage sau khi phòng ngự
        damage = nPoint.subDameInjureWithDeff(damage / 7);
        if (!piercing && effectSkill.isShielding) {
            if (damage > nPoint.hpMax) {
                EffectSkillService.gI().breakShield(this);
            }
            damage /= 5;
        }
        // Khấu trừ máu
        nPoint.subHP(damage);
        if (isDie()) {
            setDie(attacker);
            die(attacker);
        }
        return (int) damage;
    }
}