package models.Cooler;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import models.boss.Boss;
import models.boss.BossID;
import models.boss.BossesData;
import models.item.Item;
import services.EffectSkillService;
import services.Service;
import utils.Util;

import java.util.Random;
import models.player.Player;
import models.Top.RealTop;
import services.TaskService;

public class Chiller extends Boss {

    private long st;

    public Chiller() throws Exception {
        super(BossID.CHILLER, BossesData.CHILLER, BossesData.CHILLER_2);
    }

    @Override
    public void reward(Player plKill) {
        plKill.effect.addPointTrumSanBoss();
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
        byte randomDo = (byte) new Random().nextInt(Item.itemIds_tl_GN.length - 1);
        byte randomDo1 = (byte) new Random().nextInt(Item.itemIds_tl_AWJ.length - 1);
        if (Util.isTrue(8, 30)) {
            Service.gI().dropItemMap(this.zone, Util.ratiDTL(zone, Item.itemIds_tl_GN[randomDo], 1, this.location.x, this.location.y, plKill.id));
        } else if (Util.isTrue(15, 50)) {
            Service.gI().dropItemMap(this.zone, Util.ratiDTL(zone, Item.itemIds_tl_AWJ[randomDo1], 1, this.location.x, this.location.y, plKill.id));
        }
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (this.currentLevel != 0) {
                damage /= 2;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage - Util.nextInt(100000));
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
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
        if (Util.canDoWithTime(st, 900000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

}
