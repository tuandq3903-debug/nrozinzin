package models.event.event_manifest;

/**
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import consts.ConstNpc;
import models.event.Event;

public class Halloween extends Event {

    @Override
    public void npc() {
        createNpc(5, 82, 242, 288);
    }

    @Override
    public void boss() {
        createBoss(BossID.BIMA, 10);
        createBoss(BossID.MATROI, 10);
        createBoss(BossID.DOI, 10);
    }
}
