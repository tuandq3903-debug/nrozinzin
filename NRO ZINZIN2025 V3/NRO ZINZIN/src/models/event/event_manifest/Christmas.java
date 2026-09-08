package models.event.event_manifest;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.event.Event;

public class Christmas extends Event {

    @Override
    public void boss() {
        createBoss(BossID.ONG_GIA_NOEL, 30);
    }
}
