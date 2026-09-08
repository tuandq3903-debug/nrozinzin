package models.event.event_manifest;

/**
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.event.Event;

public class Default extends Event {

    @Override
    public void boss() {
        createBoss(BossID.SUPER_BROLY, 50);
    }

}
