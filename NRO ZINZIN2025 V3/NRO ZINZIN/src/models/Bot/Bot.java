package models.Bot;

import consts.ConstPlayer;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import models.map.Map;
import models.map.Zone;
import models.player.Player;
import server.Load_Database;
import services.func.ChangeMapService;
import services.EffectSkillService;
import services.MapService;
import services.PlayerService;
import services.Service;
import services.SkillService;
import models.skill.NClass;
import models.skill.Skill;
import models.Template.SkillTemplate;
import utils.Util;

/**
 * Lớp Bot, kế thừa Player, điều khiển hành vi bot không spawn thread mới,
 * sử dụng BOT_SCHEDULER chung để chạy các task có delay hoặc bất đồng bộ.
 */
public class Bot extends Player {
    private static final int MAX_ZONE_ATTEMPTS = 20;
    private short head_;
    private short body_;
    private short leg_;
    private short flag_;
    private int type;
    private int index_ = 0;
    public ShopBot shop;
    public Sanb boss;
    public Mobb mo1;

    private Player plAttack;

    // Mảng chọn map dựa vào power
    private static final int[] TraiDat = {1,2,3,4,6,29,30,28,27,42};
    private static final int[] Namec   = {8,9,10,11,12,13,33,34,32,31};
    private static final int[] XayDa   = {15,16,17,18,19,20,37,36,35,44,52};

    /**
     * Constructor Bot
     */
    public Bot(short head, short body, short leg, int type, String name, ShopBot shop, short flag) {
        this.head_ = head;
        this.body_ = body;
        this.leg_ = leg;
        this.shop = shop;
        this.name = name;
        this.id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        this.type = type;
        this.isBot = true;
        this.flag_ = flag;
    }

    /**
     * Chọn map dựa vào power của bot
     */
    public int MapToPow() {
        long power = this.nPoint.power;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        if (power < 20_000_000L) {
            if (gender == 0) return TraiDat[rnd.nextInt(TraiDat.length)];
            if (gender == 1) return Namec[rnd.nextInt(Namec.length)];
            return XayDa[rnd.nextInt(XayDa.length)];
        } else if (power < 100_000_000L) {
            return 62 + rnd.nextInt(15);
        } else if (power < 1_000_000_000L) {
            if (rnd.nextInt(100) < 30)      return 91 + rnd.nextInt(3);
            else if (rnd.nextInt(100) < 30) return 95 + rnd.nextInt(5);
            else                             return 102 + rnd.nextInt(2);
        } else {
            if (rnd.nextInt(100) < 30)      return 104 + rnd.nextInt(6);
            else if (rnd.nextInt(100) < 30) return 173 + rnd.nextInt(3);
            else                             return 157 + rnd.nextInt(2);
        }
    }

    /**
     * Bot tham gia map, chọn zone rỗng hoặc ngẫu nhiên
     */
    public void joinMap() {
        Zone zone = getRandomZone(MapToPow());
        if (zone != null) {
            ChangeMapService.gI().goToMap(this, zone);
            this.zone.load_Me_To_Another(this);
            this.mo1.lastTimeChanM = System.currentTimeMillis();
        }
    }

    /**
     * Lấy zone ngẫu nhiên, tối đa MAX_ZONE_ATTEMPTS lần
     */
    public Zone getRandomZone(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        if (map == null) return null;
        List<Zone> zones = map.zones;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int attempt = 0; attempt < MAX_ZONE_ATTEMPTS; attempt++) {
            // 1) ưu tiên zone trống
            for (Zone z : zones) {
                if (z.getNumOfPlayers() == 0) {
                    index_ = 0;
                    return z;
                }
            }
            // 2) chọn ngẫu nhiên nếu chưa full
            Zone z = zones.get(rnd.nextInt(zones.size()));
            if (!z.isFullPlayer()) {
                index_ = 0;
                return z;
            }
        }
        // thử nhiều lần không được, remove bot
        BotManager.gI().bot.remove(this);
        ChangeMapService.gI().exitMap(this);
        return null;
    }

    @Override
    public short getHead() {
        return effectSkill.isMonkey
            ? (short)ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1]
            : head_;
    }

    @Override
    public short getBody() {
        return effectSkill.isMonkey ? (short)193 : body_;
    }

    @Override
    public short getLeg() {
        return effectSkill.isMonkey ? (short)194 : leg_;
    }

    @Override
    public short getFlagBag() {
        return flag_;
    }

    /**
     * Cập nhật bot mỗi chu kỳ
     */
    @Override
    public void update() {
        super.update();
        increasePoint();
        switch (type) {
            case 0: mo1.update(); break;
            case 1: shop.update(); break;
            case 2: boss.update(); break;
        }
        if (isDie()) {
            Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
        }
    }

    /**
     * Clone skill từ NClass
     */
    public void leakSkill() {
        for (NClass n : Load_Database.NCLASS) {
            if (n.classId == gender) {
                for (SkillTemplate tpl : n.skillTemplatess) {
                    if (!tpl.skillss.isEmpty()) {
                        this.playerSkill.skills.add(new Skill(tpl.skillss.get(0)));
                    }
                }
                break;
            }
        }
    }

    
    public boolean UseLastTimeSkill() {
        long now = System.currentTimeMillis();
        long last = this.playerSkill.skillSelect.lastTimeUseThisSkillbot;
        long cd   = this.playerSkill.skillSelect.coolDown;
        if (now - last >= cd) {
            this.playerSkill.skillSelect.lastTimeUseThisSkillbot = now;
            return true;
        }
        return false;
    }

    /**
     * Tăng điểm tự động
     */
    private void increasePoint() {
        if (nPoint == null) return;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        long tiemNangUse;
        int point;
        if (rnd.nextInt(100) < 50) {
            point = 100;
            tiemNangUse = point * (2L * (nPoint.hpg + 1000) + point * 20 - 20) / 2;
            if (doUseTiemNang(tiemNangUse)) {
                nPoint.hpMax += point;
                nPoint.hpg   += point;
                Service.gI().point(this);
            }
        } else {
            point = 10;
            tiemNangUse = point * (2L * nPoint.dameg + point - 1) / 2 * 100;
            if (doUseTiemNang(tiemNangUse)) {
                nPoint.dameg += point;
                Service.gI().point(this);
            }
        }
    }

    private boolean doUseTiemNang(long tiemNang) {
        if (nPoint.tiemNang < tiemNang) return false;
        nPoint.tiemNang -= tiemNang;
        return true;
    }

    /**
     * Sử dụng skill, không tạo thread mới, dùng BOT_SCHEDULER chung
     */
    public void useSkill(int skillId) {
        switch (skillId) {
            case Skill.BIEN_KHI:
                BotManager.BOT_SERVICE.execute(this::doBienKhi);
                break;
            case Skill.QUA_CAU_KENH_KHI:
                togglePrepareQCKK();
                BotManager.BOT_SERVICE.schedule(
                    () -> SkillService.gI().sendPlayerPrepareBom(this, 1000),
                    1000, TimeUnit.MILLISECONDS
                );
                break;
            case Skill.MAKANKOSAPPO:
                togglePrepareLaze();
                BotManager.BOT_SERVICE.schedule(
                    () -> SkillService.gI().sendPlayerPrepareBom(this, 3000),
                    3000, TimeUnit.MILLISECONDS
                );
                break;
            // ... các case skill khác tương tự ...
        }
    }

    private void doBienKhi() {
        EffectSkillService.gI().sendEffectMonkey(this);
        EffectSkillService.gI().setIsMonkey(this);
        EffectSkillService.gI().sendEffectMonkey(this);
        Service.gI().sendSpeedPlayer(this, 0);
        Service.gI().Send_Caitrang(this);
        Service.gI().sendSpeedPlayer(this, -1);
        PlayerService.gI().sendInfoHpMp(this);
        Service.gI().point(this);
        Service.gI().Send_Info_NV(this);
        Service.gI().sendInfoPlayerEatPea(this);
    }

    private void togglePrepareQCKK() {
        this.playerSkill.prepareQCKK = !this.playerSkill.prepareQCKK;
        this.playerSkill.lastTimePrepareQCKK = System.currentTimeMillis();
    }

    private void togglePrepareLaze() {
        this.playerSkill.prepareLaze = !this.playerSkill.prepareLaze;
        this.playerSkill.lastTimePrepareLaze = System.currentTimeMillis();
    }
}
