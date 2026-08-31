package PvZ2.APproject.controllers;

import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.PlantTag;
import PvZ2.APproject.enums.SunType;
import PvZ2.APproject.enums.TileType;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.MiniGameRelated.VaseBreaker;
import PvZ2.APproject.models.Sun;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.specialLevels.LockedPlantsLevel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantController {
    private static Level currentLevel = null;
    private static final int MAX_PLANT_FOOD = 4;


    public PlantController() {
        currentLevel = GameManagerController.getInstance().getCurrentLevel();
    }

    public static void produceSun(Plant plant) {
        plant.setProducedSun(true);
        Sun sun = new Sun(plant.getX(), plant.getY(), SunType.NORMAL.getValue(), 0f, false, SunType.NORMAL);
        Level level = currentLevel;
        if (level != null) {
            level.getActiveSuns().add(sun);
        }
    }

    public static boolean canPlaceOnTile(Plant plant, Tile tile) {
        if (plant == null || tile == null) {
            return false;
        }
        if (tile.isGrave()) return false;
        if (tile.getType() == TileType.WATER) {
            if (plant.getData().getName().equalsIgnoreCase("LilyPad")) return tile.getLilyPadPlant() == null && tile.getPlant() == null;
            if (plant.hasThisTag(PlantTag.WATER)) return tile.getPlant() == null;
            return tile.getLilyPadPlant() != null && tile.getPlant() == null;
        }
        if (!tile.getType().isCanPlant()) return false;
        if (tile.isEmpty()) {
            return true;
        }
        Plant currentPlant = tile.getPlant();
        return currentPlant != null && currentPlant.getData().getId().equalsIgnoreCase(plant.getData().getId())
            && (plant.hasThisTag(PlantTag.STACK) || currentPlant.hasThisTag(PlantTag.STACK))
            && currentPlant.getStackCount() < 5;
    }

    public static String plantPlant(String type, int x, int y) {
        currentLevel = GameManagerController.getInstance().getCurrentLevel();
        if (currentLevel == null) return "no active level";
//        Matcher matcher = Pattern.compile(Commands.PLANT_PLANT.getPattern()).matcher(input);
//        if (!matcher.matches()) {
//            System.out.println("invalid command");
//            return;
//        }
//        String type = matcher.group("type").trim();
//        int x = Integer.parseInt(matcher.group("x"));
//        int y = Integer.parseInt(matcher.group("y"));
        String error = getPlantingError(type, x, y);

        if (currentLevel instanceof VaseBreaker) {
            GameManagerController.getInstance().plantVasebreakerSeed((VaseBreaker) currentLevel, type, x, y);
            return null;
        }
        if (error != null) {
           return error;
        }
        PlantData data = PlantRepository.getInstance().findByName(type);
        Plant plant = new Plant(data, x, y, getOwnedPlantLevel(data));
        if (currentLevel.getSpecialLevelStrategy() instanceof LockedPlantsLevel &&
            ((LockedPlantsLevel) currentLevel.getSpecialLevelStrategy()).isPlantLocked(plant.getData().getName())) {
            return "Plant is locked";
        }
        Tile tile = currentLevel.getGameMap().getTile(x, y);
        Plant existing = tile.getPlant();
        if (existing != null) {
            existing.incrementStackCount();
        } else {
            currentLevel.getActivePlants().add(plant);
            if (currentLevel.getCurrentSeason() != null) {
                currentLevel.getCurrentSeason().PlantPlaced(currentLevel, plant, x, y);
            }
            if (data.getName().equalsIgnoreCase("LilyPad") && tile.getType() == TileType.WATER) {
                tile.setLilyPadPlant(plant);
            } else {
                tile.setPlant(plant);
            }
            if (GameManagerController.getInstance().isBoostedPlant(plant)) plant.activatePlantFood();
            if (App.getCurrentUser() != null && App.getCurrentUser().getGreenHouse().storedBoosts.remove(data.getId().toLowerCase()) != null) {
                plant.activatePlantFood();
            }
            QuestController.onPlantPlaced(plant);
        }
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() - plant.getSunCost());
        if (!GameManagerController.getInstance().isCooldownRemoved()) {
            GameManagerController.getInstance().setPlantCooldowns(data.getName().toLowerCase(), GameManagerController.getInstance().secondsToTicks(plant.getRecharge()));
        }
//        return "Plant " + data.getDisplayName() + " planted at (" + x + ", " + y + ")";
        return null;
    }

    public static String getPlantingError(String type, int x, int y) {
        currentLevel = GameManagerController.getInstance().getCurrentLevel();
        if (currentLevel == null) return "no active level";
        PlantData data = PlantRepository.getInstance().findByName(type);
        if (data == null) return "plant type does not exist";
        if (!currentLevel.getChosenPlants().isEmpty() &&
            currentLevel.getChosenPlants().stream().noneMatch(name -> name.equalsIgnoreCase(type)))
            return "plant was not selected";
        Tile tile = currentLevel.getGameMap().getTile(x, y);
        if (tile == null) {
            return "location is out of map";
        }
        Plant temp = new Plant(data, x, y, getOwnedPlantLevel(data));
        if (!PlantController.canPlaceOnTile(temp, tile)) {
            return "cannot plant on this tile";
        }
        if (currentLevel.getCollectedSunsAmount() < temp.getSunCost()) {
            return "not enough suns";
        }
        if (!GameManagerController.getInstance().isCooldownRemoved() && GameManagerController.getInstance().getPlantCooldowns().getOrDefault(data.getName().toLowerCase(), 0) > 0) {
            return "plant is on cooldown";
        }
        return null;
    }

    public void cheatRemoveCooldown() {
        GameManagerController.getInstance().setCooldownRemoved(true);
        GameManagerController.getInstance().getPlantCooldowns().clear();
        System.out.println("all plant cooldowns removed");
    }

    public void pluckPlant(Tile tile) {
        if (tile == null) return;
        String error = removePlantAt(tile.getColumn(), tile.getRow());
        if (error != null) System.out.println(error);
    }

    public static String removePlantAt(int x, int y) {
        currentLevel = GameManagerController.getInstance().getCurrentLevel();
        if (currentLevel == null) return "no active level";
        Tile tile = currentLevel.getGameMap().getTile(x, y);
        if (tile == null) return "location is out of map";
        Plant plant = tile.getPlant() != null ? tile.getPlant() : tile.getLilyPadPlant();
        if (plant == null) return "there is no plant at this location";
        currentLevel.getActivePlants().remove(plant);
        if (tile.getPlant() == plant) tile.removePlant();
        if (tile.getLilyPadPlant() == plant) tile.setLilyPadPlant(null);
        currentLevel.setRemovedPlantsCount(currentLevel.getRemovedPlantsCount() + 1);
        if (currentLevel.getSpecialLevelStrategy() != null) currentLevel.getSpecialLevelStrategy().plantLost(currentLevel, plant);
        return null;
    }

    public void pluckPlant(String input) {
        currentLevel = GameManagerController.getInstance().getCurrentLevel();
        if (currentLevel == null) return;
        Matcher matcher = Pattern.compile(Commands.PLUCK_PLANT.getPattern()).matcher(input);
        if (!matcher.matches()) {
            System.out.println("invalid command");
            return;
        }
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        Plant plant = findPlant(x, y);
        String displayName = plant == null || plant.getData() == null ? null : plant.getData().getDisplayName();
        String error = removePlantAt(x, y);
        if (error != null) {
            System.out.println(error);
            return;
        }
        System.out.println("Plant " + displayName + " at (" + x + ", " + y + ") removed");
    }

    public void feedPlant(String input) {
        currentLevel = GameManagerController.getInstance().getCurrentLevel();
        if (currentLevel == null) return;
        Matcher matcher = Pattern.compile(Commands.FEED_PLANT.getPattern()).matcher(input);
        if (!matcher.matches()) {
            System.out.println("invalid command");
            return;
        }
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        Plant plant = findPlant(x, y);
        if (plant == null) {
            System.out.println("there is no plant at this location");
            return;
        }
        if (currentLevel.getPlantFoodCount() <= 0) {
            System.out.println("you do not have plant food");
            return;
        }
        currentLevel.setPlantFoodCount(currentLevel.getPlantFoodCount() - 1);
        plant.activatePlantFood();
        System.out.println("Plant " + plant.getData().getDisplayName() + " at (" + x + ", " + y + ") was fed; you have "
            + currentLevel.getPlantFoodCount() + " plant foods now");
    }

    public void cheatAddPlantFood(int count) {
        if (currentLevel.getPlantFoodCount() >= MAX_PLANT_FOOD || count > 4 || count + currentLevel.getPlantFoodCount() > 4) {
            System.out.println("plant food storage is full or will get full by this amount");
            return;
        }
        currentLevel.setPlantFoodCount(currentLevel.getPlantFoodCount() + count);
        System.out.println("you have " + currentLevel.getPlantFoodCount() + " plant foods now");
    }

    private static int getOwnedPlantLevel(PlantData data) {
        if (data == null || App.getCurrentUser() == null) return 1;
        for (Plant owned : App.getCurrentUser().getCollection().getAvailablePlants()) {
            if (owned != null && owned.getData() != null && owned.getData().getId().equalsIgnoreCase(data.getId())) {
                return Math.max(1, owned.getLevel());
            }
        }
        return 1;
    }

    private Plant findPlant(int x, int y) {
        Tile tile = currentLevel.getGameMap().getTile(x, y);
        if (tile != null && tile.getPlant() != null) return tile.getPlant();
        if (tile != null && tile.getLilyPadPlant() != null) return tile.getLilyPadPlant();
        for (Plant plant : currentLevel.getActivePlants()) {
            if (plant.getX() == x && plant.getY() == y) {
                return plant;
            }
        }
        return null;
    }
}
