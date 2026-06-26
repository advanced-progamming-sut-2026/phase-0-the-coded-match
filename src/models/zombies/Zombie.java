package models.zombies;

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
    private ZombieArmor armor;
    private List<ZombieEffect> effects;
    private boolean eating;
    private boolean moving;
    private boolean isLocked;

    public Zombie(ZombieData data, double x, int y) {
        this.data = data;
        this.currentHp = data.getHealth();
        this.x = x;
        this.y = y;
        this.effects = new ArrayList<>();
        this.eating = false;
        this.moving = true;

        if (data.getArmor() != null) {
            this.armor = new ZombieArmor(data.getArmor());
        }
    }

    @Override
    public void update() {
        if(isDead()){
            return;
        }
        Plant target = App.getFrontMostPlantInRow(this.y);

        if(target != null && isAdjacentTo(target)){
            eating = true;
            attack(target);
        }else{
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
        plant.takeDamage(data.getDamage());
        if(plant.isDead()){
            App.removePlant(plant); // TODO: After plant dies we need to print "Plant <type> at (<x>, <y>) is destroyed."; but how do we send it to view?
        }
    }

    public void takeDamage(int damage) {
        if (armor != null && !armor.isDestroyed()) {
            int remainingDamage = armor.takeDamage(damage);
            if (remainingDamage > 0) {
                currentHp = Math.max(0, currentHp - remainingDamage);
            }
        } else {
            currentHp = Math.max(0, currentHp - damage);
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

    public ZombieArmor getArmor() {
        return armor;
    }

    public List<ZombieEffect> getEffects() {
        return effects;
    }

    public int getWaveCost() {
        return data.getWaveCost();
    }

    public List<String> getAbilities() {
        return data.getAbilities();
    }

    public void shootProjectile() {}

}