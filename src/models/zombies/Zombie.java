package models.zombies;

import controllers.GameManagerController;
import enums.ZombieEffect;
import enums.ZombieState;
import models.App;
import models.GameMapRelated.Tile;
import models.Level;
import models.Update;
import models.factories.ZombieBehaviorFactory;
import models.plants.Plant;
import models.strategies.ZombieBehavior;

import java.util.ArrayList;
import java.util.List;

public class Zombie implements Update {
    private ZombieData data;
    private int currentHp;
    private int currentDamage;
    private ZombieState currentState;
    private double x;
    private int y;
    private List<ZombieArmor> armors;
    private ZombieBehavior behavior;
    private List<ZombieEffect> effects;
//    private boolean eating;
//    private boolean moving;
    private boolean isLocked;
    private boolean hasThrownImp;
    private double runningSpeed;
    private boolean wasRunning;
    private boolean hasParasol;
    private int stolenSuns;
    private int abilityTickTimer = 0;
    private boolean abilityDone;

    public Zombie(ZombieData data, double x, int y) {
        this.data = data;
        this.currentHp = data.getMaxHP();
        this.currentState = data.getState();
        this.x = x;
        this.y = y;
        this.effects = new ArrayList<>();
//        this.eating = false;
//        this.moving = true;
        this.hasThrownImp = false;
        this.runningSpeed = data.getRunningSpeed();
        this.wasRunning = false;
        this.hasParasol = data.isHasParasol(); //TODO: Parasol Zombie's talent should be handled in the method in which zombies are attacked and takes damage
        this.abilityDone = false;

        if (!data.getArmors().isEmpty()) {
            this.armors = new ArrayList<>();
            for (ZombieArmorData armorData : data.getArmors()) {
                this.armors.add(new ZombieArmor(armorData));
            }
        }

        this.behavior = ZombieBehaviorFactory.getBehavior(data.getBehaviorType());
    }

    @Override
    public void update() {
        Plant target = App.getFrontMostPlantInRow(this.y);

        if (target != null && isAdjacentTo(target)){
            if (currentState == ZombieState.RUNNING) {
                wasRunning = true;
            }
            currentState = ZombieState.EATING;
        } else {
            if (currentState != ZombieState.RUNNING) {//TODO: if not paralyzed
                currentState = ZombieState.WALKING;
            }
        }

        if (behavior != null) {
            behavior.updateZombie(this, target);
        }

    }

    private boolean isAdjacentTo(Plant target){
        if (this.x == target.getX() && this.y == target.getY()){ //TODO: Maybe later on change the condition to be more flexible
            return true;
        }
        return false;
    }

    public void walk() {
        if (currentState == ZombieState.EATING) {
            return;
        }
        x -= data.getSpeed();
    }

    public void run() {
        x -= runningSpeed;
    }

    public void attack(Plant plant) {
        plant.takeDamage(data.getEatDPS());
        if(plant.isDead()){
            GameManagerController.getCurrentLevel().getActivePlants().remove(plant); // TODO: After plant dies we need to print "Plant <type> at (<x>, <y>) is destroyed."; but how do we send it to view?
        }
    }

    public void destroyPlant(Plant plant) {
        plant.setCurrentHp(0);
        GameManagerController.getCurrentLevel().getActivePlants().remove(plant); // TODO: After plant dies we need to print "Plant <type> at (<x>, <y>) is destroyed."; but how do we send it to view?
    }

    public void destroyZombie(Zombie zombie) {
        zombie.setCurrentHp(0);
        GameManagerController.getCurrentLevel().getActiveZombies().remove(zombie);
    }

    public int stealSuns() {
        int sunsToSteal = Math.max(25, GameManagerController.getCurrentLevel().getCollectedSunsAmount());
        if (sunsToSteal > 0) {
            Level level = GameManagerController.getCurrentLevel();
            level.setCollectedSunsAmount(level.getCollectedSunsAmount() - sunsToSteal);
        }
        return sunsToSteal;
    }

    public void lazer() {
        for (Tile tile : GameManagerController.getCurrentLevel().getGameMap().getTiles()) {
            if (tile.getRow() == y && x - tile.getColumn() <= 4 && !tile.isEmpty()) {
                tile.getPlant().setCurrentHp(0);
                GameManagerController.getCurrentLevel().getActivePlants().remove(tile.getPlant());//TODO: if there is a specific method that kills the plant instantly, replace
            }
        }
    }

    public void takeDamage(int damage, Plant plant) {
        if (data.getDisplayName().equalsIgnoreCase("knight zombie") && !armors.isEmpty()) {
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
        if (isDead()) {
            GameManagerController.getCurrentLevel().getActiveZombies().remove(this);
            if (data.getId().matches("ZombieCrystalSkull")) {
                Level level = GameManagerController.getCurrentLevel();
                level.setCollectedSunsAmount(level.getCollectedSunsAmount() + (int) (stolenSuns / 2));
            }
        }
    }

    public void throwImp() {
        ZombieData impData = ZombieRepository.getInstance().findByDisplayName("Imp");
        Zombie newImp = new Zombie(impData, 3.0, this.y);
        GameManagerController.getCurrentLevel().getActiveZombies().add(newImp);
    }

    public void explodeDynamite() {
        y = 0; //TODO: should be moved to the first tile. but is it y = 0?
        currentState = ZombieState.WALKING_BACKWARD;
    }

    public void walkBackWard() {
        x += data.getSpeed();
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public ZombieData getData() {
        return data;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getCurrentDamage() {
        return currentDamage;
    }

    public void setCurrentDamage(int currentDamage) {
        this.currentDamage = currentDamage;
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

    public List<ZombieEffect> getEffects() {
        return effects;
    }

    public int getWaveCost() {
        return data.getWaveCost();
    }

//    public boolean isEating() {
//        return eating;
//    }
//
//    public boolean isMoving() {
//        return moving;
//    }

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
}