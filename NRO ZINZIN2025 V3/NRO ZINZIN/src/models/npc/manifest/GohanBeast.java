
package models.npc.manifest;

import consts.ConstNpc;
import models.item.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jdbc.DatabaseManager;
import network.Message;
import models.npc.Npc;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import models.player.Player;
import services.ItemService;
import services.Service;
import models.Top.TopService;
import models.shop.ShopService;
import utils.Util;

/**
 *
 * @author Administrator
 */
public class GohanBeast extends Npc {

    public GohanBeast(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

   @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Ta có Bảng Xếp Hạng của toàn bộ Vũ Trụ\nĐừng quên đến đây để nhận quà khi chốt top nhé",
                    "TOP\nSức Mạnh", 
                    "TOP\nNhiệm Vụ",
                    "TOP\nNạp Tiền");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (select) {
                case 0 ->
                    showTop(player,"SELECT id, name, gender, items_body, CAST(JSON_EXTRACT(data_point, '$[1]') AS UNSIGNED) AS value FROM player ORDER BY value DESC LIMIT 100",
                        "Sức Mạnh");
                case 1 -> TopService.showListTopTask(player);
                case 2 -> TopService.showListTopVnd(player);
//                case 3 -> ShopService.gI().opendShop(player, "CUA_HANG_VAT_PHAM", true);
            }
        }
    }
    private void showTop(Player player, String sqlQuery, String rankName) {
        try (Connection con = DatabaseManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery()) {
            Message msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100 " + rankName);
            int count = 0;
            while (rs.next()) {
                count++;
            }
            msg.writer().writeByte(count);
            rs.beforeFirst();
            int rank = 1;
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                byte gender = rs.getByte("gender");
                long value = rs.getLong("value");
                short head = (short) (gender == 2 ? 28 : (gender == 1 ? 32 : 64));
                short body = (short) (gender == 2 ? 16 : (gender == 1 ? 10 : 14));
                short leg = (short) (gender == 2 ? 17 : (gender == 1 ? 11 : 15));
                String itemsBodyJson = rs.getString("items_body");
                if (itemsBodyJson != null && !itemsBodyJson.isEmpty()) {
                    JSONArray dataArray = (JSONArray) JSONValue.parse(itemsBodyJson);
                    if (dataArray != null) {
                        for (int i = 0; i < dataArray.size() && i < 6; i++) {
                            JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                            if (dataItem != null && dataItem.get(0) != null) {
                                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                                if (tempId != -1) {
                                    Item item = ItemService.gI().createNewItem(tempId,
                                      Integer.parseInt(String.valueOf(dataItem.get(1))));
                                    if (item.template.head != -1) {
                                        head = (short) item.template.head;
                                    }
                                    if (item.template.body != -1) {
                                        body = (short) item.template.body;
                                    }
                                    if (item.template.leg != -1) {
                                        leg = (short) item.template.leg;
                                    }
                                }
                            }
                        }
                    }
                }
                msg.writer().writeInt(rank);
                msg.writer().writeInt(id);
                msg.writer().writeShort(head);
                if (player.getSession().version >= 214) {
                msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(body);
                msg.writer().writeShort(leg);
                msg.writer().writeUTF(name);
                String unit = rankName.equals("Sức Mạnh") ? "Sức mạnh" : "VNĐ";
                msg.writer().writeUTF(Util.numberToMoney(value) + " " + unit);
                msg.writer().writeUTF("...");
                rank++;
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
//             e.printStackTrace();
        }
    }
}
