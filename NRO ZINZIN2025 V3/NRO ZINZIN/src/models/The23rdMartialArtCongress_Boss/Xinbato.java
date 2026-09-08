package models.The23rdMartialArtCongress_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;

import models.player.Player;

public class Xinbato extends The23rdMartialArtCongress {

    public Xinbato(Player player) throws Exception {
        super( BossID.XINBATO, BossesData.XINBATO);
        this.playerAtt = player;
    }
}
