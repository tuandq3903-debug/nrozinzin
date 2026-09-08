package models.The23rdMartialArtCongress_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;

import models.player.Player;

public class TauPayPay extends The23rdMartialArtCongress {

    public TauPayPay(Player player) throws Exception {
        super( BossID.TAU_PAY_PAY, BossesData.TAU_PAY_PAY);
        this.playerAtt = player;
    }
}
