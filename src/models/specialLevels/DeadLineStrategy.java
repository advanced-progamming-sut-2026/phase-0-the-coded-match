package models.specialLevels;

import controllers.GameManagerController;
import models.Level;
import models.plants.Plant;
import models.zombies.Zombie;

import java.util.Random;

public class DeadLineStrategy implements SpecialLevelStrategy{
    transient Random random = new Random();
    private final int deadLineColumn;

    public DeadLineStrategy(int deadLineColumn) {
        this.deadLineColumn = deadLineColumn;
    }

    @Override
    public void levelStart(Level level) {

    }

    @Override
    public void update(Level level) {
        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie.getX() <= deadLineColumn) {
                GameManagerController.getInstance().gameOver();
                break;
            }
        }
    }

    @Override
    public void plantLost(Level level, Plant plant) {

    }
}
