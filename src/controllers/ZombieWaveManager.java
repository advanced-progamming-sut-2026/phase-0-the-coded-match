package controllers;


import models.App;
import models.Update;
import models.WavePatternData;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombieWaveManager implements Update {
    private int currentWave = 0;
    private List<Zombie> previousWaveZombies = new ArrayList<>();
    private WavePatternData wavePattern = App.getCurrentLevel().getData().getWavePatterns();

    public WavePatternData getWavePattern(){return wavePattern;}



    public void calculateWaveDifficulty() {
        if(currentWave == 1){
            return;
        }
        wavePattern.setWaveDifficulty(wavePattern.getWaveDifficulty()*1.25);
    }

    public void spawnZombies() {
        int remainingCost = (int) wavePattern.getWaveDifficulty();

        while (remainingCost > 0) {
            Zombie z = createRandomZombie();   // weighted random based on cost
            if (z.getWaveCost() > remainingCost) continue;

            int lane = Random.nextInt(NUM_ROWS); // todo: fixed number of rows and columns!!!!
            z.setY(lane);
            z.setX(START_COL);   // right side

            //zombies.add(z);
            previousWaveZombies.add(z); // if tracking per wave

            System.out.println("Zombie " + z.getData().getCategory() + " spawned at wave " + currentWave
                    + " in lane " + lane + " which costed " + z.getWaveCost() + ".");

            remainingCost -= z.getWaveCost();
        }
    }

    private Zombie createRandomZombie(){
        int index = Random.nextInt(ZombieRepository.getZombies().size());
        ZombieData template = ZombieRepository.getZombies().get(index);
        Zombie zombie = new Zombie(template, 0, 0);
        return zombie;
    }



    public boolean shouldNextWaveStart() { //TODO: check if its time for the new wave
        if (currentWave == 0) return true;

        int totalHealth = 0;
        int currentHealth = 0;

        for (Zombie z : previousWaveZombies) {
            if (!z.isDead()) {
                totalHealth += z.getData().getMaxHp();
                currentHealth += z.getCurrentHp();
            }
        }

        return (double)currentHealth / totalHealth <= 0.25; // 75% dead
    }

    @Override
    public void update() {
        if(shouldNextWaveStart()){
            currentWave++;
            if(currentWave == wavePattern.getWaveNumber()){
                System.out.println("The final wave has come.");
            }else{
                System.out.println("Wave " + currentWave + " started.");
            }
            calculateWaveDifficulty();
            spawnZombies();
        }
    }
}
