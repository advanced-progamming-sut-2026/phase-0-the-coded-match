package models;

public class PlantUpgradeData {
    private int level;
    private int requiredSeedPackets;
    private int requiredCoins;
    private int hpBonus;
    private int damageBonus;
    private int costReduction;
    private double rechargeReduction;

    public int getLevel() {
        return level;
    }

    public int getRequiredSeedPackets() {
        return requiredSeedPackets;
    }

    public int getRequiredCoins() {
        return requiredCoins;
    }

    public int getHpBonus() {
        return hpBonus;
    }

    public int getDamageBonus() {
        return damageBonus;
    }

    public int getCostReduction() {
        return costReduction;
    }

    public double getRechargeReduction() {
        return rechargeReduction;
    }
}