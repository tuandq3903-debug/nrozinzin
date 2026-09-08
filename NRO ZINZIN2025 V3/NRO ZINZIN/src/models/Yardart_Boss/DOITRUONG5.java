package models.Yardart_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;


public class DOITRUONG5 extends Yardart {

    public DOITRUONG5() throws Exception {
        super( BossID.DOI_TRUONG_5, BossesData.DOI_TRUONG_5);
    }

    @Override
    protected void init() {
        x = 1199;
        x2 = 1269;
        y = 456;
        y2 = 456;
        range = 1000;
        range2 = 150;
        timeHoiHP = 15000;
        rewardRatio = 2;
    }
}
