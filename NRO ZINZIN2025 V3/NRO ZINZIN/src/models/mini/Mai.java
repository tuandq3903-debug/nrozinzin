package models.mini;

/*
 * @Author: NgocRongMeow
 * @Description: Source Không Có Vấn Đề - Vấn Đề Nằm Ở Chính Bản Thân Bạn
 * @Group Zalo: https://zalo.me/g/qabzvn331
 */
import models.boss.Boss;
import models.boss.BossID;
import models.boss.BossStatus;
import models.boss.BossesData;
import java.util.Random;
import models.map.ItemMap;

import models.item.Item;
import models.player.Player;
import services.EffectSkillService;
import services.Service;
import services.TaskService;
import utils.Util;

public class Mai extends Boss {

    public Mai() throws Exception {
        super(BossID.MAI, false, true, BossesData.MAI);
    }

    @Override
    public void reward(Player plKill) {
        short itTemp = 636;
        ItemMap it = new ItemMap(zone, itTemp, 1, this.location.x + Util.nextInt(-50, 50), this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
        it.options.add(new Item.ItemOption(50, Util.nextInt(10, 10)));
        it.options.add(new Item.ItemOption(148, Util.nextInt(33, 33)));
        it.options.add(new Item.ItemOption(101, Util.nextInt(10, 10)));
        it.options.add(new Item.ItemOption(151, Util.nextInt(1, 1)));
        it.options.add(new Item.ItemOption(152, Util.nextInt(1, 1)));
        it.options.add(new Item.ItemOption(108, Util.nextInt(2, 40)));
        it.options.add(new Item.ItemOption(93, 7));
        it.options.add(new Item.ItemOption(30, Util.nextInt(1, 1)));
        Service.gI().dropItemMap(this.zone, it);
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

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            damage = 1; // luôn bằng 1

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
}
