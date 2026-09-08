/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.shop;

import java.util.ArrayList;
import models.player.Player;
import models.skill.Skill;
import services.Service;
import utils.SkillUtil;

/**
 *
 * @author Administrator
 */
public class TabShopHocKynang extends TabShop {

    public TabShopHocKynang(TabShop tabShop, Player player) {
        this.itemShops = new ArrayList<>();
        this.shop = tabShop.shop;
        this.id = tabShop.id;
        this.name = tabShop.name;

        for (ItemShop itemShop : tabShop.itemShops) {
            if (itemShop.temp.gender == player.gender || itemShop.temp.gender > 2) {

                Skill skill = SkillUtil.getSkillByItemID(player, itemShop.temp.id);
                if (skill == null) {
                    // Không phải item học kỹ năng → bỏ qua
                    Service.gI().sendThongBao(player,
                        "Vật phẩm “" + itemShop.temp.name + "” không phải kỹ năng!");
                    return;
                }

                Skill skillPlayer = player.playerSkill.getSkillbyId(skill.template.id);

                byte level;

                String[] subName = itemShop.temp.name.split("");

                try {
                    level = Byte.parseByte(subName[subName.length - 1]);
                } catch (Exception e) {
                    continue;
                }

                if (skillPlayer != null && skillPlayer.point >= level) {
                    continue;
                }

                this.itemShops.add(new ItemShop(itemShop));

//                boolean daHoc = false;
//                for (Integer i : player.CheckHocSkill) {//check xem co ki nang chua
//                    if (skill == i) {
//                        daHoc = true;
//                        break;
//                    }
//                }
//                if (!daHoc) {
//                    this.itemShops.add(new ItemShop(itemShop));
//                }
            }
        }
    }
}
