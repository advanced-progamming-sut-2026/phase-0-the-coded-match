package PvZ2.APproject.models.zombies;

public class ZombieArmor {

    private ZombieArmorData data;
    private int currentHp;

    public ZombieArmor(ZombieArmorData data) {
        this.data = data;
        this.currentHp = data.getHp();
    }

    public int takeDamage(Zombie zombie, int damage) {
        currentHp -= damage;
        if (currentHp < 0) {
            int remainingDamage =- currentHp;
            currentHp = 0;
            zombie.getArmors().remove(this);
            return remainingDamage;
        }
        return 0;
    }

    public boolean isDestroyed() {
        return currentHp <= 0;
    }

    public ZombieArmorData getData() {
        return data;
    }

    public int getCurrentHp() {
        return currentHp;
    }
}
