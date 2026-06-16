package models.plants;

import models.Update;

import java.util.ArrayList;
import java.util.List;

public class Plant implements Update {
    private PlantData data;
    private int currentHp;
    private int x;
    private int y;
    private int level;
    private boolean boosted;
    private double cooldownRemaining;
    private List<String> activeEffects;
    private boolean isLocked;
    private int attackCooldownTicks;
    private int currentCooldownTimer;

    public Plant(PlantData data, int x, int y, int level) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.level = level;
        this.currentHp = data.getBaseHp();
        this.boosted = false;
        this.cooldownRemaining = 0;
        this.activeEffects = new ArrayList<>();
    }

    @Override
    public void update() {
        // TODO
    }

    public void takeDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public void activatePlantFood() {
        // TODO
    }

    public String getBehaviorType() {
        return data.getBehaviorType();
    }

    public List<String> getAbilities() {
        return data.getAbilities();
    }

    public PlantData getData() {
        return data;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLevel() {
        return level;
    }

    public boolean isBoosted() {
        return boosted;
    }

    public double getCooldownRemaining() {
        return cooldownRemaining;
    }

    public void shootProjectile() {}
}