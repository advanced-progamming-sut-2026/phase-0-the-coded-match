package controllers;


import models.App;
import models.GameMapRelated.GameMap;
import models.Level;
import models.Update;
import models.WavePatternData;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombieWaveManager implements Update {
    Random random = new Random();
    private Level currentLevel = GameManagerController.getInstance().getCurrentLevel();
    private int currentWave = 0;
    private int timeWaveStarted = 0;
    private static List<Zombie> previousWaveZombies = new ArrayList<>();
    private List<WavePatternData> wavePattern = GameManagerController.getInstance().getCurrentLevel().getData().getWavePatterns();

    public List<WavePatternData> getWavePattern(){return wavePattern;}

    public void calculateWaveDifficulty() {
        if(currentWave == 1){
            return;
        }
        wavePattern.get(currentWave).setWaveDifficulty(wavePattern.get(currentWave).getWaveDifficulty()*1.25);
    }

    public void spawnZombies() {
        int remainingCost = (int) wavePattern.get(currentWave).getWaveDifficulty();
        timeWaveStarted = GameManagerController.getInstance().getCurrentLevel().getCurrentTick();

        while (remainingCost > 0) {
            Zombie z = createRandomZombie();   // weighted random based on cost
            if (z.getWaveCost() > remainingCost) continue;

            int lane = random.nextInt(GameManagerController.getInstance().getCurrentLevel().getGameMap().getRows()); // todo: fixed number of rows and columns!!!!
            z.setY(lane);
            z.setX(9);   // right side : starts from the first column (figure out the number)

            GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(z);
            previousWaveZombies.add(z); // if tracking per wave

            System.out.println("Zombie " + z.getData().getDisplayName() + " spawned at wave " + currentWave
                    + " in lane " + lane + " which costed " + z.getWaveCost() + ".");

            remainingCost -= z.getWaveCost();
        }
    }

    private Zombie createRandomZombie(){
        int index = random.nextInt(ZombieRepository.getInstance().getAllZombies().size());
        ZombieData template = ZombieRepository.getInstance().getAllZombies().get(index);
        Zombie zombie = new Zombie(template, 0, 0);
        return zombie;
    }

    public boolean shouldNextWaveStart() {
        if (currentWave == 0) return true;

        int totalHealth = 0;
        int currentHealth = 0;

        for (Zombie z : previousWaveZombies) {
            if (!z.isDead()) {
                totalHealth += z.getData().getMaxHP();
                currentHealth += z.getCurrentHp();
            }
        }

        return (double)currentHealth / totalHealth <= 0.25; // 75% dead
    }

    public static void releaseTheNuke(){
        for(Zombie z: previousWaveZombies){
            z.setCurrentHp(0);
        }
    }

    @Override
    public void update() {
        if(shouldNextWaveStart()){
            currentWave++;
            if(currentWave > wavePattern.size()){
                GameManagerController.getInstance().gameWon();
            }
            if(currentWave == wavePattern.size()){
                System.out.println("The final wave has come.");
            } else{
                System.out.println("Wave " + currentWave + " started.");
            }
            calculateWaveDifficulty();
            spawnZombies();

            currentLevel.getCurrentSeason().WaveStarted(currentLevel, currentWave);
            //currentLevel.getSpecialRuleManager().onWaveStarted(currentLevel, currentWave);
        }
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getTimeWaveStarted() {
        return timeWaveStarted;
    }
}
