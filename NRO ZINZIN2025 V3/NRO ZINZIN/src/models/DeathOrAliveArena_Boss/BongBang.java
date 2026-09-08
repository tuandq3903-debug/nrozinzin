package models.DeathOrAliveArena_Boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.boss.BossesData;

import models.player.Player;

public class BongBang extends DeathOrAliveArena {

    public BongBang(Player player) throws Exception {
        super( BossID.BONG_BANG, BossesData.BONG_BANG);
        this.playerAtt = player;
    }
}
