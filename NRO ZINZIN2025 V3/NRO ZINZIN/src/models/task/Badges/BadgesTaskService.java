package models.task.Badges;

import models.item.Item;
import models.item.Item.ItemOption;
import models.player.Player;
import models.player.badges.BadgesData;
import server.Load_Database;
import models.Top.RealTop;

public class BadgesTaskService {

    public static void createAndResetTask(Player player) { // xử lý nếu chưa có nhiệm vụ hoặc reset hằng ngày.
        player.dataTaskBadges.clear();
        for (BadgesTaskTemplate BTT : Load_Database.TASKS_BADGES_TEMPLATE) {
            BadgesTask data = new BadgesTask();
            data.id = BTT.id;
            data.count = 0;
            data.countMax = BTT.count;
            data.idBadgesReward = BTT.idbadgesReward;
            player.dataTaskBadges.add(data);
        }
    }

    public static void updateDoneTask(Player player) { // xử lý nếu hoàn thành nhiệm vụ tự add vào người
        for (BadgesTask data : player.dataTaskBadges) {
            if (data.isDone()) {
                for (BadgesData bg : player.dataBadges) {
                    if (bg.idBadGes == data.idBadgesReward) {
                        return;
                    }
                }
                BadgesData danhHieu = new BadgesData(player, data.idBadgesReward, 30);
                player.dataBadges.add(danhHieu);
                data.count = 0;
            }
        }
    }

    public static void updateCountBagesTask(Player player, int id, int amount) {
        for (BadgesTask data : player.dataTaskBadges) {
            if (data.id == id) {
                data.count += amount;
                if (data.count > data.countMax) {
                    data.count = data.countMax;
                }
                break;
            }
        }
    }

    public static void updateCountBagesTaskWithMiniDragon(Player player) {
        int count = (int) player.inventory.itemsBag.stream().filter(item -> item.isNotNullItem() && (item.template.id >= 1813 && item.template.id <= 1819) && item.itemOptions.stream().noneMatch(ItemOption::haveExpiryDate)).count();
            updateCountBagesTask(player, consts.ConstTaskBadges.ME_RONG,count);
        if (count >= 7) {
            updateDoneTask(player);
        }
    }

    public static int sendPercenBadgesTask(Player player, int idBadgesReward) {
        for (BadgesTask data : player.dataTaskBadges) {
            if (data.idBadgesReward == idBadgesReward) {
                if (data.getPercentProcess() > 0) {
                    return data.getPercentProcess();
                } else {
                    return 0;
                }
            }
        }
        return 0;
    }

    public static int sendDay(Player player, int id) {
        for (BadgesData data : player.dataBadges) {
            if (data.idBadGes == id) {
                long timeDifference = data.timeofUseBadges - System.currentTimeMillis();
                return (int) (timeDifference / (24 * 60 * 60 * 1000L));
            }
        }
        return 0;
    }

}
