package models.ClanWar;

import java.util.*;
import models.player.Player;

public class ClanWarManager {
    private static ClanWarManager instance;
    // Lưu trữ thành viên tham chiến theo clanId
    private final Map<Integer, Set<Player>> warParticipants = new HashMap<>();

    private ClanWarManager() {}

    public static ClanWarManager gI() {
        if (instance == null) {
            instance = new ClanWarManager();
        }
        return instance;
    }

    // Người chơi gia nhập chiến trường
    public void joinWar(Player player) {
        int clanId = player.clan.id;
        warParticipants.computeIfAbsent(clanId, k -> new HashSet<>()).add(player);
    }

    // Kiểm tra người chơi có đang tham chiến không
    public boolean isParticipating(Player player) {
        Set<Player> set = warParticipants.get(player.clan.id);
        return set != null && set.contains(player);
    }

    // Người chơi thoát chiến trường (kiệt sức hoặc chết)
    public void leaveWar(Player player) {
        Set<Player> set = warParticipants.get(player.clan.id);
        if (set != null) set.remove(player);
    }

    public Map<Integer, Set<Player>> getWarParticipants() {
        return warParticipants;
    }
}