package models.Combine.manifest;

import consts.ConstNpc;
import models.Combine.CombineService;
import models.Combine.manifest.NhapNgocRong;
import models.player.Player;
import network.Message;

import java.io.IOException;

/**
 * CombineHandler: xử lý packet do client gửi khi người chơi bấm các nút trong dialog Combine.
 *
 * Cấu trúc packet client gửi lên:
 *   1) 1 byte (unsigned): menuId (ví dụ 900 hoặc 901)
 *   2) 1 byte (unsigned): buttonIndex (index của nút người chơi chọn: 0,1,2…)
 *
 * - Với menuId = MENU_START_COMBINE (900), dialog có 3 nút:
 *     buttonIndex = 0 → “Làm phép” (không dùng đá bảo vệ)
 *     buttonIndex = 1 → “Nâng cấp dùng đá bảo vệ”
 *     buttonIndex = 2 → “Từ chối”
 *
 * - Với menuId = MENU_QUICK_COMBINE (901), dialog có 2 nút:
 *     buttonIndex = 0 → “Nhập nhanh”
 *     buttonIndex = 1 → “Từ chối”
 */
public class CombineHandler {

    /**
     * Phương thức này được gọi mỗi khi server nhận packet do client gửi
     * (khi người chơi bấm nút trong dialog Combine).
     *
     * @param player đối tượng Player đang thực hiện combine
     * @param msg    packet do client gửi chứa [menuId, buttonIndex]
     */
    public void onPlayerSelectMenu(Player player, Message msg) {
        try {
            // ĐỌC menuId (1 byte unsigned → 0..255)
            int menuId = msg.reader().readUnsignedByte();

            switch (menuId) {
                // ============================
                // CASE 900: “Làm phép” (combine 1 lần)
                // ============================
                case ConstNpc.MENU_START_COMBINE: {
                    // ĐỌC tiếp buttonIndex (1 byte unsigned → 0..255)
                    int buttonIndex = msg.reader().readUnsignedByte();

                    if (buttonIndex == 0) {
                        // Người chơi chọn "Làm phép" (không dùng đá bảo vệ)
                        CombineService.gI().startCombine(player, (byte) 0);
                    }
                    else if (buttonIndex == 1) {
                        // Người chơi chọn "Nâng cấp dùng đá bảo vệ"
                        CombineService.gI().startCombine(player, (byte) 1);
                    }
                    // Nếu buttonIndex == 2 (Từ chối) → không làm gì
                    break;
                }

                // ============================
                // CASE 901: “Nhập nhanh” (batch combine tất cả)
                // ============================
                case ConstNpc.MENU_QUICK_COMBINE: {
                    // ĐỌC tiếp buttonIndex (1 byte unsigned → 0..255)
                    int buttonIndex = msg.reader().readUnsignedByte();

                    if (buttonIndex == 0) {
                        // Người chơi chọn "Nhập nhanh"
                        NhapNgocRong.nhapNgocRong(player, false);
                    }
                    // Nếu buttonIndex == 1 (Từ chối) → không làm gì
                    break;
                }

                // ============================
                // Các CASE khác (nếu có thêm menu)
                // ============================
                default:
                    break;
            }
        } catch (IOException e) {
//             e.printStackTrace();
        } finally {
            // Sau khi xử lý xong, cleanup để giải phóng resource
            msg.cleanup();
        }
        
    }
}
