package models.The23rdMartialArtCongress_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;

import models.player.Player;

public class ODo extends The23rdMartialArtCongress {

    public ODo(Player player) throws Exception {
        super( BossID.O_DO, BossesData.O_DO);
        this.playerAtt = player;
    }
}
