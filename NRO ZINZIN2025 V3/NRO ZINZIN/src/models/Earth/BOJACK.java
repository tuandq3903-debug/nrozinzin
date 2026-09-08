package models.Earth;

/*
 *
 *
 * @author ZINZIN
 */

import models.boss.Boss;
import models.boss.BossID;
import models.boss.BossStatus;
import models.boss.BossesData;
import models.item.Item;
import java.util.List;
import models.map.ItemMap;
import models.player.Player;
import services.ItemService;
import services.Service;
import utils.Util;

public class BOJACK extends Boss {

    private long st;

    public BOJACK() throws Exception {
        super(BossID.BOJACK, false, true, BossesData.BOJACK, BossesData.SUPER_BOJACK);
    }

    @Override
    public void reward(Player plKill) {
        plKill.effect.addPointTrumSanBoss();
//        Service.gI().dropItemMap(this.zone, new ItemMap(zone, 2055, Util.nextInt(1, 10), this.location.x + Util.nextInt(-50, 50), this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        for (int i = 0; i < Util.nextInt(2); i++) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 821, Util.nextInt(1, 3), this.location.x + i * Util.nextInt(-50, 50), this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
        for (int i = 0; i < Util.nextInt(10, 20); i++) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 76, Util.nextInt(5_000, 20_000), this.location.x + i * 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id));
        }
        short itTemp = 427;
        ItemMap it = new ItemMap(zone, itTemp, 1, this.location.x + Util.nextInt(-50, 50), this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
        List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
        if (!ops.isEmpty()) {
            it.options = ops;
        }
        Service.gI().dropItemMap(this.zone, it);
    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 900000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    @Override
    public void doneChatS() {
        if (this.currentLevel == 1) {
            return;
        }
        this.changeStatus(BossStatus.AFK);
    }
}
