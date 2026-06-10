package Model;

import java.util.List;

public class WavePatternData {
    private int waveNumber;
    private int startTick;
    private List<ZombieSpawnData> zombies;

    public int getWaveNumber() {
        return waveNumber;
    }

    public int getStartTick() {
        return startTick;
    }

    public List<ZombieSpawnData> getZombies() {
        return zombies;
    }
}