package models.event.event_manifest;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.BossID;
import models.event.Event;

public class HungVuong extends Event {
    
    @Override
    public void npc() {
        createNpc(5, 52, 242, 288);
    }

    @Override
    public void boss() {
        createBoss(BossID.THUY_TINH, 10);
    }
}
