package models.npc.manifest;

import models.npc.NonInteractiveNPC;
import models.player.Player;
import models.map.Zone;
import services.MapService;
import services.PlayerService;
import services.Service;
import utils.Util;

// dev ZINZIN
public class EscortPetRabbit extends NonInteractiveNPC {

    private final Player owner;
    private final int maxDistance;   // thất bại nếu > maxDistance
    private final int keepDistance;  // mục tiêu bám theo
    private boolean finished;        // đã bàn giao
    private long lastZoneSync = 0;
    private static final long ZONE_SYNC_COOLDOWN_MS = 600;
    private long graceUntil = 0;
    
    public EscortPetRabbit(Player owner) {
        this.owner = owner;
        this.maxDistance = 300;
        this.keepDistance = 200;
        this.name = "Thỏ Lạc";
        this.gender = 0;

        // 1686 là sprite phần head (kiểu dữ liệu hiện có)
        this.head = 1619;
        this.body = 1620;
        this.leg  = 1621;

        this.nPoint.hpMax = 100;
        this.nPoint.hpg   = 100;
        this.nPoint.hp    = 100;
        this.nPoint.mpMax = 1;
        this.nPoint.mpg   = 1;
        this.nPoint.mp    = 1;
        this.nPoint.dame  = 0;
        this.nPoint.dameg = 0;
        this.nPoint.def   = 0;

        this.isPet = false;
        this.isNewPet = false;
    }

    public void spawnNearOwner() {
        Zone z = owner.zone;
        if (z == null) return;
        this.location.x = owner.location.x + Util.nextInt(-40, 40);
        this.location.y = owner.location.y;
        MapService.gI().goToMap(this, z);
        z.load_Me_To_Another(this);
    }

    private int distanceToOwner() {
        int dx = Math.abs(this.location.x - owner.location.x);
        int dy = Math.abs(this.location.y - owner.location.y);
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    public void finishAndDespawn() {
        finished = true;
        if (this.zone != null) {
            this.zone.removePlayer(this);
        }
        this.beforeDispose = true;
        Service.gI().sendThongBao(owner, "Thỏ đã rời đi.");
    }

    private void failAndDespawn(String reason) {
        if (owner != null) {
            Service.gI().sendThongBao(owner, reason != null ? reason : "Bạn đã để lạc mất Thỏ!");
            owner.escortRabbit = null;
        }
        if (this.zone != null) {
            this.zone.removePlayer(this);
        }
        this.beforeDispose = true;
    }

    @Override
    public void update() {
        super.update();
        if (beforeDispose || finished) return;

        if (owner == null || owner.beforeDispose) {
            failAndDespawn("Nhiệm vụ thất bại!");
            return;
        }
        if (this.zone == null || owner.zone == null || this.zone != owner.zone) {
            long now = System.currentTimeMillis();
            if (now - lastZoneSync >= ZONE_SYNC_COOLDOWN_MS) {
                lastZoneSync = now;
                try {
                    Zone oz = owner.zone;
                    if (oz != null) {
                        this.location.x = owner.location.x + Util.nextInt(-40, 40);
                        this.location.y = owner.location.y;
                        MapService.gI().goToMap(this, oz);
                        oz.load_Me_To_Another(this);
                        graceUntil = now + 800;
                    }
                } catch (Exception ignored) {}
            }
            return; 
        }
        int d = distanceToOwner();
        if (System.currentTimeMillis() < graceUntil) {
            if (d > keepDistance) {
                int targetX = owner.location.x + (owner.location.x > this.location.x ? -40 : 40);
                int targetY = owner.location.y;
                PlayerService.gI().playerMove(this, targetX, targetY);
            }
            return;
        }
        if (d > maxDistance) {
            failAndDespawn("Bạn đi quá xa, Thỏ đã chạy mất!");
            return;
        }
        if (d > keepDistance) {
            int targetX = owner.location.x + (owner.location.x > this.location.x ? -40 : 40);
            int targetY = owner.location.y;
            PlayerService.gI().playerMove(this, targetX, targetY);
        }
    }
}
