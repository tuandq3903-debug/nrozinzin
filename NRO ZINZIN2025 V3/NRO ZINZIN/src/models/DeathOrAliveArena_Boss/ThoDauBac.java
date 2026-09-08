package models.DeathOrAliveArena_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;

import models.player.Player;

public class ThoDauBac extends DeathOrAliveArena {

    public ThoDauBac(Player player) throws Exception {
        super( BossID.THO_DAU_BAC, BossesData.THO_DAU_BAC);
        this.playerAtt = player;
    }
}
