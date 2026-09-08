package models.Combine;

import consts.ConstNpc;
import models.item.Item;
import models.Combine.manifest.CheTaoTrangBiThienSu;
import models.Combine.manifest.ChuyenHoaBangNgoc;
import models.Combine.manifest.ChuyenHoaBangVang;
import models.Combine.manifest.CuongHoaLoSaoPhaLe;
import models.Combine.manifest.DanhBongSaoPhaLe;
import models.Combine.manifest.EpSaoTrangBi;
import models.Combine.manifest.GiamDinhSach;
import models.Combine.manifest.HoiPhucSach;
import models.Combine.manifest.NangCapBongTai;
import models.Combine.manifest.NangCapChanMenh;
import models.Combine.manifest.NangCapKichHoat;
import models.Combine.manifest.NangCapKichHoatVip;
import models.Combine.manifest.NangCapSachTuyetKy;
import models.Combine.manifest.NangCapSaoPhaLe;
import models.Combine.manifest.NangCapVatPham;
import models.Combine.manifest.NangChiSoBongTai;
import models.Combine.manifest.NhapNgocRong;
import models.Combine.manifest.PhaLeHoaTrangBi;
import models.Combine.manifest.PhanRaSach;
import models.Combine.manifest.TaoDaHematite;
import models.Combine.manifest.TaySach;
import models.player.Player;
import network.Message;
import models.npc.Npc;
import models.npc.NpcManager;
import services.InventoryService;

import java.io.IOException;
import models.Combine.manifest.Lamphepnhapda;
import models.Combine.manifest.NangCapBongTai3;
import models.Combine.manifest.NangChiSoBongTai3;

public class CombineService {

    private static final int COST = 500_000_000;
    private static final int TIME_COMBINE = 1500;
    public static final byte MAX_STAR_ITEM = 9;
    public static final byte MAX_LEVEL_ITEM = 8;
    private static final byte OPEN_TAB_COMBINE = 0;
    private static final byte REOPEN_TAB_COMBINE = 1;
    private static final byte combineSUCCESS = 2;
    private static final byte combineFAIL = 3;
    private static final byte combineCHANGE_OPTION = 4;
    private static final byte combineDRAGON_BALL = 5;
    public static final byte OPEN_ITEM = 6;

    public static final int EP_SAO_TRANG_BI            = 500;
    public static final int PHA_LE_HOA_TRANG_BI        = 501;
    public static final int CHUYEN_HOA_BANG_VANG       = 502;
    public static final int CHUYEN_HOA_BANG_NGOC       = 503;
    public static final int NHAP_DA                    = 504;
    public static final int NANG_CAP_SAO_PHA_LE        = 100;
    public static final int DANH_BONG_SAO_PHA_LE       = 101;
    public static final int CUONG_HOA_LO_SAO_PHA_LE    = 102;
    public static final int TAO_DA_HEMATITE            = 103;
    public static final int GIAM_DINH_SACH             = 104;
    public static final int TAY_SACH                   = 105;
    public static final int NANG_CAP_SACH_TUYET_KY      = 106;
    public static final int HOI_PHUC_SACH              = 107;
    public static final int PHAN_RA_SACH               = 108;
    public static final int CHE_TAO_TRANG_BI_THIEN_SU   = 109;
    public static final int NANG_CAP_VAT_PHAM          = 510;
    public static final int NANG_CAP_BONG_TAI          = 511;
    public static final int NANG_CAP_BONG_TAI_3          = 522;
    public static final int LAM_PHEP_NHAP_DA           = 512;
    public static final int NHAP_NGOC_RONG             = 513;
    public static final int NANG_CHI_SO_BONG_TAI       = 517;
    public static final int NANG_CHI_SO_BONG_TAI_3       = 521;
    public static final int NANG_CAP_KICH_HOAT         = 518;
    public static final int NANG_CAP_KICH_HOAT_VIP     = 519;
    public static final int NANG_CAP_CHAN_MENH         = 520;

    private static CombineService instance;

    public final Npc baHatMit;
    public final Npc whis;

    private CombineService() {
        this.baHatMit = NpcManager.getNpc(ConstNpc.BA_HAT_MIT);
        this.whis   = NpcManager.getNpc(ConstNpc.WHIS);
    }

    public static CombineService gI() {
        if (instance == null) {
            instance = new CombineService();
        }
        return instance;
    }

    // -----------------------------------------------------------------------------------
    // HỖ TRỢ CHECK ITEM (giữ nguyên nếu bạn đã có)
    // -----------------------------------------------------------------------------------
    private boolean isDoLuongLong(Item item) {
        if (item != null && item.isNotNullItem()) {
            int id = item.template.id;
            return id == 241 || id == 253 || id == 265 || id == 277 || id == 281;
        }
        return false;
    }

    private boolean isDoZelot(Item item) {
        if (item != null && item.isNotNullItem()) {
            int id = item.template.id;
            return id == 237 || id == 249 || id == 261 || id == 273 || id == 281;
        }
        return false;
    }

    private boolean isDoJean(Item item) {
        if (item != null && item.isNotNullItem()) {
            int id = item.template.id;
            return id == 233 || id == 245 || id == 257 || id == 269 || id == 281;
        }
        return false;
    }

    public boolean isTrangBiGoc(Item item) {
        return item != null && item.isNotNullItem() && (isDoLuongLong(item) || isDoZelot(item) || isDoJean(item));
    }

    private boolean isDoThanXD(Item item) {
        if (item != null && item.isNotNullItem()) {
            int id = item.template.id;
            return id == 559 || id == 560 || id == 566 || id == 567 || id == 561;
        }
        return false;
    }

    private boolean isDoThanTD(Item item) {
        if (item != null && item.isNotNullItem()) {
            int id = item.template.id;
            return id == 555 || id == 556 || id == 562 || id == 563 || id == 561;
        }
        return false;
    }

    private boolean isDoThanNM(Item item) {
        if (item != null && item.isNotNullItem()) {
            int id = item.template.id;
            return id == 557 || id == 558 || id == 564 || id == 565 || id == 561;
        }
        return false;
    }

    public boolean isTrangBiChuyenHoa(Item item) {
        return item != null && item.isNotNullItem() && (isDoThanXD(item) || isDoThanTD(item) || isDoThanNM(item));
    }

    public boolean isCheckTrungTypevsGender(Item item, Item item2) {
        return item != null && item.isNotNullItem() && item2 != null && item2.isNotNullItem()
                && item.template.type == item2.template.type
                && item.template.gender == item2.template.gender;
    }

    // -----------------------------------------------------------------------------------
    // HIỂN THỊ MENU “Combine” (khi user chọn item trong hành trang)
    // -----------------------------------------------------------------------------------
    public void showInfoCombine(Player player, int[] index) {
        if (player.combine == null) {
            return;
        }
        player.combine.clearItemCombine();
        if (index.length > 0) {
            for (int i = 0; i < index.length; i++) {
                player.combine.itemsCombine.add(player.inventory.itemsBag.get(index[i]));
            }
        }

        switch (player.combine.typeCombine) {
            case CHUYEN_HOA_BANG_NGOC ->
                ChuyenHoaBangNgoc.showInfoCombine(player);
            case CHUYEN_HOA_BANG_VANG ->
                ChuyenHoaBangVang.showInfoCombine(player);
            case EP_SAO_TRANG_BI ->
                EpSaoTrangBi.showInfoCombine(player);
            case PHA_LE_HOA_TRANG_BI ->
                PhaLeHoaTrangBi.showInfoCombine(player);
            case NHAP_NGOC_RONG ->
                NhapNgocRong.showInfoCombine(player);
            case NANG_CAP_VAT_PHAM ->
                NangCapVatPham.showInfoCombine(player);
            case NANG_CAP_BONG_TAI ->
                NangCapBongTai.showInfoCombine(player);
            case NANG_CHI_SO_BONG_TAI ->
                NangChiSoBongTai.showInfoCombine(player);
            case NANG_CAP_BONG_TAI_3 ->
                NangCapBongTai3.showInfoCombine(player);
            case NANG_CHI_SO_BONG_TAI_3 ->
                NangChiSoBongTai3.showInfoCombine(player);    
            case NANG_CAP_SAO_PHA_LE ->
                NangCapSaoPhaLe.showInfoCombine(player);
            case DANH_BONG_SAO_PHA_LE ->
                DanhBongSaoPhaLe.showInfoCombine(player);
            case CUONG_HOA_LO_SAO_PHA_LE ->
                CuongHoaLoSaoPhaLe.showInfoCombine(player);
            case TAO_DA_HEMATITE ->
                TaoDaHematite.showInfoCombine(player);
            case GIAM_DINH_SACH ->
                GiamDinhSach.showInfoCombine(player);
            case TAY_SACH ->
                TaySach.showInfoCombine(player);
            case NANG_CAP_SACH_TUYET_KY ->
                NangCapSachTuyetKy.showInfoCombine(player);
            case HOI_PHUC_SACH ->
                HoiPhucSach.showInfoCombine(player);
            case PHAN_RA_SACH ->
                PhanRaSach.showInfoCombine(player);
            case CHE_TAO_TRANG_BI_THIEN_SU ->
                CheTaoTrangBiThienSu.showInfoCombine(player);
            case NANG_CAP_KICH_HOAT ->
                NangCapKichHoat.showInfoCombine(player);
            case NANG_CAP_KICH_HOAT_VIP ->
                NangCapKichHoatVip.showInfoCombine(player);
            case NANG_CAP_CHAN_MENH ->
                NangCapChanMenh.showInfoCombine(player);
            case LAM_PHEP_NHAP_DA ->
                Lamphepnhapda.showInfoCombine(player);    
        }
    }

    // -----------------------------------------------------------------------------------
    // KHỞI TẠO COMBINE (khi user bấm “Làm phép” ở menu)
    // -----------------------------------------------------------------------------------
    public void startCombine(Player player, int... n) {
        int num = 0;
        if (n.length > 0) {
            num = n[0];
        }
        switch (player.combine.typeCombine) {
            case CHUYEN_HOA_BANG_NGOC ->
                ChuyenHoaBangNgoc.ChuyenHoaBangngoc(player);
            case CHUYEN_HOA_BANG_VANG ->
                ChuyenHoaBangVang.ChuyenHoaBangvang(player);
            case EP_SAO_TRANG_BI ->
                EpSaoTrangBi.epSaoTrangBi(player);
            case PHA_LE_HOA_TRANG_BI ->
                PhaLeHoaTrangBi.phaLeHoa(player, num);
            case NHAP_NGOC_RONG ->
                // num == 1 nếu người chơi chọn “dùng đá bảo vệ”, num == 0 nếu không
                NhapNgocRong.nhapNgocRong(player, num == 1);
            case NANG_CAP_VAT_PHAM ->
                NangCapVatPham.nangCapVatPham(player, num == 1);
            case NANG_CAP_BONG_TAI ->
                NangCapBongTai.nangCapBongTai(player);
            case NANG_CHI_SO_BONG_TAI ->
                NangChiSoBongTai.nangChiSoBongTai(player);
            case NANG_CAP_BONG_TAI_3 ->
                NangCapBongTai3.nangCapBongTai3(player);
            case NANG_CHI_SO_BONG_TAI_3 ->
                NangChiSoBongTai3.nangChiSoBongTai3(player);    
            case NANG_CAP_SAO_PHA_LE ->
                NangCapSaoPhaLe.nangCapSaoPhaLe(player);
            case DANH_BONG_SAO_PHA_LE ->
                DanhBongSaoPhaLe.danhBongSaoPhaLe(player);
            case CUONG_HOA_LO_SAO_PHA_LE ->
                CuongHoaLoSaoPhaLe.cuongHoaLoSaoPhaLe(player);
            case TAO_DA_HEMATITE ->
                TaoDaHematite.taoDaHematite(player);
            case GIAM_DINH_SACH ->
                GiamDinhSach.giamDinhSach(player);
            case TAY_SACH ->
                TaySach.taySach(player);
            case NANG_CAP_SACH_TUYET_KY ->
                NangCapSachTuyetKy.nangCapSachTuyetKy(player);
            case HOI_PHUC_SACH ->
                HoiPhucSach.hoiPhucSach(player);
            case PHAN_RA_SACH ->
                PhanRaSach.phanRaSach(player);
            case CHE_TAO_TRANG_BI_THIEN_SU ->
                CheTaoTrangBiThienSu.cheTaoTrangBiThienSu(player);
            case NANG_CAP_KICH_HOAT ->
                NangCapKichHoat.startCombine(player);
            case NANG_CAP_KICH_HOAT_VIP ->
                NangCapKichHoatVip.startCombine(player);
            case NANG_CAP_CHAN_MENH ->
                NangCapChanMenh.startCombine(player);
            case LAM_PHEP_NHAP_DA ->
                Lamphepnhapda.lamPhepNhapDa(player);    
        }

        player.iDMark.setIndexMenu(ConstNpc.IGNORE_MENU);
        player.combine.clearParamCombine();
        player.combine.lastTimeCombine = System.currentTimeMillis();
    }

    // -----------------------------------------------------------------------------------
    // MỞ TAB COMBINE (khi user click vào NPC và chọn tab)
    // -----------------------------------------------------------------------------------
    public void openTabCombine(Player player, int type) {
        player.combine.setTypeCombine(type);
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_TAB_COMBINE);
            msg.writer().writeUTF(getTextInfoTabCombine(type));
            msg.writer().writeUTF(getTextTopTabCombine(type));
            if (player.iDMark.getNpcChose() != null) {
                msg.writer().writeShort(player.iDMark.getNpcChose().tempId);
            }
            player.sendMessage(msg);
        } catch (IOException e) {
//             e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    // -----------------------------------------------------------------------------------
    // HIỆU ỨNG OPEN ITEM / COMBINE
    // -----------------------------------------------------------------------------------
    public void sendEffectOpenItem(Player player, short icon1, short icon2) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_ITEM);
            msg.writer().writeShort(icon1);
            msg.writer().writeShort(icon2);
            player.sendMessage(msg);
        } catch (IOException e) {
//             e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void sendEffectCombineItem(Player player, byte type, short icon1, short icon2) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(type);
            switch (type) {
                case 0:
                    msg.writer().writeUTF("");
                    msg.writer().writeUTF("");
                    break;
                case 1:
                    msg.writer().writeByte(0);
                    msg.writer().writeByte(-1);
                    break;
                case 2: // success
                case 3: // fail
                    break;
                case 4: // change option
                    msg.writer().writeShort(icon1);
                    break;
                case 5: // combine dragon ball
                    msg.writer().writeShort(icon1);
                    break;
                case 6: // open item 2 icons
                    msg.writer().writeShort(icon1);
                    msg.writer().writeShort(icon2);
                    break;
                case 7: // success VIP
                    msg.writer().writeShort(icon1);
                    break;
                case 8: // fail VIP
                    break;
            }
            msg.writer().writeShort(-1); // id npc
            player.sendMessage(msg);
        } catch (IOException e) {
//             e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void sendEffectSuccessCombine(Player player) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(combineSUCCESS);
            player.sendMessage(msg);
        } catch (IOException e) {
//             e.printStackTrace();
        } finally {
            if (msg != null) msg.cleanup();
        }
    }

    public void sendEffectFailCombine(Player player) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(combineFAIL);
            player.sendMessage(msg);
        } catch (IOException e) {
//             e.printStackTrace();
        } finally {
            if (msg != null) msg.cleanup();
        }
    }

    // -----------------------------------------------------------------------------------
    // REOPEN TAB COMBINE SAU MỖI LẦN KHÔNG ĐÓNG DIALOG
    // -----------------------------------------------------------------------------------
    public void reOpenItemCombine(Player player) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(REOPEN_TAB_COMBINE);
            msg.writer().writeByte(player.combine.itemsCombine.size());
            for (Item it : player.combine.itemsCombine) {
                for (int j = 0; j < player.inventory.itemsBag.size(); j++) {
                    if (it == player.inventory.itemsBag.get(j)) {
                        msg.writer().writeByte(j);
                    }
                }
            }
            player.sendMessage(msg);
        } catch (IOException e) {
//             e.printStackTrace();
        } finally {
            if (msg != null) msg.cleanup();
        }
    }

    // -----------------------------------------------------------------------------------
    // HIỆU ỨNG GHÉP NGỌC RỒNG
    // -----------------------------------------------------------------------------------
    public void sendEffectCombineDB(Player player, short icon) {
        Message msg = null;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(combineDRAGON_BALL);
            msg.writer().writeShort(icon);
            player.sendMessage(msg);
        } catch (IOException e) {
//             e.printStackTrace();
        } finally {
            if (msg != null) msg.cleanup();
        }
    }

    // -----------------------------------------------------------------------------------
    // GỬI THÊM ITEM COMBINE VÀO DIALOG
    // -----------------------------------------------------------------------------------
    public void sendAddItemCombine(Player player, int npcId, Item... items) {
        Message msg;
        try {
            // Dòng 0: header
            msg = new Message(-81);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("By ZINZIN");
            msg.writer().writeUTF("ZINZIN");
            msg.writer().writeShort(npcId);
            player.sendMessage(msg);
            msg.cleanup();
            // Dòng 1: danh sách index item
            msg = new Message(-81);
            msg.writer().writeByte(1);
            msg.writer().writeByte(items.length);
            for (Item item : items) {
                msg.writer().writeByte(InventoryService.gI().getIndexItemBag(player, item));
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
//             e.printStackTrace();
        }
    }

    public void sendEffSuccessVip(Player player, int iconID) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(7);
            msg.writer().writeShort(iconID);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
//             e.printStackTrace();
        }
    }

    public void sendEffFailVip(Player player) {
        try {
            Message msg = new Message(-81);
            msg.writer().writeByte(8);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
//             e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------------------
    // VĂN BẢN HIỂN THỊ TRONG TAB COMBINE
    // -----------------------------------------------------------------------------------
    private String getTextTopTabCombine(int type) {
        return switch (type) {
            case CHUYEN_HOA_BANG_NGOC, CHUYEN_HOA_BANG_VANG ->
                "Ta sẽ phù phép\ncho trang bị của ngươi\nchuyển hóa thành trang bị khác";
            case EP_SAO_TRANG_BI ->
                "Ta sẽ phù phép\ncho trang bị của ngươi\ntrở nên mạnh mẽ";
            case PHA_LE_HOA_TRANG_BI ->
                "Ta sẽ phù phép\ncho trang bị của ngươi\ntrở thành trang bị pha lê";
            case NHAP_NGOC_RONG ->
                "Ta sẽ phù phép\ncho 7 viên Ngọc Rồng\nthành 1 viên Ngọc Rồng cấp cao";
            case NHAP_DA ->
                "Ta sẽ phù phép\ncho 10 mảnh đá vụn\ntrở thành 1 đá nâng cấp";
            case NANG_CAP_VAT_PHAM ->
                "Ta sẽ phù phép\ncho trang bị của ngươi\ntrở nên mạnh mẽ";
            case NANG_CAP_BONG_TAI ->
                "Ta sẽ phù phép\ncho bông tai Porata của ngươi\nthành cấp 2";
            case NANG_CHI_SO_BONG_TAI ->
                "Ta sẽ phù phép\ncho bông tai Porata cấp 2 của ngươi\ncó 1 chỉ số ngẫu nhiên";
            case NANG_CAP_BONG_TAI_3 ->
                "Ta sẽ phù phép\ncho bông tai Porata cấp 2 của ngươi\nthành cấp 3";
            case NANG_CHI_SO_BONG_TAI_3 ->
                "Ta sẽ phù phép\ncho bông tai Porata cấp 3 của ngươi\ncó 1 chỉ số ngẫu nhiên";    
            case NANG_CAP_SAO_PHA_LE ->
                "Ta sẽ phù phép\nnâng cấp Sao Pha Lê\nthành cấp 2";
            case DANH_BONG_SAO_PHA_LE ->
                "Đánh bóng\nSao pha lê cấp 2";
            case CUONG_HOA_LO_SAO_PHA_LE ->
                "Cường hóa\nÔ Sao Pha Lê";
            case TAO_DA_HEMATITE ->
                "Ta sẽ phù phép\ntạo đá hematite";
            case GIAM_DINH_SACH ->
                "Ta sẽ phù phép\ngiám định sách đó cho ngươi";
            case TAY_SACH ->
                "Ta sẽ phù phép\ntẩy sách đó cho ngươi";
            case NANG_CAP_SACH_TUYET_KY ->
                "Ta sẽ phù phép\nnâng cấp Sách Tuyệt Kỹ cho ngươi";
            case HOI_PHUC_SACH ->
                "Ta sẽ phù phép\nphục hồi sách cho ngươi";
            case PHAN_RA_SACH ->
                "Ta sẽ phù phép\nphân rã sách đó cho ngươi";
            case CHE_TAO_TRANG_BI_THIEN_SU ->
                "Chế tạo\ntrang bị thiên sứ";
            case LAM_PHEP_NHAP_DA ->
                "Ta sẽ phù phép\ncho 10 mảnh đá vụn\ntrở thành 1 đá nâng cấp";
            case NANG_CAP_KICH_HOAT ->
                "Ta sẽ phù phép\nchế tạo trang bị Huỷ Diệt\nthành trang bị Kích Hoạt";
            case NANG_CAP_KICH_HOAT_VIP ->
                "Thiên sứ nhờ ta nâng cấp\ntrang bị của người thành SKH VIP!";
            case NANG_CAP_CHAN_MENH ->
                "Ta sẽ nâng cấp\ncho Chân Mệnh của ngươi\ntrở nên mạnh mẽ";
            default ->
                "";
        };
    }

    private String getTextInfoTabCombine(int type) {
        return switch (type) {
            case EP_SAO_TRANG_BI ->
                "Vào hành trang\nChọn trang bị\n(Áo, quần, găng, giày hoặc rada)\ncó ô đặt sao pha lê\nChọn loại sao pha lê\nSau đó chọn 'Nâng cấp'";
            case PHA_LE_HOA_TRANG_BI ->
                "Vào hành trang\nChọn trang bị\n(Áo, quần, găng, giày hoặc rada)\nChọn 'Nâng cấp'";
            case NHAP_NGOC_RONG ->
                "Vào hành trang\nChọn 7 viên Ngọc Rồng\n(hoặc nhiều hơn để nhập nhanh)\nSau đó chọn 'Làm phép' hoặc 'Nhập nhanh'";
            case NHAP_DA ->
                "Vào hành trang\nChọn 10 mảnh đá vụn\nChọn 1 bình nước phép\n(mua tại Uron ở trạm tàu vũ trụ)\nSau đó chọn 'Làm phép'";
            case NANG_CAP_VAT_PHAM ->
                "Vào hành trang\nChọn trang bị\n(Áo, quần, găng, giày hoặc rada)\nChọn loại đá để nâng cấp\nSau đó chọn 'Nâng cấp'";
            case NANG_CAP_BONG_TAI ->
                "Vào hành trang\nChọn bông tai Porata\nChọn mảnh bông tai để nâng cấp, số lượng 9999 cái\nSau đó chọn 'Nâng cấp'";
            case NANG_CHI_SO_BONG_TAI ->
                "Vào hành trang\nChọn bông tai Porata\nChọn mảnh hồn porata số lượng 99\ncái và đá xanh lam để nâng cấp.\nSau đó chọn 'Nâng cấp chỉ số'";
            case NANG_CAP_BONG_TAI_3 ->
                "Vào hành trang\nChọn bông tai Porata 2\nChọn mảnh bông tai để nâng cấp, số lượng 9999 cái\nSau đó chọn 'Nâng cấp'";
            case NANG_CHI_SO_BONG_TAI_3 ->
                "Vào hành trang\nChọn bông tai Porata 3\nChọn mảnh hồn porata số lượng 99\ncái và đá xanh lam để nâng cấp.\nSau đó chọn 'Nâng cấp chỉ số'";    
            case NANG_CAP_SAO_PHA_LE ->
                "Vào hành trang\nChọn đá Hematite\nChọn loại sao pha lê (cấp 1)\nSau đó chọn 'Nâng cấp'";
            case DANH_BONG_SAO_PHA_LE ->
                "Vào hành trang\nChọn loại sao pha lê cấp 2 có từ 2 viên trở lên\nChọn 1 đá mài\nSau đó chọn 'Đánh bóng'";
            case CUONG_HOA_LO_SAO_PHA_LE ->
                "Vào hành trang\nChọn trang bị có Ô sao thứ 8 trở lên chưa cường hóa\nChọn đá Hematite\nChọn dùi đục\nSau đó chọn 'Cường hóa'";
            case TAO_DA_HEMATITE ->
                "Vào hành trang\nChọn 5 sao pha lê cấp 2 cùng màu\nChọn 'Tạo đá Hematite'";
            case GIAM_DINH_SACH ->
                "Vào hành trang chọn\n1 sách cần giám định";
            case TAY_SACH ->
                "Vào hành trang chọn\n1 sách cần tẩy";
            case NANG_CAP_SACH_TUYET_KY ->
                "Vào hành trang chọn\nSách Tuyệt Kỹ 1 cần nâng cấp và 10 Kìm bấm giấy";
            case HOI_PHUC_SACH ->
                "Vào hành trang chọn\nCác Sách Tuyệt Kỹ cần phục hồi";
            case PHAN_RA_SACH ->
                "Vào hành trang chọn\n1 sách cần phân rã";
            case CHE_TAO_TRANG_BI_THIEN_SU ->
                "Cần 1 công thức\nMảnh trang bị tương ứng\n1 đá nâng cấp (tùy chọn)\n1 đá may mắn (tùy chọn)";
            case LAM_PHEP_NHAP_DA ->
                "Vào hành trang\nChọn 10 mảnh đá vụn\nChọn 1 bình nước phép\n(mua tại Uron ở trạm tàu vũ trụ)\nSau đó chọn 'Làm phép'";
            case NANG_CAP_KICH_HOAT ->
                "Vào hành trang\nChọn 1 trang bị Huỷ Diệt\nChọn 1 viên đá Kích Hoạt\nSau đó chọn 'Nâng cấp'";
            case NANG_CAP_KICH_HOAT_VIP ->
                "Vào hành trang\nChọn 1 trang bị Hủy Diệt\nChọn tiếp ngẫu nhiên 2 món Thần Linh\nĐồ SKH VIP sẽ cùng loại với đồ Hủy Diệt!\nChỉ cần chọn 'Nâng cấp'";
            case CHUYEN_HOA_BANG_NGOC, CHUYEN_HOA_BANG_VANG ->
                "Vào hành trang\nChọn trang bị gốc ô 1\n(Áo, quần, găng, giày hoặc radar)\ntừ cấp [+4] trở lên\nChọn tiếp trang bị cần chuyển hóa ô 2\nvà chưa nâng cấp\nSau đó chọn 'Nâng cấp'";
            case NANG_CAP_CHAN_MENH ->
                "Vào hành trang\nChọn 1 Chân Mệnh\nvà x99 Đá Thiên Tử\nSau đó chọn 'Nâng cấp'";
            default ->
                "";
        };
    }
}
