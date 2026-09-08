package models.Frieza;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.Boss;
import models.boss.BossID;
import models.boss.BossStatus;
import models.boss.BossesData;
import models.map.ItemMap;
import models.player.Player;
import services.Service;
import services.TaskService;
import utils.Util;

public class Fide extends Boss {

    private long st;

    public Fide() throws Exception {
        super(BossID.FIDE, BossesData.FIDE_DAI_CA_1, BossesData.FIDE_DAI_CA_2, BossesData.FIDE_DAI_CA_3);
    }

    @Override
public void reward(Player plKill) {
    // điểm săn boss như cũ
    plKill.effect.addPointTrumSanBoss();

    // 15% rơi item thường (ID 19)
    if (Util.isTrue(20, 100)) {
        ItemMap it = new ItemMap(
            this.zone,
            19, // template ID cũ
            1,
            this.location.x,
            this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
            plKill.id
        );
        Service.gI().dropItemMap(this.zone, it);
    }

    // 15% (có thể điều chỉnh) rơi trứng đệ ID = 1855
    if (Util.isTrue(1, 100)) {
        ItemMap egg = new ItemMap(
            this.zone,
            1855, // template ID trứng đệ
            1,
            this.location.x,
            this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
            plKill.id
        );
        Service.gI().dropItemMap(this.zone, egg);
    }

    // xử lý nhiệm vụ kill boss
    TaskService.gI().checkDoneTaskKillBoss(plKill, this);
}

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
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
