package models.The23rdMartialArtCongress_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;

import models.player.Player;

public class PonPut extends The23rdMartialArtCongress {

    public PonPut(Player player) throws Exception {
        super( BossID.PON_PUT, BossesData.PON_PUT);
        this.playerAtt = player;
    }
}
