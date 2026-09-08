package models.Yardart_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;


public class TANBINH3 extends Yardart {

    public TANBINH3() throws Exception {
        super( BossID.TAN_BINH_3, BossesData.TAN_BINH_3);
    }

    @Override
    protected void init() {
        x = 787;
        x2 = 857;
        y = 432;
        y2 = 408;
        range = 1000;
        range2 = 150;
        timeHoiHP = 25000;
        rewardRatio = 4;
    }
}
