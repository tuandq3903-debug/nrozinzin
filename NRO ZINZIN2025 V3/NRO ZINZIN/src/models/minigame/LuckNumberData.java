/*
 * Copyright by ZINZIN
 */

package models.minigame;

public class LuckNumberData {
    public long id;
    public int number;
    public boolean isGem;
    public boolean isReward;

    public static class LuckyNumberResul {
        public long id;
        public long money;
        public int number;
        public String text;
    }
}
