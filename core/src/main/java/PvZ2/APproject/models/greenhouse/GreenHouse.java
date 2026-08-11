package PvZ2.APproject.models.greenhouse;

import PvZ2.APproject.models.App;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.utils.AssetPaths;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.util.*;

public class GreenHouse {
    private static final String FILE_PATH = "Greenhouse.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public Map<String, Integer> greenhouse_currency = new HashMap<>();
    public Map<String, Boolean> stored_boosts = new HashMap<>();
    private final GreenHousePot[][] grid = new GreenHousePot[4][5];

    public GreenHouse() {
        greenhouse_currency.putIfAbsent("coins", 0);
        greenhouse_currency.putIfAbsent("diamonds", 0);
        for (int y = 1; y <= 4; y++) for (int x = 1; x <= 5; x++) if (grid[y - 1][x - 1] == null) grid[y - 1][x - 1] = new GreenHousePot(x, y, y > 1);
    }


    public GreenHousePot getPot(int x, int y) {
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

        Random random = new Random();
        List<PlantData> boostablePlants = new ArrayList<>();
        for (String plantId : unlockedPlants) {
            PlantData plant = PlantRepository.getInstance().findById(plantId);
            if (plant != null && plant.getPlantFoodAbilities() != null && !plant.getPlantFoodAbilities().isEmpty()) {
                boostablePlants.add(plant);
            }
        }
        boolean isMarigold = random.nextBoolean();
        if (isMarigold || boostablePlants.isEmpty()) {
            pot.plant_type = "marigold";
            pot.growth_duration_hours = 2;
        } else {
            PlantData plant = boostablePlants.get(random.nextInt(boostablePlants.size()));
            pot.plant_type = plant.getName().toLowerCase();
            pot.growth_duration_hours = 8;
        }

        pot.status = "GROWING";
        pot.planted_timestamp = System.currentTimeMillis() / 1000;

        System.out.println("Planted " + pot.plant_type + " at (" + x + "," + y + ").");
    }

    public void collect(int x, int y) {
        GreenHousePot pot = getPot(x, y);

        if (pot == null || pot.is_locked || "EMPTY".equals(pot.status)) {
            System.out.println("Error: Nothing to harvest here.");
            return;
        }

        if (!pot.isReady()) {
            System.out.println("Error: Plant is not ready for harvest yet!");
            return;
        }

        if ("marigold".equalsIgnoreCase(pot.plant_type)) {
            if (App.getCurrentUser() != null) App.getCurrentUser().addCoins(500);
            System.out.println("Collected Marigold! Earned 500 coins.");
        } else {
            if (!stored_boosts.getOrDefault(pot.plant_type, false)) {
                stored_boosts.put(pot.plant_type, true);
                System.out.println("Collected " + pot.plant_type + "! Boost saved for next match.");
            } else {
                System.out.println("Collected " + pot.plant_type + "! Boost already stored.");
            }
        }

        pot.status = "EMPTY";
        pot.plant_type = null;
        pot.planted_timestamp = null;
        pot.growth_duration_hours = 0;
    }

    public void grow(int x, int y) {
        GreenHousePot pot = getPot(x, y);

        if (pot == null || pot.is_locked || "EMPTY".equals(pot.status)) {
            System.out.println("Error: Cannot speed up growth on this pot.");
            return;
        }

        if (pot.isReady()) {
            System.out.println("Error: Plant is already fully grown!");
            return;
        }

        long now = System.currentTimeMillis() / 1000;
        double elapsedHours = (now - pot.planted_timestamp) / 3600.0;
        double remainingHours = pot.growth_duration_hours - elapsedHours;

        int diamondCost = (int) Math.ceil(remainingHours);
        int currentDiamonds = App.getCurrentUser() == null ? 0 : App.getCurrentUser().getGemsCount();

        if (currentDiamonds < diamondCost) {
            System.out.println("Error: Not enough diamonds. Cost: " + diamondCost);
            return;
        }

        App.getCurrentUser().setGemsCount(currentDiamonds - diamondCost);
        pot.planted_timestamp = now - (pot.growth_duration_hours * 3600L);

        System.out.println("Speed up successful! Spent " + diamondCost + " diamonds.");
    }

    public void showGreenhouse() {
        for (int y = 1; y <= 4; y++) {
            for (int x = 1; x <= 5; x++) {
                GreenHousePot pot = getPot(x, y);

                System.out.print("(" + x + "," + y + "): ");

                if (pot == null) {
                    System.out.print("[INVALID]");
                } else if (pot.is_locked) {
                    System.out.print("[LOCKED]");
                } else if ("EMPTY".equals(pot.status)) {
                    System.out.print("[EMPTY]");
                } else if (pot.isReady()) {
                    System.out.print("[" + pot.plant_type + " - READY]");
                } else {
                    long now = System.currentTimeMillis() / 1000;
                    long elapsedSeconds = now - pot.planted_timestamp;
                    long totalDurationSeconds = pot.growth_duration_hours * 3600L;
                    long remainingSeconds = Math.max(0, totalDurationSeconds - elapsedSeconds);

                    long hours = remainingSeconds / 3600;
                    long minutes = (remainingSeconds % 3600) / 60;
                    long seconds = remainingSeconds % 60;

                    System.out.printf("[%s - %02dh %02dm %02ds remaining]",
                            pot.plant_type, hours, minutes, seconds);
                }
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    public static GreenHouse loadFromFile() {
        try (Reader reader = AssetPaths.reader(FILE_PATH)) {
            GreenhouseDataDTO data = gson.fromJson(reader, GreenhouseDataDTO.class);

            GreenHouse greenhouse = new GreenHouse();
            greenhouse.greenhouse_currency = data.greenhouse_currency;
            greenhouse.stored_boosts = data.stored_boosts;

            for (GreenHousePot p : data.pots) {
                greenhouse.grid[p.y - 1][p.x - 1] = p;
            }
            return greenhouse;
        } catch (IOException e) {
            System.out.println("No existing save found. Creating new Greenhouse grid...");
            return createNewGreenhouse();
        }
    }

    public void saveToFile() {
        GreenhouseDataDTO data = new GreenhouseDataDTO();
        data.greenhouse_currency = this.greenhouse_currency;
        data.stored_boosts = this.stored_boosts;

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 5; col++) {
                data.pots.add(grid[row][col]);
            }
        }

        try (Writer writer = AssetPaths.writer(FILE_PATH)) {
            gson.toJson(data, writer);
            System.out.println("Game saved to " + AssetPaths.resolve(FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static GreenHouse createNewGreenhouse() {
        GreenHouse gh = new GreenHouse();
        for (int y = 1; y <= 4; y++) {
            for (int x = 1; x <= 5; x++) {
                boolean locked = (y > 1);
                gh.grid[y - 1][x - 1] = new GreenHousePot(x, y, locked);
            }
        }
        return gh;
    }

    private static class GreenhouseDataDTO {
        Map<String, Integer> greenhouse_currency = new HashMap<>();
        Map<String, Boolean> stored_boosts = new HashMap<>();
        List<GreenHousePot> pots = new ArrayList<>();
    }

    public int getPotsCount(){
        int count = 0;
        for (GreenHousePot[] row : grid) {
            for (GreenHousePot pot : row) {
                if (pot != null && !pot.is_locked) {
                    count++;
                }
            }
        }

        return count;
    }
    public int unlockPots(int count) {
        int unlocked = 0;
        for (GreenHousePot[] row : grid) for (GreenHousePot pot : row) if (pot.is_locked && unlocked < count) { pot.is_locked = false; unlocked++; }
        return unlocked;
    }

    public int getPotXOrY(String z) {
        for (GreenHousePot[] row : grid) {
            for (GreenHousePot pot : row) {
                if (pot.is_locked) {
                    if (z.matches("x")) {
                        return pot.x;
                    } else {
                        return pot.y;
                    }
                }
            }
        }
        return -1;
    }
}
