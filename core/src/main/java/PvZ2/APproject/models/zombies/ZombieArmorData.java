package PvZ2.APproject.models.zombies;

import PvZ2.APproject.enums.ArmorType;

public class ZombieArmorData {
    private ArmorType type;
    private int hp;
    private boolean metallic;
    private String path;

    public ZombieArmorData(ArmorType type) {
        this.type = type;
        this.hp = type.getHp();
        this.metallic = type.isMetallic();
    }

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
