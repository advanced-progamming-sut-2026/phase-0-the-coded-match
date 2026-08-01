package models.GameMapRelated;

import controllers.GameManagerController;
import enums.SunType;
import models.App;
import models.Level;
import models.Sun;
import models.Update;

import java.util.Random;

public class SkySunProducer implements Update {
    private static final int TICKS_PER_SECOND = 10;
    private static final int FALL_DURATION_TICKS = 5 * TICKS_PER_SECOND;
    private double timeSinceLastDrop;
    private final Random random;
    private boolean producedASun;
    private Sun sun;

    public SkySunProducer() {
        timeSinceLastDrop = 0.0;
        random = new Random();
        producedASun = false;
    }

    public double getTimeSinceLastDrop() {
        return timeSinceLastDrop;
    }

    public void setTimeSinceLastDrop(int timeSinceLastDrop) {
        this.timeSinceLastDrop = Math.max(0, timeSinceLastDrop);
    }

    public boolean isProducedASun() {
        return producedASun;
    }

    public void setProducedASun(boolean producedASun) {
        this.producedASun = producedASun;
    }

    public Sun getSun() {
        return sun;
    }

    public void setSun(Sun sun) {
        this.sun = sun;
    }

    public void calculateDropTime() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        double elapsedSeconds = level.getCurrentTick() / TICKS_PER_SECOND;
        double intervalSeconds = Math.max(6 + 0.05 * elapsedSeconds, 12);
        int difficulty = App.getCurrentUser() == null ? 3 : App.getCurrentUser().getDifficultyLevel();
        intervalSeconds *= difficulty / 3.0;
        double intervalTicks = intervalSeconds * TICKS_PER_SECOND;
        if (timeSinceLastDrop >= intervalTicks) {
            spawnRandomSun();
            timeSinceLastDrop = 0.0;
        }
    }

    public void spawnRandomSun() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null || level.getGameMap() == null) {
            return;
        }
        int columns = level.getGameMap().getColumns();
        int rows = level.getGameMap().getRows();
        if (columns <= 0 || rows <= 0) {
            return;
        }
        int chance = random.nextInt(100);
        int x = random.nextInt(columns) + 1;
        int y = random.nextInt(rows) + 1;
        SunType type;
        if (chance < SunType.NORMAL.getDropChancePercentage()) {
            type = SunType.NORMAL;
        } else if (chance < SunType.NORMAL.getDropChancePercentage() + SunType.SPECIAL.getDropChancePercentage()) {
            type = SunType.SPECIAL;
        } else {
            type = SunType.RADIOACTIVE;
        }
        Sun droppedSun = new Sun(x, y, type.getValue(), FALL_DURATION_TICKS, true, type);
        level.getActiveSuns().add(droppedSun);
        sun = droppedSun;
        producedASun = true;
    }

    @Override
    public void update() {
        timeSinceLastDrop++;
        calculateDropTime();
    }
}
