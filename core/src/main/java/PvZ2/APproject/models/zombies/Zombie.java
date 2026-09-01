package PvZ2.APproject.models.zombies;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.QuestController;
import PvZ2.APproject.enums.*;
import PvZ2.APproject.models.*;
import PvZ2.APproject.models.factories.ZombieBehaviorFactory;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.strategies.ZombieBehavior;

import java.util.ArrayList;
import java.util.List;

public class Zombie implements Update {
    private ZombieData data;
    private int difficultyLevel;
    private int currentHp;
    private int maxHp;
    private int eatDPS;
    private ZombieState currentState;
    private double x;
    private int y;
    private List<ZombieArmor> armors;
    private ZombieBehavior behavior;
    private List<ZombieEffect> effects;
    private boolean hasThrownImp;
    private double runningSpeed;
    private boolean wasRunning;
    private boolean hasParasol;
    private int stolenSuns;
    private int abilityTickTimer = 0;
    private boolean abilityDone;
    private List<Sun> stolenActiveSuns;
    private boolean isSubmerged;
    private boolean isFrozenInBlock;
    private int blockIceHP = 0;
    private boolean isChilled;
    private float chilledTimer = 0;
    private boolean sunProduced;
    private boolean glowing;
    private int lastDamageTaken;
    private boolean deathProcessed;
    private float updateDelta = 0.1f;

    public Zombie(ZombieData data, double x, int y) {
        this.data = data;
        this.difficultyLevel = App.getCurrentUser() == null ? 3 : App.getCurrentUser().getDifficultyLevel();
        this.maxHp = Math.max(1, (int) (data.getHP() * (difficultyLevel / 3.0)));
        this.currentHp = maxHp;
        this.eatDPS = (int) (data.getEatDPS() * (difficultyLevel / 3.0));
        this.currentState = data.getState();
        this.x = x;
        this.y = y;
        this.effects = new ArrayList<>();
        this.hasThrownImp = false;
        this.runningSpeed = data.getRunningSpeed();
        this.wasRunning = false;
        this.hasParasol = data.isHasParasol();
        this.abilityDone = false;
        this.stolenActiveSuns = new ArrayList<>();
        this.isSubmerged = false;
        this.isFrozenInBlock = false;
        this.isChilled = false;
        this.sunProduced = false;
        this.glowing = Math.random() < 0.05;
        this.lastDamageTaken = 0;
        this.deathProcessed = false;

        this.armors = new ArrayList<>();
        if (data.getArmors() != null) {
            for (ZombieArmorData armorData : data.getArmors()) {
                this.armors.add(new ZombieArmor(armorData));
            }
        }

        this.behavior = ZombieBehaviorFactory.getBehavior(data.getBehaviorType());
    }

    @Override
    public void update(float delta) {
       updateDelta = delta > 0 ? delta : 0.1f;
       if(isFrozenInBlock){
           return;
       }
       if(isChilled){
           chilledTimer -= delta;
           if(chilledTimer <= 0){
               chilledTimer = 0;
               isChilled = false;
               effects.remove(ZombieEffect.CHILLED);
           }
       }

        double previousX = x;
        Plant target = GameManagerController.getInstance().getCurrentLevel().getClosestPlantInFront(this);
        if (data.getId().equalsIgnoreCase("ZombieIceAgeDodo")) {
            target = GameManagerController.getInstance().getCurrentLevel().getPlantInFrontOfZombie(this);
        }

        if (target != null && isAdjacentTo(target)){
            if (currentState == ZombieState.RUNNING) {
                wasRunning = true;
            }
            currentState = ZombieState.EATING;
        } else {
            if (currentState != ZombieState.RUNNING) {//TODO: and if not paralyzed
                currentState = ZombieState.WALKING;
            }
        }

        if (behavior != null) {
            behavior.updateZombie(this, target);
        }

        if (target != null && !target.isDead() && this.y == target.getY()
                && !data.getId().equalsIgnoreCase("ZombieIceAgeDodo")) {
            double contactX = target.getX() + 0.58;
            if (previousX > contactX && this.x < contactX) {
                this.x = contactX;
                this.currentState = ZombieState.EATING;
            }
        }
    }

    private boolean isAdjacentTo(Plant target){
        return this.x >= target.getX() - 0.1 && this.x <= target.getX() + 0.58 && this.y == target.getY();
    }

    public void walk() {
        if (currentState == ZombieState.EATING) {
            return;
        }
        double multiplier = isChilled ? 0.5 : 1.0;
        x -= data.getSpeed() * updateDelta * multiplier * 0.6;
    }

    public void run() {
        double multiplier = isChilled ? 0.5 : 1.0;
        x -= runningSpeed * updateDelta * multiplier * 0.6;
    }

    public void attack(Plant plant) {
        if (plant == null) return;
        if (this.getData().getId().equalsIgnoreCase("ZombieArmZombieNewspaper")) {
            plant.takeDamage(Math.max(1, eatDPS / 5));
        } else {
            plant.takeDamage(Math.max(1, eatDPS / 10));
        }
    }

    public void destroyPlant(Plant plant) {
        if (plant != null) plant.setCurrentHp(0);
    }

    public void takeDamage(int damage, Plant killerPlant) {
        lastDamageTaken = Math.max(0, damage);
        if(isFrozenInBlock){
            blockIceHP -= damage;
            if(blockIceHP <= 0){
                isFrozenInBlock = false;
                blockIceHP = 0;
                effects.remove(ZombieEffect.FROZEN);
            }
        }
        else if (killerPlant != null && killerPlant.hasThisTag(PlantTag.POISON) &&
                (data.getId().equalsIgnoreCase("ZombieArmor1") ||
                        data.getId().equalsIgnoreCase("ZombieArmor2") ||
                        data.getId().equalsIgnoreCase("ZombieDarkArmor3") ||
                        data.getId().equalsIgnoreCase("ZombieArmor4"))) {
            currentHp = Math.max(0, currentHp - damage);
        }
        else if (data.getDisplayName().equalsIgnoreCase("knight zombie") && !armors.isEmpty()) {
            int remainingDamage = armors.get(armors.size() - 1).takeDamage(this ,damage);
            if (remainingDamage > 0) {
                if (armors.size() == 2) {
                    remainingDamage = armors.get(armors.size() - 2).takeDamage(this, remainingDamage);
                } else {
                    currentHp = Math.max(0, currentHp - remainingDamage);
                }
            }
        } else if (!armors.isEmpty()) {
            int remainingDamage = armors.get(0).takeDamage(this ,damage);
            if (remainingDamage > 0) {
                currentHp = Math.max(0, currentHp - remainingDamage);
            }
        } else {
            currentHp = Math.max(0, currentHp - damage);
        }
        if (isDead() && !deathProcessed) {
            deathProcessed = true;
            Level level = GameManagerController.getInstance().getCurrentLevel();
            if (data.getId().matches("ZombieCrystalSkull")) {
                level.setCollectedSunsAmount(level.getCollectedSunsAmount() + (stolenSuns / 2));
            } else if (data.getId().matches("ZombieRa")) {
                for (Sun sun : stolenActiveSuns)
                level.getActiveSuns().add(sun);
            }
            if (killerPlant != null) QuestController.onZombieDefeated(killerPlant);
        }
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public ZombieData getData() {return data;}


    public int getLastDamageTaken() {
        return lastDamageTaken;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
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

    public void setX(double x) {this.x = x;}

    public int getY() {
        return y;
    }

    public void setY(int y) {this.y = y;}

    public List<ZombieArmor> getArmors() {
        return armors;
    }

    public void addArmor(ArmorType type) {
        if (armors == null) armors = new ArrayList<>();
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
        return isSubmerged;
    }

    public void setSubmerged(boolean submerged) {
        isSubmerged = submerged;
    }

    public boolean isFrozenInBlock() {
        return isFrozenInBlock;
    }

    public void setFrozenInBlock(boolean frozen) {
        isFrozenInBlock = frozen;
        if (frozen) {
            if (!effects.contains(ZombieEffect.FROZEN)) effects.add(ZombieEffect.FROZEN);
        } else {
            effects.remove(ZombieEffect.FROZEN);
        }
    }

    public void setBlockIceHP(int iceHP){
        this.blockIceHP = iceHP;
    }

    public boolean getIsChilled(){
        return isChilled;
    }

    public void setChilled(boolean chilled){
        isChilled = chilled;
        chilledTimer = chilled ? 10f : 0f;
        if (chilled) {
            if (!effects.contains(ZombieEffect.CHILLED)) effects.add(ZombieEffect.CHILLED);
        } else {
            effects.remove(ZombieEffect.CHILLED);
        }
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

    public boolean isSunProduced() {
        return sunProduced;
    }

    public void setSunProduced(boolean sunProduced) {
        this.sunProduced = sunProduced;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

}
