package services;

/*
 *
 *
 * @author ZINZIN
 */

import consts.ConstNpc;
import models.npc.Npc;
import models.npc.NpcFactory;
import models.player.Player;
import models.Top.RealTop;
import network.Message;
import server.Load_Database;
import services.func.Input;
import services.func.UseItem;
import utils.Logger;

public class NpcService {

    private static NpcService i;

    public static NpcService gI() {
        if (i == null) {
            i = new NpcService();
        }
        return i;
    }

    public void createMenuRongThieng(Player player, int indexMenu, String npcSay, String... menuSelect) {
        createMenu(player, indexMenu, ConstNpc.RONG_THIENG, -1, npcSay, menuSelect);
    }

    public void createMenuConMeo(Player player, int indexMenu, int avatar, String npcSay, String... menuSelect) {
        createMenu(player, indexMenu, ConstNpc.CON_MEO, avatar, npcSay, menuSelect);
    }

    public void createMenuConMeo(Player player, int indexMenu, int avatar, String npcSay, String[] menuSelect, Object object) {
        NpcFactory.PLAYERID_OBJECT.put(player.id, object);
        createMenuConMeo(player, indexMenu, avatar, npcSay, menuSelect);
    }
    
    public void confirmMenu(Player player, int select) throws Exception {
    int indexMenu = player.iDMark.getIndexMenu();
    Logger.warning("confirmMenu ▶ indexMenu=" + indexMenu + ", select=" + select);
    switch (indexMenu) {
        case ConstNpc.MENU_BOT: {
            switch (select) {
                case 0: Input.gI().createFormBotQuai(player); break;
                case 1: Input.gI().createFormBotItem(player); break;
                case 2: Input.gI().createFormBotBoss(player); break;
                case 3: Service.gI().sendThongBao(player, "Đã đóng menu Bot."); break;
            }
            break;
        }
        
        default: {
            Logger.warning("NpcService.confirmMenu: không xử lý indexMenu="
                + indexMenu + ", select=" + select);
        }
    }
}




    private void createMenu(Player player, int indexMenu, byte npcTempId, int avatar, String npcSay, String... menuSelect) {
        if (player == null || !player.isPl()) {
            return;
        }
        Message msg;
        try {
            player.iDMark.setIndexMenu(indexMenu);
            msg = new Message(32);
            msg.writer().writeShort(npcTempId);
            msg.writer().writeUTF(npcSay);
            msg.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                msg.writer().writeUTF(menu);
            }
            if (avatar != -1) {
                msg.writer().writeShort(avatar);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(NpcService.class, e);
        }
    }

    public void createTutorial(Player player, int avatar, String npcSay) {
        Message msg;
        try {
            msg = new Message(38);
            msg.writer().writeShort(ConstNpc.CON_MEO);
            msg.writer().writeUTF(npcSay);
            if (avatar != -1) {
                msg.writer().writeShort(avatar);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createTutorial(Player player, int tempId, int avatar, String npcSay) {
        Message msg;
        try {
            msg = new Message(38);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(npcSay);
            if (avatar != -1) {
                msg.writer().writeShort(avatar);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public int getAvatar(int npcId) {
        for (Npc npc : Load_Database.NPCS) {
            if (npc.tempId == npcId) {
                return npc.avartar;
            }
        }
        return 1139;
    }
    
    public void createBigMessage(Player player, int avatar, String npcSay, byte type, String select, String confirn) {
        Message msg;
        try {
            msg = new Message(-70);
            msg.writer().writeShort(avatar);
            msg.writer().writeUTF(npcSay);
            msg.writer().writeByte(type);
            if (type == 1) {
                msg.writer().writeUTF(confirn);// select
                msg.writer().writeUTF(select);// string Select
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception ex) {
        }
    }
}
