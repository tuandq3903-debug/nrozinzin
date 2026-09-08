/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.task;

import models.item.Item;
import java.util.ArrayList;
import services.ItemService;

/**
 *
 * @author Administrator
 */
public class KolTaskTemplate {

    public int id;
    public String info;
    public int max_count;
    public ArrayList<Item> rewards;

    public KolTaskTemplate(int id, String name, int max_count) {
        this.id = id;
        this.info = name;
        this.max_count = max_count;
        this.rewards = getRewardItem();
    }

    private ArrayList<Item> getRewardItem() {
        rewards = new ArrayList<>();
        switch (id) {
            case 0 -> {
                rewards.add(ItemService.gI().createNewItemLock(1820, 3));
            }
            case 1 -> {
                rewards.add(ItemService.gI().createNewItemLock(1592, 5));
                rewards.add(ItemService.gI().createNewItemLock(1757, 5));
            }
            case 2 -> {
                rewards.add(ItemService.gI().createNewItemLock(1360, 1));
            }
            case 3 -> {
                rewards.add(ItemService.gI().createNewItemLock(457, 10));
                rewards.add(ItemService.gI().createNewItemLock(861, 1000));
            }
            case 4 -> {
                rewards.add(ItemService.gI().createNewItemLock(457, 10));
                rewards.add(ItemService.gI().createNewItemLock(861, 1000));
            }
        }
        return rewards;
    }
}
