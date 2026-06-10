package Model;

import java.util.List;

public class PlantData {
    private String id;
    private String name;
    private String displayName;
    private String category;
    private List<String> tags;
    private int sun_cost;
    private int baseHp;
    private int damage;
    private double actionInterval;
    private double recharge;
    private String behaviorType;
    private List<String> abilities;
    private String baseAbility;
    private String plantFoodEffect;
    private List<PlantUpgradeData> upgrades;
    private String description;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getCost() {
        return sun_cost;
    }

    public int getBaseHp() {
        return baseHp;
    }

    public int getDamage() {
        return damage;
    }

    public double getActionInterval() {
        return actionInterval;
    }

    public double getRecharge() {
        return recharge;
    }

    public String getBehaviorType() {
        return behaviorType;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public String getBaseAbility() {
        return baseAbility;
    }

    public String getPlantFoodEffect() {
        return plantFoodEffect;
    }

    public List<PlantUpgradeData> getUpgrades() {
        return upgrades;
    }

    public String getDescription() {
        return description;
    }
}