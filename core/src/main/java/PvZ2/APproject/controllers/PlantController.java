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
    private static final int MAX_PLANT_FOOD = 3;


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
            if (plant.getData().getName().equalsIgnoreCase("LilyPad") || plant.hasThisTag(PlantTag.WATER)) return tile.getPlant() == null;
            return tile.getLilyPadPlant() != null && tile.getPlant() == null;
        }
        if (!tile.getType().isCanPlant()) return false;
        if (tile.isEmpty()) {
            return true;
        }
        Plant currentPlant = tile.getPlant();
        return plant.hasThisTag(PlantTag.STACK) || currentPlant.hasThisTag(PlantTag.STACK);
    }

    public static String plantPlant(String type, int x, int y) {
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
        Plant plant = new Plant(data, x, y, 1);
        if (currentLevel.getSpecialLevel() instanceof LockedPlantsLevel &&
            ((LockedPlantsLevel) currentLevel.getSpecialLevel()).isPlantLocked(plant.getData().getName())) {
            return "Plant is locked";
        }
        currentLevel.getActivePlants().add(plant);
        if (currentLevel.getCurrentSeason() != null) {
            currentLevel.getCurrentSeason().PlantPlaced(currentLevel, plant, x, y);
        }
        currentLevel.getGameMap().getTile(plant.getX(), plant.getY()).setPlant(plant);
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() - data.getSunCost());
        if (!GameManagerController.getInstance().isCooldownRemoved()) {
            GameManagerController.getInstance().setPlantCooldowns(data.getName().toLowerCase(), GameManagerController.getInstance().secondsToTicks(data.getRecharge()));
        }
        if (GameManagerController.getInstance().isBoostedPlant(plant)) plant.activatePlantFood();
        if (App.getCurrentUser().getGreenHouse().storedBoosts.remove(data.getName().toLowerCase()) != null)
            plant.activatePlantFood();
        QuestController.onPlantPlaced(plant);
//        return "Plant " + data.getDisplayName() + " planted at (" + x + ", " + y + ")";
        return null;
    }

    private static String getPlantingError(String type, int x, int y) {
        PlantData data = PlantRepository.getInstance().findByName(type);
        if (data == null) return "plant type does not exist";
        if (!currentLevel.getChosenPlants().isEmpty() &&
            currentLevel.getChosenPlants().stream().noneMatch(name -> name.equalsIgnoreCase(type)))
            return "plant was not selected";
        Tile tile = currentLevel.getGameMap().getTile(x, y);
        if (tile == null) {
            return "location is out of map";
        }
        Plant temp = new Plant(data, x, y, 1);
        if (!PlantController.canPlaceOnTile(temp, tile)) {
            return "cannot plant on this tile";
        }
        if (currentLevel.getCollectedSunsAmount() < data.getSunCost()) {
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

    public void pluckPlant(String input) {
        Matcher matcher = Pattern.compile(Commands.PLUCK_PLANT.getPattern()).matcher(input);
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
        currentLevel.getActivePlants().remove(plant);
        Tile tile = currentLevel.getGameMap().getTile(x, y);
        if (tile != null) {
            tile.removePlant();
        }
        System.out.println("Plant " + plant.getData().getDisplayName() + " at (" + x + ", " + y + ") removed");
    }

    public void feedPlant(String input) {
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

    public void cheatAddPlantFood() {
        if (currentLevel.getPlantFoodCount() >= MAX_PLANT_FOOD) {
            System.out.println("plant food storage is full");
            return;
        }
        currentLevel.setPlantFoodCount(currentLevel.getPlantFoodCount() + 1);
        System.out.println("you have " + currentLevel.getPlantFoodCount() + " plant foods now");
    }

    private Plant findPlant(int x, int y) {
        for (Plant plant : currentLevel.getActivePlants()) {
            if (plant.getX() == x && plant.getY() == y) {
                return plant;
            }
        }
        return null;
    }
}
