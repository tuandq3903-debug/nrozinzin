package services;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import models.ClanWar.ClanWarManager;
import models.player.Player;
import services.func.ChangeMapService;
import services.Service;
import services.ItemService;
import services.InventoryService;
import utils.TimeUtil;

/**
 * Dịch vụ Đại Chiến Bang Hội
 */
public class ClanWarService {
    static final int WAR_MAP = 145;
    private static final int LOBBY_MAP = 13;
    private static final int LOBBY_X = 100;
    private static final int LOBBY_Y = 100;
    private static final int WAR_SPAWN_X = 754;
    private static final int WAR_SPAWN_Y = 160;

    private static final int[] REWARD_IDS = {1849, 1850, 1851, 1852};

    private static ClanWarService instance;
    private final Timer timer = new Timer(true);

    // Danh sách đã tham gia và bị loại
    private final Set<Long> joinedPlayers = new HashSet<>();
    private final Set<Long> disqualifiedPlayers = new HashSet<>();

    private ClanWarService() {
        scheduleRewards();
    }

    public static synchronized ClanWarService gI() {
        if (instance == null) {
            instance = new ClanWarService();
        }
        return instance;
    }

    /**
     * Xử lý khi người chơi kết nối lại sau mất kết nối.
     * Nếu vẫn còn tham chiến và chưa bị loại, đưa họ trở lại chiến trường.
     */
    public void handleReconnect(Player player) {
        long pid = player.id;
        if (joinedPlayers.contains(pid) && !disqualifiedPlayers.contains(pid)) {
            ChangeMapService.gI().changeMapNonSpaceship(player, WAR_MAP, WAR_SPAWN_X, WAR_SPAWN_Y);
            ClanWarManager.gI().joinWar(player);
            Service.gI().sendThongBao(player, "Bạn đã được đưa trở lại Đại Chiến Bang Hội");
        }
    }

    /**
     * Người chơi đăng ký tham chiến (chỉ 1 lần, không thể đăng ký lại sau khi bị loại).
     */
    public void joinWar(Player player) {
        long pid = player.id;
        if (disqualifiedPlayers.contains(pid)) {
            Service.gI().sendThongBao(player, "Bạn đã bị loại, không thể tham gia lại");
            return;
        }
        if (joinedPlayers.contains(pid)) {
            Service.gI().sendThongBao(player, "Bạn đã tham gia rồi, không thể tham gia thêm");
            return;
        }
        joinedPlayers.add(pid);
        ChangeMapService.gI().changeMapNonSpaceship(player, WAR_MAP, WAR_SPAWN_X, WAR_SPAWN_Y);
        final int MAX_ICONS = 12; 
        int flagIndex = player.clan.imgId % MAX_ICONS;
        if (flagIndex < 0) flagIndex += MAX_ICONS;
        Service.gI().changeFlag(player, flagIndex);
        ClanWarManager.gI().joinWar(player);
        Service.gI().sendThongBao(player, "Bạn đã tham gia Đại Chiến Bang Hội");
    }

    /**
     * Xử lý khi người chơi chết trong chiến trường: loại khỏi cuộc chiến, không thể hồi sinh,
     * teleport về lobby và gửi thông báo.
     */
    public void onPlayerDeath(Player player) {
        long pid = player.id;
        if (player.zone.map.mapId == WAR_MAP && ClanWarManager.gI().isParticipating(player)) {
            ClanWarManager.gI().leaveWar(player);
            disqualifiedPlayers.add(pid);
            ChangeMapService.gI().changeMapNonSpaceship(player, LOBBY_MAP, LOBBY_X, LOBBY_Y);
            Service.gI().sendThongBao(player, "Bạn đã bị loại khỏi Đại Chiến Bang Hội và không thể hồi sinh");
        }
    }

    /**
     * Kiểm tra xem người chơi đã bị loại chưa.
     */
    public boolean isDisqualified(Player player) {
        return disqualifiedPlayers.contains(player.id);
    }

    /**
     * Lên lịch phát thưởng vào 21h hàng ngày.
     */
    private void scheduleRewards() {
        long delay = TimeUtil.getMillisUntilNextHour(21);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                rewardSurvivors();
            }
        }, delay, 24 * 60 * 60 * 1000);
    }

    /**
     * Phát thưởng cho những người còn sống sót trong WAR_MAP lúc 21h, sau đó teleport họ về lobby
     * và reset trạng thái cuộc chiến.
     */
    private void rewardSurvivors() {
        Random rand = new Random();
        // Phát thưởng
        for (Set<Player> players : ClanWarManager.gI().getWarParticipants().values()) {
            for (Player p : new HashSet<>(players)) {
                if (p.zone.map.mapId == WAR_MAP) {
                    short itemId = (short) REWARD_IDS[rand.nextInt(REWARD_IDS.length)];
                    var item = ItemService.gI().createNewItem(itemId);
                    InventoryService.gI().addItemBag(p, item);
                    Service.gI().sendThongBao(p, "Bạn nhận được phần thưởng: item " + itemId);
                }
            }
        }
        // Teleport survivors về
        for (Set<Player> players : ClanWarManager.gI().getWarParticipants().values()) {
            for (Player p : new HashSet<>(players)) {
                if (p.zone.map.mapId == WAR_MAP) {
                    ChangeMapService.gI().changeMapNonSpaceship(p, LOBBY_MAP, LOBBY_X, LOBBY_Y);
                }
            }
        }
        // Reset
        ClanWarManager.gI().getWarParticipants().clear();
        joinedPlayers.clear();
        disqualifiedPlayers.clear();
    }
}
