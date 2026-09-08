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

public class Kuku extends Boss {

    private long st;

    public Kuku() throws Exception {
        super(BossID.KUKU, true, true, BossesData.KUKU);
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
