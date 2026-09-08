package models.The23rdMartialArtCongress_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;

import models.player.Player;

public class Yamcha extends The23rdMartialArtCongress {

    public Yamcha(Player player) throws Exception {
        super( BossID.YAMCHA, BossesData.YAMCHA);
        this.playerAtt = player;
    }
}
