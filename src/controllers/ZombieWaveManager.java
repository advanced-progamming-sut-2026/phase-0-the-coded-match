package controllers;

import models.App;
import models.Level;
import models.Update;
import models.WavePatternData;
import models.zombies.Barrel;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;
import models.zombies.ZombieSpawnData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ZombieWaveManager implements Update {
    private final Random random;
    private final Level currentLevel;
    private final List<WavePatternData> wavePattern;
    private final List<Zombie> previousWaveZombies = new ArrayList<>();
    private int currentWave;
    private double timeWaveStarted;
    private int previousWaveInitialHealth;
    private boolean completed;

    public ZombieWaveManager(Level level) {
        currentLevel = level;
        wavePattern = new ArrayList<>(level.getData().getWavePatterns());
        long seed = BonusGameController.isActive()
                ? BonusGameController.getDailySeed() + level.getData().getId().hashCode()
                : System.nanoTime();
        random = new Random(seed);
    }

    public List<WavePatternData> getWavePattern() {
        return wavePattern;
    }

    public double calculateWaveDifficulty() {
        int minimumCost = getAllowedZombieData().stream()
                .mapToInt(this::effectiveWaveCost)
                .filter(cost -> cost > 0)
                .min()
                .orElse(1);
        double base = Math.max(currentLevel.getData().getBaseWaveCost(), minimumCost);
        if (getTotalWaves() == 1) {
            return base * 2;
        }
        if (currentWave <= 1) {
            return base;
        }
        if (currentWave == getTotalWaves()) {
            return base * Math.pow(1.25, Math.max(0, currentWave - 2)) * 2;
        }
        return base * Math.pow(1.25, currentWave - 1);
    }

    public void spawnZombies() {
        previousWaveZombies.clear();
        previousWaveInitialHealth = 0;
        timeWaveStarted = currentLevel.getCurrentTick();
        WavePatternData pattern = getCurrentPattern();
        if (pattern != null && pattern.getZombies() != null && !pattern.getZombies().isEmpty()) {
            spawnPattern(pattern);
            return;
        }
        int budget = pattern != null && pattern.getWaveDifficulty() > 0
                ? (int) Math.ceil(pattern.getWaveDifficulty())
                : (int) Math.ceil(calculateWaveDifficulty());
        spawnByCost(budget);
    }

    private void spawnPattern(WavePatternData pattern) {
        for (ZombieSpawnData spawn : pattern.getZombies()) {
            ZombieData data = ZombieRepository.getInstance().findById(spawn.getZombieAlias());
            if (data == null) {
                continue;
            }
            int count = Math.max(1, spawn.getCount());
            for (int i = 0; i < count; i++) {
                int lane = normalizeLane(spawn.getLane());
                addZombie(new Zombie(data, currentLevel.getGameMap().getZombieStartColumn(), lane));
            }
        }
    }

    private void spawnByCost(int budget) {
        List<ZombieData> allowed = getAllowedZombieData();
        if (allowed.isEmpty()) {
            return;
        }
        allowed.sort(Comparator.comparingInt(this::effectiveWaveCost));
        int minimumCost = Math.max(1, effectiveWaveCost(allowed.get(0)));
        int remaining = Math.max(budget, minimumCost);
        while (remaining >= minimumCost) {
            List<ZombieData> affordable = new ArrayList<>();
            for (ZombieData data : allowed) {
                int cost = effectiveWaveCost(data);
                if (cost > 0 && cost <= remaining) {
                    affordable.add(data);
                }
            }
            if (affordable.isEmpty()) {
                break;
            }
            ZombieData data = affordable.get(random.nextInt(affordable.size()));
            int lane = random.nextInt(currentLevel.getGameMap().getRows()) + 1;
            addZombie(new Zombie(data, currentLevel.getGameMap().getZombieStartColumn(), lane));
            remaining -= effectiveWaveCost(data);
        }
    }

    private int effectiveWaveCost(ZombieData data) {
        if (data == null || data.getWaveCost() <= 0) {
            return 0;
        }
        int difficulty = App.getCurrentUser() == null ? 3 : App.getCurrentUser().getDifficultyLevel();
        return Math.max(1, (int) Math.ceil(data.getWaveCost() * 3.0 / difficulty));
    }

    private List<ZombieData> getAllowedZombieData() {
        List<ZombieData> result = new ArrayList<>();
        for (String id : currentLevel.getData().getAllowedZombies()) {
            ZombieData data = ZombieRepository.getInstance().findById(id);
            if (data != null && !result.contains(data)) {
                result.add(data);
            }
        }
        if (result.isEmpty() && currentLevel.getCurrentSeason() != null) {
            for (String id : currentLevel.getCurrentSeason().getData().getAllowedZombies()) {
                ZombieData data = ZombieRepository.getInstance().findById(id);
                if (data != null && !result.contains(data)) {
                    result.add(data);
                }
            }
        }
        if (result.isEmpty()) {
            result.addAll(ZombieRepository.getInstance().getAllZombies());
        }
        result.removeIf(data -> data.getWaveCost() <= 0);
        return result;
    }

    private void addZombie(Zombie zombie) {
        if (App.getCurrentUser() != null) {
            App.getCurrentUser().getCollection().unlockZombie(zombie.getData().getId());
        }
        currentLevel.addActiveZombie(zombie);
        previousWaveZombies.add(zombie);
        previousWaveInitialHealth += zombie.getTotalHealth();
        String id = zombie.getData().getId();
        if (id.equalsIgnoreCase("ZombieBarrelRoller")) {
            currentLevel.addBarrel(new Barrel(zombie.getX() - 0.5, zombie.getY(), zombie, true));
        } else if (id.equalsIgnoreCase("ZombieArcade") || id.equalsIgnoreCase("ZombieTroglobite")) {
            currentLevel.addBarrel(new Barrel(zombie.getX() - 0.5, zombie.getY(), zombie, false));
        }
        System.out.println("Zombie " + zombie.getData().getDisplayName() + " spawned at wave " + currentWave
                + " in lane " + zombie.getY() + " which costed " + effectiveWaveCost(zombie.getData()) + ".");
    }

    private int normalizeLane(int lane) {
        if (lane >= 1 && lane <= currentLevel.getGameMap().getRows()) {
            return lane;
        }
        if (lane >= 0 && lane < currentLevel.getGameMap().getRows()) {
            return lane + 1;
        }
        return random.nextInt(currentLevel.getGameMap().getRows()) + 1;
    }

    private WavePatternData getCurrentPattern() {
        for (WavePatternData pattern : wavePattern) {
            if (pattern.getWaveNumber() == currentWave) {
                return pattern;
            }
        }
        return null;
    }

    public boolean shouldNextWaveStart() {
        if (currentWave == 0) {
            return true;
        }
        if (previousWaveInitialHealth <= 0) {
            return previousWaveZombies.stream().allMatch(Zombie::isDead);
        }
        int remainingHealth = 0;
        for (Zombie zombie : previousWaveZombies) {
            remainingHealth += zombie.getTotalHealth();
        }
        return remainingHealth <= previousWaveInitialHealth * 0.25;
    }

    public static void releaseTheNuke() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        for (Zombie zombie : new ArrayList<>(level.getActiveZombies())) {
            zombie.setCurrentHp(0);
        }
    }

    @Override
    public void update() {
        if (completed || !currentLevel.isZombieWavesEnabled()) {
            return;
        }
        if (currentWave >= getTotalWaves()) {
            boolean anyLivingZombie = currentLevel.getActiveZombies().stream().anyMatch(zombie -> !zombie.isDead());
            if (!anyLivingZombie) {
                completed = true;
                System.out.println(GameManagerController.getInstance().gameWon());
            }
            return;
        }
        if (!shouldNextWaveStart()) {
            return;
        }
        currentWave++;
        if (currentWave == getTotalWaves()) {
            System.out.println("The final wave has come.");
        } else {
            System.out.println("Wave " + currentWave + " started.");
        }
        spawnZombies();
        QuestController.notifyWaveStarted(currentWave, timeWaveStarted);
        SeasonController.advanceWave(currentLevel, currentWave);
    }

    public int getTotalWaves() {
        return Math.max(1, currentLevel.getData().getWaveCount());
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public double getTimeWaveStarted() {
        return timeWaveStarted;
    }

    public boolean isCompleted() {
        return completed;
    }
}
