package models.plants;

import controllers.PlantController;
import enums.PlantTag;
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
    private int cooldownRemaining;
    private List<String> activeEffects;
    private List<PlantTag> tags;
    private boolean isLocked;
    private int cooldownTicks;
    private int currentCooldownTimer;
    private boolean producedSun = false;
    private boolean sunCollected = false;

    public void setLevel(int level) {
        this.level = level;
    }

    public Plant(PlantData data, int x, int y, int level) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.level = level;
        this.currentHp = data.getBaseHp();
        this.boosted = false;
        this.cooldownRemaining = 0;
        this.activeEffects = new ArrayList<>();
        this.currentCooldownTimer = ;//TODO: where to find the base cooldown time?
    }

    @Override
    public void update() {
        // TODO
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
        if (currentCooldownTimer > 0) {
            currentCooldownTimer--;
        }
        if (currentCooldownTimer == 0) {
            switch (data.getCategory().getName().toLowerCase()) {
                case "sun producer":
                    if (sunCollected == true) {
                        PlantController.produceSun(this);
                        currentCooldownTimer = cooldownTicks; //TODO: if not collected, should cooldown reset or not?
                    }
                case "shooter":
                    PlantController.shootProjectile();
                    currentCooldownTimer = cooldownTicks;
                case "lobber":
                    PlantController.lobProjectile();
                    currentCooldownTimer = cooldownTicks;
                case "explosive":
                    PlantController.explode();
                    currentCooldownTimer = cooldownTicks;
                case "melee":
                    PlantController.attack();
                    currentCooldownTimer = cooldownTicks;
                case "strike-through":
                    PlantController.shootProjectile();
                    currentCooldownTimer = cooldownTicks;
                case "homing":
                    PlantController.detectAZombie();
                    currentCooldownTimer = cooldownTicks;
                case "mint":
                    PlantController.givePlantFood();
                    currentCooldownTimer = cooldownTicks;
            }
        }
        //TODO: check if plant gets destroyed here
        if (isDead()) {

        }
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

    public boolean hasThisTag(PlantTag tag) {
        for (PlantTag plantTag : tags) {
            if (plantTag == tag) {
                return true;
            }
        }
        return false;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
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

    public void setBoosted(boolean boosted) {this.boosted = boosted;}

    public int getCooldownRemaining() {
        return cooldownRemaining;
    }

    public boolean isSunCollected() {
        return sunCollected;
    }

    public void setSunCollected(boolean sunCollected) {
        this.sunCollected = sunCollected;
    }

    public boolean isProducedSun() {
        return producedSun;
    }

    public void setProducedSun(boolean producedSun) {
        this.producedSun = producedSun;
    }

    public List<PlantTag> getTags() {
        return tags;
    }

    public void setTags(List<PlantTag> tags) {
        this.tags = tags;
    }
}