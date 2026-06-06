package Model;

import Enums.ArmorType;

public class ZombieArmor {

    private ArmorType type;
    private int hp;

    public void takeDamage (int damage){
         hp -= damage;
    }

    public boolean isDestroyed () {
        return hp <= 0;
    }
}
