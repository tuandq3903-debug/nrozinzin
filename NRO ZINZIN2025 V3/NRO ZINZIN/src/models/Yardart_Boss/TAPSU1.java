package models.Yardart_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;


public class TAPSU1 extends Yardart {

    public TAPSU1() throws Exception {
        super( BossID.TAP_SU_1, BossesData.TAP_SU_1);
    }

    @Override
    protected void init() {
        x = 376;
        x2 = 446;
        y = 456;
        y2 = 432;
        range = 1000;
        range2 = 150;
        timeHoiHP = 30000;
        rewardRatio = 5;
    }
}
