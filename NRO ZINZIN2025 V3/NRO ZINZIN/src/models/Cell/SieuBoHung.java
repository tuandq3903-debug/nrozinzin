package models.Cell;

/*
 *
 *
 * @author ZINZIN
 */
import consts.ConstPlayer;
import models.boss.Boss;
import models.boss.BossID;
import models.boss.BossManager;
import models.boss.BossStatus;
import models.boss.BossesData;
import models.map.ItemMap;
import models.player.Player;
import models.Top.RealTop;
import services.*;
import utils.Util;

import java.util.Random;
import server.ThreadPoolManager;

public class SieuBoHung extends Boss {

    private long st;
    public boolean callCellCon;
    private long lastTimeHapThu;
    private int timeHapThu;

    private final String text[] = {"Thưa quý vị và các bạn, đây đúng là trận đấu trời long đất lở",
        "Vượt xa mọi dự đoán của chúng tôi",
        "Eo ơi toàn thân lão Xên bốc cháy kìa"};
    private long lastTimeChat;
    private long lastTimeMove;
    private int indexChat = 0;

    public SieuBoHung() throws Exception {
        super(BossID.SIEU_BO_HUNG, BossesData.SIEU_BO_HUNG_1, BossesData.SIEU_BO_HUNG_2);
    }

    @Override
    protected void resetBase() {
        super.resetBase();
        this.callCellCon = false;
    }

    public void callCellCon() {
        ThreadPoolManager.EXECUTOR.execute(() -> {
            try {
                this.changeStatus(BossStatus.AFK);
                this.changeToTypeNonPK();
                this.recoverHP();
                this.callCellCon = true;
                this.chat("Hãy đấu với 7 đứa con của ta, chúng đều là siêu cao thủ");
                Thread.sleep(2000);
                this.chat("Cứ chưởng tiếp đi haha");
                Thread.sleep(2000);
                this.chat("Liệu mà giữ mạng đấy");
                Thread.sleep(2000);
                for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
                    switch ((int) boss.id) {
                        case BossID.XEN_CON_1 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_2 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_3 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_4 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_5 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_6 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                        case BossID.XEN_CON_7 ->
                            boss.changeStatus(BossStatus.RESPAWN);
                    }
                }
            } catch (Exception e) {
            }
        });
    }

    public void recoverHP() {
        PlayerService.gI().hoiPhuc(this, this.nPoint.hpMax, 0);
    }

    @Override
public void reward(Player plKill) {
    plKill.effect.addPointTrumSanBoss();
    TaskService.gI().checkDoneTaskKillBoss(plKill, this);

    // Rơi item 1229 như cũ
    if (Util.isTrue(5, 50)) {
        for (int i = 0; i < Util.nextInt(25, 50); i++) {
            ItemMap it = new ItemMap(
                this.zone,
                1229,
                1,
                this.location.x + Util.nextInt(-15, 15),
                this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    // ✦ Thêm: 10% cơ hội rơi trứng đệ (template ID = 1854)
    if (Util.isTrue(5, 100)) {
        ItemMap egg = new ItemMap(
            this.zone,
            1854, // ID trứng đệ
            1,
            this.location.x + Util.nextInt(-15, 15),
            this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
            plKill.id
        );
        Service.gI().dropItemMap(this.zone, egg);
    }
}


    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.attack();
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (prepareBom) {
            return 0;
        }
        if (!this.callCellCon && damage >= this.nPoint.hp) {
//            if (Util.isTrue(1, 3)) {
            this.callCellCon();
            return 0;
//            } else {
//                this.callCellCon = true;
//            }
        }
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage / 2);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 2;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                setBom(plAtt);
                return 0;
            }
            return (int) damage;
        } else {
            return 0;
        }

    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void autoLeaveMap() {
        this.mc();
        if (this.currentLevel > 0) {
            if (this.bossStatus == BossStatus.AFK) {
                this.changeStatus(BossStatus.ACTIVE);
            }
        }
        if (Util.canDoWithTime(st, 900000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    public void mc() {
        Player mc = zone.getNpc();
        if (mc != null) {
            if (Util.canDoWithTime(lastTimeChat, 3000)) {
                String textchat = text[indexChat];
                Service.gI().chat(mc, textchat);
                indexChat++;
                if (indexChat == text.length) {
                    indexChat = 0;
                    lastTimeChat = System.currentTimeMillis() + 7000;
                } else {
                    lastTimeChat = System.currentTimeMillis();
                }
            }

            if (Util.canDoWithTime(lastTimeMove, 15000)) {
                if (Util.isTrue(2, 3)) {
                    int x = this.location.x + Util.nextInt(-100, 100);
                    int y = x > 156 && x < 611 ? 288 : 312;
                    PlayerService.gI().playerMove(mc, x, y);
                }
                lastTimeMove = System.currentTimeMillis();
            }
        }
    }

}
