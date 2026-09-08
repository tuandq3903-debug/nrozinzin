package models.Yardart_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;


public class TANBINH4 extends Yardart {

    public TANBINH4() throws Exception {
        super( BossID.TAN_BINH_4, BossesData.TAN_BINH_4);
    }

    @Override
    protected void init() {
        x = 993;
        x2 = 1063;
        y = 456;
        y2 = 456;
        range = 1000;
        range2 = 150;
        timeHoiHP = 25000;
        rewardRatio = 4;
    }
}
