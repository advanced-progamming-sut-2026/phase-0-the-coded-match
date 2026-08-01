package models.zombies;

import controllers.GameManagerController;
import controllers.QuestController;
import enums.ArmorType;
import enums.PlantTag;
import enums.ZombieEffect;
import enums.ZombieState;
import models.App;
import models.Level;
import models.Sun;
import models.Update;
import models.factories.ZombieBehaviorFactory;
import models.plants.Plant;
import models.zombies.strategies.ZombieBehavior;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Zombie implements Update {
    private final ZombieData data;
    private final int difficultyLevel;
    private final int maxHp;
    private int currentHp;
    private int eatDPS;
    private ZombieState currentState;
    private double x;
    private int y;
    private final List<ZombieArmor> armors;
    private ZombieBehavior behavior;
    private final List<ZombieEffect> effects;
    private final Map<ZombieEffect, Integer> effectDurations;
    private boolean hasThrownImp;
    private final double runningSpeed;
    private boolean wasRunning;
    private boolean hasParasol;
    private int stolenSuns;
    private int abilityTickTimer;
    private boolean abilityDone;
    private List<Sun> stolenActiveSuns;
    private boolean submerged;
    private boolean frozen;
    private boolean sunProduced;
    private boolean deathHandled;
    private int attackTickTimer;
    private final boolean glowing;

    public Zombie(ZombieData data, double x, int y) {
        if (data == null) {
            throw new IllegalArgumentException("zombie type does not exist");
        }
        this.data = data;
        this.difficultyLevel = App.getCurrentUser() == null ? 3 : App.getCurrentUser().getDifficultyLevel();
        this.maxHp = Math.max(1, (int) Math.round(data.getMaxHP() * difficultyLevel / 3.0));
        this.currentHp = maxHp;
        this.eatDPS = Math.max(0, (int) Math.round(data.getEatDPS() * difficultyLevel / 3.0));
        this.currentState = data.getState();
        this.x = x;
        this.y = y;
        this.armors = new ArrayList<>();
        for (ZombieArmorData armorData : data.getArmors()) {
            this.armors.add(new ZombieArmor(armorData));
        }
        this.effects = new ArrayList<>();
        this.effectDurations = new EnumMap<>(ZombieEffect.class);
        this.runningSpeed = data.getRunningSpeed();
        this.hasParasol = data.isHasParasol();
        this.stolenActiveSuns = new ArrayList<>();
        this.behavior = ZombieBehaviorFactory.getBehavior(data.getBehaviorType());
        this.glowing = ThreadLocalRandom.current().nextInt(100) < 5;
    }

    @Override
    public void update() {
        updateEffects();
        if (frozen || effects.contains(ZombieEffect.FROZEN) || isDead()) {
            return;
        }
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        Plant target = level.getFrontMostPlantInRow(y, x);
        if (data.getId().equalsIgnoreCase("ZombieIceAgeDodo")) {
            target = level.getPlantInFrontOfZombie(this);
        }
        if (target != null && isAdjacentTo(target)) {
            if (currentState == ZombieState.RUNNING) {
                wasRunning = true;
            }
            currentState = ZombieState.EATING;
        } else if (currentState != ZombieState.RUNNING && currentState != ZombieState.WALKING_BACKWARD
                && currentState != ZombieState.STEALING) {
            currentState = ZombieState.WALKING;
            attackTickTimer = 0;
        }
        behavior.updateZombie(this, target);
    }

    private void updateEffects() {
        List<ZombieEffect> expired = new ArrayList<>();
        for (Map.Entry<ZombieEffect, Integer> entry : new ArrayList<>(effectDurations.entrySet())) {
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                expired.add(entry.getKey());
            } else {
                effectDurations.put(entry.getKey(), remaining);
            }
        }
        for (ZombieEffect effect : expired) {
            effectDurations.remove(effect);
            effects.remove(effect);
        }
    }

    private boolean isAdjacentTo(Plant target) {
        return target != null && y == target.getY() && x <= target.getX() + 0.5 && x >= target.getX() - 0.2;
    }

    public void walk() {
        if (currentState != ZombieState.EATING) {
            double multiplier = effects.contains(ZombieEffect.CHILLED) ? 0.5 : 1.0;
            x -= data.getSpeed() * multiplier;
        }
    }

    public void run() {
        double multiplier = effects.contains(ZombieEffect.CHILLED) ? 0.5 : 1.0;
        x -= runningSpeed * multiplier;
    }

    public void attack(Plant plant) {
        if (plant == null) {
            attackTickTimer = 0;
            return;
        }
        attackTickTimer++;
        double intervalSeconds = data.getAttackInterval() > 0 ? data.getAttackInterval() : 1.0;
        int intervalTicks = Math.max(1, (int) Math.ceil(intervalSeconds * 10));
        if (attackTickTimer < intervalTicks) {
            return;
        }
        attackTickTimer = 0;
        int damage = eatDPS;
        if (data.getId().equalsIgnoreCase("ZombieArmZombieNewspaper") && armors.isEmpty()) {
            damage *= 2;
        }
        plant.takeDamage(damage);
    }

    public void destroyPlant(Plant plant) {
        if (plant != null) {
            plant.setCurrentHp(0);
        }
    }

    public void takeDamage(int damage, Plant killerPlant) {
        int remainingDamage = Math.max(0, damage);
        boolean bypassArmor = killerPlant != null && killerPlant.hasThisTag(PlantTag.POISON);
        if (!bypassArmor) {
            while (remainingDamage > 0 && !armors.isEmpty()) {
                remainingDamage = armors.get(0).takeDamage(this, remainingDamage);
            }
        }
        if (remainingDamage > 0) {
            currentHp = Math.max(0, currentHp - remainingDamage);
        }
        if (isDead()) {
            handleDeath(killerPlant);
        }
    }


    private void handleDeath(Plant killerPlant) {
        if (deathHandled) {
            return;
        }
        deathHandled = true;
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level != null) {
            if (data.getId().equalsIgnoreCase("ZombieCrystalSkull")) {
                level.setCollectedSunsAmount(level.getCollectedSunsAmount() + stolenSuns / 2);
            } else if (data.getId().equalsIgnoreCase("ZombieRa")) {
                level.getActiveSuns().addAll(stolenActiveSuns);
                stolenActiveSuns.clear();
            }
            if (QuestController.isReady()) {
                QuestController.onZombieDefeated(killerPlant);
            }
        }
        behavior.onDeath(this);
    }

    public void addEffect(ZombieEffect effect, int durationTicks) {
        if (!effects.contains(effect)) {
            effects.add(effect);
        }
        if (durationTicks > 0) {
            effectDurations.put(effect, durationTicks);
        }
    }


    public void removeEffect(ZombieEffect effect) {
        effects.remove(effect);
        effectDurations.remove(effect);
    }

    public double getEffectRemainingSeconds(ZombieEffect effect) {
        return effectDurations.getOrDefault(effect, 0) / 10.0;
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public boolean isBoss() {
        return data.isBoss();
    }

    public ZombieData getData() {
        return data;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(0, currentHp);
        if (this.currentHp == 0) {
            handleDeath(null);
        }
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getTotalHealth() {
        int total = currentHp;
        for (ZombieArmor armor : armors) {
            total += Math.max(0, armor.getCurrentHp());
        }
        return total;
    }

    public int getEatDPS() {
        return eatDPS;
    }

    public void setEatDPS(int eatDPS) {
        this.eatDPS = eatDPS;
    }

    public ZombieState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ZombieState currentState) {
        this.currentState = currentState;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<ZombieArmor> getArmors() {
        return armors;
    }

    public void addArmor(ArmorType type) {
        armors.add(new ZombieArmor(new ZombieArmorData(type)));
    }

    public List<ZombieEffect> getEffects() {
        return effects;
    }

    public int getWaveCost() {
        return data.getWaveCost();
    }

    public boolean isHasThrownImp() {
        return hasThrownImp;
    }

    public void setHasThrownImp(boolean hasThrownImp) {
        this.hasThrownImp = hasThrownImp;
    }

    public boolean isWasRunning() {
        return wasRunning;
    }

    public void setWasRunning(boolean wasRunning) {
        this.wasRunning = wasRunning;
    }

    public boolean isHasParasol() {
        return hasParasol;
    }

    public void setHasParasol(boolean hasParasol) {
        this.hasParasol = hasParasol;
    }

    public int getStolenSuns() {
        return stolenSuns;
    }

    public void setStolenSuns(int stolenSuns) {
        this.stolenSuns = stolenSuns;
    }

    public int getAbilityTickTimer() {
        return abilityTickTimer;
    }

    public void setAbilityTickTimer(int abilityTickTimer) {
        this.abilityTickTimer = abilityTickTimer;
    }

    public boolean isAbilityDone() {
        return abilityDone;
    }

    public void setAbilityDone(boolean abilityDone) {
        this.abilityDone = abilityDone;
    }

    public boolean isSubmerged() {
        return submerged;
    }

    public void setSubmerged(boolean submerged) {
        this.submerged = submerged;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public ZombieBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(ZombieBehavior behavior) {
        this.behavior = behavior;
    }

    public List<Sun> getStolenActiveSuns() {
        return stolenActiveSuns;
    }

    public void setStolenActiveSuns(List<Sun> stolenActiveSuns) {
        this.stolenActiveSuns = stolenActiveSuns;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public int getAttackTickTimer() {
        return attackTickTimer;
    }

    public boolean isSunProduced() {
        return sunProduced;
    }

    public void setSunProduced(boolean sunProduced) {
        this.sunProduced = sunProduced;
    }
}
