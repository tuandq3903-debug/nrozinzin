package models.The23rdMartialArtCongress_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;

import models.player.Player;

public class Pocolo extends The23rdMartialArtCongress {

    public Pocolo(Player player) throws Exception {
        super( BossID.JACKY_CHUN, BossesData.JACKY_CHUN);
        this.playerAtt = player;
    }
}
