package models.npc.manifest;

/**
 * @author ZINZIN
 */
import consts.ConstNpc;
import models.npc.Npc;
import models.player.Player;
import models.Top.RealTop;
import services.NpcService;
import models.Top.TopService;

public class DaiThienSu extends Npc {

    public DaiThienSu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
    }

    @Override
    public void confirmMenu(Player player, int select) {
        
    }
}
