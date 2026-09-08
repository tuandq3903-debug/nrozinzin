package models.Nappa;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.Boss;
import models.boss.BossID;
import models.boss.BossStatus;
import models.boss.BossesData;
import utils.Util;

public class Rambo extends Boss {

    private long st;

    public Rambo() throws Exception {
        super(BossID.RAMBO, true, true, BossesData.RAMBO);
    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 900000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
//        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
//            st = System.currentTimeMillis();
//        }
    }
}
