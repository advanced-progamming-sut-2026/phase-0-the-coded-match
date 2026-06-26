package models.zombies;

import enums.ArmorType;

public class ZombieArmorData {
    private ArmorType type;
    private int hp;
    private boolean metallic;

    public ArmorType getType() {
        return type;
    }

    public int getHp() {
        return hp;
    }

    public boolean isMetallic() {
        return metallic;
    }
}