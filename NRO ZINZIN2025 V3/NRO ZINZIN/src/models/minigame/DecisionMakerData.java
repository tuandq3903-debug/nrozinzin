/*
 * Copyright by ZINZIN
 */

package models.minigame;

public class DecisionMakerData {
    public long id;
    public long money;
    public byte type;
    public boolean isNormal;

    public static class resulPlayer {
        public String name;
        public long money;
        public byte type;
    }
}
