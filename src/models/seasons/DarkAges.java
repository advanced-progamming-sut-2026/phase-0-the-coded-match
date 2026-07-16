package models.seasons;

import models.Level;
import models.plants.Plant;

public class DarkAges extends Season {
    public DarkAges(SeasonData data) {
        super(data);
    }

    @Override
    public void applySpecialRules() {
        // TODO: apply dark ages rules later
    }

    @Override
    public void initializeGrid() {

    }

    @Override
    public void LevelStarted(Level level) {

    }

    @Override
    public void Update(Level level, double deltaTime) {

    }

    @Override
    public void WaveStarted(Level level, int waveNumber) {

    }

    @Override
    public void PlantPlaced(Level level, Plant plant, int x, int y) {

    }


}

