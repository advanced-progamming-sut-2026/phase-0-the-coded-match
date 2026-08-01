package models.zombies;

import enums.SeasonType;
import enums.ZombieState;

import java.util.Collections;
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
    private double runningSpeed;
    private boolean hasParasol;
    private int cost;
    private boolean boss;

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<SeasonType> getSeasons() {
        return seasons == null ? Collections.emptyList() : seasons;
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
        return armors == null ? Collections.emptyList() : armors;
    }

    public String getBehaviorType() {
        return behaviorType == null || behaviorType.isBlank() ? "Normal" : behaviorType;
    }

    public ZombieState getState() {
        return state == null ? ZombieState.WALKING : state;
    }

    public boolean isHasParasol() {
        return hasParasol;
    }

    public double getRunningSpeed() {
        return runningSpeed > 0 ? runningSpeed : speed * 2;
    }

    public int getCost() {
        return cost;
    }

    public boolean isBoss() {
        if (boss) {
            return true;
        }
        String idValue = id == null ? "" : id.toLowerCase();
        String nameValue = displayName == null ? "" : displayName.toLowerCase();
        String behaviorValue = behaviorType == null ? "" : behaviorType.toLowerCase();
        return idValue.contains("boss") || nameValue.contains("boss") || behaviorValue.contains("boss");
    }
}
