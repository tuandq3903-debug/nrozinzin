/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.BaConSoi;
import models.item.Item;
import models.boss.Boss;
import models.boss.BossID;
import models.boss.BossesData;
import consts.ConstPlayer;
import java.util.Random;
import models.map.ItemMap;
import models.player.Player;
import models.Top.RealTop;
import services.EffectSkillService;
import services.Service;
import services.TaskService;
import utils.Util;

/**
 *
 * @author Administrator
 */
public class BaConSoi extends Boss {

    private long st;

    public BaConSoi() throws Exception {
        super(BossID.BA_CON_SOI, BossesData.BA_CON_SOI_1, BossesData.BA_CON_SOI_2, BossesData.BA_CON_SOI_3);
    }

    @Override
    public void reward(Player plKill) {
        Util.dropItemBossSoi(this, plKill);
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    @Override
    public void joinMap() {
        super.joinMap(); // To change body of generated methods, choose Tools | Templates.
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
}
