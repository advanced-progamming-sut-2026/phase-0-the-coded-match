package models.specialLevels;

import controllers.GameManagerController;
import models.Level;
import models.plants.Plant;

public class TimedWarStrategy implements SpecialLevelStrategy {
    private final int limitTicks;
    private double startTick;
    private boolean finished;

    public TimedWarStrategy(int limitSeconds) {
        limitTicks = Math.max(1, limitSeconds * 10);
    }

    @Override
    public void levelStart(Level level) {
        startTick = level.getCurrentTick();
    }

    @Override
    public void update(Level level) {
        if (!finished && level.getCurrentTick() - startTick >= limitTicks) {
            finished = true;
            if (level.getActiveZombies().isEmpty()) {
                GameManagerController.getInstance().gameWon();
            } else {
                GameManagerController.getInstance().gameOver();
            }
        }
    }

    @Override
    public void plantLost(Level level, Plant plant) {
    }
}
