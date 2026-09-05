package PvZ2.APproject.models.zombies;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.SeasonType;
import PvZ2.APproject.enums.TileType;
import PvZ2.APproject.enums.ZombieState;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.plants.Plant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Zomboss extends Zombie {
    public enum Action {
        IDLE,
        MOVE,
        SUMMON,
        STUNNED,
        EGYPT_MISSILE,
        EGYPT_CHARGE,
        EGYPT_RETREAT,
        DARK_FIREBALL,
        DARK_BREATH,
        FROST_MISSILE,
        FROST_WIND,
        FROST_COLUMN,
        BEACH_SHARK,
        BEACH_TURBINE
    }

    public static final class TargetCell {
        private final int column;
        private final int row;

        public TargetCell(int column, int row) {
            this.column = column;
            this.row = row;
        }

        public int getColumn() {
            return column;
        }

        public int getRow() {
            return row;
        }
    }

    private final Random random = new Random();
    private final SeasonType season;
    private final List<TargetCell> targetCells = new ArrayList<>();
    private float stunTimer;
    private float actionCooldown = 1.8f;
    private float actionTimer;
    private float actionDuration;
    private float effectTimer;
    private float turbineStepTimer;
    private int phase = 1;
    private int targetRowA;
    private int targetRowB;
    private Action action = Action.IDLE;
    private Action lastStartedAction = Action.IDLE;
    private boolean actionResolved;
    private double homeX = Double.NaN;
    private double chargeTargetX;

    public Zomboss(ZombieData data, double x, int y) {
        super(data, x, y);
        season = data instanceof ZombossData ? ((ZombossData) data).getSeason() : SeasonType.ANCIENT_EGYPT;
        setCurrentState(ZombieState.IDLE);
    }

    @Override
    public void update(float delta) {
        if (isDead()) return;
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) return;
        if (Double.isNaN(homeX)) homeX = getX();
        if (effectTimer > 0f) effectTimer = Math.max(0f, effectTimer - delta);
        if (stunTimer > 0f) {
            stunTimer = Math.max(0f, stunTimer - delta);
            action = Action.STUNNED;
            setCurrentState(ZombieState.IDLE);
            if (stunTimer == 0f) {
                if (getX() < homeX - 0.05) {
                    action = Action.EGYPT_RETREAT;
                    setCurrentState(ZombieState.WALKING);
                } else {
                    finishAction();
                }
            }
            return;
        }
        if (action != Action.IDLE) {
            updateAction(level, delta);
            return;
        }
        actionCooldown -= delta;
        if (actionCooldown <= 0f) startRandomAction(level);
    }

    private void updateAction(Level level, float delta) {
        switch (action) {
            case MOVE, SUMMON -> {
                actionTimer = Math.max(0f, actionTimer - delta);
                if (actionTimer == 0f) finishAction();
            }
            case EGYPT_MISSILE -> updateDelayed(level, delta, 0.34f, this::resolveEgyptMissile);
            case DARK_FIREBALL -> updateDelayed(level, delta, 0.34f, this::resolveDarkFireballs);
            case DARK_BREATH -> updateDelayed(level, delta, 0.42f, this::resolveDarkBreath);
            case FROST_MISSILE -> updateDelayed(level, delta, 0.34f, this::resolveFrostMissile);
            case FROST_WIND -> updateDelayed(level, delta, 0.52f, this::resolveFrostWind);
            case FROST_COLUMN -> updateDelayed(level, delta, 0.42f, this::resolveFrostColumn);
            case BEACH_SHARK -> updateDelayed(level, delta, 0.30f, this::resolveBeachShark);
            case BEACH_TURBINE -> updateBeachTurbine(level, delta);
            case EGYPT_CHARGE -> updateEgyptCharge(level, delta);
            case EGYPT_RETREAT -> updateEgyptRetreat(delta);
            case STUNNED, IDLE -> {
            }
        }
    }

    private void updateDelayed(Level level, float delta, float resolveAt, LevelAction resolver) {
        actionTimer = Math.max(0f, actionTimer - delta);
        if (!actionResolved && actionTimer <= resolveAt) {
            actionResolved = true;
            resolver.run(level);
            effectTimer = 0.65f;
        }
        if (actionTimer == 0f) finishAction();
    }

    private void updateEgyptCharge(Level level, float delta) {
        double previousX = getX();
        double speed = 5.4 + phase * 0.65;
        setX(Math.max(chargeTargetX, getX() - speed * delta));
        crushSweptPlants(level, previousX, getX());
        if (getX() > chargeTargetX) return;
        action = Action.EGYPT_RETREAT;
        actionResolved = true;
        effectTimer = 0.35f;
        setCurrentState(ZombieState.WALKING);
    }

    private void crushSweptPlants(Level level, double fromX, double toX) {
        double min = Math.min(fromX, toX) - 2.2;
        double max = Math.max(fromX, toX) + 0.35;
        int rowA = getOccupiedRowA();
        int rowB = getOccupiedRowB();
        for (Plant plant : level.getActivePlants()) {
            if (plant == null || plant.isDead()) continue;
            if (plant.getY() != rowA && plant.getY() != rowB) continue;
            if (plant.getX() >= min && plant.getX() <= max) plant.setCurrentHp(0);
        }
    }

    private void updateEgyptRetreat(float delta) {
        double speed = 6.0 + phase * 0.7;
        setX(Math.min(homeX, getX() + speed * delta));
        if (getX() < homeX) return;
        setX(homeX);
        finishAction();
    }

    private void updateBeachTurbine(Level level, float delta) {
        actionTimer = Math.max(0f, actionTimer - delta);
        turbineStepTimer -= delta;
        if (turbineStepTimer <= 0f) {
            pullBeachEntities(level);
            turbineStepTimer = 0.22f;
        }
        if (!actionResolved && actionTimer <= 0.15f) {
            actionResolved = true;
            swallowBeachRows(level);
            effectTimer = 0.45f;
        }
        if (actionTimer == 0f) finishAction();
    }

    private void startRandomAction(Level level) {
        List<Action> actions = new ArrayList<>();
        actions.add(Action.MOVE);
        switch (season) {
            case ANCIENT_EGYPT -> {
                actions.add(Action.SUMMON);
                actions.add(Action.EGYPT_MISSILE);
                actions.add(Action.EGYPT_CHARGE);
            }
            case DARK_AGES -> {
                actions.add(Action.SUMMON);
                actions.add(Action.DARK_FIREBALL);
                actions.add(Action.DARK_BREATH);
            }
            case FROSTBITE_CAVES -> {
                actions.add(Action.FROST_MISSILE);
                actions.add(Action.FROST_WIND);
                actions.add(Action.FROST_COLUMN);
            }
            case BIG_WAVE_BEACH -> {
                actions.add(Action.SUMMON);
                actions.add(Action.BEACH_SHARK);
                actions.add(Action.BEACH_TURBINE);
            }
        }
        Action selected = actions.get(random.nextInt(actions.size()));
        if (selected == lastStartedAction && actions.size() > 1) {
            selected = actions.get((actions.indexOf(selected) + 1 + random.nextInt(actions.size() - 1)) % actions.size());
        }
        lastStartedAction = selected;
        switch (selected) {
            case MOVE -> startMove(level);
            case SUMMON -> startSummon(level);
            case EGYPT_MISSILE -> startEgyptMissile(level);
            case EGYPT_CHARGE -> startEgyptCharge(level);
            case DARK_FIREBALL -> startDarkFireball(level);
            case DARK_BREATH -> startDarkBreath(level);
            case FROST_MISSILE -> startFrostMissile(level);
            case FROST_WIND -> startFrostWind(level);
            case FROST_COLUMN -> startFrostColumn(level);
            case BEACH_SHARK -> startBeachShark(level);
            case BEACH_TURBINE -> startBeachTurbine(level);
            default -> finishAction();
        }
    }

    private void startMove(Level level) {
        clearTargets();
        setBossLane(level, randomBossLane(level));
        action = Action.MOVE;
        actionDuration = actionTimer = 0.8f;
        setCurrentState(ZombieState.WALKING);
    }

    private void startSummon(Level level) {
        clearTargets();
        summonZombies(level);
        action = Action.SUMMON;
        actionDuration = actionTimer = 1.0f;
        setCurrentState(ZombieState.IDLE);
    }

    private void startEgyptMissile(Level level) {
        clearTargets();
        targetCells.add(randomCell(level));
        beginTimedAction(Action.EGYPT_MISSILE, 1.15f, ZombieState.IDLE);
    }

    private void startEgyptCharge(Level level) {
        clearTargets();
        setBossLane(level, randomBossLane(level));
        chargeTargetX = Math.max(2.2, Math.min(4.0, level.getGameMap().getColumns() * 0.40));
        action = Action.EGYPT_CHARGE;
        actionDuration = actionTimer = 2.2f;
        actionResolved = false;
        setCurrentState(ZombieState.RUNNING);
    }

    private void startDarkFireball(Level level) {
        clearTargets();
        int count = phase >= 2 ? 3 : 2;
        targetCells.addAll(randomUniqueCells(level, count));
        beginTimedAction(Action.DARK_FIREBALL, 1.35f, ZombieState.IDLE);
    }

    private void startDarkBreath(Level level) {
        clearTargets();
        setBossLane(level, randomBossLane(level));
        targetRowA = getOccupiedRowA();
        targetRowB = getOccupiedRowB();
        beginTimedAction(Action.DARK_BREATH, 1.25f, ZombieState.IDLE);
    }

    private void startFrostMissile(Level level) {
        clearTargets();
        targetCells.add(randomCell(level));
        beginTimedAction(Action.FROST_MISSILE, 1.15f, ZombieState.IDLE);
    }

    private void startFrostWind(Level level) {
        clearTargets();
        int rows = Math.max(1, level.getGameMap().getRows());
        targetRowA = 1 + random.nextInt(rows);
        do {
            targetRowB = 1 + random.nextInt(rows);
        } while (rows > 1 && targetRowB == targetRowA);
        beginTimedAction(Action.FROST_WIND, 1.1f, ZombieState.IDLE);
    }

    private void startFrostColumn(Level level) {
        clearTargets();
        int column = 1 + random.nextInt(Math.max(1, level.getGameMap().getColumns()));
        for (int row = 1; row <= level.getGameMap().getRows(); row++) targetCells.add(new TargetCell(column, row));
        beginTimedAction(Action.FROST_COLUMN, 1.1f, ZombieState.IDLE);
    }

    private void startBeachShark(Level level) {
        clearTargets();
        List<Plant> waterPlants = new ArrayList<>();
        for (Plant plant : level.getActivePlants()) {
            if (plant == null || plant.isDead()) continue;
            Tile tile = level.getGameMap().getTile(plant.getX(), plant.getY());
            if (tile != null && tile.getType() == TileType.WATER) waterPlants.add(plant);
        }
        if (waterPlants.isEmpty()) {
            startMove(level);
            return;
        }
        Collections.shuffle(waterPlants, random);
        int count = Math.min(waterPlants.size(), phase >= 2 ? 3 : 2);
        for (int i = 0; i < count; i++) {
            Plant target = waterPlants.get(i);
            targetCells.add(new TargetCell(target.getX(), target.getY()));
        }
        beginTimedAction(Action.BEACH_SHARK, 1.25f, ZombieState.IDLE);
    }

    private void startBeachTurbine(Level level) {
        clearTargets();
        setBossLane(level, randomBossLane(level));
        targetRowA = getOccupiedRowA();
        targetRowB = getOccupiedRowB();
        action = Action.BEACH_TURBINE;
        actionDuration = actionTimer = 1.85f;
        actionResolved = false;
        turbineStepTimer = 0.12f;
        setCurrentState(ZombieState.IDLE);
    }

    private void beginTimedAction(Action nextAction, float duration, ZombieState state) {
        action = nextAction;
        actionDuration = actionTimer = duration;
        actionResolved = false;
        setCurrentState(state);
    }

    private void resolveEgyptMissile(Level level) {
        if (!targetCells.isEmpty()) killPlantAt(level, targetCells.get(0));
        createRandomGraves(level, 2);
    }

    private void resolveDarkFireballs(Level level) {
        for (TargetCell cell : targetCells) {
            killPlantAt(level, cell);
            Tile tile = level.getGameMap().getTile(cell.getColumn(), cell.getRow());
            if (tile != null) tile.setBurning(4f);
            spawnDragonImp(level, cell);
        }
    }

    private void resolveDarkBreath(Level level) {
        killPlantsInRows(level, targetRowA, targetRowB);
        for (int row : new int[]{targetRowA, targetRowB}) {
            for (int column = 1; column <= level.getGameMap().getColumns(); column++) {
                Tile tile = level.getGameMap().getTile(column, row);
                if (tile != null) tile.setBurning(4f);
            }
        }
    }

    private void resolveFrostMissile(Level level) {
        if (!targetCells.isEmpty()) killPlantAt(level, targetCells.get(0));
    }

    private void resolveFrostWind(Level level) {
        for (Plant plant : level.getActivePlants()) {
            if (plant == null || plant.isDead()) continue;
            if (plant.getY() == targetRowA || plant.getY() == targetRowB) plant.addFreezeLevel(1);
        }
        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie == this || zombie.isDead()) continue;
            if (zombie.getY() == targetRowA || zombie.getY() == targetRowB) zombie.setChilled(true);
        }
    }

    private void resolveFrostColumn(Level level) {
        if (targetCells.isEmpty()) return;
        int column = targetCells.get(0).getColumn();
        for (Plant plant : level.getActivePlants()) {
            if (plant != null && !plant.isDead() && plant.getX() == column) plant.addFreezeLevel(3);
        }
        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie == this || zombie.isDead()) continue;
            if ((int) Math.round(zombie.getX()) == column) {
                zombie.setFrozenInBlock(true);
                zombie.setBlockIceHP(60);
            }
        }
        spawnFrozenColumnZombies(level, column);
    }

    private void spawnFrozenColumnZombies(Level level, int column) {
        ZombieData data = findFallbackZombie(level);
        if (data == null) return;
        List<Integer> rows = new ArrayList<>();
        for (int row = 1; row <= level.getGameMap().getRows(); row++) {
            boolean occupied = false;
            for (Zombie zombie : level.getActiveZombies()) {
                if (zombie != this && !zombie.isDead() && zombie.getY() == row && Math.abs(zombie.getX() - column) < 0.55) {
                    occupied = true;
                    break;
                }
            }
            if (!occupied) rows.add(row);
        }
        Collections.shuffle(rows, random);
        int count = Math.min(rows.size(), phase >= 3 ? 3 : 2);
        for (int i = 0; i < count; i++) {
            Zombie zombie = new Zombie(data, column, rows.get(i));
            zombie.setFrozenInBlock(true);
            zombie.setBlockIceHP(60);
            level.addActiveZombie(zombie);
            unlockZombie(data);
        }
    }

    private void resolveBeachShark(Level level) {
        for (TargetCell target : targetCells) killPlantAt(level, target);
    }

    private void pullBeachEntities(Level level) {
        List<Plant> plants = new ArrayList<>();
        for (Plant plant : level.getActivePlants()) {
            if (plant != null && !plant.isDead() && isTargetRow(plant.getY())) plants.add(plant);
        }
        plants.sort(Comparator.comparingInt(Plant::getX).reversed());
        for (Plant plant : plants) pullPlantOneColumn(level, plant);
        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie == this || zombie.isDead() || !isTargetRow(zombie.getY())) continue;
            zombie.setX(Math.min(homeX - 0.25, zombie.getX() + 0.65));
            if (zombie.getX() >= homeX - 0.35) zombie.setCurrentHp(0);
        }
    }

    private void pullPlantOneColumn(Level level, Plant plant) {
        int oldColumn = plant.getX();
        int row = plant.getY();
        int newColumn = oldColumn + 1;
        if (newColumn > level.getGameMap().getColumns()) {
            plant.setCurrentHp(0);
            return;
        }
        Tile oldTile = level.getGameMap().getTile(oldColumn, row);
        Tile nextTile = level.getGameMap().getTile(newColumn, row);
        if (nextTile == null || nextTile.getPlant() != null || nextTile.isGrave() || nextTile.isBurning()) return;
        if (!nextTile.isPlantable(plant)) return;
        if (oldTile != null && oldTile.getPlant() == plant) oldTile.removePlant();
        nextTile.setPlant(plant);
        if (nextTile.getPlant() == plant) plant.setPosition(newColumn, row);
        else if (oldTile != null) oldTile.setPlant(plant);
    }

    private void swallowBeachRows(Level level) {
        killPlantsInRows(level, targetRowA, targetRowB);
        for (Zombie zombie : level.getActiveZombies()) {
            if (zombie != this && !zombie.isDead() && isTargetRow(zombie.getY())) zombie.setCurrentHp(0);
        }
    }

    private boolean isTargetRow(int row) {
        return row == targetRowA || row == targetRowB;
    }

    private void killPlantAt(Level level, TargetCell cell) {
        Plant plant = level.getPlantAt(cell.getColumn(), cell.getRow());
        if (plant != null) plant.setCurrentHp(0);
    }

    private void killPlantsInRows(Level level, int rowA, int rowB) {
        for (Plant plant : level.getActivePlants()) {
            if (plant != null && !plant.isDead() && (plant.getY() == rowA || plant.getY() == rowB)) plant.setCurrentHp(0);
        }
    }

    private void createRandomGraves(Level level, int count) {
        List<Tile> candidates = new ArrayList<>();
        GameMap map = level.getGameMap();
        for (int row = 1; row <= map.getRows(); row++) {
            for (int column = 1; column <= map.getColumns(); column++) {
                Tile tile = map.getTile(column, row);
                if (tile != null && tile.getType() == TileType.NORMAL && tile.getPlant() == null && !tile.isGrave() && !tile.isBurning()) {
                    candidates.add(tile);
                }
            }
        }
        Collections.shuffle(candidates, random);
        for (int i = 0; i < Math.min(count, candidates.size()); i++) candidates.get(i).setGrave(true, Tile.GraveReward.NONE);
    }

    private void spawnDragonImp(Level level, TargetCell cell) {
        ZombieData data = ZombieRepository.getInstance().findById("ZombieDarkImpDragon");
        if (data == null) data = findFallbackZombie(level);
        if (data == null) return;
        Zombie zombie = new Zombie(data, cell.getColumn(), cell.getRow());
        level.addActiveZombie(zombie);
        unlockZombie(data);
    }

    private void summonZombies(Level level) {
        List<ZombieData> candidates = getSummonCandidates(level);
        if (candidates.isEmpty()) return;
        int count = phase >= 3 ? 3 : 2;
        for (int i = 0; i < count; i++) {
            ZombieData chosen = candidates.get(random.nextInt(candidates.size()));
            int lane = 1 + random.nextInt(Math.max(1, level.getGameMap().getRows()));
            double x = level.getGameMap().getColumns() + 0.85 + i * 0.18;
            Zombie zombie = new Zombie(chosen, x, lane);
            level.addActiveZombie(zombie);
            unlockZombie(chosen);
        }
    }

    private List<ZombieData> getSummonCandidates(Level level) {
        List<ZombieData> candidates = new ArrayList<>();
        if (level.getCurrentSeason() == null || level.getCurrentSeason().getAllowedZombies() == null) return candidates;
        for (ZombieData data : level.getCurrentSeason().getAllowedZombies()) {
            if (data == null || data.getWaveCost() <= 0) continue;
            String id = data.getId() == null ? "" : data.getId().toLowerCase();
            String path = data.getPath() == null ? "" : data.getPath().toLowerCase();
            if (id.contains("zomboss") || path.contains("zomboss")) continue;
            candidates.add(data);
        }
        return candidates;
    }

    private ZombieData findFallbackZombie(Level level) {
        List<ZombieData> candidates = getSummonCandidates(level);
        return candidates.isEmpty() ? null : candidates.get(random.nextInt(candidates.size()));
    }

    private void unlockZombie(ZombieData data) {
        if (data != null && App.getCurrentUser() != null) App.getCurrentUser().getCollection().unlockZombie(data.getId());
    }

    private TargetCell randomCell(Level level) {
        return new TargetCell(
            1 + random.nextInt(Math.max(1, level.getGameMap().getColumns())),
            1 + random.nextInt(Math.max(1, level.getGameMap().getRows()))
        );
    }

    private List<TargetCell> randomUniqueCells(Level level, int count) {
        List<TargetCell> all = new ArrayList<>();
        for (int row = 1; row <= level.getGameMap().getRows(); row++) {
            for (int column = 1; column <= level.getGameMap().getColumns(); column++) all.add(new TargetCell(column, row));
        }
        Collections.shuffle(all, random);
        return new ArrayList<>(all.subList(0, Math.min(count, all.size())));
    }

    private int randomBossLane(Level level) {
        int rows = Math.max(1, level.getGameMap().getRows());
        int maxLane = Math.max(1, rows - 2);
        if (maxLane == 1) return 1;
        int lane = 1 + random.nextInt(maxLane);
        if (lane == getY()) lane = lane % maxLane + 1;
        return lane;
    }

    private void setBossLane(Level level, int lane) {
        int rows = Math.max(1, level.getGameMap().getRows());
        setY(Math.max(1, Math.min(Math.max(1, rows - 2), lane)));
    }

    private void clearTargets() {
        targetCells.clear();
        targetRowA = 0;
        targetRowB = 0;
        actionResolved = false;
    }

    private void finishAction() {
        action = Action.IDLE;
        actionTimer = 0f;
        actionDuration = 0f;
        actionResolved = false;
        setCurrentState(ZombieState.IDLE);
        actionCooldown = cooldown();
    }

    private float cooldown() {
        float base = switch (phase) {
            case 1 -> 3.4f;
            case 2 -> 2.8f;
            default -> 2.25f;
        };
        return base + random.nextFloat() * 0.9f;
    }

    @Override
    public void takeDamage(int damage, Plant killerPlant) {
        if (damage <= 0 || isDead() || stunTimer > 0f) return;
        int max = Math.max(1, getMaxHp());
        int floor = phase == 1 ? max * 2 / 3 : phase == 2 ? max / 3 : 0;
        int allowed = floor > 0 ? Math.max(0, getCurrentHp() - floor) : damage;
        int applied = floor > 0 ? Math.min(damage, allowed) : damage;
        if (applied <= 0 && floor > 0) {
            startStun();
            return;
        }
        super.takeDamage(applied, killerPlant);
        if (floor > 0 && getCurrentHp() <= floor && !isDead()) {
            setCurrentHp(floor);
            phase++;
            startStun();
        }
    }

    private void startStun() {
        stunTimer = 1.25f;
        action = Action.STUNNED;
        actionTimer = 0f;
        actionDuration = 0f;
        actionResolved = false;
        setCurrentState(ZombieState.IDLE);
    }

    public double horizontalDistanceTo(double x) {
        double left = getX() - 2.2;
        double right = getX() + 0.35;
        if (x < left) return left - x;
        if (x > right) return x - right;
        return 0;
    }

    public boolean occupiesLane(int lane) {
        return lane == getOccupiedRowA() || lane == getOccupiedRowB();
    }

    public int getOccupiedRowA() {
        return getY();
    }

    public int getOccupiedRowB() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        int rows = level == null ? 5 : Math.max(1, level.getGameMap().getRows());
        return Math.min(rows, getY() + 1);
    }

    public boolean isStunned() {
        return stunTimer > 0f;
    }

    public int getPhase() {
        return phase;
    }

    public Action getAction() {
        return action;
    }

    public SeasonType getSeason() {
        return season;
    }

    public List<TargetCell> getTargetCells() {
        return Collections.unmodifiableList(targetCells);
    }

    public int getTargetRowA() {
        return targetRowA;
    }

    public int getTargetRowB() {
        return targetRowB;
    }

    public boolean isActionResolved() {
        return actionResolved;
    }

    public float getEffectTimer() {
        return effectTimer;
    }

    public float getActionProgress() {
        if (actionDuration <= 0f) return 0f;
        return Math.max(0f, Math.min(1f, 1f - actionTimer / actionDuration));
    }

    private interface LevelAction {
        void run(Level level);
    }
}
