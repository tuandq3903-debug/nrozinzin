package models.npc.manifest;

/**
 * @author ZINZIN
 */
import consts.ConstDailyGift;
import consts.ConstNpc;
import models.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.Combine.CombineService;
import models.Combine.manifest.CheTaoCuonSachCu;
import models.Combine.manifest.DoiSachTuyetKy;
import models.Combine.manifest.NangCapVatPham;
import models.Combine.manifest.NhapNgocRong;            // <<<=== CHỈNH: Import thêm NhapNgocRong
import models.DeathOrAliveArena.DeathOrAliveArena;
import models.DeathOrAliveArena.DeathOrAliveArenaManager;
import models.DeathOrAliveArena.DeathOrAliveArenaService;
import models.npc.Npc;
import models.player.Player;
import models.player.dailyGift.DailyGiftService;
import services.InventoryService;
import services.ItemService;
import services.Service;
import services.func.ChangeMapService;
import models.shop.ShopService;
import utils.Util;

public class BaHatMit extends Npc {

    public BaHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", 
                            "Chức năng\nPha lê", 
                            "Nâng Cấp\nSKH VIP", 
                            "Chuyển hoá\ntrang bị", 
                            "Võ đài\nSinh Tử",
                            "Xuống\nĐịa Ngục",
                            "Nâng Cấp\nChân Mệnh");
                case 112 -> {
                    if (Util.isAfterMidnight(player.lastTimePKVoDaiSinhTu)) {
                        player.haveRewardVDST = false;
                        player.thoiVangVoDaiSinhTu = 0;
                    }
                    if (player.haveRewardVDST) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đây là phần thưởng cho con.", "1 ngọc bí\nbất kì", "1 bí ngô");
                        return;
                    }
                    if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().equals(player)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi muốn hủy đăng ký thi đấu võ đài?",
                                "Top 100", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
                            return;
                        }
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi muốn đăng ký thi đấu võ đài?\nnhiều phần thưởng giá trị đang đợi ngươi đó",
                                "Top 100", "Bình chọn", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
                        return;
                    }
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi muốn đăng ký thi đấu võ đài?\nnhiều phần thưởng giá trị đang đợi ngươi đó",
                            "Top 100", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
                }
                case 174 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", "Quay về", "Từ chối");
                case 181 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", "Quay về", "Từ chối");
                default -> {
                // 1. Các mục cố định
                List<String> menu = new ArrayList<>();
                menu.add("Sách\nTuyệt Kỹ");
                menu.add("Cửa hàng\nBùa");
                menu.add("Nâng cấp\nVật phẩm");

                // 2. Xử lý bông tai
                boolean hasCap2 = InventoryService.gI().findItemBongTaiCap2(player);
                boolean hasCap3 = InventoryService.gI().findItemBongTaiCap3(player);
                if (hasCap3) {
                    // Đã lên cap 3 ⇒ chỉ hiện mở chỉ số cap 3
                    menu.add("Mở chỉ số\nBông tai\nPorata cấp\n3");
                } else {
                    if (hasCap2) {
                        menu.add("Mở chỉ số\nBông tai\nPorata cấp\n2");
                    } else {
                        menu.add("Nâng cấp\nBông tai\nPorata\n2");
                    }
                    // luôn thêm phần cap 3 (nâng cấp vì hasCap3 == false)
                    menu.add("Nâng cấp\nBông tai\nPorata\n3");
                }

                // 3. Các mục cuối
                menu.add("Làm phép\nNhập đá");
                menu.add("Nhập\nNgọc Rồng");

                // 4. Chèn thêm DailyGift nếu cần (giữ nguyên logic cũ)
                String[] menus;
                if (DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI)) {
                    menus = new String[menu.size() + 1];
                    menus[0] = "Thưởng\nBùa 1h\nngẫu nhiên";
                    for (int i = 0; i < menu.size(); i++) {
                        menus[i + 1] = menu.get(i);
                    }
                } else {
                    menus = menu.toArray(new String[0]);
                }
                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?", menus);
            }
                        }
                    }
                }


    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5 -> {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU -> {
                            switch (select) {
                                case 0 ->
                                    createOtherMenu(player, ConstNpc.MENU_PHA_LE, "Ta có thể giúp gì cho ngươi ?",
                                            "Ép sao\ntrang bị",
                                            "Pha lê\nhoá\ntrang bị",
                                            "Nâng cấp\nSao pha lê",
                                            "Đánh bóng\nSao pha lê",
                                            "Cường hoá\nLỗ sao\npha lê",
                                            "Tạo đá Hematite");
                                case 1 ->
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_KICH_HOAT_VIP);
                                case 2 ->
                                    createOtherMenu(player, ConstNpc.MENU_CHUYEN_HOA_TRANG_BI,
                                            "Ta có thể giúp gì cho ngươi ?",
                                            "Chuyển hoá\nbằng vàng",
                                            "Chuyển hoá\nbằng ngọc");
                                case 3 ->
                                    ChangeMapService.gI().changeMapNonSpaceship(player,
                                            112,
                                            200 + Util.nextInt(-100, 100),
                                            408);
                                case 4 ->
                                    ChangeMapService.gI().changeMapNonSpaceship(player,
                                            174,
                                            110 + Util.nextInt(0, 100),
                                            408);
                                case 5 ->
                                     CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_CHAN_MENH);
                            }
                        }
                        case ConstNpc.MENU_PHA_LE -> {
                            switch (select) {
                                case 0:
                                    CombineService.gI().openTabCombine(player, CombineService.EP_SAO_TRANG_BI);
                                    break;
                                case 1:
                                    createOtherMenu(player, ConstNpc.MENU_PHA_LE_HOA_TRANG_BI,
                                            "Ngươi muốn pha lê hoá trang bị bằng cách nào?",
                                            "Bằng ngọc",
                                            "Từ chối");
                                    break;
                                case 2:
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_SAO_PHA_LE);
                                    break;
                                case 3:
                                    CombineService.gI().openTabCombine(player, CombineService.DANH_BONG_SAO_PHA_LE);
                                    break;
                                case 4:
                                    CombineService.gI().openTabCombine(player, CombineService.CUONG_HOA_LO_SAO_PHA_LE);
                                    break;
                                case 5:
                                    CombineService.gI().openTabCombine(player, CombineService.TAO_DA_HEMATITE);
                                    break;
                            }
                        }
                        case ConstNpc.MENU_CHUYEN_HOA_TRANG_BI -> {
                            switch (select) {
                                case 0 -> CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_BANG_VANG);
                                case 1 -> CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_BANG_NGOC);
                            }
                        }
                        case ConstNpc.MENU_PHA_LE_HOA_TRANG_BI -> {
                            if (select == 0) {
                                CombineService.gI().openTabCombine(player, CombineService.PHA_LE_HOA_TRANG_BI);
                            }
                        }
                        case ConstNpc.MENU_START_COMBINE -> {
                            // Khi ở dialog “Làm phép” (menuId = 900):
                            // Có 3 nút: select=0 → Làm phép bình thường; select=1 → Nâng cấp dùng đá; select=2 → Từ chối
                            switch (player.combine.typeCombine) {
                                case CombineService.PHA_LE_HOA_TRANG_BI -> {
                                    switch (select) {
                                        case 0 ->
                                            CombineService.gI().startCombine(player, 100);
                                        case 1 ->
                                            CombineService.gI().startCombine(player, 10);
                                        case 2 ->
                                            CombineService.gI().startCombine(player);
                                    }
                                }
                                case CombineService.NANG_CAP_CHAN_MENH,
                                     CombineService.NANG_CAP_KICH_HOAT_VIP,
                                     CombineService.CHUYEN_HOA_BANG_VANG,
                                     CombineService.CHUYEN_HOA_BANG_NGOC,
                                     CombineService.NANG_CAP_KICH_HOAT,
                                     CombineService.NANG_CAP_SAO_PHA_LE,
                                     CombineService.DANH_BONG_SAO_PHA_LE,
                                     CombineService.CUONG_HOA_LO_SAO_PHA_LE,
                                     CombineService.TAO_DA_HEMATITE,
                                     CombineService.EP_SAO_TRANG_BI -> {
                                    if (select == 0) {
                                        CombineService.gI().startCombine(player);
                                    }
                                }
                                // <<<=== CHỈNH Ở ĐÂY: XỬ LÝ NHẬP NGỌC RỒNG 1 LẦN (MENU_START_COMBINE, type = NHAP_NGOC_RONG) ===>>>
                                case CombineService.NHAP_NGOC_RONG -> {
                                    // Ở showInfoCombine nếu maxCombine <= 1, sẽ dùng MENU_START_COMBINE
                                    // Có 3 nút trong dialog:
                                    //   select = 0 → “Làm phép” (không dùng đá)
                                    //   select = 1 → “Nâng cấp dùng đá bảo vệ”
                                    //   select = 2 → “Từ chối”
                                    if (select == 0) {
                                        // Làm phép bình thường (useDBV = false)
                                        CombineService.gI().startCombine(player, (byte) 0);
                                    } else if (select == 1) {
                                        // Dùng đá bảo vệ (useDBV = true)
                                        CombineService.gI().startCombine(player, (byte) 1);
                                    }
                                    // select == 2 thì thoát không làm gì
                                }
                                // <<<=== CHỈNH Ở ĐÂY: KẾT THÚC XỬ LÝ NHẬP NGỌC RỒNG 1 LẦN ===>>>
                            }
                        }
                    }
                }
                case 112 -> {
                    if (player.iDMark.isBaseMenu()) {
                        if (player.haveRewardVDST) {
                            switch (select) {
                                case 0 -> {
                                    if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                        Item item = ItemService.gI().createNewItem((short) (Util.nextInt(705, 708)));
                                        item.itemOptions.add(new Item.ItemOption(93, 30));
                                        InventoryService.gI().addItemBag(player, item);
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                                        player.haveRewardVDST = false;
                                    } else {
                                        Service.gI().sendThongBao(player, "Hành trang không còn chỗ trống, không thể nhặt thêm");
                                    }
                                }
                                case 1 -> {
                                    if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                        Item item = ItemService.gI().createNewItem((short) 585);
                                        item.itemOptions.add(new Item.ItemOption(93, 30));
                                        InventoryService.gI().addItemBag(player, item);
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                                        player.haveRewardVDST = false;
                                    } else {
                                        Service.gI().sendThongBao(player, "Hành trang không còn chỗ trống, không thể nhặt thêm");
                                    }
                                }
                            }
                            return;
                        }
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                            if (DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().equals(player)) {
                                switch (select) {
                                    case 0 -> {
                                    }
                                    case 1 ->
                                        this.npcChat("Không thể thực hiện");
                                    case 2 -> {
                                    }
                                    case 3 ->
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                }
                                return;
                            }
                            switch (select) {
                                case 0 -> {
                                }
                                case 1 ->
                                    this.createOtherMenu(player, ConstNpc.DAT_CUOC_HAT_MIT,
                                            "Phí bình chọn là 1 triệu vàng\nkhi trận đấu kết thúc\n90% tổng tiền bình chọn sẽ chia đều cho phe bình chọn chính xác",
                                            "Bình chọn cho " + DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().name +
                                                    " (" + DeathOrAliveArenaManager.gI().getVDST(player.zone).getCuocPlayer() + ")",
                                            "Bình chọn cho hạt mít (" +
                                                    DeathOrAliveArenaManager.gI().getVDST(player.zone).getCuocBaHatMit() +
                                                    ")");
                                case 2 ->
                                    DeathOrAliveArenaService.gI().startChallenge(player);
                                case 3 -> {
                                }
                                case 4 ->
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                            }
                            return;
                        }
                        switch (select) {
                            case 0 -> {
                            }
                            case 1 ->
                                DeathOrAliveArenaService.gI().startChallenge(player);
                            case 2 -> {
                            }
                            case 3 ->
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.DAT_CUOC_HAT_MIT) {
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                            switch (select) {
                                case 0 -> {
                                    if (player.inventory.gold >= 1_000_000) {
                                        DeathOrAliveArena vdst = DeathOrAliveArenaManager.gI().getVDST(player.zone);
                                        vdst.setCuocPlayer(vdst.getCuocPlayer() + 1);
                                        vdst.addBinhChon(player);
                                        player.binhChonPlayer++;
                                        player.zoneBinhChon = player.zone;
                                        player.inventory.gold -= 1_000_000;
                                        Service.gI().sendMoney(player);
                                    } else {
                                        Service.gI().sendThongBao(player,
                                                "Bạn không đủ vàng, còn thiếu " +
                                                        Util.numberToMoney(1_000_000 - player.inventory.gold) +
                                                        " vàng nữa");
                                    }
                                }
                                case 1 -> {
                                    if (player.inventory.gold >= 1_000_000) {
                                        DeathOrAliveArena vdst = DeathOrAliveArenaManager.gI().getVDST(player.zone);
                                        vdst.setCuocBaHatMit(vdst.getCuocBaHatMit() + 1);
                                        vdst.addBinhChon(player);
                                        player.binhChonHatMit++;
                                        player.zoneBinhChon = player.zone;
                                        player.inventory.gold -= 1_000_000;
                                        Service.gI().sendMoney(player);
                                    } else {
                                        Service.gI().sendThongBao(player,
                                                "Bạn không đủ vàng, còn thiếu " +
                                                        Util.numberToMoney(1_000_000 - player.inventory.gold) +
                                                        " vàng nữa");
                                    }
                                }
                            }
                        }
                    }
                }
                case 174, 181 -> {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                        }
                    }
                }
                case 42, 43, 44, 84 -> {
                    if (player.iDMark.isBaseMenu()) {
                        if (!DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI)) {
                            select++;
                        }
                        if (!InventoryService.gI().findItem(player, 454) && !InventoryService.gI().findItem(player, 921)) {
                            if (select >= 4) {
                                select++;
                            }
                        }
                        switch (select) {
                            case 0:
                                if (DailyGiftService.checkDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI)) {
                                    int idItem = Util.nextInt(213, 219);
                                    player.charms.addTimeCharms(idItem, 60);
                                    Item bua = ItemService.gI().createNewItem((short) idItem);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận thưởng " + bua.template.name);
                                    DailyGiftService.updateDailyGift(player, ConstDailyGift.NHAN_BUA_MIEN_PHI);
                                } else {
                                    Service.gI().sendThongBao(player, "Hôm nay bạn đã nhận bùa miễn phí rồi!!!");
                                }
                                break;
                            case 1:
                                createOtherMenu(player, ConstNpc.MENU_SACH_TUYET_KY,
                                        "Ta có thể giúp gì cho ngươi ?",
                                        "Đóng thành\nSách cũ",
                                        "Đổi Sách\nTuyệt kỹ",
                                        "Giám định\nSách",
                                        "Tẩy\nSách",
                                        "Nâng cấp\nSách\nTuyệt kỹ",
                                        "Hồi phục\nSách",
                                        "Phân rã\nSách");
                                break;
                            case 2:
                                createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                        "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                        "Bùa\n1 giờ",
                                        "Bùa\n8 giờ",
                                        "Bùa\n1 tháng",
                                        "Đóng");
                                break;
                            case 3:
                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_VAT_PHAM);
                                break;
                            case 4:
                                if (InventoryService.gI().findItemBongTaiCap2(player)) {
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CHI_SO_BONG_TAI);
                                } else {
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_BONG_TAI);
                                }
                                break;
                            case 5:
                                if (InventoryService.gI().findItemBongTaiCap3(player)) {
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CHI_SO_BONG_TAI_3);
                                } else {
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_BONG_TAI_3);
                                }
                                break;    
                            case 6:
                                CombineService.gI().openTabCombine(player, CombineService.LAM_PHEP_NHAP_DA);
                                break;
                            case 7:
                                // “Nhập Ngọc Rồng” → mở tab combine type NHAP_NGOC_RONG
                                CombineService.gI().openTabCombine(player, CombineService.NHAP_NGOC_RONG);
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_SACH_TUYET_KY) {
                        switch (select) {
                            case 0:
                                CheTaoCuonSachCu.showCombine(player);
                                break;
                            case 1:
                                DoiSachTuyetKy.showCombine(player);
                                break;
                            case 2:
                                CombineService.gI().openTabCombine(player, CombineService.GIAM_DINH_SACH);
                                break;
                            case 3:
                                CombineService.gI().openTabCombine(player, CombineService.TAY_SACH);
                                break;
                            case 4:
                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_SACH_TUYET_KY);
                                break;
                            case 5:
                                CombineService.gI().openTabCombine(player, CombineService.HOI_PHUC_SACH);
                                break;
                            case 6:
                                CombineService.gI().openTabCombine(player, CombineService.PHAN_RA_SACH);
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.DONG_THANH_SACH_CU) {
                        CheTaoCuonSachCu.cheTaoCuonSachCu(player);
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.DOI_SACH_TUYET_KY) {
                        DoiSachTuyetKy.doiSachTuyetKy(player);
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                        switch (select) {
                            case 0 ->
                                ShopService.gI().opendShop(player, "BUA_1H", true);
                            case 1 ->
                                ShopService.gI().opendShop(player, "BUA_8H", true);
                            case 2 ->
                                ShopService.gI().opendShop(player, "BUA_1M", true);
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                        // Trường hợp này chỉ bắt các loại combine “chỉ 1 lần” với menuId = 900
                        switch (player.combine.typeCombine) {
                            case CombineService.NANG_CAP_BONG_TAI,
                                 CombineService.NANG_CHI_SO_BONG_TAI,
                                 CombineService.NANG_CAP_BONG_TAI_3,
                                 CombineService.NANG_CHI_SO_BONG_TAI_3,
                                 CombineService.LAM_PHEP_NHAP_DA,
                                 CombineService.NHAP_NGOC_RONG,
                                 CombineService.GIAM_DINH_SACH,
                                 CombineService.TAY_SACH,
                                 CombineService.NANG_CAP_SACH_TUYET_KY,
                                 CombineService.HOI_PHUC_SACH,
                                 CombineService.PHAN_RA_SACH -> {
                                if (select == 0) {
                                    CombineService.gI().startCombine(player);
                                }
                            }
                            case CombineService.NANG_CAP_VAT_PHAM -> {
                                if (select == 0) {
                                    CombineService.gI().startCombine(player);
                                } else if (select == 1) {
                                    NangCapVatPham.nangCapVatPham(player, true);
                                }
                            }
                        }
                    }
                    // <<<=== CHỈNH Ở ĐÂY: Thêm case cho MENU_QUICK_COMBINE (901) để xử lý “Nhập nhanh” ===>>>
                    else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_QUICK_COMBINE) {
                        // Chỉ có 2 nút trong dialog “Nhập nhanh”:
                        // select = 0 → “Nhập nhanh”, select = 1 → “Từ chối”
                        if (select == 0) {
                            NhapNgocRong.nhapNgocRong(player, false);
                        }
                        // Nếu select == 1 (Từ chối), không làm gì
                    }
                    // <<<=== KẾT THÚC CHỈNH CHO MENU_QUICK_COMBINE ===>>>
                }
                default -> {
                    // Các mapId khác không liên quan
                }
            }
        }
    }
}
