package models;

import java.util.List;

public class ZombieData {
    private String id;
    private String alias;
    private String displayName;
    private String category;
    private List<String> seasons;
    private int health;
    private int damage;
    private double speed;
    private double attackInterval;
    private int waveCost;
    private ZombieArmorData armor;
    private String behaviorType;
    private String description;
    private List<String> abilities;


    public String getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getSeasons() {
        return seasons;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAttackInterval() {
        return attackInterval;
    }

    public int getWaveCost() {
        return waveCost;
    }

    public ZombieArmorData getArmor() {
        return armor;
    }

    public String getBehaviorType() {
        return behaviorType;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAbilities() {
        return abilities;
    }

}