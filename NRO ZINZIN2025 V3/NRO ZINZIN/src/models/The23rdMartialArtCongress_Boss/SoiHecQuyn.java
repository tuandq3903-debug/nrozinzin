package models.The23rdMartialArtCongress_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;

import models.player.Player;

public class SoiHecQuyn extends The23rdMartialArtCongress {

    public SoiHecQuyn(Player player) throws Exception {
        super( BossID.SOI_HEC_QUYN, BossesData.SOI_HEC_QUYN);
        this.playerAtt = player;
    }
}
