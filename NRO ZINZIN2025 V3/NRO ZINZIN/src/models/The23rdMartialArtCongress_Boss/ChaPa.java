package models.The23rdMartialArtCongress_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;

import models.player.Player;

public class ChaPa extends The23rdMartialArtCongress {

    public ChaPa(Player player) throws Exception {
        super( BossID.CHA_PA, BossesData.CHA_PA);
        this.playerAtt = player;
    }
}
