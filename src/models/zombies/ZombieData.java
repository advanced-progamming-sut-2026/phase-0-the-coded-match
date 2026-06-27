package models.zombies;

import enums.SeasonType;
import enums.ZombieState;

import java.util.List;

public class ZombieData {
    private String id;
    private String displayName;
    private List<SeasonType> seasons;
    private int maxHP;
    private int eatDPS;
    private double speed;
    private double attackInterval;
    private int waveCost;
    private List<ZombieArmorData> armors;
    private String behaviorType;
    private ZombieState state;

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<SeasonType> getSeasons() {
        return seasons;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getEatDPS() {
        return eatDPS;
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

    public List<ZombieArmorData> getArmors() {
        return armors;
    }

    public String getBehaviorType() {
        return behaviorType;
    }

    public ZombieState getState() {
        return state;
    }
}