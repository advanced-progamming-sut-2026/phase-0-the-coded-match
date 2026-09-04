package PvZ2.APproject.models.MiniGameRelated;

import PvZ2.APproject.controllers.MiniGameController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.LevelType;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.LevelData;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IZombie extends MiniGame {
    private static final int PLACEMENT_COOLDOWN_TICKS = 20;
    private final int stageNumber;
    private int sunAmount;
    private final Boolean[] brainsEatenInLane;
    private final double redLineCoordinateX = 6;
    private final List<String> availableZombies;
    private final Map<String, Integer> zombieCooldowns;

    public IZombie(int stageNumber) {
        super(createIZombieLevelData(stageNumber));
        this.stageNumber = stageNumber;
        this.sunAmount = 350 + stageNumber * 150;
        this.brainsEatenInLane = new Boolean[5];
        java.util.Arrays.fill(this.brainsEatenInLane, false);
        this.availableZombies = new ArrayList<>();
        this.zombieCooldowns = new HashMap<>();
        isGameOver = false;
        setupStage(stageNumber);
    }

    private static LevelData createIZombieLevelData(int stageNumber) {
        LevelData data = new LevelData();
        data.setLevelNumber(stageNumber);
        data.setLevelType(LevelType.I_ZOMBIE);
        data.setUnlocked(true);
        data.setMap(new GameMap(5, 9));
        return data;
    }

    private void setupStage(int stage) {
        PlantRepository plants = PlantRepository.getInstance();
        switch (stage) {
            case 1 -> setUpStage1(plants);
            case 2 -> setUpStage2(plants);
            case 3 -> setUpStage3(plants);
            default -> throw new IllegalArgumentException("I, Zombie stage must be between 1 and 3");
        }
        for (Tile[] row : getGameMap().getGrid()) {
            for (Tile tile : row) {
                if (tile.getPlant() != null) addActivePlants(tile.getPlant());
            }
        }
    }

    public void setUpStage1(PlantRepository plants) {
        availableZombies.add("Default");
        availableZombies.add("Gargantuar");
        availableZombies.add("Buckethead Zombie");
        availableZombies.add("Knight Zombie");
        availableZombies.add("Brickhead Zombie");
        for (int row = 1; row <= 5; row++) {
            setPlant(plants, "Peashooter", 2, row);
            setPlant(plants, "Wall-nut", 5, row);
        }
        setPlant(plants, "Cabbage-pult", 3, 2);
        setPlant(plants, "Cabbage-pult", 3, 4);
        setPlant(plants, "Cabbage-pult", 4, 3);
    }

    public void setUpStage2(PlantRepository plants) {
        availableZombies.add("Default");
        availableZombies.add("Imp");
        availableZombies.add("AllStar");
        availableZombies.add("Arcade");
        availableZombies.add("Parasol Zombie");
        for (int row = 1; row <= 5; row++) {
            setPlant(plants, "Peashooter", 2, row);
            setPlant(plants, "Cabbage-pult", 3, row);
            setPlant(plants, "Wall-nut", 5, row);
        }
        setPlant(plants, "Bonk Choy", 4, 2);
        setPlant(plants, "Bonk Choy", 4, 4);
    }

    public void setUpStage3(PlantRepository plants) {
        availableZombies.add("Default");
        availableZombies.add("Conehead Zombie");
        availableZombies.add("AllStar");
        availableZombies.add("Parasol Zombie");
        availableZombies.add("Buckethead Zombie");
        for (int row = 1; row <= 5; row++) {
            setPlant(plants, "Peashooter", 2, row);
            setPlant(plants, "Cabbage-pult", 3, row);
            setPlant(plants, "Cabbage-pult", 4, row);
            setPlant(plants, "Wall-nut", 5, row);
        }
    }

    private void setPlant(PlantRepository repository, String name, int column, int row) {
        if (repository.findByName(name) == null) return;
        getGameMap().getTile(column, row).setPlant(new Plant(repository.findByName(name), column, row, 1));
    }

    public void Update() {
        for (String key : new ArrayList<>(zombieCooldowns.keySet())) {
            int value = Math.max(0, zombieCooldowns.getOrDefault(key, 0) - 1);
            zombieCooldowns.put(key, value);
        }
        for (Zombie zombie : new ArrayList<>(getActiveZombies())) {
            if (zombie.getX() <= 0.85) {
                eatBrainAtRow(zombie.getY());
                getActiveZombies().remove(zombie);
                continue;
            }
            if (zombie.isSunProduced()) {
                addSun();
                zombie.setSunProduced(false);
            }
        }
        MiniGameController.verifyWinLossConditions();
    }

    public String placeZombie(String input) {
        Pattern pattern = Pattern.compile(Commands.PLACE_ZOMBIE.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) return "invalid command";
        return placeZombie(matcher.group("name"), Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")));
    }

    public String placeZombie(String zombieName, int col, int row) {
        if (row < 1 || row > 5 || col < 1 || col > 9) return "invalid location";
        if (col < redLineCoordinateX) return "place zombies on the right side of the red line";
        ZombieData zombieData = getZombieDataFromAvailable(zombieName);
        if (zombieData == null) return "this zombie is not available";
        String key = normalize(zombieData.getDisplayName());
        if (zombieCooldowns.getOrDefault(key, 0) > 0) return "zombie is on cooldown";
        if (sunAmount < zombieData.getCost()) return "not enough sun";
        sunAmount -= zombieData.getCost();
        Zombie newZombie = new Zombie(zombieData, col, row);
        getActiveZombies().add(newZombie);
        zombieCooldowns.put(key, PLACEMENT_COOLDOWN_TICKS);
        return null;
    }

    public void eatBrainAtRow(int row) {
        if (row >= 1 && row <= brainsEatenInLane.length) brainsEatenInLane[row - 1] = true;
    }

    private ZombieData getZombieDataFromAvailable(String name) {
        boolean available = false;
        for (String z : availableZombies) {
            if (z.equalsIgnoreCase(name)) {
                available = true;
                break;
            }
        }
        if (!available) return null;
        ZombieRepository repository = ZombieRepository.getInstance();
        ZombieData direct = repository.findByDisplayName(name);
        if (direct != null) return direct;
        String wanted = normalize(name);
        for (ZombieData data : repository.getAllZombies()) {
            if (data == null) continue;
            String display = normalize(data.getDisplayName());
            String id = normalize(data.getId());
            if (display.equals(wanted) || id.equals(wanted) || id.endsWith(wanted) || display.endsWith(wanted)) {
                return data;
            }
        }
        return null;
    }

    public ZombieData getAvailableZombieData(String name) {
        return getZombieDataFromAvailable(name);
    }

    public int getCheapestAvailableZombieCost() {
        int cheapest = Integer.MAX_VALUE;
        for (String name : availableZombies) {
            ZombieData data = ZombieRepository.getInstance().findByDisplayName(name);
            if (data != null && data.getCost() > 0) cheapest = Math.min(cheapest, data.getCost());
        }
        return cheapest == Integer.MAX_VALUE ? 0 : cheapest;
    }

    public int getZombieCooldown(String name) {
        ZombieData data = getZombieDataFromAvailable(name);
        if (data == null) return 0;
        return zombieCooldowns.getOrDefault(normalize(data.getDisplayName()), 0);
    }

    public List<String> getAvailableZombies() {
        return Collections.unmodifiableList(availableZombies);
    }

    public boolean isBrainEaten(int row) {
        return row >= 1 && row <= brainsEatenInLane.length && Boolean.TRUE.equals(brainsEatenInLane[row - 1]);
    }

    public boolean allBrainsEaten() {
        for (Boolean eaten : brainsEatenInLane) if (!Boolean.TRUE.equals(eaten)) return false;
        return true;
    }

    public int getSunAmount() { return sunAmount; }
    public void addSun() { this.sunAmount += 50; }
    public int getStageNumber() { return stageNumber; }
    public double getRedLineCoordinateX() { return redLineCoordinateX; }

    private String normalize(String value) {
        return value == null ? "" : value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }
}
