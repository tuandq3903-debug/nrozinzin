package models.npc.manifest;

/**
 * @author ZINZIN
 */
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import models.npc.Npc;
import models.player.Player;
import services.Service;
import services.func.ChangeMapService;
import utils.Util;

public class Tapion extends Npc {

    public Tapion(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (mapId == 19) {
                this.createOtherMenu(player, 0, "Ác quỷ truyền thuyết Hirudegarn\nđã thoát khỏi phong ấn ngàn năm\nHãy giúp tôi chế ngự nó", "OK", "Từ chối");
            } else if (mapId == 126) {
                this.createOtherMenu(player, 0, "Tôi sẽ đưa bạn về", "OK", "Từ chối");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (select) {
                case 0 -> {
                    if (mapId == 19) {
                        Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        int second = calendar.get(Calendar.SECOND);
                        if (hour == 22) {
                        long pwr = player.nPoint != null ? player.nPoint.power : 0L;
                        if (minute < 30) {
                            // Slot 1: 22:00–22:29:59 -> chỉ < 20 tỷ
                            if (pwr < 20_000_000_000L) {
                                ChangeMapService.gI().changeMapNonSpaceship(player, 126, 200 + Util.nextInt(-100, 100), 360);
                            } else {
                                Service.gI().sendThongBao(player, "Khung 22:00–22:30 chỉ dành cho sức mạnh dưới 20 tỷ");
                            }
                        } else {
                            // Slot 2: 22:30–22:59:59 -> chỉ > 20 tỷ (STRICT)
                            if (pwr > 20_000_000_000L) {
                                ChangeMapService.gI().changeMapNonSpaceship(player, 126, 200 + Util.nextInt(-100, 100), 360);
                            } else {
                                Service.gI().sendThongBao(player, "Khung 22:30–23:00 chỉ dành cho sức mạnh trên 20 tỷ (20 tỷ đúng bằng không được)");
                            }
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Vui lòng quay lại đúng 22:00–22:59");
                    }
                    } else if (mapId == 126) {
                        ChangeMapService.gI().changeMapNonSpaceship(player, 19, 1000 + Util.nextInt(-100, 100), 360);
                    }
                }
            }
        }
    }
}
