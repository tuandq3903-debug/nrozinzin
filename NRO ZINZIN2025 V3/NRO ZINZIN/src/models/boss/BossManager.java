package models.boss;

/*
 *
 *
 * @author ZINZIN
 */
import models.BaConSoi.BaConSoi;
import models.Test_Dame.Test_Dame;
import models.Black.BlackGoku;
import models.BossRongNhi.RongNhi1Sao;
import models.BossRongNhi.RongNhi2Sao;
import models.BossRongNhi.RongNhi3Sao;
import models.BossRongNhi.RongNhi4Sao;
import models.BossRongNhi.RongNhi5Sao;
import models.BossRongNhi.RongNhi6Sao;
import models.BossRongNhi.RongNhi7Sao;
import models.Nappa.Rambo;
import models.Nappa.MapDauDinh;
import models.Nappa.Kuku;
import models.Android.Android19;
import models.Android.Pic;
import models.Android.Android14;
import models.Android.Poc;
import models.Android.Android13;
import models.Android.KingKong;
import models.Android.DrKore;
import models.Android.Android15;
import models.Baby.Baby;
import models.Baby.BabyMonKeyYellow;
import models.GoldenFrieza.DeathBeam1;
import models.GoldenFrieza.DeathBeam2;
import models.GoldenFrieza.DeathBeam3;
import models.GoldenFrieza.DeathBeam4;
import models.GoldenFrieza.DeathBeam5;
import models.GoldenFrieza.GoldenFrieza;
import models.Cooler.Cooler;
import models.Cell.SieuBoHung;
import models.Cell.XenBoHung;
import models.Broly.Broly;
import models.ChristmasEvent.OngGiaNoel;
import models.TauPayPay.TaoPaiPai;
import models.Frieza.Fide;
import models.HungVuongEvent.SonTinh;
import models.HungVuongEvent.ThuyTinh;
import models.HalloweenEvent.BiMa;
import models.HalloweenEvent.Doi;
import models.HalloweenEvent.MaTroi;
import models.TrungThuEvent.KhiDot;
import models.TrungThuEvent.NguyetThan;
import models.TrungThuEvent.NhatThan;
import models.MajinBuu12H.Mabu;
import models.MajinBuu12H.BuiBui;
import models.MajinBuu12H.BuiBui2;
import models.MajinBuu12H.Cadic;
import models.MajinBuu12H.Drabura;
import models.MajinBuu12H.Drabura2;
import models.MajinBuu12H.Drabura3;
import models.MajinBuu12H.Goku;
import models.MajinBuu12H.Yacon;
import models.MajinBuu14H.Mabu2H;
import models.MajinBuu14H.SuperBu;
import models.GinyuForce.SO1;
import models.GinyuForce.SO2;
import models.GinyuForce.SO3;
import models.GinyuForce.SO4;
import models.GinyuForce.TDT;
import models.NamekGinyuForce.SO1_NM;
import models.NamekGinyuForce.SO2_NM;
import models.NamekGinyuForce.SO3_NM;
import models.NamekGinyuForce.SO4_NM;
import models.NamekGinyuForce.TDT_NM;
import models.Earth.BIDO;
import models.Earth.BOJACK;
import models.Earth.BUJIN;
import models.Earth.KOGU;
import models.Earth.SUPER_BOJACK;
import models.Earth.ZANGYA;
import models.Yardart_Boss.CHIENBINH0;
import models.Yardart_Boss.CHIENBINH1;
import models.Yardart_Boss.CHIENBINH2;
import models.Yardart_Boss.CHIENBINH3;
import models.Yardart_Boss.CHIENBINH4;
import models.Yardart_Boss.CHIENBINH5;
import models.Yardart_Boss.DOITRUONG5;
import models.Yardart_Boss.TANBINH0;
import models.Yardart_Boss.TANBINH1;
import models.Yardart_Boss.TANBINH2;
import models.Yardart_Boss.TANBINH3;
import models.Yardart_Boss.TANBINH4;
import models.Yardart_Boss.TANBINH5;
import models.Yardart_Boss.TAPSU0;
import models.Yardart_Boss.TAPSU1;
import models.Yardart_Boss.TAPSU2;
import models.Yardart_Boss.TAPSU3;
import models.Yardart_Boss.TAPSU4;
import models.Cell.XENCON1;
import models.Cell.XENCON2;
import models.Cell.XENCON3;
import models.Cell.XENCON4;
import models.Cell.XENCON5;
import models.Cell.XENCON6;
import models.Cell.XENCON7;
import models.Cooler.Chiller;
import models.Cumber.Cumber;
import models.player.Player;
import network.Message;
import services.MapService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import models.map.Zone;
import models.mini.AnTrom;
import models.mini.Mai;
import models.mini.Odo;
import models.mini.Pilap;
import models.mini.SoiHecQuyn;
import models.mini.Su;
import models.mini.Xinbato;
import server.Maintenance;
import utils.Logger;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class BossManager implements Runnable {
    private static BossManager instance;
    public static byte ratioReward = 10;
    protected final List<Boss> bosses;

    private BossManager() {
        this.bosses = new CopyOnWriteArrayList<>();
    }

    public static synchronized BossManager gI() {
        if (instance == null) {
            instance = new BossManager();
            instance.loadBoss();
        }
        return instance;
    }

    public List<Boss> getBosses() {
        return this.bosses;
    }

    public void addBoss(Boss boss) {
        this.bosses.add(boss);
    }

    public void removeBoss(Boss boss) {
        this.bosses.remove(boss);
    }
    
    private Boss instantiateBoss(int bossID) {
        try {
            switch (bossID) {
                case BossID.AN_TROM:        return new AnTrom();
                case BossID.SOI_HEC_QUYN_1: return new SoiHecQuyn();
                case BossID.XINBATO_1:      return new Xinbato();
                case BossID.O_DO_NEW:       return new Odo();
                case BossID.BROLY:          return new Broly();
                case BossID.BABY:           return new Baby();
                case BossID.KUKU:           return new Kuku();
                case BossID.MAP_DAU_DINH:   return new MapDauDinh();
                case BossID.RAMBO:          return new Rambo();
                case BossID.KING_KONG:      return new KingKong();
                case BossID.DR_KORE:        return new DrKore();
                case BossID.ANDROID_14:     return new Android14();
                case BossID.PIC:            return new Pic();
                case BossID.POC:            return new Poc();
                case BossID.ANDROID_19:     return new Android19();
                case BossID.TEST_DAME:      return new Test_Dame();
                case BossID.BLACK_GOKU:     return new BlackGoku();
                case BossID.RONG_1_SAO:     return new RongNhi1Sao();
                case BossID.RONG_2_SAO:     return new RongNhi2Sao();
                case BossID.RONG_3_SAO:     return new RongNhi3Sao();
                case BossID.RONG_4_SAO:     return new RongNhi4Sao();
                case BossID.RONG_5_SAO:     return new RongNhi5Sao();
                case BossID.RONG_6_SAO:     return new RongNhi6Sao();
                case BossID.RONG_7_SAO:     return new RongNhi7Sao();
                case BossID.TIEU_DOI_TRUONG:     return new TDT();
                case BossID.TIEU_DOI_TRUONG_NM:     return new TDT_NM();
                case BossID.BOJACK:     return new BOJACK();
                case BossID.SIEU_BO_HUNG:     return new SieuBoHung();
                case BossID.XEN_BO_HUNG:     return new XenBoHung();
                case BossID.COOLER:     return new Cooler();
                case BossID.FIDE:     return new Fide();
                case BossID.CUMBER:     return new Cumber();
                case BossID.CHILLER:     return new Chiller();
                case BossID.BA_CON_SOI:     return new BaConSoi();
                case BossID.KHIDOT:     return new KhiDot();
                case BossID.NGUYETTHAN:     return new NguyetThan();
                default:                   return null;
            }
        } catch (Exception e) {
            Logger.error(e + "\n");
            return null;
        }
    }
    
    private void spawnBoss(int bossID, int count) {
        for (int i = 0; i < count; i++) {
            
            instantiateBoss(bossID);
        }
    }

     private boolean bossLoaded = false;

    public synchronized void loadBoss() {
        if (bossLoaded) {
//            Logger.debug("loadBoss() called again; skipping duplicate spawn.");
            return;
        }
        bossLoaded = true;
        Map<Integer, Integer> bossSpawnCounts = new LinkedHashMap<>();
        // Những boss spawn multi-instance
        bossSpawnCounts.put(BossID.BROLY,          10);
        bossSpawnCounts.put(BossID.O_DO_NEW,       10);
        bossSpawnCounts.put(BossID.XINBATO_1,      10);
        bossSpawnCounts.put(BossID.AN_TROM,        10);
        bossSpawnCounts.put(BossID.SOI_HEC_QUYN_1, 20);
        bossSpawnCounts.put(BossID.KHIDOT, 5);
        bossSpawnCounts.put(BossID.NGUYETTHAN, 5);
        // Những boss spawn 1 lần
        bossSpawnCounts.put(BossID.TIEU_DOI_TRUONG,    1);
        bossSpawnCounts.put(BossID.TIEU_DOI_TRUONG_NM, 1);
        bossSpawnCounts.put(BossID.BOJACK,             1);
        bossSpawnCounts.put(BossID.KING_KONG,          1);
        bossSpawnCounts.put(BossID.XEN_BO_HUNG,        1);
        bossSpawnCounts.put(BossID.SIEU_BO_HUNG,       1);
        bossSpawnCounts.put(BossID.KUKU,               1);
        bossSpawnCounts.put(BossID.MAP_DAU_DINH,       1);
        bossSpawnCounts.put(BossID.RAMBO,              1);
        bossSpawnCounts.put(BossID.FIDE,               1);
        bossSpawnCounts.put(BossID.ANDROID_14,         1);
        bossSpawnCounts.put(BossID.DR_KORE,            1);
        bossSpawnCounts.put(BossID.COOLER,             1);
        bossSpawnCounts.put(BossID.BLACK_GOKU,         1);
        bossSpawnCounts.put(BossID.CUMBER,             1);
        bossSpawnCounts.put(BossID.CHILLER,            1);
        bossSpawnCounts.put(BossID.BA_CON_SOI,         1);
        bossSpawnCounts.put(BossID.BABY,               1);
        bossSpawnCounts.put(BossID.RONG_1_SAO,         1);
        bossSpawnCounts.put(BossID.RONG_2_SAO,         1);
        bossSpawnCounts.put(BossID.RONG_3_SAO,         1);
        bossSpawnCounts.put(BossID.RONG_4_SAO,         1);
        bossSpawnCounts.put(BossID.RONG_5_SAO,         1);
        bossSpawnCounts.put(BossID.RONG_6_SAO,         1);
        bossSpawnCounts.put(BossID.RONG_7_SAO,         1);
        bossSpawnCounts.put(BossID.TEST_DAME,          1);
        
        // Spawn tất cả
        bossSpawnCounts.forEach(this::spawnBoss);
    }



    public void createBoss(int bossID, int total) {
        for (int i = 0; i < total; i++) {
            createBoss(bossID);
        }
    }

    public Boss createBoss(int bossID) {
        try {
            return switch (bossID) {
//                case BossID.SU ->
//                    new Su();
//                case BossID.MAI ->
//                    new Mai();
//                case BossID.FILAP ->
//                    new Pilap();
                case BossID.AN_TROM ->
                    new AnTrom();
                case BossID.SOI_HEC_QUYN_1 ->
                    new SoiHecQuyn();
                case BossID.XINBATO_1 ->
                    new Xinbato();
                case BossID.O_DO_NEW ->
                    new Odo();    
                case BossID.BABY_MONKEY ->
                    new BabyMonKeyYellow();
                case BossID.BABY ->
                    new Baby();
                case BossID.CHILLER ->
                    new Chiller();
                case BossID.TAP_SU_0 ->
                    new TAPSU0();
                case BossID.TAP_SU_1 ->
                    new TAPSU1();
                case BossID.TAP_SU_2 ->
                    new TAPSU2();
                case BossID.TAP_SU_3 ->
                    new TAPSU3();
                case BossID.TAP_SU_4 ->
                    new TAPSU4();
                case BossID.TAN_BINH_5 ->
                    new TANBINH5();
                case BossID.TAN_BINH_0 ->
                    new TANBINH0();
                case BossID.TAN_BINH_1 ->
                    new TANBINH1();
                case BossID.TAN_BINH_2 ->
                    new TANBINH2();
                case BossID.TAN_BINH_3 ->
                    new TANBINH3();
                case BossID.TAN_BINH_4 ->
                    new TANBINH4();
                case BossID.CHIEN_BINH_5 ->
                    new CHIENBINH5();
                case BossID.CHIEN_BINH_0 ->
                    new CHIENBINH0();
                case BossID.CHIEN_BINH_1 ->
                    new CHIENBINH1();
                case BossID.CHIEN_BINH_2 ->
                    new CHIENBINH2();
                case BossID.CHIEN_BINH_3 ->
                    new CHIENBINH3();
                case BossID.CHIEN_BINH_4 ->
                    new CHIENBINH4();
                case BossID.DOI_TRUONG_5 ->
                    new DOITRUONG5();
                case BossID.SO_4 ->
                    new SO4();
                case BossID.SO_3 ->
                    new SO3();
                case BossID.SO_2 ->
                    new SO2();
                case BossID.SO_1 ->
                    new SO1();
                case BossID.TIEU_DOI_TRUONG ->
                    new TDT();
                case BossID.SO_4_NM ->
                    new SO4_NM();
                case BossID.SO_3_NM ->
                    new SO3_NM();
                case BossID.SO_2_NM ->
                    new SO2_NM();
                case BossID.SO_1_NM ->
                    new SO1_NM();
                case BossID.TIEU_DOI_TRUONG_NM ->
                    new TDT_NM();
                case BossID.BUJIN ->
                    new BUJIN();
                case BossID.KOGU ->
                    new KOGU();
                case BossID.ZANGYA ->
                    new ZANGYA();
                case BossID.BIDO ->
                    new BIDO();
                case BossID.BOJACK ->
                    new BOJACK();
                case BossID.SUPER_BOJACK ->
                    new SUPER_BOJACK();
                case BossID.KUKU ->
                    new Kuku();
                case BossID.MAP_DAU_DINH ->
                    new MapDauDinh();
                case BossID.RAMBO ->
                    new Rambo();
                case BossID.TAU_PAY_PAY_DONG_NAM_KARIN ->
                    new TaoPaiPai();
                case BossID.DRABURA ->
                    new Drabura();
                case BossID.BUI_BUI ->
                    new BuiBui();
                case BossID.BUI_BUI_2 ->
                    new BuiBui2();
                case BossID.YA_CON ->
                    new Yacon();
                case BossID.DRABURA_2 ->
                    new Drabura2();
                case BossID.GOKU ->
                    new Goku();
                case BossID.CADIC ->
                    new Cadic();
                case BossID.MABU_12H ->
                    new Mabu();
                case BossID.DRABURA_3 ->
                    new Drabura3();
                case BossID.MABU ->
                    new Mabu2H();
                case BossID.SUPERBU ->
                    new SuperBu();
                case BossID.FIDE ->
                    new Fide();
                case BossID.DR_KORE ->
                    new DrKore();
                case BossID.ANDROID_19 ->
                    new Android19();
                case BossID.ANDROID_13 ->
                    new Android13();
                case BossID.ANDROID_14 ->
                    new Android14();
                case BossID.ANDROID_15 ->
                    new Android15();
                case BossID.PIC ->
                    new Pic();
                case BossID.POC ->
                    new Poc();
                case BossID.KING_KONG ->
                    new KingKong();
                case BossID.XEN_BO_HUNG ->
                    new XenBoHung();
                case BossID.SIEU_BO_HUNG ->
                    new SieuBoHung();
                case BossID.XEN_CON_1 ->
                    new XENCON1();
                case BossID.XEN_CON_2 ->
                    new XENCON2();
                case BossID.XEN_CON_3 ->
                    new XENCON3();
                case BossID.XEN_CON_4 ->
                    new XENCON4();
                case BossID.XEN_CON_5 ->
                    new XENCON5();
                case BossID.XEN_CON_6 ->
                    new XENCON6();
                case BossID.XEN_CON_7 ->
                    new XENCON7();
                case BossID.COOLER ->
                    new Cooler();
                case BossID.BROLY ->
                    new Broly();
                case BossID.KHIDOT ->
                    new KhiDot();
                case BossID.NGUYETTHAN ->
                    new NguyetThan();
                case BossID.NHATTHAN ->
                    new NhatThan();
                case BossID.GOLDEN_FRIEZA ->
                    new GoldenFrieza();
                case BossID.DEATH_BEAM_1 ->
                    new DeathBeam1();
                case BossID.DEATH_BEAM_2 ->
                    new DeathBeam2();
                case BossID.DEATH_BEAM_3 ->
                    new DeathBeam3();
                case BossID.DEATH_BEAM_4 ->
                    new DeathBeam4();
                case BossID.DEATH_BEAM_5 ->
                    new DeathBeam5();
                case BossID.BIMA ->
                    new BiMa();
                case BossID.MATROI ->
                    new MaTroi();
                case BossID.DOI ->
                    new Doi();
                case BossID.ONG_GIA_NOEL ->
                    new OngGiaNoel();
                case BossID.SON_TINH ->
                    new SonTinh();
                case BossID.THUY_TINH ->
                    new ThuyTinh();
                // case BossID.BE_NA ->
                //     new BeNa();
                case BossID.BLACK_GOKU ->
                    new BlackGoku();
                case BossID.CUMBER ->
                    new Cumber();
                case BossID.RONG_1_SAO ->
                    new RongNhi1Sao();
                case BossID.RONG_2_SAO ->
                    new RongNhi2Sao();
                case BossID.RONG_3_SAO ->
                    new RongNhi3Sao();
                case BossID.RONG_4_SAO ->
                    new RongNhi4Sao();
                case BossID.RONG_5_SAO ->
                    new RongNhi5Sao();
                case BossID.RONG_6_SAO ->
                    new RongNhi6Sao();
                case BossID.RONG_7_SAO ->
                    new RongNhi7Sao();
                case BossID.TEST_DAME ->
                    new Test_Dame();    
                 case BossID.BA_CON_SOI->
                    new BaConSoi();
                default ->
                    null;
            };
        } catch (Exception e) {
            Logger.error(e + "\n");
            return null;
        }
    }

    public Boss getBoss(int id) {
        try {
            Boss boss = this.bosses.get(id);
            if (boss != null) {
                return boss;
            }
        } catch (Exception e) {
        }
        return null;
    }
    public Boss getBossById(int bossId) {
        return this.bosses.stream().filter(boss -> boss.id == bossId && !boss.isDie()).findFirst().orElse(null);
    }

    public boolean checkBosses(Zone zone, int BossID) {
        return this.bosses.stream()
                .filter(boss -> boss.id == BossID && boss.zone != null && boss.zone.equals(zone) && !boss.isDie())
                .findFirst().orElse(null) != null;
    }

    public Player findBossClone(Player player) {
        return player.zone.getBosses().stream().filter(boss -> boss.id < -100_000_000 && !boss.isDie()).findFirst()
                .orElse(null);
    }

    public Boss getBossById(int bossId, int mapId, int zoneId) {
        return this.bosses.stream().filter(boss -> boss.id == bossId && boss.zone != null
                && boss.zone.map.mapId == mapId && boss.zone.zoneId == zoneId && !boss.isDie()).findFirst()
                .orElse(null);
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                int delay = 500;
                long st = System.currentTimeMillis();
                for (int i = this.bosses.size() - 1; i >= 0; i--) {
                    try {
                        this.bosses.get(i).update();
                    } catch (Exception e) {
//                         e.printStackTrace();
                    }
                }
                if (delay - (System.currentTimeMillis() - st) > 0) {
                    Thread.sleep(delay - (System.currentTimeMillis() - st));
                }
            } catch (Exception e) {
//                 e.printStackTrace();
            }
        }
    }
    
    
    
    public void showListBoss(Player player) {
    if (!player.isAdmin()) {
        return;
    }
    player.iDMark.setMenuType(3);

    try {
        Message msg = new Message((byte)-96);
        // Tiêu đề menu
        msg.writer().writeByte(0);
        msg.writer().writeUTF("Danh Sách Boss");

        // Những tên boss cần ẩn
        Set<String> bossNamesToHide = Set.of(
            "Tâm Ma", "Rồng Nhí", "Sói hẹc quyn",
            "Ăn trộm ", "Xinbatô", "Ở dơ",
            "Sơn Tinh", "Thủy Tinh", ""
        );

        // 1) Lọc boss còn sống, đúng map, không ẩn
        List<Boss> filtered = new ArrayList<>();
        for (Boss b : bosses) {
            if (b.isDie()) continue;
            int mapId = (b.zone != null
                ? b.zone.map.mapId
                : b.data[0].getMapJoin()[0]);
            if ( MapService.gI().isMapBossFinal(mapId)
              || MapService.gI().isMapHuyDiet(mapId)
              || MapService.gI().isMapYardart(mapId)
              || MapService.gI().isMapMaBu(mapId)
              || MapService.gI().isMapMabu2H(mapId)
              || MapService.gI().isMapBlackBallWar(mapId) ) {
                continue;
            }
            if (bossNamesToHide.contains(b.data[0].getName())) continue;
            filtered.add(b);
        }

        // 2) Loại trùng theo boss.id
        List<Boss> unique = new ArrayList<>();
        Set<Long> seenIds = new HashSet<>();
        for (Boss b : filtered) {
            if (seenIds.add(b.id)) {
                unique.add(b);
            }
        }

        // 3) Giới hạn ≤ 127 và gửi
        int count = Math.min(unique.size(), Byte.MAX_VALUE);
        msg.writer().writeByte(count);
        for (int i = 0; i < count; i++) {
            Boss boss = unique.get(i);
            msg.writer().writeInt(i);
            msg.writer().writeInt(i);
            // outfit
            msg.writer().writeShort(boss.data[0].getOutfit()[0]);
            if (player.getSession().version >= 214) {
                msg.writer().writeShort(-1);
            }
            msg.writer().writeShort(boss.data[0].getOutfit()[1]);
            msg.writer().writeShort(boss.data[0].getOutfit()[2]);
            // tên
            msg.writer().writeUTF(boss.data[0].getName());
            // status và vị trí
            if (boss.zone != null) {
                msg.writer().writeUTF(boss.bossStatus.toString());
                msg.writer().writeUTF(
                    boss.zone.map.mapName
                    + "(" + boss.zone.map.mapId + ") khu " 
                    + boss.zone.zoneId
                );
            } else {
                msg.writer().writeUTF(boss.bossStatus.toString());
                msg.writer().writeUTF("Chết rồi");
            }
        }

        player.sendMessage(msg);
        msg.cleanup();

    } catch (Exception e) {
        // xử lý ngoại lệ
    }
}
}




