package models.plants;

import controllers.PlantController;
import enums.PlantTag;
import models.Update;

import java.util.ArrayList;
import java.util.List;

public class Plant implements Update {
    private static final int TICKS_PER_SECOND = 10;

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
    private boolean sunCollected = true;

    public Plant(PlantData data, int x, int y, int level) {
        this.data = data;
        this.x = x;
        this.y = y;
        this.level = Math.max(1, level);
        this.currentHp = data.getBaseHp();
        this.boosted = false;
        this.cooldownRemaining = 0;
        this.activeEffects = new ArrayList<>();
        this.tags = convertTags(data.getTags());
        this.cooldownTicks = secondsToTicks(data.getActionInterval());
        this.currentCooldownTimer = cooldownTicks;
    }

    @Override
    public void update() {
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
        if (currentCooldownTimer > 0) {
            currentCooldownTimer--;
        }
        if (currentCooldownTimer == 0) {
            doAction();
        }
    }

    private void doAction() {
        String categoryName = data.getCategory().getName().toLowerCase();
        if (categoryName.equals("sun producer")) {
            if (sunCollected) {
                PlantController.produceSun(this);
                sunCollected = false;
                currentCooldownTimer = cooldownTicks;
            }
            return;
        }
        if (categoryName.equals("shooter") || categoryName.equals("strike-through")) {
            PlantController.shootProjectile();
        } else if (categoryName.equals("lobber")) {
            PlantController.lobProjectile();
        } else if (categoryName.equals("explosive")) {
            PlantController.explode();
        } else if (categoryName.equals("melee")) {
            PlantController.attack();
        } else if (categoryName.equals("homing")) {
            PlantController.detectAZombie();
        } else if (categoryName.equals("mint")) {
            PlantController.givePlantFood();
        }
        currentCooldownTimer = cooldownTicks;
    }

    public void takeDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public void activatePlantFood() {
        boosted = true;
        currentHp = Math.max(currentHp, data.getBaseHp());
        activeEffects.add("plantFood:" + data.getPlantFoodEffect());
        if (data.getCategory().getName().equalsIgnoreCase("sun producer")) {
            PlantController.produceSun(this);
            sunCollected = false;
        }
    }

    public boolean hasThisTag(PlantTag tag) {
        for (PlantTag plantTag : tags) {
            if (plantTag == tag) {
                return true;
            }
        }
        return false;
    }

    public boolean canBePlantedNow() {
        return cooldownRemaining <= 0;
    }

    public void startRecharge() {
        this.cooldownRemaining = secondsToTicks(data.getRecharge());
    }

    public void removeCooldown() {
        this.cooldownRemaining = 0;
    }

    private int secondsToTicks(double seconds) {
        if (seconds <= 0) {
            return 1;
        }
        return Math.max(1, (int) Math.ceil(seconds * TICKS_PER_SECOND));
    }

    private List<PlantTag> convertTags(List<String> rawTags) {
        List<PlantTag> result = new ArrayList<>();
        if (rawTags == null) {
            return result;
        }
        for (String rawTag : rawTags) {
            try {
                result.add(PlantTag.valueOf(rawTag.trim().toUpperCase().replace('-', '_')));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return result;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public void setBoosted(boolean boosted) {
        this.boosted = boosted;
    }

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

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}