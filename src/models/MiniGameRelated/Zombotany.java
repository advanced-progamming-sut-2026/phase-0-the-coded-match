package models.MiniGameRelated;

import enums.LevelType;
import models.GameMapRelated.GameMap;
import models.LevelData;
import models.plants.Plant;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Zombotany extends MiniGame {
    private static final int ROWS = 5;
    private static final int COLUMNS = 9;
    private static final int TICKS_PER_SECOND = 10;
    private static final int JALAPENO_FUSE_TICKS = 10 * TICKS_PER_SECOND;
    private static final int PEA_SHOT_COOLDOWN_TICKS = 15;
    private static final int PEA_DAMAGE = 20;
    private static final double PEA_SPEED = 0.35;

    public enum PlantZombieType {
        PEASHOOTER,
        WALL_NUT,
        JALAPENO,
        SQUASH
    }

    public static class ZombiePea {
        private double x;
        private final int row;
        private boolean destroyed;

        private ZombiePea(double x, int row) {
            this.x = x;
            this.row = row;
        }

        public double getX() { return x; }
        public int getRow() { return row; }
        public boolean isDestroyed() { return destroyed; }
    }

    private static class PlantZombieState {
        private final PlantZombieType type;
        private int ageTicks;
        private int abilityCooldown;
        private boolean abilityUsed;

        private PlantZombieState(PlantZombieType type) {
            this.type = type;
            this.abilityCooldown = PEA_SHOT_COOLDOWN_TICKS;
        }
    }

    private final int stageNumber;
    private final Map<Zombie, PlantZombieState> plantZombieStates;
    private final List<ZombiePea> activePeas;
    private boolean won;

    public Zombotany(int stageNumber) {
        super(createLevelData(stageNumber));
        if (stageNumber < 1 || stageNumber > 3) {
            throw new IllegalArgumentException("Zombotany stage must be between 1 and 3");
        }
        this.stageNumber = stageNumber;
        this.plantZombieStates = new IdentityHashMap<>();
        this.activePeas = new ArrayList<>();
        this.isGameOver = false;
        initializeStage();
    }

    private static LevelData createLevelData(int stageNumber) {
        LevelData data = new LevelData();
        data.setLevelNumber(stageNumber);
        data.setLevelType(LevelType.NORMAL);
        data.setUnlocked(true);
        data.setMap(new GameMap(ROWS, COLUMNS));
        return data;
    }

    public final void initializeStage() {
        plantZombieStates.clear();
        activePeas.clear();
        getActiveZombies().clear();
        getActivePlants().clear();
        setCollectedSunsAmount(300 + stageNumber * 100);
        won = false;
        isGameOver = false;
        ZombieData baseData = ZombieRepository.getInstance().findByDisplayName("Default");
        if (baseData == null) {
            throw new IllegalStateException("default zombie data is missing");
        }
        PlantZombieType[] types = switch (stageNumber) {
            case 1 -> new PlantZombieType[]{PlantZombieType.PEASHOOTER, PlantZombieType.WALL_NUT};
            case 2 -> new PlantZombieType[]{PlantZombieType.PEASHOOTER, PlantZombieType.WALL_NUT, PlantZombieType.JALAPENO};
            default -> PlantZombieType.values();
        };
        int count = stageNumber + 3;
        for (int i = 0; i < count; i++) {
            spawnPlantZombie(baseData, types[i % types.length], COLUMNS - i % 2, i % ROWS + 1);
        }
    }

    public Zombie spawnPlantZombie(ZombieData baseData, PlantZombieType type,
                                   double x, int row) {
        if (baseData == null) {
            throw new IllegalArgumentException("base zombie data cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("plant zombie type cannot be null");
        }
        if (row < 1 || row > ROWS || x < 1 || x > COLUMNS) {
            throw new IllegalArgumentException("invalid spawn position");
        }

        Zombie zombie = new Zombie(baseData, x, row);
        if (type == PlantZombieType.WALL_NUT) {
            zombie.setCurrentHp(Math.max(zombie.getCurrentHp() * 5, 4000));
        }
        addActiveZombie(zombie);
        plantZombieStates.put(zombie, new PlantZombieState(type));
        return zombie;
    }

    public void processInteraction() {
        tick();
    }

    public void tick() {
        if (isGameOver) {
            return;
        }

        updatePlantZombies();
        updatePeas();
        removeDestroyedEntities();
        checkRules();
    }

    private void updatePlantZombies() {
        for (Zombie zombie : new ArrayList<>(getActiveZombies())) {
            PlantZombieState state = plantZombieStates.get(zombie);
            if (state == null || zombie.isDead()) {
                continue;
            }
            state.ageTicks++;

            switch (state.type) {
                case PEASHOOTER:
                    updatePeashooterZombie(zombie, state);
                    break;
                case WALL_NUT:
                    break;
                case JALAPENO:
                    updateJalapenoZombie(zombie, state);
                    break;
                case SQUASH:
                    updateSquashZombie(zombie, state);
                    break;
                default:
                    break;
            }
        }
    }

    private void updatePeashooterZombie(Zombie zombie, PlantZombieState state) {
        if (state.abilityCooldown > 0) {
            state.abilityCooldown--;
        }
        if (state.abilityCooldown == 0 && hasPlantToLeft(zombie)) {
            activePeas.add(new ZombiePea(zombie.getX() - 0.25, zombie.getY()));
            state.abilityCooldown = PEA_SHOT_COOLDOWN_TICKS;
        }
    }

    private void updateJalapenoZombie(Zombie zombie, PlantZombieState state) {
        if (!state.abilityUsed && state.ageTicks >= JALAPENO_FUSE_TICKS) {
            burnEntireRow(zombie.getY());
            zombie.setCurrentHp(0);
            state.abilityUsed = true;
        }
    }

    private void updateSquashZombie(Zombie zombie, PlantZombieState state) {
        zombie.setX(zombie.getX() - Math.max(0.015, zombie.getData().getSpeed() * 0.3));
        Plant target = findCollidingPlant(zombie, 0.55);
        if (target != null && !state.abilityUsed) {
            target.setCurrentHp(0);
            zombie.setCurrentHp(0);
            state.abilityUsed = true;
        }
    }

    private void updatePeas() {
        Iterator<ZombiePea> iterator = activePeas.iterator();
        while (iterator.hasNext()) {
            ZombiePea pea = iterator.next();
            pea.x -= PEA_SPEED;
            Plant target = findPlantHitByPea(pea);
            if (target != null) {
                target.takeDamage(PEA_DAMAGE);
                pea.destroyed = true;
            }
            if (pea.destroyed || pea.x < 0) {
                iterator.remove();
            }
        }
    }

    private boolean hasPlantToLeft(Zombie zombie) {
        for (Plant plant : getActivePlants()) {
            if (!plant.isDead() && plant.getY() == zombie.getY()
                    && plant.getX() < zombie.getX()) {
                return true;
            }
        }
        return false;
    }

    private Plant findPlantHitByPea(ZombiePea pea) {
        Plant closest = null;
        double smallestDistance = Double.MAX_VALUE;
        for (Plant plant : getActivePlants()) {
            if (plant.isDead() || plant.getY() != pea.row || plant.getX() > pea.x) {
                continue;
            }
            double distance = pea.x - plant.getX();
            if (distance <= 0.45 && distance < smallestDistance) {
                closest = plant;
                smallestDistance = distance;
            }
        }
        return closest;
    }

    private Plant findCollidingPlant(Zombie zombie, double distanceLimit) {
        Plant closest = null;
        double smallestDistance = Double.MAX_VALUE;
        for (Plant plant : getActivePlants()) {
            if (plant.isDead() || plant.getY() != zombie.getY()) {
                continue;
            }
            double distance = Math.abs(zombie.getX() - plant.getX());
            if (distance <= distanceLimit && distance < smallestDistance) {
                closest = plant;
                smallestDistance = distance;
            }
        }
        return closest;
    }

    private void burnEntireRow(int row) {
        for (Plant plant : getActivePlants()) {
            if (plant.getY() == row) {
                plant.setCurrentHp(0);
            }
        }
    }

    private void removeDestroyedEntities() {
        Iterator<Plant> plantIterator = getActivePlants().iterator();
        while (plantIterator.hasNext()) {
            Plant plant = plantIterator.next();
            if (!plant.isDead()) continue;
            if (getGameMap().getTile(plant.getX(), plant.getY()) != null) {
                getGameMap().getTile(plant.getX(), plant.getY()).removePlant();
            }
            plantIterator.remove();
        }
        getActiveZombies().removeIf(Zombie::isDead);
        plantZombieStates.keySet().removeIf(Zombie::isDead);
    }

    public void checkRules() {
        if (getActiveZombies().isEmpty()) {
            isGameOver = true;
            won = true;
            return;
        }
        for (Zombie zombie : getActiveZombies()) {
            if (zombie.getX() <= 0) {
                isGameOver = true;
                won = false;
                return;
            }
        }
    }

    public PlantZombieType getTypeOf(Zombie zombie) {
        PlantZombieState state = plantZombieStates.get(zombie);
        return state == null ? null : state.type;
    }

    public int getStageNumber() { return stageNumber; }
    public boolean hasWon() { return won; }
    public List<ZombiePea> getActivePeas() {
        return Collections.unmodifiableList(activePeas);
    }
}
