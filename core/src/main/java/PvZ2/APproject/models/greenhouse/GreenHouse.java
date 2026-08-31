package PvZ2.APproject.models.greenhouse;

import PvZ2.APproject.models.App;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

public class GreenHouse {
    public Map<String, Integer> greenhouseCurrency = new HashMap<>(); //what is this used for???
    public Map<String, Boolean> storedBoosts = new HashMap<>();
    private final GreenHousePot[][] grid = new GreenHousePot[3][4];

    public GreenHouse() {
        greenhouseCurrency.putIfAbsent("coins", 0);
        greenhouseCurrency.putIfAbsent("diamonds", 0);
        for (int y = 1; y <= 3; y++) {
            for (int x = 1; x <= 4; x++) {
                if (grid[y - 1][x - 1] == null) {
                    grid[y - 1][x - 1] = new GreenHousePot(x, y, y == 1);
                }
            }
        }
    }


    public GreenHousePot getPot(int x, int y) {
        if (x < 1 || x > grid[0].length || y < 1 || y > grid.length) return null;
        return grid[y - 1][x - 1];
    }

    public void plantPot(int x, int y, List<String> unlockedPlants) {
        GreenHousePot pot = getPot(x, y);
        if (pot == null || pot.isLocked || !"EMPTY".equals(pot.status) || unlockedPlants == null) return;

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
            pot.plantType = "marigold";
            pot.growthDurationHours = 2;
        } else {
            PlantData plant = boostablePlants.get(random.nextInt(boostablePlants.size()));
            pot.plantType = plant.getId();
            pot.growthDurationHours = 8;
        }

        pot.status = "GROWING";
        pot.plantedTimestamp = System.currentTimeMillis() / 1000;
    }

    public String collect(int x, int y) {
        GreenHousePot pot = getPot(x, y);
        if (pot == null) return "Invalid pot";
        if (pot.isLocked) return "Pot is locked";
        if (!pot.isReady()) return "Plant is not ready";
        String message = "";

        if ("marigold".equalsIgnoreCase(pot.plantType)) {
            if (App.getCurrentUser() != null) {
                App.getCurrentUser().addCoins(500);
            }
            message = "Collected Marigold! Earned 500 coins.";
        } else {
            if (!storedBoosts.getOrDefault(pot.plantType, false)) {
                storedBoosts.put(pot.plantType, true);
                message =  "Collected " + pot.plantType + "! Boost saved for next match.";
            } else {
                message =  "Collected " + pot.plantType + "! Boost already stored.";
            } //todo: does this boosting work? where are boosts saved?
        }

        pot.status = "EMPTY";
        pot.plantType = null;
        pot.plantedTimestamp = null;
        pot.growthDurationHours = 0;
        return message;
    }

    public void grow(int x, int y) {
        GreenHousePot pot = getPot(x, y);
        if (pot == null || pot.isLocked || !"GROWING".equals(pot.status) || pot.plantedTimestamp == null) return;
        if (pot.isReady()) return;

//        if (pot == null || pot.isLocked || "EMPTY".equals(pot.status)) {
//            System.out.println("Error: Cannot speed up growth on this pot.");
//            return;
//        }
//
//        if (pot.isReady()) {
//            System.out.println("Error: Plant is already fully grown!");
//            return;
//        }

        long now = System.currentTimeMillis() / 1000;
        double elapsedHours = (now - pot.plantedTimestamp) / 3600.0;
        double remainingHours = pot.growthDurationHours - elapsedHours;

        int diamondCost = Math.max(0, (int) Math.ceil(remainingHours));
        int currentDiamonds = App.getCurrentUser() == null ? 0 : App.getCurrentUser().getGemsCount();

        if (currentDiamonds < diamondCost) {
            System.out.println("Error: Not enough diamonds. Cost: " + diamondCost);
            return;
        }

        App.getCurrentUser().setGemsCount(currentDiamonds - diamondCost);
        pot.plantedTimestamp = now - (pot.growthDurationHours * 3600L);

        System.out.println("Speed up successful! Spent " + diamondCost + " diamonds.");
    }

//    public void showGreenhouse() {
//        for (int y = 1; y <= 4; y++) {
//            for (int x = 1; x <= 5; x++) {
//                GreenHousePot pot = getPot(x, y);
//
//                System.out.print("(" + x + "," + y + "): ");
//
//                if (pot == null) {
//                    System.out.print("[INVALID]");
//                } else if (pot.isLocked) {
//                    System.out.print("[LOCKED]");
//                } else if ("EMPTY".equals(pot.status)) {
//                    System.out.print("[EMPTY]");
//                } else if (pot.isReady()) {
//                    System.out.print("[" + pot.plantType + " - READY]");
//                } else {
//                    long now = System.currentTimeMillis() / 1000;
//                    long elapsedSeconds = now - pot.plantedTimestamp;
//                    long totalDurationSeconds = pot.growthDurationHours * 3600L;
//                    long remainingSeconds = Math.max(0, totalDurationSeconds - elapsedSeconds);
//
//                    long hours = remainingSeconds / 3600;
//                    long minutes = (remainingSeconds % 3600) / 60;
//                    long seconds = remainingSeconds % 60;
//
//                    System.out.printf("[%s - %02dh %02dm %02ds remaining]",
//                            pot.plantType, hours, minutes, seconds);
//                }
//                System.out.print("\t");
//            }
//            System.out.println();
//        }
//    }

    public int getCapacity() {
        return grid.length * grid[0].length;
    }

    public int getPotsCount(){
        int count = 0;
        for (GreenHousePot[] row : grid) {
            for (GreenHousePot pot : row) {
                if (pot != null && !pot.isLocked) {
                    count++;
                }
            }
        }

        return count;
    }
    public int unlockPots(int count) {
        int unlocked = 0;
        for (GreenHousePot[] row : grid) {
            for (GreenHousePot pot : row) {
                if (pot.isLocked && unlocked < count) {
                    pot.isLocked = false;
                    unlocked++;
                }
            }
        }
        return unlocked;
    }

//    public int getPotXOrY(String z) {
//        for (GreenHousePot[] row : grid) {
//            for (GreenHousePot pot : row) {
//                if (pot.isLocked) {
//                    if (z.matches("x")) {
//                        return pot.x;
//                    } else {
//                        return pot.y;
//                    }
//                }
//            }
//        }
//        return -1;
//    }

    public GreenHousePot[][] getGrid() {
        return grid;
    }


}
