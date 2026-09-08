package models.The23rdMartialArtCongress_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;

import models.player.Player;

public class LiuLiu extends The23rdMartialArtCongress {

    public LiuLiu(Player player) throws Exception {
        super( BossID.LIU_LIU, BossesData.LIU_LIU);
        this.playerAtt = player;
    }
}
