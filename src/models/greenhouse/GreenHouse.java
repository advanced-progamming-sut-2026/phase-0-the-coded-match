package models.greenhouse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.App;
import models.plants.PlantData;
import models.plants.PlantRepository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GreenHouse {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Random RANDOM = new Random();
    public Map<String, Integer> greenhouse_currency = new HashMap<>();
    public Map<String, Boolean> stored_boosts = new HashMap<>();
    private GreenHousePot[][] grid;

    public GreenHouse() {
        initializeGrid();
    }

    private void initializeGrid() {
        if (grid == null || grid.length != 4 || grid[0].length != 5) {
            grid = new GreenHousePot[4][5];
        }
        for (int y = 1; y <= 4; y++) {
            for (int x = 1; x <= 5; x++) {
                if (grid[y - 1][x - 1] == null) {
                    grid[y - 1][x - 1] = new GreenHousePot(x, y, y > 1);
                } else if (grid[y - 1][x - 1].status == null) {
                    grid[y - 1][x - 1].status = grid[y - 1][x - 1].is_locked ? "LOCKED" : "EMPTY";
                }
            }
        }
        if (greenhouse_currency == null) {
            greenhouse_currency = new HashMap<>();
        }
        if (stored_boosts == null) {
            stored_boosts = new HashMap<>();
        }
    }

    public GreenHousePot getPot(int x, int y) {
        initializeGrid();
        if (x < 1 || x > 5 || y < 1 || y > 4) {
            return null;
        }
        return grid[y - 1][x - 1];
    }

    public void plantPot(int x, int y, List<String> unlockedPlants) {
        GreenHousePot pot = getPot(x, y);
        if (pot == null) {
            System.out.println("Error: Invalid pot coordinates!");
            return;
        }
        if (pot.is_locked) {
            System.out.println("Error: Pot at (" + x + "," + y + ") is locked!");
            return;
        }
        if (!"EMPTY".equals(pot.status)) {
            System.out.println("Error: Pot at (" + x + "," + y + ") is already occupied!");
            return;
        }
        List<String> eligiblePlants = new ArrayList<>();
        if (unlockedPlants != null) {
            for (String plantName : unlockedPlants) {
                PlantData data = PlantRepository.getInstance().findByName(plantName);
                if (data == null) {
                    data = PlantRepository.getInstance().findById(plantName);
                }
                if (data != null && data.getPlantFoodAbilities() != null && !data.getPlantFoodAbilities().isEmpty()) {
                    eligiblePlants.add(data.getName());
                }
            }
        }
        if (RANDOM.nextBoolean() || eligiblePlants.isEmpty()) {
            pot.plant_type = "marigold";
            pot.growth_duration_hours = 2;
        } else {
            pot.plant_type = eligiblePlants.get(RANDOM.nextInt(eligiblePlants.size()));
            pot.growth_duration_hours = 8;
        }
        pot.status = "GROWING";
        pot.planted_timestamp = System.currentTimeMillis() / 1000;
        System.out.println("Planted " + pot.plant_type + " at (" + x + "," + y + ").");
    }

    public void collect(int x, int y) {
        GreenHousePot pot = getPot(x, y);
        if (pot == null || pot.is_locked || "EMPTY".equals(pot.status) || "LOCKED".equals(pot.status)) {
            System.out.println("Error: Nothing to harvest here.");
            return;
        }
        if (!pot.isReady()) {
            System.out.println("Error: Plant is not ready for harvest yet!");
            return;
        }
        if ("marigold".equalsIgnoreCase(pot.plant_type)) {
            if (App.getCurrentUser() != null) {
                App.getCurrentUser().addCoins(500);
            }
            System.out.println("Collected Marigold! Earned 500 coins.");
        } else {
            boolean alreadyStored = hasStoredBoost(pot.plant_type);
            storeBoost(pot.plant_type);
            System.out.println(alreadyStored ? "Collected " + pot.plant_type + "! Boost already stored."
                    : "Collected " + pot.plant_type + "! Boost saved for next match.");
        }
        clearPot(pot);
    }

    public void grow(int x, int y) {
        GreenHousePot pot = getPot(x, y);
        if (pot == null || pot.is_locked || "EMPTY".equals(pot.status) || "LOCKED".equals(pot.status)) {
            System.out.println("Error: Cannot speed up growth on this pot.");
            return;
        }
        if (pot.isReady()) {
            System.out.println("Error: Plant is already fully grown!");
            return;
        }
        long now = System.currentTimeMillis() / 1000;
        double remainingHours = pot.growth_duration_hours - (now - pot.planted_timestamp) / 3600.0;
        int diamondCost = Math.max(1, (int) Math.ceil(remainingHours));
        if (App.getCurrentUser() == null || App.getCurrentUser().getGemsCount() < diamondCost) {
            System.out.println("Error: Not enough diamonds. Cost: " + diamondCost);
            return;
        }
        App.getCurrentUser().setGemsCount(App.getCurrentUser().getGemsCount() - diamondCost);
        pot.planted_timestamp = now - pot.growth_duration_hours * 3600L;
        System.out.println("Speed up successful! Spent " + diamondCost + " diamonds.");
    }

    public boolean unlockPot(int x, int y) {
        GreenHousePot pot = getPot(x, y);
        if (pot == null || !pot.is_locked) {
            return false;
        }
        pot.is_locked = false;
        pot.status = "EMPTY";
        return true;
    }

    public boolean unlockNextPot() {
        initializeGrid();
        for (int y = 2; y <= 4; y++) {
            for (int x = 1; x <= 5; x++) {
                if (unlockPot(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void storeBoost(String plantName) {
        initializeGrid();
        if (plantName != null) {
            stored_boosts.put(plantName.toLowerCase(), true);
        }
    }

    public boolean hasStoredBoost(String plantName) {
        initializeGrid();
        return plantName != null && stored_boosts.getOrDefault(plantName.toLowerCase(), false);
    }

    public boolean consumeStoredBoost(String plantName) {
        if (!hasStoredBoost(plantName)) {
            return false;
        }
        stored_boosts.put(plantName.toLowerCase(), false);
        return true;
    }

    public void showGreenhouse() {
        initializeGrid();
        for (int y = 1; y <= 4; y++) {
            for (int x = 1; x <= 5; x++) {
                GreenHousePot pot = getPot(x, y);
                System.out.print("(" + x + "," + y + "): ");
                if (pot.is_locked) {
                    System.out.print("[LOCKED]");
                } else if ("EMPTY".equals(pot.status)) {
                    System.out.print("[EMPTY]");
                } else if (pot.isReady()) {
                    System.out.print("[" + pot.plant_type + " - READY]");
                } else {
                    long remainingSeconds = Math.max(0, pot.growth_duration_hours * 3600L
                            - (System.currentTimeMillis() / 1000 - pot.planted_timestamp));
                    System.out.printf("[%s - %02dh %02dm %02ds remaining]", pot.plant_type,
                            remainingSeconds / 3600, remainingSeconds % 3600 / 60, remainingSeconds % 60);
                }
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    private void clearPot(GreenHousePot pot) {
        pot.status = "EMPTY";
        pot.plant_type = null;
        pot.planted_timestamp = null;
        pot.growth_duration_hours = 0;
    }

    public static GreenHouse loadFromFile() {
        String path = resolvePath("assets/Greenhouse.json");
        try (FileReader reader = new FileReader(path)) {
            GreenhouseDataDTO data = GSON.fromJson(reader, GreenhouseDataDTO.class);
            GreenHouse greenhouse = new GreenHouse();
            if (data != null) {
                greenhouse.greenhouse_currency = data.greenhouse_currency == null ? new HashMap<>() : data.greenhouse_currency;
                greenhouse.stored_boosts = data.stored_boosts == null ? new HashMap<>() : data.stored_boosts;
                if (data.pots != null) {
                    for (GreenHousePot pot : data.pots) {
                        if (pot != null && pot.x >= 1 && pot.x <= 5 && pot.y >= 1 && pot.y <= 4) {
                            greenhouse.grid[pot.y - 1][pot.x - 1] = pot;
                        }
                    }
                }
            }
            greenhouse.initializeGrid();
            return greenhouse;
        } catch (IOException | RuntimeException e) {
            return new GreenHouse();
        }
    }

    public void saveToFile() {
        initializeGrid();
        GreenhouseDataDTO data = new GreenhouseDataDTO();
        data.greenhouse_currency = greenhouse_currency;
        data.stored_boosts = stored_boosts;
        for (GreenHousePot[] row : grid) {
            for (GreenHousePot pot : row) {
                data.pots.add(pot);
            }
        }
        try (FileWriter writer = new FileWriter(resolvePath("assets/Greenhouse.json"))) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.out.println("Error saving greenhouse: " + e.getMessage());
        }
    }

    private static String resolvePath(String path) {
        return new File(path).exists() ? path : "src/" + path;
    }

    public int getPotsCount() {
        initializeGrid();
        int count = 0;
        for (GreenHousePot[] row : grid) {
            for (GreenHousePot pot : row) {
                if (!pot.is_locked) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getPotXOrY(String axis) {
        initializeGrid();
        for (GreenHousePot[] row : grid) {
            for (GreenHousePot pot : row) {
                if (pot.is_locked) {
                    return "x".equalsIgnoreCase(axis) ? pot.x : pot.y;
                }
            }
        }
        return -1;
    }

    private static class GreenhouseDataDTO {
        Map<String, Integer> greenhouse_currency = new HashMap<>();
        Map<String, Boolean> stored_boosts = new HashMap<>();
        List<GreenHousePot> pots = new ArrayList<>();
    }
}
