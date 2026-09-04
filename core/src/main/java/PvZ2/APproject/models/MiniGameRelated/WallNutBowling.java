package PvZ2.APproject.models.MiniGameRelated;

import PvZ2.APproject.enums.BowlingNutType;
import PvZ2.APproject.enums.LevelType;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.LevelData;
import PvZ2.APproject.models.RollingNut;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public final class WallNutBowling extends MiniGame {
    private static final int ROWS = 5;
    private static final int COLUMNS = 9;
    private static final int EXPLOSION_DAMAGE = 1800;

    private final int stageNumber;
    private final Queue<BowlingNutType> conveyorBelt;
    private final List<RollingNut> activeRollingNuts;
    private transient final Random random;
    private final double redLineCoordinateX;
    private final int conveyorSpawnCooldownTicks;
    private int currentCooldownTimer;

    public WallNutBowling(int stageNumber) {
        super(createLevelData(stageNumber));
        if (stageNumber < 1 || stageNumber > 3) {
            throw new IllegalArgumentException("Wall-nut Bowling stage must be between 1 and 3");
        }
        this.stageNumber = stageNumber;
        this.conveyorBelt = new ArrayDeque<>();
        this.activeRollingNuts = new ArrayList<>();
        this.random = new Random();
        this.redLineCoordinateX = 3.0;
        this.conveyorSpawnCooldownTicks = Math.max(12, 30 - stageNumber * 5);
        this.currentCooldownTimer = 0;
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
        conveyorBelt.clear();
        activeRollingNuts.clear();
        conveyorBelt.offer(createRandomNutType());
        currentCooldownTimer = conveyorSpawnCooldownTicks;
        createStageZombies();
    }

    private void createStageZombies() {
        getActiveZombies().clear();
        ZombieRepository repository = ZombieRepository.getInstance();
        int count = 4 + stageNumber * 3;
        for (int i = 0; i < count; i++) {
            String type = "Default";
            if (stageNumber >= 2 && i % 3 == 0) type = "Conehead Zombie";
            if (stageNumber >= 3 && i % 5 == 0) type = "Buckethead Zombie";
            ZombieData data = repository.findByDisplayName(type);
            if (data != null) {
                addActiveZombie(new Zombie(data, 7 + random.nextInt(3), 1 + random.nextInt(ROWS)));
            }
        }
    }

    public void processInteraction() {
        tick();
    }

    public void checkRules() {
        if (getActiveZombies().isEmpty()) {
            isGameOver = true;
            return;
        }
        for (Zombie zombie : getActiveZombies()) {
            if (!zombie.isDead() && zombie.getX() <= 0.0) {
                isGameOver = true;
                return;
            }
        }
    }

    public void tick() {
        if (isGameOver) {
            return;
        }
        updateConveyor();
        updateNuts();
        removeDeadZombies();
        checkRules();
    }

    private void updateConveyor() {
        if (currentCooldownTimer > 0) {
            currentCooldownTimer--;
        }
        if (currentCooldownTimer == 0 && conveyorBelt.size() < 8) {
            conveyorBelt.offer(createRandomNutType());
            currentCooldownTimer = conveyorSpawnCooldownTicks;
        }
    }

    private BowlingNutType createRandomNutType() {
        int roll = random.nextInt(100);
        int explosiveChance = 10 + stageNumber * 3;
        int giantChance = 5 + stageNumber * 3;
        if (roll < explosiveChance) {
            return BowlingNutType.EXPLODE_O_NUT;
        }
        if (roll < explosiveChance + giantChance) {
            return BowlingNutType.GIANT_WALLNUT;
        }
        return BowlingNutType.REGULAR_BOWLING_WALLNUT;
    }

    public String executePlaceNutFromBelt(int row) {
        if (row < 1 || row > ROWS) {
            return "invalid row";
        }
        BowlingNutType type = conveyorBelt.poll();
        if (type == null) {
            return "conveyor belt is empty";
        }
        activeRollingNuts.add(new RollingNut(type, 1.0, row));
        return type + " placed in row " + row;
    }

    public String executePlaceNutFromBelt() {
        return executePlaceNutFromBelt(1);
    }

    private void updateNuts() {
        Iterator<RollingNut> iterator = activeRollingNuts.iterator();
        while (iterator.hasNext()) {
            RollingNut nut = iterator.next();
            nut.advancePosition();
            handleCollisions(nut);
            nut.clearLastHitWhenSeparated();
            if (nut.isDestroyed() || nut.getXCoordinate() > COLUMNS + 1) {
                iterator.remove();
            }
        }
    }

    private void handleCollisions(RollingNut nut) {
        for (Zombie zombie : new ArrayList<>(getActiveZombies())) {
            if (!nut.collidesWith(zombie)) {
                continue;
            }
            switch (nut.getNutType()) {
                case EXPLODE_O_NUT:
                    explodeAt(nut.getXCoordinate(), nut.getYCoordinate());
                    nut.triggerExplosionImpact();
                    return;
                case GIANT_WALLNUT:
                    nut.crushZombieAndMaintainPath(zombie);
                    break;
                case REGULAR_BOWLING_WALLNUT:
                    nut.applyRegularDamage(zombie);
                    break;
                default:
                    break;
            }
        }
    }

    private void explodeAt(double centerX, double centerY) {
        for (Zombie zombie : getActiveZombies()) {
            if (Math.abs(zombie.getX() - centerX) <= 1.0
                    && Math.abs(zombie.getY() - centerY) <= 1.0) {
                zombie.setCurrentHp(Math.max(0, zombie.getCurrentHp() - EXPLOSION_DAMAGE));
            }
        }
    }

    private void removeDeadZombies() {
        getActiveZombies().removeIf(Zombie::isDead);
    }

    public void addZombie(Zombie zombie) {
        if (zombie != null) {
            addActiveZombie(zombie);
        }
    }

    public int getStageNumber() { return stageNumber; }
    public double getRedLineCoordinateX() { return redLineCoordinateX; }
    public Queue<BowlingNutType> getConveyorBelt() {
        return new ArrayDeque<>(conveyorBelt);
    }
    public List<RollingNut> getActiveRollingNuts() {
        return Collections.unmodifiableList(activeRollingNuts);
    }
    public boolean hasWon() { return isGameOver && getActiveZombies().isEmpty(); }
}
