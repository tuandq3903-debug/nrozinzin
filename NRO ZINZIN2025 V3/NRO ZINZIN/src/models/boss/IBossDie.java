package models.boss;

/*
 *
 *
 * @author ZINZIN
 */

import models.player.Player;

public interface IBossDie {

    void doSomeThing(Player playerKill);

    void notifyDie(Player playerKill);

    void rewards(Player playerKill);

    void leaveMap();

}
