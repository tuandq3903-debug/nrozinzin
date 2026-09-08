package models.npc.manifest;

/**
 *
 * @author ZINZIN
 */

import models.npc.Npc;
import models.player.Player;
import models.shop.ShopService;

public class Uron extends Npc {

    public Uron(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player pl) {
        if (canOpenNpc(pl)) {
            ShopService.gI().opendShop(pl, "URON", false);
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {

        }
    }
}
