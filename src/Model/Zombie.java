package Model;

import Enums.ZombieCategory;
import Enums.ZombieEffect;
import Enums.ZombieType;

import java.util.ArrayList;
import java.util.List;

public abstract class Zombie {
    protected ZombieType type;
    protected ZombieCategory category;
    protected int health;
    protected int damage;
    protected double speed;
    protected int waveCost;
    protected double x;
    protected int y;
    protected List<ZombieEffect> effects;
    protected ZombieArmor armor;

    public Zombie(ZombieType type, ZombieCategory category, int health, int damage,
                  double speed, int waveCost, double x, int y) {
        this.type = type;
        this.category = category;
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.waveCost = waveCost;
        this.x = x;
        this.y = y;
        this.effects = new ArrayList<>();
    }

    public abstract void move();

    public abstract void attack(Plant plant);

    public void takeDamage() {

    }

    public void takeDamage(int damage) {
        if (armor != null && !armor.isDestroyed()) {
            armor.takeDamage(damage);
        } else {
            health = Math.max(0, health - damage);
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public static int getWaveCost() {

    }
}