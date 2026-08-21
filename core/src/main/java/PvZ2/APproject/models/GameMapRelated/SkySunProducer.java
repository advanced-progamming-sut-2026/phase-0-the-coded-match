package PvZ2.APproject.models.GameMapRelated;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.SunType;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.Sun;
import PvZ2.APproject.models.Update;

import java.util.Random;

public class SkySunProducer implements Update {
    private double timeSinceLastDrop;
    private double elapsedTime;
    private transient Random random;
    private boolean producedASun;
    private Sun sun;

    public SkySunProducer() {
        this.timeSinceLastDrop = 0.0;
        this.elapsedTime = 0.0;
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
        Level level = GameManagerController.getInstance().getCurrentLevel();
        double time = Math.min(12, 6 + 0.05 * elapsedTime);
        int dl = App.getCurrentUser() == null ? 3 : App.getCurrentUser().getDifficultyLevel();
        time *= dl / 3.0;
        if (level.isDay() && timeSinceLastDrop >= time) {
            spawnRandomSun();
            timeSinceLastDrop = 0.0;
        }
    }

    public void spawnRandomSun() {
        Level currentLevel = GameManagerController.getInstance().getCurrentLevel();
        if (random == null) random = new Random();
        int chance = random.nextInt(100);
        int randomX = random.nextInt(currentLevel.getGameMap().getColumns());
        int randomY = random.nextInt(currentLevel.getGameMap().getRows());

        Sun droppedSun = null;
        if (chance < SunType.NORMAL.getDropChancePercentage()) {
            droppedSun = new Sun(randomX, randomY, SunType.NORMAL.getValue(), 5f,
                    true, SunType.NORMAL);
        } else if (chance < SunType.NORMAL.getDropChancePercentage() + SunType.SPECIAL.getDropChancePercentage()) {
            droppedSun = new Sun(randomX, randomY, SunType.SPECIAL.getValue(), 5f,
                    true, SunType.SPECIAL);
        } else {
            droppedSun = new Sun(randomX, randomY, SunType.RADIOACTIVE.getValue(), 5f,
                    true, SunType.RADIOACTIVE);
        }
        if (droppedSun != null) {
            currentLevel.getActiveSuns().add(droppedSun);
            sun = droppedSun;
            producedASun = true;
        }
    }

    @Override
    public void update(float delta) {
        timeSinceLastDrop += delta;
        elapsedTime += delta;
        calculateDropTime();
    }
}
