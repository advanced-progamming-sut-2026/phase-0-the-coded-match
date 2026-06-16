package models;

import models.zombies.ZombieSpawnData;

import java.util.List;

public class WavePatternData {
    private int waveNumber;
    private int startTick;
    private List<ZombieSpawnData> zombies;
    private int waveDifficulty;

    public int getWaveNumber() {
        return waveNumber;
    }

    public int getStartTick() {
        return startTick;
    }

    public List<ZombieSpawnData> getZombies() {
        return zombies;
    }

    public int getWaveDifficulty() {
        return waveDifficulty;
    }

    public void setWaveDifficulty(int waveDifficulty) {
        this.waveDifficulty = waveDifficulty;
    }
}