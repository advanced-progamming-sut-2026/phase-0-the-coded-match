package models.zombies;

import controllers.GameManagerController;
import enums.ZombieEffect;
import enums.ZombieState;
import models.App;
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
        Plant target = GameManagerController.getCurrentLevel().getFrontMostPlantInRow(this.y);

        if (target != null && isAdjacentTo(target)){
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
        }
    }

    public void throwImp() {
        ZombieData impData = ZombieRepository.getInstance().findByDisplayName("Imp");
        Zombie newImp = new Zombie(impData, 3.0, this.y);
        GameManagerController.getCurrentLevel().getActiveZombies().add(newImp);
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
}