package PvZ2.APproject.models.specialLevels;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.zombies.Zombie;

import java.util.List;
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

    @Override
    public List<Plant> getProtectedPlantsList() {
        return List.of();
    }
}
