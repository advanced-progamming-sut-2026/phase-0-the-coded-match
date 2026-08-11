package PvZ2.APproject.models.plants;

import PvZ2.APproject.enums.PlantTag;
import PvZ2.APproject.models.Update;
import PvZ2.APproject.models.plants.abilities.PlantAbilityFactory;
import PvZ2.APproject.models.plants.abilities.PlantAbilityHandler;

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
    private int freezeLevel = 0;
    private int iceHP = 0;

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
        if (data.getAbilities() == null || data.getAbilities().isEmpty()) {
            currentCooldownTimer = cooldownTicks;
            return;
        }
        if (data.getAbilities().contains("PRODUCE_SUN") && !sunCollected) {
            currentCooldownTimer = cooldownTicks;
            return;
        }
        executeAbilities(data.getAbilities());
        currentCooldownTimer = cooldownTicks;
    }

    private void executeAbilities(List<String> abilities) {
        if (abilities == null) {
            return;
        }
        for (String ability : abilities) {
            PlantAbilityHandler handler = PlantAbilityFactory.getHandler(ability);
            if (handler != null) {
                handler.execute(this);
            }
        }
    }

    public void addFreezeLevel(int amount){
        if(this.hasThisTag(PlantTag.FIRE)){
            return;
        }
        this.freezeLevel = Math.min(3, this.freezeLevel + amount);
        if(this.freezeLevel == 3 && this.iceHP <= 0){
            this.iceHP = 600;
        }
    }

    public boolean isFullyFrozen(){return this.freezeLevel == 3;}

    public int getIceHP(){return iceHP;}

    public void decreaseIceHP(int amount){
        this.iceHP -= amount;
        if(iceHP <= 0){
            this.iceHP = 0;
            this.freezeLevel = 0;
        }
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
        executeAbilities(data.getPlantFoodAbilities());
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

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
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
