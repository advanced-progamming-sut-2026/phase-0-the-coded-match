package PvZ2.APproject.models;

public class WavePatternData {
    private int waveNumber;
    private int startTick;
    private double waveDifficulty;

    public int getWaveNumber() {
        return waveNumber;
    }

    public int getStartTick() {
        return startTick;
    }

    public double getWaveDifficulty() {
        return waveDifficulty;
    }

    public void setWaveDifficulty(double waveDifficulty) {
        this.waveDifficulty = waveDifficulty;
    }
}
