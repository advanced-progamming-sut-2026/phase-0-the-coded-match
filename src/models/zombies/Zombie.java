package models.zombies;

import controllers.GameManagerController;
import enums.ZombieEffect;
import models.App;
import models.Update;
import models.plants.Plant;

import java.util.ArrayList;
import java.util.List;

public class Zombie implements Update {
    private ZombieData data;
    private int currentHp;
    private int currentDamage;
    private double x;
    private int y;
    private List<ZombieArmor> armors;
    private List<ZombieEffect> effects;
    private boolean eating;
    private boolean moving;
    private boolean isLocked;

    public Zombie(ZombieData data, double x, int y) {
        this.data = data;
        this.currentHp = data.getmaxHP();
        this.x = x;
        this.y = y;
        this.effects = new ArrayList<>();
        this.eating = false;
        this.moving = true;

        if (!data.getArmors().isEmpty()) {
            this.armors = new ArrayList<>();
            for (ZombieArmorData armorData : data.getArmors()) {
                this.armors.add(new ZombieArmor(armorData));
            }
        }
    }

    @Override
    public void update() {
//        if (isDead()){
//            return;
//        }

        Plant target = App.getFrontMostPlantInRow(this.y);

        if (target != null && isAdjacentTo(target)){
            eating = true;
            attack(target);
        } else {
            eating = false;
            move();
        }

    }

    private boolean isAdjacentTo(Plant target){
        if (this.x == target.getX() && this.y == target.getY()){ //TODO: Maybe later on change the condition to be more flexible
            return true;
        }
        return false;
    }

    public void move() {
        if (!moving || eating) {
            return;
        }
        x -= data.getSpeed();
    }

    public void attack(Plant plant) {
        plant.takeDamage(data.getEatDPS());
        if(plant.isDead()){
            App.removePlant(plant); // TODO: After plant dies we need to print "Plant <type> at (<x>, <y>) is destroyed."; but how do we send it to view?
        }
    }

    public void takeDamage(int damage) {
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

    public void shootProjectile() {}

}