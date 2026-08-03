package models.MiniGameRelated;

import enums.LevelType;
import enums.TileType;
import models.GameMapRelated.GameMap;
import models.GameMapRelated.Tile;
import models.LevelData;
import models.plants.Plant;
import models.plants.PlantData;
import models.plants.PlantRepository;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Beghouled extends MiniGame {
    private static final int ROWS = 5;
    private static final int COLUMNS = 9;
    private final Plant[][] grid;
    private final boolean[][] craters;
    private final transient Random random = new Random();
    private final int stageNumber;
    private final List<PlantData> plantPool;
    private final Map<String, Upgrade> upgrades = new HashMap<>();
    private int matchCount;
    private int targetMatches;
    private int spawnTimer;
    private boolean won;

    private record Upgrade(String target, int cost) {}

    private static class MatchResult {
        private final boolean[][] cells = new boolean[ROWS][COLUMNS];
        private final List<Integer> lengths = new ArrayList<>();

        private boolean isEmpty() {
            return lengths.isEmpty();
        }
    }

    public Beghouled(int stage) {
        super(createData(stage));
        this.stageNumber = stage;
        this.grid = new Plant[ROWS][COLUMNS];
        this.craters = new boolean[ROWS][COLUMNS];
        this.targetMatches = stage * 10;
        this.matchCount = 0;
        this.spawnTimer = 0;
        this.won = false;
        this.plantPool = createPlantPool();
        initializeUpgrades();
        initializeStage();
    }

    private static LevelData createData(int stage) {
        LevelData data = new LevelData();
        data.setLevelNumber(stage);
        data.setLevelType(LevelType.NORMAL);
        data.setUnlocked(true);
        data.setMap(new GameMap(ROWS, COLUMNS));
        return data;
    }

    private List<PlantData> createPlantPool() {
        PlantRepository repository = PlantRepository.getInstance();
        List<PlantData> result = new ArrayList<>();
        for (String name : List.of("peashooter", "sunflower", "wall-nut", "puff-shroom", "cabbage-pult")) {
            PlantData data = repository.findByName(name);
            if (data != null) result.add(data);
        }
        for (PlantData data : repository.getAllPlants()) {
            if (result.size() >= 5) break;
            if (data != null && !result.contains(data)) result.add(data);
        }
        return result;
    }

    private void initializeUpgrades() {
        upgrades.put(normalize("peashooter"), new Upgrade("repeater", 500));
        upgrades.put(normalize("repeater"), new Upgrade("Mega Gatling Pea", 1500));
        upgrades.put(normalize("wall-nut"), new Upgrade("tall-nut", 500));
        upgrades.put(normalize("puff-shroom"), new Upgrade("Fume-shroom", 250));
        upgrades.put(normalize("cabbage-pult"), new Upgrade("melon-pult", 1000));
        upgrades.put(normalize("melon-pult"), new Upgrade("winter melon", 750));
    }

    public final void initializeStage() {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                craters[row][column] = false;
            }
        }
        resetGrid();
        matchCount = 0;
        isGameOver = false;
        won = false;
        spawnTimer = 0;
        for (int i = 0; i < stageNumber + 1; i++) {
            spawnZombie();
        }
    }

    public void processInteraction() {
        checkRules();
    }

    public boolean swapPlants(int r1, int c1, int r2, int c2) {
        if (isGameOver || !valid(r1, c1) || !valid(r2, c2)) return false;
        if (craters[r1][c1] || craters[r2][c2]) return false;
        if (Math.abs(r1 - r2) + Math.abs(c1 - c2) != 1) return false;
        swap(r1, c1, r2, c2);
        if (findMatches().isEmpty()) {
            swap(r1, c1, r2, c2);
            return false;
        }
        resolveMatches();
        checkRules();
        return true;
    }

    public String upgradePlants(String from, String to) {
        String sourceKey = normalize(from);
        Upgrade upgrade = upgrades.get(sourceKey);
        if (upgrade == null || !normalize(upgrade.target()).equals(normalize(to))) {
            return "invalid upgrade";
        }
        PlantData targetData = PlantRepository.getInstance().findByName(upgrade.target());
        if (targetData == null) {
            return "upgrade target does not exist";
        }
        int count = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                Plant plant = grid[row][column];
                if (plant != null && normalize(plant.getData().getName()).equals(sourceKey)) count++;
            }
        }
        if (count == 0) return "no matching plants found";
        if (getCollectedSunsAmount() < upgrade.cost()) return "not enough suns";
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                Plant plant = grid[row][column];
                if (plant != null && normalize(plant.getData().getName()).equals(sourceKey)) {
                    grid[row][column] = new Plant(targetData, column + 1, row + 1, 1);
                }
            }
        }
        setCollectedSunsAmount(getCollectedSunsAmount() - upgrade.cost());
        syncGrid();
        return count + " plant(s) upgraded";
    }

    public void updateMinigameTick() {
        if (isGameOver) return;
        spawnTimer++;
        int interval = Math.max(25, 70 - stageNumber * 15);
        if (spawnTimer >= interval) {
            spawnTimer = 0;
            spawnZombie();
        }
        checkRules();
    }

    private void spawnZombie() {
        ZombieRepository repository = ZombieRepository.getInstance();
        List<String> types = new ArrayList<>();
        types.add("default");
        if (stageNumber >= 2) types.add("conehead");
        if (stageNumber >= 3) types.add("buckethead");
        ZombieData data = repository.findByDisplayName(types.get(random.nextInt(types.size())));
        if (data != null) {
            addActiveZombie(new Zombie(data, COLUMNS, 1 + random.nextInt(ROWS)));
        }
    }

    public void checkRules() {
        if (matchCount >= targetMatches) {
            won = true;
            isGameOver = true;
            return;
        }
        for (Zombie zombie : getActiveZombies()) {
            if (!zombie.isDead() && zombie.getX() <= 0) {
                won = false;
                isGameOver = true;
                return;
            }
        }
    }

    public void onPlantDestroyed(Plant plant) {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (grid[row][column] == plant) {
                    grid[row][column] = null;
                    craters[row][column] = true;
                    Tile tile = getGameMap().getTile(column + 1, row + 1);
                    if (tile != null) {
                        tile.removePlant();
                        tile.setType(TileType.CRATER);
                    }
                    return;
                }
            }
        }
    }

    private void resolveMatches() {
        int cascade = 0;
        while (cascade < 20) {
            MatchResult result = findMatches();
            if (result.isEmpty()) break;
            int rewardUnits = 0;
            for (int length : result.lengths) {
                rewardUnits += Math.max(1, length - 2);
                if (cascade > 0) rewardUnits++;
            }
            matchCount += result.lengths.size();
            setCollectedSunsAmount(getCollectedSunsAmount() + rewardUnits * 50);
            for (int row = 0; row < ROWS; row++) {
                for (int column = 0; column < COLUMNS; column++) {
                    if (result.cells[row][column]) grid[row][column] = null;
                }
            }
            collapseAndFill();
            cascade++;
        }
        if (!hasPossibleMove()) resetGrid();
        else syncGrid();
    }

    private MatchResult findMatches() {
        MatchResult result = new MatchResult();
        for (int row = 0; row < ROWS; row++) {
            int column = 0;
            while (column < COLUMNS) {
                if (grid[row][column] == null || craters[row][column]) {
                    column++;
                    continue;
                }
                int end = column + 1;
                while (end < COLUMNS && same(grid[row][column], grid[row][end]) && !craters[row][end]) end++;
                int length = end - column;
                if (length >= 3) {
                    result.lengths.add(length);
                    for (int i = column; i < end; i++) result.cells[row][i] = true;
                }
                column = end;
            }
        }
        for (int column = 0; column < COLUMNS; column++) {
            int row = 0;
            while (row < ROWS) {
                if (grid[row][column] == null || craters[row][column]) {
                    row++;
                    continue;
                }
                int end = row + 1;
                while (end < ROWS && same(grid[row][column], grid[end][column]) && !craters[end][column]) end++;
                int length = end - row;
                if (length >= 3) {
                    result.lengths.add(length);
                    for (int i = row; i < end; i++) result.cells[i][column] = true;
                }
                row = end;
            }
        }
        return result;
    }

    private void collapseAndFill() {
        for (int column = 0; column < COLUMNS; column++) {
            int segmentStart = 0;
            for (int row = 0; row <= ROWS; row++) {
                if (row == ROWS || craters[row][column]) {
                    collapseSegment(column, segmentStart, row - 1);
                    segmentStart = row + 1;
                }
            }
        }
    }

    private void collapseSegment(int column, int top, int bottom) {
        if (top > bottom) return;
        List<Plant> existing = new ArrayList<>();
        for (int row = bottom; row >= top; row--) {
            if (grid[row][column] != null) existing.add(grid[row][column]);
        }
        int index = 0;
        for (int row = bottom; row >= top; row--) {
            if (index < existing.size()) grid[row][column] = existing.get(index++);
            else grid[row][column] = randomPlant(column, row);
        }
    }

    private Plant randomPlant(int column, int row) {
        PlantData data = plantPool.get(random.nextInt(plantPool.size()));
        return new Plant(data, column + 1, row + 1, 1);
    }

    private void resetGrid() {
        int attempts = 0;
        do {
            for (int row = 0; row < ROWS; row++) {
                for (int column = 0; column < COLUMNS; column++) {
                    if (craters[row][column]) {
                        grid[row][column] = null;
                        continue;
                    }
                    Plant plant;
                    do {
                        plant = randomPlant(column, row);
                    } while (wouldCreateInitialMatch(row, column, plant));
                    grid[row][column] = plant;
                }
            }
            attempts++;
        } while (!hasPossibleMove() && attempts < 100);
        syncGrid();
    }

    private boolean wouldCreateInitialMatch(int row, int column, Plant plant) {
        if (column >= 2 && same(plant, grid[row][column - 1]) && same(plant, grid[row][column - 2])) return true;
        return row >= 2 && same(plant, grid[row - 1][column]) && same(plant, grid[row - 2][column]);
    }

    private boolean hasPossibleMove() {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (column + 1 < COLUMNS && canSwap(row, column, row, column + 1)) return true;
                if (row + 1 < ROWS && canSwap(row, column, row + 1, column)) return true;
            }
        }
        return false;
    }

    private boolean canSwap(int r1, int c1, int r2, int c2) {
        if (craters[r1][c1] || craters[r2][c2] || grid[r1][c1] == null || grid[r2][c2] == null) return false;
        swap(r1, c1, r2, c2);
        boolean result = !findMatches().isEmpty();
        swap(r1, c1, r2, c2);
        return result;
    }

    private void swap(int r1, int c1, int r2, int c2) {
        Plant temp = grid[r1][c1];
        grid[r1][c1] = grid[r2][c2];
        grid[r2][c2] = temp;
    }

    private void syncGrid() {
        getActivePlants().clear();
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                Tile tile = getGameMap().getTile(column + 1, row + 1);
                if (tile != null) {
                    tile.removePlant();
                    tile.setType(craters[row][column] ? TileType.CRATER : TileType.NORMAL);
                }
                Plant plant = grid[row][column];
                if (plant != null) {
                    plant.setPosition(column + 1, row + 1);
                    getActivePlants().add(plant);
                    if (tile != null) tile.setPlant(plant);
                }
            }
        }
    }

    private boolean same(Plant first, Plant second) {
        return first != null && second != null && first.getData().getId().equalsIgnoreCase(second.getData().getId());
    }

    private boolean valid(int row, int column) {
        return row >= 0 && row < ROWS && column >= 0 && column < COLUMNS;
    }

    private String normalize(String value) {
        return value == null ? "" : value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

    public boolean hasWon() {
        return isGameOver && won;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public int getTargetMatches() {
        return targetMatches;
    }
}
