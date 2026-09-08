package models.item;

import models.Template;
import models.Template.ItemTemplate;
import services.ItemService;
import utils.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import models.Combine.CombineUtil;

public class Item {

    public ItemTemplate template;

    public String info;

    public String content;

    public int quantity;

    public int quantityGD = 0;

    public List<ItemOption> itemOptions;

    public long createTime;
    public static final short[] itemIds_Cai_Trang_BABY = {1763};
    public static final short[] itemIds_Kaio_AWJ = {232, 236, 240, 244, 248, 252, 268, 272, 276};
    public static final short[] itemIds_tl_AWJ = {555, 557, 559, 556, 558, 560, 563, 565, 567};
    public static final short[] itemIds_tl_GN = {562, 564, 566, 561};
    public static final short[] itemIds_Kaio_GN = {256, 260, 264, 280};
    public static final short[] itemIds_LuongLong_AWJ = {233, 237, 241, 245, 249, 253, 269, 273, 277};
    public static final short[] itemIds_LuongLong_GN = {257, 261, 265, 281};
    public static final short[] itemIds_GIAY_TL = {563, 565, 567};
    public static final short[][] trangBiKichHoat = {{0, 6, 21, 27}, {1, 7, 22, 28}, {2, 8, 23, 29}};
    public static final short[][] trangBiKichHoatVip = {{555, 556, 562, 563}, {557, 558, 564, 565},
    {559, 560, 566, 567}};
    public static final short[] aotd = {138, 139, 230, 231, 232, 233, 555};
    public static final short[] quantd = {142, 143, 242, 243, 244, 245, 556};
    public static final short[] gangtd = {146, 147, 254, 255, 256, 257, 562};
    public static final short[] giaytd = {150, 151, 266, 267, 268, 269, 563};
    public static final short[] aoxd = {170, 171, 238, 239, 240, 241, 559};
    public static final short[] quanxd = {174, 175, 250, 251, 252, 253, 560};
    public static final short[] gangxd = {178, 179, 262, 263, 264, 265, 566};
    public static final short[] giayxd = {182, 183, 274, 275, 276, 277, 567};
    public static final short[] aonm = {154, 155, 234, 235, 236, 237, 557};
    public static final short[] quannm = {158, 159, 246, 247, 248, 249, 558};
    public static final short[] gangnm = {162, 163, 258, 259, 260, 261, 564};
    public static final short[] giaynm = {166, 167, 270, 271, 272, 273, 565};
    public static final short[] radaSKHVip = {186, 187, 278, 279, 280, 281, 561};
    public static final short[][][] doSKHVip = {{aotd, quantd, gangtd, giaytd}, {aonm, quannm, gangnm, giaynm},
    {aoxd, quanxd, gangxd, giayxd}};
    public static final int[] LIST_ITEM_NLSK_TET_2023 = {2027, 2028, 2029, 2030, 2030, 2037, 2038};
    public static final int[][][] LIST_ITEM_CLOTHES = {
        {{0, 33, 3, 34, 136, 137, 138, 139, 230, 231, 232, 233, 555}, {6, 35, 9, 36, 140, 141, 142, 143, 242, 243, 244, 245, 556}, {21, 24, 37, 38, 144, 145, 146, 147, 254, 255, 256, 257, 562}, {27, 30, 39, 40, 148, 149, 150, 151, 266, 267, 268, 269, 563}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}},
        {{1, 41, 4, 42, 152, 153, 154, 155, 234, 235, 236, 237, 557}, {7, 43, 10, 44, 156, 157, 158, 159, 246, 247, 248, 249, 558}, {22, 46, 25, 45, 160, 161, 162, 163, 258, 259, 260, 261, 564}, {28, 47, 31, 48, 164, 165, 166, 167, 270, 271, 272, 273, 565}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}},
        {{2, 49, 5, 50, 168, 169, 170, 171, 238, 239, 240, 241, 559}, {8, 51, 11, 52, 172, 173, 174, 175, 250, 251, 252, 253, 560}, {23, 53, 26, 54, 176, 177, 178, 179, 262, 263, 264, 265, 566}, {29, 55, 32, 56, 180, 181, 182, 183, 274, 275, 276, 277, 567}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}}
    };
    public boolean isNotNullItem() {
        return this.template != null;
    }

    public Item() {
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }
    public String getName() {
        return template.name;
    }

    public Item(short itemId) {
        this.template = ItemService.gI().getTemplate(itemId);
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public String getInfo() {
        String strInfo = "";
        for (ItemOption itemOption : itemOptions) {
            strInfo += itemOption.getOptionString();
        }
        return strInfo;
    }

    public String getContent() {
        return "Yêu cầu sức mạnh " + this.template.strRequire + " trở lên";
    }

    public void dispose() {
        this.template = null;
        this.info = null;
        this.content = null;
        if (this.itemOptions != null) {
            for (ItemOption io : this.itemOptions) {
                io.dispose();
            }
            this.itemOptions.clear();
        }
        this.itemOptions = null;
    }

    public static class ItemOption {

        public int param;

        public Template.ItemOptionTemplate optionTemplate;

        public ItemOption(ItemOption io) {
            this.param = io.param;
            this.optionTemplate = io.optionTemplate;
        }

        public ItemOption(int tempId, int param) {
            this.optionTemplate = ItemService.gI().getItemOptionTemplate(tempId);
            this.param = param;
        }

        public ItemOption(Template.ItemOptionTemplate temp, int param) {
            this.optionTemplate = temp;
            this.param = param;
        }

        public String getOptionString() {
            return Util.replace(this.optionTemplate.name, "#", String.valueOf(this.param));
        }
        private static Map<String, String> OPTION_STRING = new HashMap<>();
        public String getOptionString(int param) {
            String key = this.optionTemplate.name + "#" + param + "#";
            String value = OPTION_STRING.get(key);
            if (value == null) {
                value = Util.replace(this.optionTemplate.name, "#", String.valueOf(param));
                OPTION_STRING.put(key, value);
            }
            return value;
        }

        public boolean isOptionCanUpgrade() {
            int opId = this.optionTemplate.id;
            return opId == 0 || opId == 6 || opId == 7 || opId == 14 || opId == 22 || opId == 23 || opId == 27 || opId == 28 || opId == 47;
        }
        public boolean haveExpiryDate() {
            return optionTemplate.id == 93 || optionTemplate.id == 188;
        }

        public void dispose() {
            this.optionTemplate = null;
        }

        @Override
        public String toString() {
            final String n = "\"";
            return "{"
                    + n + "id" + n + ":" + n + optionTemplate.id + n + ","
                    + n + "param" + n + ":" + n + param + n
                    + "}";
        }
    }

    public boolean isSKH() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id >= 127 && itemOption.optionTemplate.id <= 135) {
                return true;
            }
        }
        return false;
    }

    public boolean isDTS() {
        return this.template.level == 15;
    }

    public boolean isDTL() {
        return this.template.level == 13;
    }

    public boolean isDHD() {
        return this.template.level == 14;
    }

    public boolean isManhThienSu() {
        return this.template.id >= 1066 && this.template.id <= 1070;
    }

    public boolean isDaMayMan() {
        return this.template.id >= 1079 && this.template.id <= 1083;
    }

    public boolean isDaNangCapTS() {
        return this.template.id >= 1074 && this.template.id <= 1078;
    }

    public boolean isCongThuc() {
        return this.template.id >= 1071 && this.template.id <= 1073;
    }

    public boolean isCongThucVip() {
        return this.template.id >= 1084 && this.template.id <= 1086;
    }

    public boolean isDaNangCap() {
        return this.template.type == 14;
    }

    public String typeName() {
    return switch (typeManh()) {
        case 0 -> "Áo";
        case 1 -> "Quần";
        case 2 -> "Găng";
        case 3 -> "Giày";
        case 4 -> "Nhẫn";
        default -> "";
    };
}

    public String getGenderName() {
        return template.gender == 0 ? "Trái Đất" : template.gender == 1 ? "Namếc" : "Xay da";
    }

    public byte typeManh() {
    return switch (this.template.id) {
        case 1066 -> 0;  // Mảnh Áo   → type 0
        case 1067 -> 1;  // Mảnh Quần → type 1
        case 1068 -> 2;  // Mảnh Găng → type 2
        case 1069 -> 3;  // Mảnh Giày → type 3
        case 1070 -> 4;  // Mảnh Nhẫn → type 4
        default   -> -1;
    };
}
    
    public boolean canPutInCollectionBox() {
        return isNotNullItem() && (template.type == 5 || template.type == 11 || template.type == 21 || template.type == 23) && itemOptions.stream().noneMatch(ItemOption::haveExpiryDate);
    }

    public boolean isSachTuyetKy() {
        return template.id == 1044 || template.id == 1211 || template.id == 1212;
    }

    public boolean isSachTuyetKy2() {
        return template.id >= 1278 && template.id <= 1280;
    }

    public boolean canNangCapWithNDC(Item daNangCap) {
        if (this.template.type == 0 && daNangCap.template.id == 223) {
            return true;
        } else if (this.template.type == 1 && daNangCap.template.id == 222) {
            return true;
        } else if (this.template.type == 2 && daNangCap.template.id == 224) {
            return true;
        } else if (this.template.type == 3 && daNangCap.template.id == 221) {
            return true;
        } else {
            return this.template.type == 4 && daNangCap.template.id == 220;
        }
    }

    public boolean isDaPhaLeEpSao() {
        return template != null && (template.type == 30 || (template.id >= 14 && template.id <= 20));
    }

    public boolean isDaPhaLeC1() {
        return template != null && template.id >= 411 && template.id <= 447;
    }

    public boolean isDaPhaLeC2() {
        return template != null && template.id >= 1416 && template.id <= 1422 || template.id == 964 || template.id == 965;
    }

    public boolean isDaPhaLeMoi() {
        return template != null && template.id >= 1416 && template.id <= 1422 || template.id == 964 || template.id == 965
                || template.id >= 1426 && template.id <= 1434;
    }

    public boolean isDaPhaLeCu() {
        return template != null && template.id >= 441 && template.id <= 447;
    }

    public boolean isTypeBody() {
        return template != null && (0 <= template.type && template.type < 6) || template.type == 32 || template.type == 35 || template.type == 11 || template.type == 23;
    }

    public boolean isHaveOption(int id) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                return true;
            }
        }
        return false;
    }

    public int getPercentOption() {
        int percent = 0;
        switch (this.template.type) {
            case 0 -> {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 47);
                int param = CombineUtil.reversePoint(getOptionParam(47), getOptionParam(72));
                percent = (param * 100) / paramZin;
            }
            case 1 -> {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 6);
                int param = CombineUtil.reversePoint(getOptionParam(6), getOptionParam(72));
                percent = (param * 100) / paramZin;
            }
            case 2 -> {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 0);
                int param = CombineUtil.reversePoint(getOptionParam(0), getOptionParam(72));
                percent = (param * 100) / paramZin;
            }
            case 3 -> {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 7);
                int param = CombineUtil.reversePoint(getOptionParam(7), getOptionParam(72));
                percent = (param * 100) / paramZin;
            }
            case 4 -> {
                int paramZin = ItemService.gI().getOptionParamItemShop(this.template.id, 14);
                int param = CombineUtil.reversePoint(getOptionParam(14), getOptionParam(72));
                percent = (param * 100) / paramZin;
            }
        }
        return percent;
    }

    public int getOptionParam(int id) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                return itemOption.param;
            }
        }
        return 0;
    }

    public void addOptionParam(int id, int param) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                itemOption.param += param;
                return;
            }
        }
        this.itemOptions.add(new ItemOption(id, param));
    }

    public void subOptionParam(int id, int param) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                itemOption.param -= param;
                return;
            }
        }
    }

    public void subOptionParamAndRemoveIfZero(int id, int param) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                itemOption.param -= param;
                if (param <= 0) {
                    this.itemOptions.remove(i);
                }
                break;
            }
        }
    }

    public void removeOption(int id) {
        for (int i = 0; i < this.itemOptions.size(); i++) {
            ItemOption itemOption = this.itemOptions.get(i);
            if (itemOption != null && itemOption.optionTemplate.id == id) {
                this.itemOptions.remove(i);
                break;
            }
        }
    }

    public ItemOption getOptionDaPhaLe() {
        return switch (template.id) {
            case 20 ->
                new ItemOption(77, 5);
            case 19 ->
                new ItemOption(103, 5);
            case 18 ->
                new ItemOption(80, 5);
            case 17 ->
                new ItemOption(81, 5);
            case 16 ->
                new ItemOption(50, 3);
            case 15 ->
                new ItemOption(94, 2);
            case 14 ->
                new ItemOption(108, 2);
            default ->
                itemOptions.get(0);
        };
    }

    public String getOptionInfo(Item item) {
        boolean haveOption = false;
        StringJoiner optionInfo = new StringJoiner("\n");
        Item itC = this.cloneItem();
        ItemOption iodpl = item.getOptionDaPhaLe();
        for (ItemOption io : itC.itemOptions) {
            if (!haveOption && io.optionTemplate.id == iodpl.optionTemplate.id) {
                io.param += iodpl.param;
                haveOption = true;
            }
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107) {
                optionInfo.add(io.getOptionString());
            }
        }
        if (!haveOption) {
            optionInfo.add(iodpl.getOptionString());
        }
        itC.dispose();
        return optionInfo.toString();
    }

    public String getOptionInfoCuongHoa(Item item) {
        StringJoiner optionInfo = new StringJoiner("\n");
        Item itC = this.cloneItem();
        ItemOption iodpl = item.getOptionDaPhaLe();
        for (ItemOption io : itC.itemOptions) {
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        optionInfo.add(iodpl.getOptionString());
        itC.dispose();
        return optionInfo.toString();
    }

    public String getOptionInfoChuyenHoa(Item item, int level) {
        StringJoiner optionInfo = new StringJoiner("\n");
        Item itC = this.cloneItem();
        int percent = item.getPercentOption();
        for (ItemOption io : itC.itemOptions) {
            if (io.isOptionCanUpgrade()) {
                io.param = CombineUtil.pointUp(io.param * percent / 100, level);
            }
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        for (ItemOption io : item.itemOptions) {
            if (!io.isOptionCanUpgrade() && io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        itC.dispose();
        return optionInfo.toString();
    }

    public String getOptionInfo() {
        StringJoiner optionInfo = new StringJoiner("\n");
        for (ItemOption io : this.itemOptions) {
            if (io.optionTemplate.id != 72 && io.optionTemplate.id != 73 && io.optionTemplate.id != 102 && io.optionTemplate.id != 107 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        return optionInfo.toString();
    }

    public String getOptionInfoUpgrade() {
        StringJoiner optionInfo = new StringJoiner("\n");
        for (ItemOption io : this.itemOptions) {
            if (io.isOptionCanUpgrade() || io.optionTemplate.id == 21 || io.param == 30 && io.optionTemplate.id != 218) {
                optionInfo.add(io.getOptionString());
            }
        }
        return optionInfo.toString();
    }

    public String getOptionInfoUpgradeFinal() {
        StringJoiner optionInfo = new StringJoiner("\n");
        Item clone = this.cloneItem();
        for (ItemOption io : clone.itemOptions) {
            if (io.isOptionCanUpgrade()) {
                io.param = CombineUtil.pointUp(io.param, 1);
            }
            if (io.isOptionCanUpgrade() || io.param == 30) {
                optionInfo.add(io.getOptionString());
            }
        }
        return optionInfo.toString();
    }

    public boolean canPhaLeHoa() {
        return this.template != null && (this.template.type < 5 || this.template.type == 32);
    }

    public Item cloneItem() {
        Item item = new Item();
        item.itemOptions = new ArrayList<>();
        item.template = this.template;
        item.info = this.info;
        item.content = this.content;
        item.quantity = this.quantity;
        item.createTime = this.createTime;
        for (Item.ItemOption io : this.itemOptions) {
            item.itemOptions.add(new Item.ItemOption(io));
        }
        return item;
    }
}
