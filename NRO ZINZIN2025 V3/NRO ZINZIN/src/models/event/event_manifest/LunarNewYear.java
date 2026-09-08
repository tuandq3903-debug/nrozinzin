package models.event.event_manifest;

/**
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.event.Event;

public class LunarNewYear extends Event {

    @Override
    public void npc() {
        createNpc(0, 49, 850, 432);
    }

    @Override
    public void boss() {
        createBoss(BossID.BE_NA, 10);
    }
}
