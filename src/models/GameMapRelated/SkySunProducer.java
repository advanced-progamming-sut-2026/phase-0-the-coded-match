package models.GameMapRelated;

import controllers.GameManagerController;
import enums.SunType;
import models.App;
import models.Level;
import models.Sun;
import models.Update;

import java.util.Random;

public class SkySunProducer implements Update {
    private double timeSinceLastDrop;
    private transient Random random;
    private boolean producedASun;
    private Sun sun;

    public SkySunProducer() {
        this.timeSinceLastDrop = 0.0;
        this.random = new Random();
        producedASun = false;
    }

    public double getTimeSinceLastDrop() {
        return timeSinceLastDrop;
    }

    public void setTimeSinceLastDrop(int timeSinceLastDrop) {
        this.timeSinceLastDrop = timeSinceLastDrop;
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
        double time = Math.max(6 + 0.05 * GameManagerController.getInstance().getCurrentLevel().getCurrentTick(), 12);
        int dl = App.getCurrentUser().getDifficultyLevel();
        time = time * (dl / 3.0);
        if (timeSinceLastDrop >= time) {
            spawnRandomSun();
            timeSinceLastDrop = 0.0;
        }
    }

    public void spawnRandomSun() {
        Level currentLevel = GameManagerController.getInstance().getCurrentLevel();
        int chance = random.nextInt();
        int randomX = random.nextInt(currentLevel.getGameMap().getLength()); //x and y >= 0
        int randomY = random.nextInt(currentLevel.getGameMap().getWidth());

        Sun droppedSun = null;
        if (chance < SunType.NORMAL.getDropChancePercentage()) {
            droppedSun = new Sun(randomX, randomY, SunType.NORMAL.getValue(), 5,
                    true, SunType.NORMAL);
        } else if (SunType.NORMAL.getDropChancePercentage() < chance && chance < SunType.SPECIAL.getDropChancePercentage()) {
            droppedSun = new Sun(randomX, randomY, SunType.SPECIAL.getValue(), 5,
                    true, SunType.SPECIAL);
        } else {
            droppedSun = new Sun(randomX, randomY, SunType.RADIOACTIVE.getDropChancePercentage(), 5,
                    true, SunType.RADIOACTIVE);
        }
        if (droppedSun != null) {
            currentLevel.getActiveSuns().add(droppedSun);
            sun = droppedSun;
            producedASun = true;
        }
    }

    @Override
    public void update() {
        timeSinceLastDrop++;
        calculateDropTime();
    }
}
