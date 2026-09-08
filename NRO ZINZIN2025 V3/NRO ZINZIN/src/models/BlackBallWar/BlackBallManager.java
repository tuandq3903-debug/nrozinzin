package models.BlackBallWar;

import utils.Functions;
import models.BlackBallWar.BlackBallWarService;
import server.Maintenance;
import server.Maintenance;
import utils.Util;

public class BlackBallManager implements Runnable {

    private volatile long lastUpdate;

    private static class SingletonHelper {

        private static final BlackBallManager INSTANCE = new BlackBallManager();
    }

    public static BlackBallManager gI() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                long start = System.currentTimeMillis();
                update();
                Functions.sleep(Math.max(1000 - (System.currentTimeMillis() - start), 10));
            } catch (Exception ex) {
            }
        }
    }

    public void update() {
        if (Util.canDoWithTime(lastUpdate, 1000)) {
            lastUpdate = System.currentTimeMillis();
            for (int i = BlackBallWarService.gI().blackBallWars.size() - 1; i >= 0; i--) {
                if (i < BlackBallWarService.gI().blackBallWars.size()) {
                    BlackBallWarService.gI().blackBallWars.get(i).update();
                }
            }
        }
    }

}
