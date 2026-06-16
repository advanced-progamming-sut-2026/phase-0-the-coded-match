package models.zombies;

import enums.ZombieEffect;
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
        move();
    }

    public void move() {
        if (!moving || eating) {
            return;
        }
        x -= data.getSpeed();
    }

    public void attack(Plant plant) {
        eating = true;
        plant.takeDamage(data.getDamage());
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

    public int getY() {
        return y;
    }

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