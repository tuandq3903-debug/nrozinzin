package models.event.event_manifest;

/**
 *
 * @author ZINZIN
 */

import consts.ConstNpc;
import models.event.Event;
import jdbc.daos.EventDAO;

public class InternationalWomensDay extends Event {

    @Override
    public void init() {
        super.init();
        EventDAO.loadInternationalWomensDayEvent();
    }

    @Override
    public void npc() {
    }
}
