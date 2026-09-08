package services;

import consts.ConstPlayer;
import models.item.Item;
import models.player.NewPet;
import models.player.Pet;
import models.player.Player;
import services.func.ChangeMapService;
import services.ItemService;
import services.InventoryService;
import services.Service;
import utils.SkillUtil;
import utils.Util;

/**
 * Quản lý việc tạo và thay đổi đệ tử (Pet) cho người chơi.
 */
public class PetService {

    private static PetService instance;

    private PetService() {}

    public static PetService gI() {
        if (instance == null) {
            instance = new PetService();
        }
        return instance;
    }

    //---- Public API: tạo pet mới ----

    public void createNormalPet(Player player, int gender, byte... limitPower) {
        spawnPet(() -> createNewPet(player, false, false, false, false, (byte) gender),
                 player, limitPower, "Xin hãy thu nhận làm đệ tử");
    }

    public void createNormalPet(Player player, byte... limitPower) {
        spawnPet(() -> createNewPet(player, false, false, false, false),
                 player, limitPower, "Xin hãy thu nhận làm đệ tử");
    }

    public void createMabuPet(Player player, byte... limitPower) {
        spawnPet(() -> createNewPet(player, true, false, false, false),
                 player, limitPower, "Oa oa oa...");
    }

    public void createMabuPet(Player player, int gender, byte... limitPower) {
        spawnPet(() -> createNewPet(player, true, false, false, false, (byte) gender),
                 player, limitPower, "Oa oa oa...");
    }

    public void createBeerusPet(Player player, byte... limitPower) {
        spawnPet(() -> createNewPet(player, false, true, false, false),
                 player, limitPower, "Thần hủy diệt hiện thân tất cả quỳ xuống...");
    }

    public void createBeerusPet(Player player, int gender, byte... limitPower) {
        spawnPet(() -> createNewPet(player, false, true, false, false, (byte) gender),
                 player, limitPower, "Thần hủy diệt hiện thân tất cả quỳ xuống...");
    }

    public void createPicPet(Player player, byte... limitPower) {
        spawnPet(() -> createNewPet(player, false, false, true, false),
                 player, limitPower, "Sư Phụ SooMe hiện thân tụi m quỳ xuống...");
    }

    public void createPicPet(Player player, int gender, byte... limitPower) {
        spawnPet(() -> createNewPet(player, false, false, true, false, (byte) gender),
                 player, limitPower, "Sư Phụ SooMe hiện thân tụi m quỳ xuống...");
    }

    //---- Pet Nhí ----

    public void createMabu1Pet(Player player, byte... limitPower) {
        spawnPet(() -> createNewPet1(player, true, false, false),
                 player, limitPower, "Oa oa oa...");
    }

    public void createMabu1Pet(Player player, int gender, byte... limitPower) {
        spawnPet(() -> createNewPet1(player, true, false, false, (byte) gender),
                 player, limitPower, "Oa oa oa...");
    }

    public void createCellPet(Player player, byte... limitPower) {
        spawnPet(() -> createNewPet1(player, false, true, false),
                 player, limitPower, "Oa oa oa...");
    }

    public void createCellPet(Player player, int gender, byte... limitPower) {
        spawnPet(() -> createNewPet1(player, false, true, false, (byte) gender),
                 player, limitPower, "Oa oa oa...");
    }

    public void createFidePet(Player player, byte... limitPower) {
        spawnPet(() -> createNewPet1(player, false, false, true),
                 player, limitPower, "Oa oa oa...");
    }

    public void createFidePet(Player player, int gender, byte... limitPower) {
        spawnPet(() -> createNewPet1(player, false, false, true, (byte) gender),
                 player, limitPower, "Oa oa oa...");
    }

    public void createBlackPet(Player player, byte... limitPower) {
        spawnPet(() -> createNewPet(player, false, false, false, true),
                 player, limitPower, "Ta sẽ cho người biết sức mạnh của một vị thần là như thế nào !");
    }

    public void createBlackPet(Player player, int gender, byte... limitPower) {
        spawnPet(() -> createNewPet(player, false, false, false, true, (byte) gender),
                 player, limitPower, "Ta sẽ cho người biết sức mạnh của một vị thần là như thế nào !");
    }

    //---- Public API: đổi pet hiện tại ----

    public void changeNormalPet(Player player, int gender) {
        changePet(player, () -> createNormalPet(player, gender));
    }

    public void changeNormalPet(Player player) {
        changePet(player, () -> createNormalPet(player));
    }

    public void changeMabuPet(Player player) {
        changePet(player, () -> createMabuPet(player));
    }

    public void changeMabuPet(Player player, int gender) {
        changePet(player, () -> createMabuPet(player, gender));
    }

    public void changeBeerusPet(Player player) {
        changePet(player, () -> createBeerusPet(player));
    }

    public void changeBeerusPet(Player player, int gender) {
        changePet(player, () -> createBeerusPet(player, gender));
    }

    public void changePicPet(Player player) {
        changePet(player, () -> createPicPet(player));
    }

    public void changePicPet(Player player, int gender) {
        changePet(player, () -> createPicPet(player, gender));
    }

    public void changeMabu1Pet(Player player) {
        changePet(player, () -> createMabu1Pet(player));
    }

    public void changeMabu1Pet(Player player, int gender) {
        changePet(player, () -> createMabu1Pet(player, gender));
    }

    public void changeCellPet(Player player) {
        changePet(player, () -> createCellPet(player));
    }

    public void changeCellPet(Player player, int gender) {
        changePet(player, () -> createCellPet(player, gender));
    }

    public void changeFidePet(Player player) {
        changePet(player, () -> createFidePet(player));
    }

    public void changeFidePet(Player player, int gender) {
        changePet(player, () -> createFidePet(player, gender));
    }

    public void changeBlackPet(Player player) {
        changePet(player, () -> createBlackPet(player));
    }

    public void changeBlackPet(Player player, int gender) {
        changePet(player, () -> createBlackPet(player, gender));
    }

    public void changeNamePet(Player player, String name) {
        if (!InventoryService.gI().isExistItemBag(player, 400)) {
            Service.gI().sendThongBao(player, "Bạn cần thẻ đặt tên đệ tử, mua tại Santa");
            return;
        }
        if (Util.haveSpecialCharacter(name)) {
            Service.gI().sendThongBao(player, "Tên không được chứa ký tự đặc biệt");
            return;
        }
        if (name.length() > 10) {
            Service.gI().sendThongBao(player, "Tên quá dài");
            return;
        }
        ChangeMapService.gI().exitMap(player.pet);
        player.pet.name = "$" + name.trim().toLowerCase();
        InventoryService.gI().subQuantityItemsBag(player,
            InventoryService.gI().findItemBag(player, 400), 1);
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException ignore) {}
            Service.gI().chatJustForMe(player, player.pet,
                "Cảm ơn sư phụ đã đặt cho con tên " + name);
        }).start();
    }

    //---- Helper: spawn pet in background thread ----
    private void spawnPet(Runnable createFn,
                          Player player,
                          byte[] limitPower,
                          String chatMessage) {
        new Thread(() -> {
            createFn.run();
            if (limitPower != null && limitPower.length == 1 && player.pet != null) {
                player.pet.nPoint.limitPower = limitPower[0];
                player.pet.nPoint.initPowerLimit();
            }
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            if (player.pet != null) {
                Service.gI().chatJustForMe(player, player.pet, chatMessage);
            }
        }).start();
    }

    //---- Helper: chung cho changePet ----
    private void changePet(Player player, Runnable createFn) {
        byte oldLimit = 0;
        // Nếu có pet, lưu lại và dispose
        if (player.pet != null) {
            oldLimit = player.pet.nPoint.limitPower;
            if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
                player.pet.unFusion();
            }
            ChangeMapService.gI().exitMap(player.pet);
            player.pet.dispose();
            player.pet = null;
        }
        // Tạo pet mới
        createFn.run();
        // Nếu spawn thành công, restore limitPower
        if (player.pet != null) {
            player.pet.nPoint.limitPower = oldLimit;
            player.pet.nPoint.initPowerLimit();
        }
    }

    //---- Core: tạo pet với các loại chuẩn ----
    private void createNewPet(Player player,
                              boolean isMabu,
                              boolean isBeerus,
                              boolean isPic,
                              boolean isBlack,
                              byte... gender) {
        int[] data = isMabu ? getDataPetMabu() :
                       isPic  ? getDataPetPic()  :
                       getDataPetNormal();
        String typeName = isMabu ? "Mabư" :
                          isBeerus? "Beerus" :
                          isPic   ? "Pic"    :
                          isBlack ? "Black"  :
                                    "Đệ tử";
        byte     typeId   = (byte) (isMabu  ? 1 :
                                    isBeerus? 2 :
                                    isPic   ? 3 :
                                    isBlack ? 4 :
                                              0);
        createPetInstance(player, data, typeName, typeId, gender);
    }

    //---- Core: tạo pet Nhí ----
    private void createNewPet1(Player player,
                               boolean isMabu1,
                               boolean isCell,
                               boolean isFide,
                               byte... gender) {
        int[] data;
        String typeName;
        byte typeId;
        if (isMabu1) {
            data = getDataPetMabu1(); typeName = "Mabư Nhí"; typeId = 5;
        } else if (isCell) {
            data = getDataPetCell();  typeName = "Cell Nhí"; typeId = 6;
        } else if (isFide) {
            data = getDataPetFide();  typeName = "Fide Nhí"; typeId = 7;
        } else {
            data = getDataPetNormal(); typeName = "Đệ tử"; typeId = 0;
        }
        createPetInstance(player, data, typeName, typeId, gender);
    }

    //---- Tạo instance Pet chung ----
    private void createPetInstance(Player player,
                                   int[] data,
                                   String typeName,
                                   byte typeId,
                                   byte... gender) {
        Pet pet = new Pet(player);
        pet.name    = "$" + typeName;
        pet.gender  = (gender != null && gender.length > 0)
                      ? gender[0]
                      : (byte) Util.nextInt(0, 2);
        pet.id      = player.isPl() ? -player.id : -Math.abs(player.id) - 100000;
        pet.nPoint.power      = (typeId != 0) ? 1_500_000 : 2_000;
        pet.typePet           = typeId;
        pet.nPoint.stamina    = pet.nPoint.maxStamina = 1000;
        pet.nPoint.hpg        = data[0];
        pet.nPoint.mpg        = data[1];
        pet.nPoint.dameg      = data[2];
        pet.nPoint.defg       = data[3];
        pet.nPoint.critg      = data[4];
        pet.inventory.itemsBody.clear();
        for (int i = 0; i < 8; i++) {
            pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
        pet.playerSkill.skills.clear();
        pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2)*2, 1));
        for (int i = 0; i < 3; i++) {
            pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
        }
        pet.nPoint.setFullHpMp();
        player.pet = pet;
    }

    //---- Dữ liệu ngẫu nhiên cho pet ----
    private int[] getDataPetNormal() {
        return new int[] {
            Util.nextInt(40, 105)*20,
            Util.nextInt(40, 105)*20,
            Util.nextInt(20, 45),
            Util.nextInt(9, 50),
            Util.nextInt(0, 2)
        };
    }

    private int[] getDataPetMabu() {
        return new int[] {
            Util.nextInt(40, 105)*20,
            Util.nextInt(40, 105)*20,
            Util.nextInt(50, 120),
            Util.nextInt(9, 50),
            Util.nextInt(0, 2)
        };
    }

    private int[] getDataPetPic() {
        return new int[] {
            Util.nextInt(40, 115)*20,
            Util.nextInt(40, 115)*20,
            Util.nextInt(70, 140),
            Util.nextInt(9, 50),
            Util.nextInt(0, 2)
        };
    }

    private int[] getDataPetMabu1() {
        return getDataPetMabu();
    }

    private int[] getDataPetCell() {
        return getDataPetPic();
    }

    private int[] getDataPetFide() {
        return getDataPetPic();
    }

    /**
     * Tạo đối tượng NewPet (thí dụ dùng cho tính năng đặc biệt)
     */
    public static void Pet2(Player pl, int h, int b, int l) {
        if (pl.newPet != null) {
            pl.newPet.dispose();
        }
        pl.newPet = new NewPet(pl, (short)h, (short)b, (short)l);
        pl.newPet.name = "$";
        pl.newPet.gender = pl.gender;
        pl.newPet.nPoint.tiemNang = 1;
        pl.newPet.nPoint.power = 1;
        pl.newPet.nPoint.limitPower = 1;
        pl.newPet.nPoint.hpg = 500_000_000;
        pl.newPet.nPoint.mpg = 500_000_000;
        pl.newPet.nPoint.hp  = 500_000_000;
        pl.newPet.nPoint.mp  = 500_000_000;
        pl.newPet.nPoint.dameg = 1;
        pl.newPet.nPoint.defg  = 1;
        pl.newPet.nPoint.critg = 1;
        pl.newPet.nPoint.stamina = 1;
        pl.newPet.nPoint.setBasePoint();
        pl.newPet.nPoint.setFullHpMp();
    }
}
