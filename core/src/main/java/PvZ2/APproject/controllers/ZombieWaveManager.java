package PvZ2.APproject.controllers;

import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.Update;
import PvZ2.APproject.models.WavePatternData;
import PvZ2.APproject.models.zombies.Barrel;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombieWaveManager implements Update {
    private final transient Random random = new Random();
    private final Level currentLevel;
    private int currentWave = -1;
    private double timeWaveStarted;
    private final List<Zombie> previousWaveZombies = new ArrayList<>();
    private final List<WavePatternData> wavePattern = new ArrayList<>();
    private boolean isLastWave;
    private boolean newWaveStarted;

    public ZombieWaveManager(Level level) {
        currentLevel = level;
        List<WavePatternData> configured = level.getData().getWavePatterns();
        if (configured != null) {
            for (WavePatternData wave : configured) {
                if (wave.getWaveDifficulty() > 0) {
                    wavePattern.add(wave);
                }
            }
        }
        if (wavePattern.isEmpty()) {
            int count = Math.max(1, level.getData().getWaveCount());
            double cost = Math.max(1, level.getData().getBaseWaveCost());
            for (int i = 0; i < count; i++) {
                WavePatternData wave = new WavePatternData();
                wave.setWaveDifficulty(i == count - 1 && i > 0 ? cost * 2 : cost);
                wavePattern.add(wave);
                cost *= 1.25;
            }
        }
    }

    public List<WavePatternData> getWavePattern() { return wavePattern; }

    public void spawnZombies() {
        int remaining = (int) Math.round(wavePattern.get(currentWave).getWaveDifficulty());
        timeWaveStarted = currentLevel.getCurrentTick();
        List<ZombieData> allowed = currentLevel.getCurrentSeason() == null ?
            List.of() : currentLevel.getCurrentSeason().getAllowedZombies();
        while (remaining > 0 && !allowed.isEmpty()) {
            List<ZombieData> affordable = new ArrayList<>();
            for (ZombieData data : allowed) {
                if (data.getWaveCost() > 0 && data.getWaveCost() <= remaining) {
                    affordable.add(data);
                }
            }
            if (affordable.isEmpty()) break;
            double spawnX = currentLevel.getGameMap().getColumns() + 0.85;
            Zombie zombie = new Zombie(affordable.get(random.nextInt(affordable.size())), spawnX, 1 +
                random.nextInt(currentLevel.getGameMap().getRows()));
            if (App.getCurrentUser() != null) {
                App.getCurrentUser().getCollection().unlockZombie(zombie.getData().getId());
            }
            if (zombie.getData().getId().equalsIgnoreCase("ZombieBarrelRoller")) {
                currentLevel.addBarrel(new Barrel(spawnX - 0.35, zombie.getY(), zombie));
            }
            currentLevel.addActiveZombie(zombie);
            previousWaveZombies.add(zombie);
            remaining -= zombie.getWaveCost();
            System.out.println("Zombie " + zombie.getData().getDisplayName() +
                " spawned at wave " + (currentWave + 1) + " in lane " + zombie.getY() +
                " which costed " + zombie.getWaveCost() + ".");
        }
    }


    private boolean canFill(int amount, List<ZombieData> allowed) {
        boolean[] possible = new boolean[amount + 1];
        possible[0] = true;
        for (int i = 1; i <= amount; i++) for (ZombieData data : allowed) {
            int cost = data.getWaveCost();
            if (cost > 0 && cost <= i && possible[i - cost]) { possible[i] = true; break; }
        }
        return possible[amount];
    }

    public boolean shouldNextWaveStart() {
        if (currentWave < 0 || previousWaveZombies.isEmpty()) return true;
        double total = 0, current = 0;
        for (Zombie zombie : previousWaveZombies) {
            total += zombie.getMaxHp(); current += Math.max(0, zombie.getCurrentHp());
        }
        return total == 0 || current / total <= 0.25;
    }

    public static void releaseTheNuke() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level != null) {
            for (Zombie zombie : new ArrayList<>(level.getActiveZombies())) {
                zombie.takeDamage(Math.max(1, zombie.getCurrentHp() + zombie.getMaxHp()), null);
            }
        }
    }

    @Override
    public void update(float delta) {
        if (currentWave == wavePattern.size() - 1) {
            if (currentLevel.getActiveZombies().isEmpty()) {
                GameManagerController.getInstance().gameWon();
            }
            return;
        }
        if (!shouldNextWaveStart()) return;
        currentWave++;
        previousWaveZombies.clear();
        isLastWave = currentWave == wavePattern.size() - 1;
        newWaveStarted = true;
        spawnZombies();
        if (currentLevel.getCurrentSeason() != null) {
            currentLevel.getCurrentSeason().waveStarted(currentLevel, currentWave);
        }
    }

    public int getCurrentWave() { return currentWave; }
    public int getTotalWaves() { return wavePattern == null ? 0 : wavePattern.size(); }
    public double getTimeWaveStarted() { return timeWaveStarted; }
    public boolean isLastWave() { return isLastWave; }
    public void setLastWave(boolean value) { isLastWave = value; }
    public boolean isNewWaveStarted() { return newWaveStarted; }
    public void setNewWaveStarted(boolean value) { newWaveStarted = value; }
}
