package server;

import consts.ConstNpc;
import models.boss.BossManager;
import models.Bot.BotManager;
import models.GiftCode.GiftCodeManager;
import models.ShenronEvent.ShenronEventManager;
import models.ShenronEvent.ShenronEvent;
import models.item.Item;
import models.minigame.LuckyNumber;
import models.player.Player;
import models.player.Pet;
import models.player.badges.BadgesData;
import models.skill.Skill;
import network.SessionManager;
import server.Client;
import services.ClanService;
import services.InventoryService;
import services.ItemService;
import services.NpcService;
import services.PetService;
import services.Service;
import services.TaskService;
import services.func.ChangeMapService;
import services.func.Input;
import services.SkillService;
import utils.SystemMetrics;

/**
 * Command: xử lý lệnh chat cho người chơi và admin
 */
public class Command {

    private static Command instance;

    private Command() {}

    public static synchronized Command gI() {
        if (instance == null) {
            instance = new Command();
        }
        return instance;
    }

    public void chat(Player player, String text) {
        if (!check(player, text)) {
            Service.gI().chat(player, text);
        }
    }

    /**
     * Xử lý các lệnh admin
     * @return true nếu là lệnh admin đã xử lý
     */
    public boolean check(Player player, String text) {
        if (player.isAdmin()) {
            switch (text) {
                case "gt" -> {
                    GiftCodeManager.gI().checkInfomationGiftCode(player);
                    return true;
                }
                case "a" -> {
                    BossManager.gI().showListBoss(player);
                    return true;
                }
                case "Skill", "r" -> {
                    Service.gI().releaseCooldownSkill(player);
                    return true;
                }
                
                default -> {
                    if (text.startsWith("sp")) {
                        try {
                            long power = Long.parseLong(text.substring(2));
                            Service.gI().addSMTN(player, (byte)2, power, false);
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "sp + số");
                        }
                        return true;
                    } else if (text.equals("battu")) {
                        player.isBattu = !player.isBattu;
                        Service.gI().sendThongBao(player, "Bất tử" + (player.isBattu ? ": ON" : ": OFF"));
                        return true;
                    } else if (text.startsWith("dt")) {
                        try {
                            long power = Long.parseLong(text.substring(2));
                            Service.gI().addSMTN(player.pet, (byte)2, power, false);
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "dt + số");
                        }
                        return true;
                    } else if (text.equals("test")) {
                        switch (player.gender) {
                            case 0 -> SkillService.gI().learSkillSpecial(player, Skill.SUPER_KAME, 1);
                            case 2 -> SkillService.gI().learSkillSpecial(player, Skill.LIEN_HOAN_CHUONG, 1);
                            default -> SkillService.gI().learSkillSpecial(player, Skill.MA_PHONG_BA, 1);
                        }
                        return true;
                    } else if (text.equals("dragon")) {
                        ShenronEvent shenron = new ShenronEvent();
                        shenron.setPlayer(player);
                        ShenronEventManager.gI().add(shenron);
                        player.shenronEvent = shenron;
                        shenron.setZone(player.zone);
                        shenron.activeShenron(true, ShenronEvent.DRAGON_EVENT);
                        shenron.sendWhishesShenron();
                        return true;
                    } else if (text.equals("ad")) {
                        // Thông tin hệ thống admin
                        String info = String.format(
                            "|7| Thời Gian Bắt Đầu: %s\n" +
                            "Số Người Chơi : %d Player\n" +
                            "Bot online : %d\n" +
                            "Sessions : %d\n" +
                            "Threads : %d\n" +
                            "%s\n",
                            ServerManager.timeStart,
                            Client.gI().getPlayers().size(),
                            BotManager.gI().bot.size(),
                            SessionManager.gI().getNumSession(),
                            Thread.activeCount(),
                            SystemMetrics.getMetrics()
                        );
                        NpcService.gI().createMenuConMeo(
                            player,
                            ConstNpc.MENU_ADMIN,
                            -1,
                            info,
                            new String[]{
                                "Ngọc rồng",
                                "Đệ tử",
                                "Bảo trì",
                                "Tìm kiếm người chơi",
                                "Boss",
                                "Buff VND",
                                "Buff Item",
                                "Hộp Thư",
                                "Đóng"
                            }
                        );
                        return true;
                    } else if (text.startsWith("m")) {
                        try {
                            int mapId = Integer.parseInt(text.substring(1));
                            ChangeMapService.gI().changeMapInYard(player, mapId, -1, -1);
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "m + id map");
                        }
                        return true;
                        } else if (text.startsWith("dmg")) {
                        try {
                            player.nPoint.dameg = Integer.parseInt(text.substring(3));
                            Service.gI().point(player);
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "dmg + số");
                        }
                        return true;
                    } else if (text.startsWith("hpg")) {
                        try {
                            player.nPoint.hpg = Integer.parseInt(text.substring(3));
                            Service.gI().point(player);
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "hpg + số");
                        }
                        return true;
                    } else if (text.startsWith("kig")) {
                        try {
                            player.nPoint.mpg = Integer.parseInt(text.substring(3));
                            Service.gI().point(player);
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "kig + số");
                        }
                        return true;
                    } else if (text.startsWith("defg")) {
                        try {
                            player.nPoint.defg = Integer.parseInt(text.substring(4));
                            Service.gI().point(player);
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "defg + số");
                        }
                        return true;
                    } else if (text.startsWith("crg")) {
                        try {
                            player.nPoint.critg = Integer.parseInt(text.substring(3));
                            Service.gI().point(player);
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "crg + số");
                        }
                        return true;
                    } else if (text.startsWith("ntask")) {
                        try {
                            int idTask = Integer.parseInt(text.substring(5));
                            player.playerTask.taskMain.id = idTask - 1;
                            player.playerTask.taskMain.index = 0;
                            TaskService.gI().sendNextTaskMain(player);
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "ntask + id nhiệm vụ");
                        }
                        return true;
                    } else if (text.startsWith("badges_")) {
                        try {
                            player.badges.idBadges = Integer.parseInt(text.substring(7));
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "badges_ + id huy hiệu");
                        }
                        return true;
                    } else if (text.startsWith("kq")) {
                        Service.gI().sendThongBao(player, "Kết quả Lucky Round tiếp theo là: " + LuckyNumber.RESULT);
                        return true;
                    } else if (text.startsWith("danhhieu_")) {
                        try {
                            new BadgesData(player, Integer.parseInt(text.substring(9)), 5);
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "danhhieu_ + id danh hiệu");
                        }
                        return true;
                    } else if (text.startsWith("gender_")) {
                        try {
                            player.gender = Byte.parseByte(text.substring(7));
                        } catch (NumberFormatException e) {
                            Service.gI().sendThongBao(player, "gender_ + id giới tính (0-2)");
                        }
                        return true;
                    } else if (text.startsWith("i")) {
                        try {
                            String[] parts = text.substring(1).split(" ");
                            short itemId = Short.parseShort(parts[0]);
                            Item it = ItemService.gI().createNewItem(itemId);
                            if (it == null) throw new IllegalArgumentException();
                            int qty = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
                            it.quantity = qty;
                            if (parts.length > 2) {
                                String target = parts[2];
                                Player p = Client.gI().getPlayer(target);
                                if (p != null) {
                                    InventoryService.gI().addItemBag(p, it);
                                    InventoryService.gI().sendItemBag(p);
                                    Service.gI().sendThongBao(p, "Đã nhận được " + it.template.name);
                                    Service.gI().sendThongBao(player, "Đã buff " + p.name);
                                } else {
                                    Service.gI().sendThongBao(player, "Không tìm thấy vật phẩm");
                                }
                            } else {
                                InventoryService.gI().addItemBag(player, it);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Đã nhận được " + it.template.name);
                            }
                        } catch (Exception e) {
                            Service.gI().sendThongBao(player, "i + id_item [số lượng] [tên người chơi]");
                        }
                        return true;
                    } else if (text.equals("item")) {
                        Input.gI().createFormGiveItem(player);
                        return true;
                    } else if (text.equals("vt")) {
                    Service.gI().sendThongBao(player, player.location.x + " - " + player.location.y + "\n"
                            + player.zone.map.yPhysicInTop(player.location.x, player.location.y));    
                    } else if (text.equals("getitem")) {
                        Input.gI().createFormGetItem(player);
                        return true;
                    } else if (text.equals("d")) {
                        Service.gI().setPos(player, player.location.x, player.location.y + 10);
                        return true;
//                    } else if (text.startsWith("admin") || text.startsWith("help")) {
//                        sendCommandSyntax(player);
//                        return true;
                    }
                }
            }
        }

        // Các lệnh chung cho người chơi khác
        if (text.startsWith("ten con la ")) {
            PetService.gI().changeNamePet(player, text.replace("ten con la ", ""));
            return true;
        }
        // Lệnh pet
//        if (player.pet != null) {
//            switch (text) {
//                case "di theo", "follow" -> player.pet.changeStatus(Pet.FOLLOW);
//                case "bao ve", "protect" -> player.pet.changeStatus(Pet.PROTECT);
//                case "tan cong", "attack" -> player.pet.changeStatus(Pet.ATTACK);
//                case "ve nha", "go home" -> player.pet.changeStatus(Pet.GOHOME);
//                case "bien hinh" -> player.pet.transform();
//            }
//        }
        return false;
    }
}

    /**
     * Gửi danh sách cú pháp lệnh cho admin
     */
//    private void sendCommandSyntax(Player player) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Danh sách lệnh admin:\n");
//        sb.append("gt: Kiểm tra thông tin gift code\n");
//        sb.append("a: Hiện danh sách boss\n");
//        sb.append("bot: Hiển thị số lượng bot online\n");
//        sb.append("Skill/r: Reset cooldown skill\n");
//        sb.append("sp + số: Tăng sức mạnh\n");
//        sb.append("dt + số: Tăng sức mạnh đệ tử\n");
//        sb.append("... (các lệnh khác)\n");
//        Service.gI().sendThongBao(player, sb.toString());
//    }
//}

                        
                        
                        
                        
                        
                    
        