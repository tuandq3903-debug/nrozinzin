package models.player.badges;

import models.item.Item;
import models.player.Player;
import models.Top.RealTop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import server.Load_Database;

public class BagesTemplate {

    public int id;
    public int idEffect;
    public int idItem;
    public String NAME;
    public List<Item.ItemOption> options = new ArrayList<>();

    public static int findIdItemByIdIdEffect(int idEffect) {
        for (BagesTemplate data : Load_Database.BAGES_TEMPLATES) {
            if (data.idEffect == idEffect) {
                return data.idItem;
            }
        }
        return -1;
    }

    public static int fineIdEffectbyIdItem(int idItem) {
        for (BagesTemplate data : Load_Database.BAGES_TEMPLATES) {
            if (data.idItem == idItem) {
                return data.idEffect;
            }
        }
        return -1;
    }

    public static List<Integer> listEffect(Player player) {
        Set<Integer> setIdItem = new HashSet<>();
        for (BadgesData data : player.dataBadges) {
            for (BagesTemplate temp : Load_Database.BAGES_TEMPLATES) {
                if (temp.idEffect == data.idBadGes) {
                    setIdItem.add(temp.idItem);
                }
            }
        }
        return new ArrayList<>(setIdItem);
    }

    public static List<Item.ItemOption> sendListItemOption(Player player) {
        List<Item.ItemOption> listOptions = new ArrayList<>();
        for (BadgesData data : player.dataBadges) {
            for (BagesTemplate temp : Load_Database.BAGES_TEMPLATES) {
                if (data.idBadGes == temp.idEffect && data.isUse) {
                    listOptions = temp.options;
                }
            }
        }
        return listOptions;
    }

}
