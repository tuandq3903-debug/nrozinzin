package models.npc;

/*
 *
 *
 * @author ZINZIN
 */
import models.npc.manifest.ChiChi;
import models.npc.manifest.HangNga;
import models.npc.manifest.Bill;
import models.npc.manifest.TruongLaoGuru;
import models.npc.manifest.RuongDo;
import models.npc.manifest.Jaco;
import models.npc.manifest.Tapion;
import models.npc.manifest.DocNhan;
import models.npc.manifest.ThanVuTru;
import models.npc.manifest.VuaVegeta;
import models.npc.manifest.DuongTang;
import models.npc.manifest.MrPoPo;
import models.npc.manifest.DrDrief;
import models.npc.manifest.Rong2Sao;
import models.npc.manifest.TrongTai;
import models.npc.manifest.Berry;
import models.npc.manifest.LyTieuNuong;
import models.npc.manifest.GhiDanh;
import models.npc.manifest.Bulma;
import models.npc.manifest.Calick;
import models.npc.manifest.GohanBeast;
import models.npc.manifest.Rong1Sao;
import models.npc.manifest.Whis;
import models.npc.manifest.Rong4Sao;
import models.npc.manifest.RongOmega;
import models.npc.manifest.ThuongDe;
import models.npc.manifest.Babiday;
import models.npc.manifest.Osin;
import models.npc.manifest.OngParagus;
import models.npc.manifest.GiuMaDauBo;
import models.npc.manifest.Karin;
import models.npc.manifest.BulmaTuongLai;
import models.npc.manifest.OngGohan;
import models.npc.manifest.Potage;
import models.npc.manifest.Vados;
import models.npc.manifest.RuongSuuTap;
import models.npc.manifest.NoiBanh;
import models.npc.manifest.Santa;
import models.npc.manifest.HungVuong;
import models.npc.manifest.Rong5Sao;
import models.npc.manifest.QuocVuong;
import models.npc.manifest.GokuSSJ2;
import models.npc.manifest.KyGui;
import models.npc.manifest.Rong3Sao;
import models.npc.manifest.Rong6Sao;
import models.npc.manifest.NgoKhong;
import models.npc.manifest.Cargo;
import models.npc.manifest.Appule;
import models.npc.manifest.BoMong;
import models.npc.manifest.Dende;
import models.npc.manifest.OngMoori;
import models.npc.manifest.QuyLaoKame;
import models.npc.manifest.Myuu;
import models.npc.manifest.GokuSSJ;
import models.npc.manifest.ToSuKaio;
import models.npc.manifest.Kibit;
import models.npc.manifest.QuaTrung;
import models.npc.manifest.LinhCanh;
import models.npc.manifest.Cui;
import models.npc.manifest.ToriBot;
import models.npc.manifest.DaiThienSu;
import models.npc.manifest.Uron;
import models.npc.manifest.Bardock;
import models.npc.manifest.DauThan;
import models.npc.manifest.Rong7Sao;
import models.npc.manifest.BaHatMit;
import services.ClanService;
import services.Service;
import services.ItemService;
import services.NgocRongNamecService;
import services.IntrinsicService;
import services.InventoryService;
import services.NpcService;
import services.PetService;
import services.PlayerService;
import services.FriendAndEnemyService;
import consts.ConstNpc;
import java.util.ArrayList;
import java.util.Arrays;
import models.boss.BossManager;
import models.clan.Clan;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import services.func.ChangeMapService;
import services.func.SummonDragon;

import static services.func.SummonDragon.SHENRON_1_STAR_WISHES_1;
import static services.func.SummonDragon.SHENRON_1_STAR_WISHES_2;
import static services.func.SummonDragon.SHENRON_SAY;

import models.player.Player;
import models.item.Item;
import jdbc.daos.ZINZINSqlFetcher;
import models.matches.PVPService;
import server.Client;
import server.Maintenance;
import models.Top.RealTop;
import services.func.Input;
import utils.Logger;
import utils.Util;
import models.SuperDivineWater.SuperDivineWaterService;
import models.ShenronEvent.ShenronEventService;
import models.kygui.ConsignItem;
import models.kygui.ConsignShopService;
import models.npc.manifest.TiemBanhTrungThu;
import server.Load_Database;
import services.func.SummonDragonNamek;
import services.func.UseItem;

public class NpcFactory {

    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<>();

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId) {
        int avatar = Load_Database.NPC_TEMPLATES.get(tempId).avatar;
        try {
            return switch (tempId) {
                case ConstNpc.RUONG_SUU_TAP ->
                    new RuongSuuTap(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.MYUU ->
                    new Myuu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.HUNG_VUONG ->
                    new HungVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GHI_DANH ->
                    new GhiDanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRONG_TAI ->
                    new TrongTai(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.POTAGE ->
                    new Potage(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.MR_POPO ->
                    new MrPoPo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUY_LAO_KAME ->
                    new QuyLaoKame(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUONG_LAO_GURU ->
                    new TruongLaoGuru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VUA_VEGETA ->
                    new VuaVegeta(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUA_HANG_KY_GUI ->
                    new KyGui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_GOHAN ->
                    new OngGohan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_MOORI ->
                    new OngMoori(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_PARAGUS ->
                    new OngParagus(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA ->
                    new Bulma(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DENDE ->
                    new Dende(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.APPULE ->
                    new Appule(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DR_DRIEF ->
                    new DrDrief(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CARGO ->
                    new Cargo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUI ->
                    new Cui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.SANTA ->
                    new Santa(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.URON ->
                    new Uron(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BA_HAT_MIT ->
                    new BaHatMit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RUONG_DO ->
                    new RuongDo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAU_THAN ->
                    new DauThan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CALICK ->
                    new Calick(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.JACO ->
                    new Jaco(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THUONG_DE ->
                    new ThuongDe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VADOS ->
                    new Vados(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THAN_VU_TRU ->
                    new ThanVuTru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KIBIT ->
                    new Kibit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.OSIN ->
                    new Osin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BABIDAY ->
                    new Babiday(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LY_TIEU_NUONG ->
                    new LyTieuNuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LINH_CANH ->
                    new LinhCanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUA_TRUNG ->
                    new QuaTrung(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUOC_VUONG ->
                    new QuocVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA_TL ->
                    new BulmaTuongLai(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_OMEGA ->
                    new RongOmega(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_1S ->
                    new Rong1Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_2S ->
                    new Rong2Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_3S ->
                    new Rong3Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_4S ->
                    new Rong4Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_5S ->
                    new Rong5Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_6S ->
                    new Rong6Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_7S ->
                    new Rong7Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAI_THIEN_SU ->
                    new DaiThienSu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.WHIS ->
                    new Whis(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BILL ->
                    new Bill(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BO_MONG ->
                    new BoMong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THAN_MEO_KARIN ->
                    new Karin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ ->
                    new GokuSSJ(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ_2 ->
                    new GokuSSJ2(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TAPION ->
                    new Tapion(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DOC_NHAN ->
                    new DocNhan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GIUMA_DAU_BO ->
                    new GiuMaDauBo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TO_SU_KAIO ->
                    new ToSuKaio(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BARDOCK ->
                    new Bardock(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BERRY ->
                    new Berry(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DUONG_TANG ->
                    new DuongTang(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NGO_KHONG ->
                    new NgoKhong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TORI_BOT ->
                    new ToriBot(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CHI_CHI ->
                    new ChiChi(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.HANG_NGA ->
                    new HangNga(mapId, status, cx, cy, tempId, avatar);    
                case ConstNpc.NOI_BANH ->
                    new NoiBanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOHAN_BEAST ->
                    new GohanBeast(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TIEM_BANH ->
                    new TiemBanhTrungThu(mapId, status, cx, cy, tempId, avatar);    
                default ->
                    new Npc(mapId, status, cx, cy, tempId, avatar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                super.openBaseMenu(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                            }
                        }
                    };
            };
        } catch (Exception e) {
            Logger.logException(NpcFactory.class,
                    e, "Lỗi load npc");
            return null;
        }
    }

    public static void createNpcRongThieng() {
        new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:
                        break;
                    case ConstNpc.SHOW_SHENRON_NAMEK_CONFIRM:
                        SummonDragonNamek.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                    case ConstNpc.SHENRON_NAMEK_CONFIRM:
                        if (select == 0) {
                            SummonDragonNamek.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragonNamek.gI().sendWhishesNamec(player);
                        }
                        break;
                    case ConstNpc.SHOW_SHENRON_EVENT_CONFIRM:
                        if (player.shenronEvent != null) {
                            player.shenronEvent.showConfirmShenron((byte) select);
                        }
                        break;
                    case ConstNpc.SHENRON_EVENT_CONFIRM:
                        if (player.shenronEvent != null) {
                            if (select == 0) {
                                player.shenronEvent.confirmWish();
                            } else if (select == 1) {
                                player.shenronEvent.sendWhishesShenron();
                            }
                        }
                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1
                                && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY,
                                    SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2
                                && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY,
                                    SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU -> {
                    }
                    case ConstNpc.SUMMON_SHENRON_EVENT -> {
                        if (select == 0) {
                            ShenronEventService.gI().summonShenron(player);
                        }
                    }
                    case ConstNpc.MAKE_MATCH_PVP -> {
                        if (Maintenance.isRunning) {
                        }
                        PVPService.gI().sendInvitePVP(player, (byte) select);
                    }
                    case ConstNpc.MAKE_FRIEND -> {
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                try {
                                    FriendAndEnemyService.gI().acceptMakeFriend(player,
                                            Integer.parseInt(String.valueOf(playerId)));
                                } catch (NumberFormatException e) {
                                }
                            }
                        }
                    }
                    case ConstNpc.REVENGE -> {
                        if (select == 0) {
                            PVPService.gI().acceptRevenge(player);
                        }
                    }
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON -> {
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                    }
                    case ConstNpc.SUMMON_SHENRON -> {
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                    }
                    case ConstNpc.MENU_OPTION_USE_ITEM726 -> {
                        if (select == 0) {
                            SuperDivineWaterService.gI().joinMapThanhThuy(player);
                        }
                    }
                    case ConstNpc.MENU_SIEU_THAN_THUY -> {
                        if (select == 0) {
                            ChangeMapService.gI().changeMap(player, 46, -1, Util.nextInt(300, 400), 408);
                        }
                    }
                    case ConstNpc.TAP_TU_DONG_CONFIRM -> {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, player.lastMapOffline,
                                    player.lastZoneOffline, player.lastXOffline);
                        }
                    }
                    case ConstNpc.INTRINSIC -> {
                        switch (select) {
                            case 0 ->
                                IntrinsicService.gI().showAllIntrinsic(player);
                            case 1 ->
                                IntrinsicService.gI().showConfirmOpen(player);
                            case 2 ->
                                IntrinsicService.gI().showConfirmOpenVip(player);
                            default -> {
                            }
                        }
                    }
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC -> {
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                    }
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP -> {
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                    }
                    case ConstNpc.CONFIRM_LEAVE_CLAN -> {
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                    }
                    case ConstNpc.CONFIRM_NHUONG_PC -> {
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                    }

                    case ConstNpc.BAN_PLAYER -> {
                        if (select == 0) {
                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.gI().sendThongBao(player,
                                    "Ban người chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                        }
                    }
                    case ConstNpc.BUFF_PET -> {
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl);
                                Service.gI().sendThongBao(player, "Phát đệ tử cho "
                                        + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                    }
                    case ConstNpc.OTT -> {
                        if (select < 3) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            player.iDMark.setOtt(select);
                            String[] selects = new String[]{"Kéo", "Búa", "Bao", "Hủy"};
                            NpcService.gI().createMenuConMeo(pl, ConstNpc.OTT_ACCEPT, -1,
                                    player.name + " muốn chơi oẳn tù tì với bạn mức cược 5tr.", selects, player);
                        }
                    }
                    case ConstNpc.OTT_ACCEPT -> {
                        if (select < 3) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            int slp1 = pl.iDMark.getOtt();
                            int slp2 = select;
                            if (slp1 == -1 || slp2 == -1) {
                                return;
                            }
                            pl.iDMark.setOtt(-1);
                            String[] selects = new String[]{"Kéo", "Búa", "Bao"};
                            Service.gI().chat(pl, selects[slp1]);
                            Service.gI().chat(player, selects[slp2]);
                            Service.gI().sendEffAllPlayer(pl, 1000 + slp1, 1, 2, 1);
                            Service.gI().sendEffAllPlayer(player, 1000 + slp2, 1, 2, 1);
                            if (slp1 == slp2) {
                                Service.gI().sendThongBao(pl, "Hòa!");
                                Service.gI().sendThongBao(player, "Hòa!");
                            } else if (slp1 == 0 && slp2 == 2 || slp1 == 1 && slp2 == 0 || slp1 == 2 && slp2 == 1) {
                                Service.gI().sendThongBao(pl, "Thắng!");
                                Service.gI().sendThongBao(player, "Thua!");
                                pl.inventory.gold += 4800000;
                                player.inventory.gold -= 5000000;
                                Service.gI().sendMoney(pl);
                                Service.gI().sendMoney(player);
                            } else {
                                Service.gI().sendThongBao(pl, "Thua!");
                                Service.gI().sendThongBao(player, "Thắng!");
                                pl.inventory.gold -= 5000000;
                                player.inventory.gold += 4800000;
                                Service.gI().sendMoney(pl);
                                Service.gI().sendMoney(player);
                            }
                        }
                    }
                    case 671 -> {
                        switch (select) {
                            case 0 -> {
                                long[] time = new long[]{900000, 1800000, 3600000, 86400000, 259200000, 604800000,
                                    1296000000};
                                var bb = ItemService.gI().getTemplate(player.HocSkill.ItemTemplateSkillId);
                                String[] subName = bb.name.split("");
                                byte level = Byte.parseByte(subName[subName.length - 1]);
                                player.HocSkill.Time = time[level - 1] + System.currentTimeMillis();
                                player.nPoint.tiemNang -= player.HocSkill.Potential;
                                Service.gI().point(player);
                                Service.gI().ClosePanel(player);
                                NpcService.gI().createTutorial(player, NpcService.gI().getAvatar(13 + player.gender),
                                        "Con đã học thành công, hãy cố gắng chờ đợi nha");
                                break;
                            }
                            case 1 -> {

                                break;
                            }
                        }
                    }
                    case ConstNpc.MENU_ADMIN -> {
                        switch (select) {
                            case 0 -> {
                                for (int i = 14; i <= 20; i++) {
                                    Item item = ItemService.gI().createNewItem((short) i);
                                    InventoryService.gI().addItemBag(player, item);
                                }
                                InventoryService.gI().sendItemBag(player);
                            }
                            case 1 -> {
                                PetService.gI().createNormalPet(player, player.gender);
                            }
                            case 2 -> {
                                if (player.isAdmin()) {
//                                     System.out.println(player.name + " Đang bảo trì game!");
                                    Maintenance.gI().start(15);
                                }
                            }
                            case 3 ->
                                Input.gI().createFormFindPlayer(player);
                            case 4 ->
                                BossManager.gI().showListBoss(player);
                            case 5 ->
                                Input.gI().createFormNapCoin(player);
                            case 6 ->
                                Input.gI().createFormSenditem2(player);
                        }
                    }
                    case ConstNpc.CONFIRM_DISSOLUTION_CLAN -> {
                        switch (select) {
                            case 0 -> {
                                Clan clan = player.clan;
                                clan.deleteDB(clan.id);
                                Load_Database.CLANS.remove(clan);
                                player.clan = null;
                                player.clanMember = null;
                                ClanService.gI().sendMyClan(player);
                                ClanService.gI().sendClanId(player);
                                Service.gI().sendThongBao(player, "Đã giải tán bang hội.");
                            }

                        }
                    }

                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND -> {
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            player.inventory.itemsBoxCrackBall.clear();
                            Service.gI().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                    }
                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_MAIL_BOX -> {
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsMailBox.size(); i++) {
                                player.inventory.itemsMailBox.set(i, ItemService.gI().createItemNull());
                            }
                            player.inventory.itemsMailBox.clear();
                            if (ZINZINSqlFetcher.updateMailBox(player)) {
                                Service.gI().sendThongBao(player, "Xóa hết vật phẩm hòm thư thành công");
                            }
                        }
                    }
                    case ConstNpc.MENU_FIND_PLAYER -> {
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0 -> {
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x,
                                                p.location.y);
                                    }
                                }
                                case 1 -> {
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMap(p, player.zone, player.location.x,
                                                player.location.y);
                                    }
                                }
                                case 2 ->
                                    Input.gI().createFormChangeName(player, p);
                                case 3 -> {
                                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                                            "Bạn có chắc chắn muốn ban " + p.name, selects, p);
                                }
                                case 4 -> {
                                    Service.gI().sendThongBao(player, "Kik người chơi " + p.name + " thành công");
                                    Client.gI().getPlayers().remove(p);
                                    Client.gI().kickSession(p.getSession());
                                }
                            }
                        }
                    }
                    case ConstNpc.CONFIRM_TELE_NAMEC -> {
                        if (select == 0) {
                            NgocRongNamecService.gI().teleportToNrNamec(player);
                            player.inventory.subGemAndRuby(50);
                            Service.gI().sendMoney(player);
                        }
                    }
                    case ConstNpc.MA_BAO_VE -> {
                        if (select == 0) {
                            if (player.mbv == 0) {
                                if (player.inventory.gold >= 30000) {
                                    player.inventory.gold -= 30000;
                                    Service.gI().sendMoney(player);
                                    player.mbv = player.iDMark.getMbv();
                                    player.baovetaikhoan = true;
                                    Service.gI().sendThongBao(player,
                                            "Kích hoạt thành công, tài khoản đang được bảo vệ");
                                } else {
                                    Service.gI().sendThongBao(player,
                                            "Bạn không đủ tiền để kích hoạt bảo vệ tài khoản");
                                }
                            } else {
                                if (player.baovetaikhoan) {
                                    player.baovetaikhoan = false;
                                    Service.gI().sendThongBao(player, "Chức năng bảo vệ tài khoản đang tắt");
                                } else {
                                    player.baovetaikhoan = true;
                                    Service.gI().sendThongBao(player, "Tài khoản đang được bảo vệ");
                                }
                            }
                        }
                    }
                    case ConstNpc.UP_TOP_ITEM -> {
                        if (select == 0) {
                            if (player.inventory.gold >= 5000000 && player.iDMark.getIdItemUpTop() != -1) {
                                ConsignItem it = ConsignShopService.gI().getItemBuy(player.iDMark.getIdItemUpTop());
                                if (it == null || it.isBuy) {
                                    Service.gI().sendThongBao(player, "Vật phẩm không tồn tại hoặc đã được bán");
                                    return;
                                }
                                if (it.player_sell != player.id) {
                                    Service.gI().sendThongBao(player, "Vật phẩm không thuộc quyền sở hữu");
                                    ConsignShopService.gI().openShopKyGui(player);
                                    return;
                                }
                                player.inventory.gold -= 5000000;
                                Service.gI().sendMoney(player);
                                Service.gI().sendThongBao(player, "Thành công");
                                it.isUpTop += 1;
                                ConsignShopService.gI().openShopKyGui(player);
                            } else {
                                Service.gI().sendThongBao(player, "Bạn không đủ vàng");
                                player.iDMark.setIdItemUpTop(-1);
                            }
                        }
                    }
                    case ConstNpc.RUONG_GO -> {
                        int i = player.indexWoodChest;
                        if (i < 0 || player.itemsWoodChest == null || i >= player.itemsWoodChest.size()) {
                            return;
                        }

                        Item itemWoodChest = player.itemsWoodChest.get(i);
                        player.indexWoodChest--;
                        String info = "|1|" + itemWoodChest.template.name;
                        String info2 = "\n|2|";
                        if (!itemWoodChest.itemOptions.isEmpty()) {
                            for (Item.ItemOption io : itemWoodChest.itemOptions) {
                                if (io.optionTemplate.id != 102 && io.optionTemplate.id != 73) {
                                    info2 += io.getOptionString() + "\n";
                                }
                            }
                        }
                        info = (info2.length() > "\n|2|".length() ? (info + info2).trim() : info.trim()) + "\n|0|"
                                + itemWoodChest.template.description;
                        NpcService.gI().createMenuConMeo(player, ConstNpc.RUONG_GO, -1, "Bạn nhận được\n"
                                + info.trim(), "OK" + (i > 0 ? " [" + i + "]" : ""));
                    }
                    

                    case ConstNpc.HOP_QUA_SKH -> {
                        // 2 bộ option chung theo select: Trái Đất, Namek, Xayda
                        int[][] options  = {
                            {128, 129, 127},
                            {130, 131, 132},
                            {133, 135, 134}
                        };
                        int[][] options1 = {
                            {140, 141, 139},
                            {142, 143, 144},
                            {136, 138, 137}
                        };

                        // Chọn ngẫu nhiên subSelect: 0,1 hoặc 2 để lấy đúng một cặp
                        int subSelect = ThreadLocalRandom.current().nextInt(options[select].length);

                        // Bảng template id cho 5 món: áo, quần, găng, giày, nhẫn
                        short[][] templates = {
                            {0,  6, 21, 27, 12},
                            {1,  7, 22, 28, 12},
                            {2,  8, 23, 29, 12}
                        };

                        // Tùy chọn đặc trưng cho từng slot (thứ tự templates)
                        List<List<Item.ItemOption>> slotOptions = Arrays.asList(
                            Arrays.asList(new Item.ItemOption(47, 10)),                             // áo
                            Arrays.asList(new Item.ItemOption(6, 100), new Item.ItemOption(27, 2)), // quần
                            Arrays.asList(new Item.ItemOption(0, 10)),                               // găng
                            Arrays.asList(new Item.ItemOption(7, 100), new Item.ItemOption(28, 2)),// giày
                            Arrays.asList(new Item.ItemOption(14, 1))                                // nhẫn
                        );

                        // Tạo và cấu hình items
                        List<Item> items = new ArrayList<>();
                        for (int slot = 0; slot < templates[select].length; slot++) {
                            short tplId = templates[select][slot];
                            Item it = ItemService.gI().createNewItem(tplId);
                            // Thêm các tùy chọn đặc trưng cho slot
                            for (Item.ItemOption opt : slotOptions.get(slot)) {
                                it.itemOptions.add(opt);
                            }
                            // Chỉ thêm cặp option chính đã chọn
                            int idA = options[select][subSelect];
                            int idB = options1[select][subSelect];
                            it.itemOptions.add(new Item.ItemOption(idA, 1));
                            it.itemOptions.add(new Item.ItemOption(idB, 1));
                            // Thêm option chung cuối
                            it.itemOptions.add(new Item.ItemOption(30, 1));
                            items.add(it);
                        }

                        // Kiểm tra số ô trống và xử lý
                        Item hopQua = InventoryService.gI().findItemBag(player, 2000);
                        int need = items.size();
                        if (InventoryService.gI().getCountEmptyBag(player) < need) {
                            Service.gI().sendThongBao(player, "Cần " + need + " ô hành trang mới có thể mở!!!");
                            return;
                        }

                        // Thêm item và trừ hộp quà
                        for (Item it : items) {
                            InventoryService.gI().addItemBag(player, it);
                        }
                        InventoryService.gI().subQuantityItemsBag(player, hopQua, 1);
                        InventoryService.gI().sendItemBag(player);

                        // Thông báo
                        String[] names = {"Trái Đất", "Namek", "Xayda"};
                        Service.gI().sendThongBao(player, "Bạn nhận được 1 set kích hoạt " + names[select]);
                        break;
                    }



                case ConstNpc.HOP_QUA_HUY_DIET -> {
                    // 2 bộ option chung theo select: Trái Đất, Namek, Xayda
                        int[][] options  = {
                            {128, 129, 127},
                            {130, 131, 132},
                            {133, 135, 134}
                        };
                        int[][] options1 = {
                            {140, 141, 139},
                            {142, 143, 144},
                            {136, 138, 137}
                        };

                        // Chọn ngẫu nhiên subSelect: 0,1 hoặc 2 để lấy đúng một cặp
                        int subSelect = ThreadLocalRandom.current().nextInt(options[select].length);

                        // Bảng template id cho 5 món: áo, quần, găng, giày, nhẫn
                        short[][] templates = {
                            {650, 651, 657, 658, 656},
                            {652, 653, 659, 660, 656},
                            {654, 655, 661, 662, 656}
                        };

                        // Tùy chọn đặc trưng cho từng slot (thứ tự templates)
                        List<List<Item.ItemOption>> slotOptions = Arrays.asList(
                            Arrays.asList(new Item.ItemOption(47, 1800)),                             // áo
                            Arrays.asList(new Item.ItemOption(6, 100000), new Item.ItemOption(27, 50000)), // quần
                            Arrays.asList(new Item.ItemOption(0, 10000)),                               // găng
                            Arrays.asList(new Item.ItemOption(7, 100000), new Item.ItemOption(28, 50000)),// giày
                            Arrays.asList(new Item.ItemOption(14, 20))                                // nhẫn
                        );

                        // Tạo và cấu hình items
                        List<Item> items = new ArrayList<>();
                        for (int slot = 0; slot < templates[select].length; slot++) {
                            short tplId = templates[select][slot];
                            Item it = ItemService.gI().createNewItem(tplId);
                            // Thêm các tùy chọn đặc trưng cho slot
                            for (Item.ItemOption opt : slotOptions.get(slot)) {
                                it.itemOptions.add(opt);
                            }
                            // Chỉ thêm cặp option chính đã chọn
                            int idA = options[select][subSelect];
                            int idB = options1[select][subSelect];
                            it.itemOptions.add(new Item.ItemOption(idA, 1));
                            it.itemOptions.add(new Item.ItemOption(idB, 1));
                            // Thêm option chung cuối
                            it.itemOptions.add(new Item.ItemOption(30, 1));
                            items.add(it);
                        }

                        // Kiểm tra số ô trống và xử lý
                        Item hopQua = InventoryService.gI().findItemBag(player, 2001);
                        int need = items.size();
                        if (InventoryService.gI().getCountEmptyBag(player) < need) {
                            Service.gI().sendThongBao(player, "Cần " + need + " ô hành trang mới có thể mở!!!");
                            return;
                        }

                        // Thêm item và trừ hộp quà
                        for (Item it : items) {
                            InventoryService.gI().addItemBag(player, it);
                        }
                        InventoryService.gI().subQuantityItemsBag(player, hopQua, 1);
                        InventoryService.gI().sendItemBag(player);

                        // Thông báo
                        String[] names = {"Trái Đất", "Namek", "Xayda"};
                        Service.gI().sendThongBao(player, "Bạn nhận được 1 set hủy diệt kích hoạt " + names[select]);
                        break;
                    }
                case ConstNpc.HOP_QUA_THIEN_SU -> {
                    // 2 bộ option chung theo select: Trái Đất, Namek, Xayda
                        int[][] options  = {
                            {128, 129, 127},
                            {130, 131, 132},
                            {133, 135, 134}
                        };
                        int[][] options1 = {
                            {140, 141, 139},
                            {142, 143, 144},
                            {136, 138, 137}
                        };

                        // Chọn ngẫu nhiên subSelect: 0,1 hoặc 2 để lấy đúng một cặp
                        int subSelect = ThreadLocalRandom.current().nextInt(options[select].length);

                        // Bảng template id cho 5 món: áo, quần, găng, giày, nhẫn
                        short[][] templates = {
                            {1048, 1051, 1054, 1057, 1060},
                            {1049, 1052, 1055, 1058, 1061},
                            {1050, 1053, 1056, 1059, 1062}
                        };

                        // Tùy chọn đặc trưng cho từng slot (thứ tự templates)
                        List<List<Item.ItemOption>> slotOptions = Arrays.asList(
                            Arrays.asList(new Item.ItemOption(47, 3800)),                             // áo
                            Arrays.asList(new Item.ItemOption(6, 200000), new Item.ItemOption(27, 100000)), // quần
                            Arrays.asList(new Item.ItemOption(0, 20000)),                               // găng
                            Arrays.asList(new Item.ItemOption(7, 200000), new Item.ItemOption(28, 100000)),// giày
                            Arrays.asList(new Item.ItemOption(14, 30))                                // nhẫn
                        );

                        // Tạo và cấu hình items
                        List<Item> items = new ArrayList<>();
                        for (int slot = 0; slot < templates[select].length; slot++) {
                            short tplId = templates[select][slot];
                            Item it = ItemService.gI().createNewItem(tplId);
                            // Thêm các tùy chọn đặc trưng cho slot
                            for (Item.ItemOption opt : slotOptions.get(slot)) {
                                it.itemOptions.add(opt);
                            }
                            // Chỉ thêm cặp option chính đã chọn
                            int idA = options[select][subSelect];
                            int idB = options1[select][subSelect];
                            it.itemOptions.add(new Item.ItemOption(idA, 1));
                            it.itemOptions.add(new Item.ItemOption(idB, 1));
                            // Thêm option chung cuối
                            it.itemOptions.add(new Item.ItemOption(30, 1));
                            items.add(it);
                        }

                        // Kiểm tra số ô trống và xử lý
                        Item hopQua = InventoryService.gI().findItemBag(player, 2002);
                        int need = items.size();
                        if (InventoryService.gI().getCountEmptyBag(player) < need) {
                            Service.gI().sendThongBao(player, "Cần " + need + " ô hành trang mới có thể mở!!!");
                            return;
                        }

                        // Thêm item và trừ hộp quà
                        for (Item it : items) {
                            InventoryService.gI().addItemBag(player, it);
                        }
                        InventoryService.gI().subQuantityItemsBag(player, hopQua, 1);
                        InventoryService.gI().sendItemBag(player);

                        // Thông báo
                        String[] names = {"Trái Đất", "Namek", "Xayda"};
                        Service.gI().sendThongBao(player, "Bạn nhận được 1 set thiên sứ kích hoạt " + names[select]);
                        break;
                    }
                    

                    case ConstNpc.HOP_QUA_THAN_LINH -> {
                        Item aotl_td = ItemService.gI().createNewItem((short) 555);
                        Item aotl_nm = ItemService.gI().createNewItem((short) 557);
                        Item aotl_xd = ItemService.gI().createNewItem((short) 559);

                        aotl_td.itemOptions.add(new Item.ItemOption(47, 800 + new Random().nextInt(200)));

                        aotl_nm.itemOptions.add(new Item.ItemOption(47, 900 + new Random().nextInt(100)));

                        aotl_xd.itemOptions.add(new Item.ItemOption(47, 950 + new Random().nextInt(200)));

                        aotl_td.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ
                        aotl_nm.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ
                        aotl_xd.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ

                        aotl_td.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ
                        aotl_nm.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ
                        aotl_xd.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ

                        Item quantl_td = ItemService.gI().createNewItem((short) 556);
                        Item quantl_nm = ItemService.gI().createNewItem((short) 558);
                        Item quantl_xd = ItemService.gI().createNewItem((short) 560);

                        quantl_td.itemOptions.add(new Item.ItemOption(22, 47 + new Random().nextInt(5)));
                        quantl_td.itemOptions
                                .add(new Item.ItemOption(27, (47 + new Random().nextInt(5)) * 1000 * 15 / 100));

                        quantl_nm.itemOptions.add(new Item.ItemOption(22, 45 + new Random().nextInt(5)));
                        quantl_nm.itemOptions
                                .add(new Item.ItemOption(27, (45 + new Random().nextInt(5)) * 1000 * 15 / 100));

                        quantl_xd.itemOptions.add(new Item.ItemOption(22, 42 + new Random().nextInt(8)));
                        quantl_xd.itemOptions
                                .add(new Item.ItemOption(27, (42 + new Random().nextInt(8)) * 1000 * 15 / 100));

                        quantl_td.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ
                        quantl_nm.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ
                        quantl_xd.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ

                        quantl_td.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ
                        quantl_nm.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ
                        quantl_xd.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ

                        Item gangtl_td = ItemService.gI().createNewItem((short) 562);
                        Item gangtl_nm = ItemService.gI().createNewItem((short) 564);
                        Item gangtl_xd = ItemService.gI().createNewItem((short) 566);

                        gangtl_td.itemOptions.add(new Item.ItemOption(0, 3500 + new Random().nextInt(1200)));
                        gangtl_nm.itemOptions.add(new Item.ItemOption(0, 3300 + new Random().nextInt(1100)));
                        gangtl_xd.itemOptions.add(new Item.ItemOption(0, 3500 + new Random().nextInt(1400)));

                        gangtl_td.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ
                        gangtl_nm.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ
                        gangtl_xd.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ

                        gangtl_td.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ
                        gangtl_nm.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ
                        gangtl_xd.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ

                        Item giaytl_td = ItemService.gI().createNewItem((short) 563);
                        Item giaytl_nm = ItemService.gI().createNewItem((short) 565);
                        Item giaytl_xd = ItemService.gI().createNewItem((short) 567);

                        giaytl_td.itemOptions.add(new Item.ItemOption(23, 42 + new Random().nextInt(5)));
                        giaytl_nm.itemOptions.add(new Item.ItemOption(23, 47 + new Random().nextInt(5)));
                        giaytl_xd.itemOptions.add(new Item.ItemOption(23, 45 + new Random().nextInt(4)));

                        giaytl_td.itemOptions
                                .add(new Item.ItemOption(28, (42 + new Random().nextInt(5)) * 1000 * 15 / 100));
                        giaytl_nm.itemOptions
                                .add(new Item.ItemOption(28, (47 + new Random().nextInt(5)) * 1000 * 15 / 100));
                        giaytl_xd.itemOptions
                                .add(new Item.ItemOption(28, (45 + new Random().nextInt(4)) * 1000 * 15 / 100));

                        giaytl_td.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ
                        giaytl_nm.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ
                        giaytl_xd.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ

                        giaytl_td.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ
                        giaytl_nm.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ
                        giaytl_xd.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ

                        Item nhan = ItemService.gI().createNewItem((short) 561);

                        nhan.itemOptions.add(new Item.ItemOption(14, 14 + new Random().nextInt(4)));
                        nhan.itemOptions.add(new Item.ItemOption(21, 18)); // ycsm 18 tỉ

                        nhan.itemOptions.add(new Item.ItemOption(30, 1)); // ycsm 18 tỉ
                        Item HopQuaThanLinh = InventoryService.gI().findItemBag(player, 1228);
                        switch (select) {
                            case 0:
                                if (InventoryService.gI().getCountEmptyBag(player) < 5) {
                                    Service.gI().sendThongBao(player, "Cần 5 ô hành trang mới có thể mở!!!");
                                    return;
                                }
                                InventoryService.gI().addItemBag(player, aotl_td);
                                InventoryService.gI().addItemBag(player, quantl_td);
                                InventoryService.gI().addItemBag(player, gangtl_td);
                                InventoryService.gI().addItemBag(player, giaytl_td);
                                InventoryService.gI().addItemBag(player, nhan);
                                InventoryService.gI().subQuantityItemsBag(player, HopQuaThanLinh, 1);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn nhận được 1 set thần linh trái đất");
                                break;
                            case 1:
                                if (InventoryService.gI().getCountEmptyBag(player) < 5) {
                                    Service.gI().sendThongBao(player, "Cần 5 ô hành trang mới có thể mở!!!");
                                    return;
                                }
                                InventoryService.gI().addItemBag(player, aotl_nm);
                                InventoryService.gI().addItemBag(player, quantl_nm);
                                InventoryService.gI().addItemBag(player, gangtl_nm);
                                InventoryService.gI().addItemBag(player, giaytl_nm);
                                InventoryService.gI().addItemBag(player, nhan);
                                InventoryService.gI().subQuantityItemsBag(player, HopQuaThanLinh, 1);
                                Service.gI().sendThongBao(player, "Bạn nhận được 1 set thần linh namek");
                                InventoryService.gI().sendItemBag(player);
                                break;
                            case 2:
                                if (InventoryService.gI().getCountEmptyBag(player) < 5) {
                                    Service.gI().sendThongBao(player, "Cần 5 ô hành trang mới có thể mở!!!");
                                    return;
                                }
                                InventoryService.gI().addItemBag(player, aotl_xd);
                                InventoryService.gI().addItemBag(player, quantl_xd);
                                InventoryService.gI().addItemBag(player, gangtl_xd);
                                InventoryService.gI().addItemBag(player, giaytl_xd);
                                InventoryService.gI().addItemBag(player, nhan);
                                InventoryService.gI().subQuantityItemsBag(player, HopQuaThanLinh, 1);
                                InventoryService.gI().sendItemBag(player);

                                Service.gI().sendThongBao(player, "Bạn nhận được 1 set thần linh xayda");
                                break;
                        }
                    }
                    case ConstNpc.MENU_XUONG_TANG_DUOI -> {
                        if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX && player.zone.map.mapId != 120) {
                            ChangeMapService.gI().changeMap(player,
                                    player.zone.map.mapIdNextMabu((short) player.zone.map.mapId), -1, -1, 100);
                        }
                    }
                }
            }
        };
    }
}
