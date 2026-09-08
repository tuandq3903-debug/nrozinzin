package models.npc.manifest;

/**
 *
 * @author ZINZIN
 */

import models.npc.Npc;
import models.player.Player;

public class Vados extends Npc {

    public Vados(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void confirmMenu(Player player, int select) {
    }
}
