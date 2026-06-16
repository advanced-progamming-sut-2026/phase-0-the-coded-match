package controllers;


import models.Update;
import models.WavePatternData;

public class ZombieWaveManager implements Update {
    private WavePatternData wavePattern; //todo: im not sure about this one

    public WavePatternData getWavePattern() {
        return wavePattern;
    }

    public static int calculateWaveDifficulty() {

    }

    public static void spawnZombies(int waveDifficulty) {

    }

    public static boolean checkNextWave() {

    }

    @Override
    public void update() {

    }
}
