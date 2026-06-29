package controllers;

import enums.Commands;
import models.App;
import models.Level;
import models.Sun;
import models.GameMapRelated.Tile;
import models.plants.Plant;
import models.plants.PlantData;
import models.plants.PlantRepository;
import models.zombies.Zombie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameManagerController {
    private static final int MAX_PLANT_FOOD = 3;
    private static final int TICKS_PER_SECOND = 10;
    private static Level currentLevel;
    private static boolean cooldownRemoved;
    private static final Map<String, Integer> plantCooldowns = new HashMap<>();

    public static Level getCurrentLevel() {
        return currentLevel;
    }

    public static void setCurrentLevel(Level level) {
        currentLevel = level;
        cooldownRemoved = false;
        plantCooldowns.clear();
    }

    public static String[] advanceTime(String input, String[] message) {
        message[0] = "";
        Matcher matcher = Pattern.compile(Commands.ADVANCE_TIME.getPattern()).matcher(input);
        if (!matcher.matches()) {
            message[0] = "invalid command";
            return message;
        }
        int count = Integer.parseInt(matcher.group("count"));
        for (int i = 0; i < count; i++) {
            currentLevel.setCurrentTick(currentLevel.getCurrentTick() + 1);
            updateObjects(message);
        }
        return message;
    }

    public static String[] updateObjects(String[] message) {
        decreasePlantCooldowns();
        updatePlants(message);
        updateZombies();
        updateWaves();
        updateSkySuns(message);
        updateSuns(message);
        updateTiles();
        return message;
    }

    private static void decreasePlantCooldowns() {
        if (cooldownRemoved) {
            return;
        }
        for (String plantName : plantCooldowns.keySet()) {
            int remaining = Math.max(0, plantCooldowns.get(plantName) - 1);
            plantCooldowns.put(plantName, remaining);
        }
    }

    private static void updatePlants(String[] message) {
        Iterator<Plant> iterator = currentLevel.getActivePlants().iterator();
        while (iterator.hasNext()) {
            Plant plant = iterator.next();
            plant.update();
            if (plant.isProducedSun()) {
                append(message, "plant " + plant.getData().getDisplayName() + " produced a sun at ("
                        + plant.getX() + ", " + plant.getY() + ")");
                plant.setProducedSun(false);
            }
            if (plant.isDead()) {
                append(message, "Plant " + plant.getData().getDisplayName() + " at (" + plant.getX() + ", "
                        + plant.getY() + ") is destroyed.");
                Tile tile = findTile(plant.getX(), plant.getY());
                if (tile != null && tile.getPlant() == plant) {
                    tile.removePlantFromTile();
                }
                iterator.remove();
            }
        }
    }

    private static void updateZombies() {
        Iterator<Zombie> iterator = currentLevel.getActiveZombies().iterator();
        while (iterator.hasNext()) {
            Zombie zombie = iterator.next();
            zombie.update();
            if (zombie.getX() <= 0) {
                App.handleLawnMower(zombie);
            }
            if (zombie.isDead()) {
                iterator.remove();
                handleZombieDrop();
            }
        }
    }

    private static void updateWaves() {
        if (currentLevel.getZombieWave() != null) {
            currentLevel.getZombieWave().update();
        }
    }

    private static void updateSkySuns(String[] message) {
        if (currentLevel.getSkySunProducer() == null) {
            return;
        }
        currentLevel.getSkySunProducer().update();
        if (currentLevel.getSkySunProducer().isProducedASun()) {
            Sun sun = currentLevel.getSkySunProducer().getSun();
            append(message, "New " + sun.getType().getName() + " sun is dropping at position (" + sun.getX()
                    + ", " + sun.getY() + ")");
            currentLevel.getSkySunProducer().setProducedASun(false);
        }
    }

    private static void updateSuns(String[] message) {
        for (Sun sun : currentLevel.getActiveSuns()) {
            boolean wasFalling = sun.isFalling();
            sun.update();
            if (wasFalling && !sun.isFalling()) {
                append(message, "Sun reached the ground at position (" + sun.getX() + ", " + sun.getY() + ")");
            }
        }
    }

    private static void updateTiles() {
        if (currentLevel.getGameMap() == null || currentLevel.getGameMap().getTiles() == null) {
            return;
        }
        for (Tile tile : currentLevel.getGameMap().getTiles()) {
            tile.update();
        }
    }

    public static void collectSun(String input) {
        Matcher matcher = Pattern.compile(Commands.COLLECT_SUN.getPattern()).matcher(input);
        if (!matcher.matches()) {
            System.out.println("invalid command");
            return;
        }
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        Sun sun = findSun(x, y);
        if (sun == null) {
            System.out.println("no sun at this location");
            return;
        }
        sun.collect();
        Plant plant = findPlant(x, y);
        if (plant != null) {
            plant.setSunCollected(true);
        }
        System.out.println("sun collected; you have " + currentLevel.getCollectedSunsAmount() + " suns now");
    }

    public static Sun findSun(int x, int y) {
        for (Sun sun : currentLevel.getActiveSuns()) {
            if (sun.getX() == x && sun.getY() == y) {
                return sun;
            }
        }
        return null;
    }

    public static int showSunsAmount() {
        return currentLevel.getCollectedSunsAmount();
    }

    public static void cheatAddSuns(String input) {
        Matcher matcher = Pattern.compile(Commands.CHEAT_ADD_SUNS.getPattern()).matcher(input);
        if (!matcher.matches()) {
            System.out.println("invalid command");
            return;
        }
        int count = Integer.parseInt(matcher.group("count"));
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() + count);
        System.out.println("you have " + currentLevel.getCollectedSunsAmount() + " suns now");
    }

    public static String[] startWave() {
        return new String[] {"wave system is not ready yet"};
    }// do we need this method?

    public void cheatReleaseTheNuke() {
        ZombieWaveManager.releaseTheNuke();
        currentLevel.getActiveZombies().clear();
        System.out.println("all zombies are dead");
    }

    public static void plantPlant(String input) {
        Matcher matcher = Pattern.compile(Commands.PLANT_PLANT.getPattern()).matcher(input);
        if (!matcher.matches()) {
            System.out.println("invalid command");
            return;
        }
        String type = matcher.group("type").trim();
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        String error = getPlantingError(type, x, y);
        if (error != null) {
            System.out.println(error);
            return;
        }
        PlantData data = new PlantRepository("assets/Plants.json").findByName(type);
        Plant plant = new Plant(data, x, y, 1);
        currentLevel.getActivePlants().add(plant);
        findTile(x, y).setPlant(plant);
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() - data.getSunCost());
        if (!cooldownRemoved) {
            plantCooldowns.put(data.getName().toLowerCase(), secondsToTicks(data.getRecharge()));
        }
        if (isBoostedPlant(plant)) {
            plant.activatePlantFood();
        }
        System.out.println("Plant " + data.getDisplayName() + " planted at (" + x + ", " + y + ")");
    }

    private static String getPlantingError(String type, int x, int y) {
        PlantData data = new PlantRepository("assets/Plants.json").findByName(type);
        if (data == null) {
            return "plant type does not exist";
        }
        Tile tile = findTile(x, y);
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
        if (!cooldownRemoved && plantCooldowns.getOrDefault(data.getName().toLowerCase(), 0) > 0) {
            return "plant is on cooldown";
        }
        return null;
    }

    public static void cheatRemoveCooldown() {
        cooldownRemoved = true;
        plantCooldowns.clear();
        System.out.println("all plant cooldowns removed");
    }

    public static void pluckPlant(String input) {
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
        Tile tile = findTile(x, y);
        if (tile != null) {
            tile.removePlantFromTile();
        }
        System.out.println("Plant " + plant.getData().getDisplayName() + " at (" + x + ", " + y + ") removed");
    }

    public static void feedPlant(String input) {
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

    public static void cheatAddPlantFood() {
        if (currentLevel.getPlantFoodCount() >= MAX_PLANT_FOOD) {
            System.out.println("plant food storage is full");
            return;
        }
        currentLevel.setPlantFoodCount(currentLevel.getPlantFoodCount() + 1);
        System.out.println("you have " + currentLevel.getPlantFoodCount() + " plant foods now");
    }

    public static StringBuilder showMap() {
        StringBuilder builder = new StringBuilder();
        builder.append("tick: ").append(currentLevel.getCurrentTick()).append('\n');
        builder.append("suns: ").append(currentLevel.getCollectedSunsAmount()).append('\n');
        builder.append("plant foods: ").append(currentLevel.getPlantFoodCount()).append('\n');
        int rows = currentLevel.getGameMap().getRows();
        int columns = currentLevel.getGameMap().getColumns();
        for (int y = 1; y <= rows; y++) {
            for (int x = 1; x <= columns; x++) {
                builder.append(tileSymbol(x, y));
            }
            builder.append('\n');
        }
        return builder;
    }

    private static String tileSymbol(int x, int y) {
        Tile tile = findTile(x, y);
        if (tile == null) {
            return "[?]";
        }
        Plant plant = tile.getPlant();
        if (plant != null) {
            return "[P]";
        }
        for (Zombie zombie : currentLevel.getActiveZombies()) {
            if ((int) Math.round(zombie.getX()) == x && zombie.getY() == y) {
                return "[Z]";
            }
        }
        return "[" + tile.getType().name().charAt(0) + "]";
    }

    public static StringBuilder showPlantsStatus() {
        StringBuilder builder = new StringBuilder();
        PlantRepository repository = new PlantRepository("assets/Plants.json");
        for (PlantData data : repository.getAllPlants()) {
            int cooldown = plantCooldowns.getOrDefault(data.getName().toLowerCase(), 0);
            builder.append(data.getDisplayName()).append(" | cost: ").append(data.getSunCost());
            if (cooldownRemoved || cooldown == 0) {
                builder.append(" | ready");
            } else {
                builder.append(" | cooldown: ").append(cooldown / TICKS_PER_SECOND).append("s");
            }
            builder.append('\n');
        }
        return builder;
    }

    public static StringBuilder showTileStatus(String input) {
        Matcher matcher = Pattern.compile(Commands.TILE_STATUS.getPattern()).matcher(input);
        StringBuilder builder = new StringBuilder();
        if (!matcher.matches()) {
            return builder.append("invalid command");
        }
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        Tile tile = findTile(x, y);
        if (tile == null) {
            return builder.append("location is out of map");
        }
        builder.append("tile: ").append(tile.getType()).append(" hp: ").append(tile.getCurrentHp()).append('\n');
        Plant plant = tile.getPlant();
        if (plant == null) {
            builder.append("plant: none\n");
        } else {
            builder.append("plant: ").append(plant.getData().getDisplayName()).append(" hp: ")
                    .append(plant.getCurrentHp()).append('\n');
        }
        builder.append("zombies:\n");
        for (Zombie zombie : currentLevel.getActiveZombies()) {
            if ((int) Math.round(zombie.getX()) == x && zombie.getY() == y) {
                builder.append(zombie.getData().getDisplayName()).append(" hp: ").append(zombie.getCurrentHp()).append('\n');
            }
        }
        return builder;
    }

    public static void ifAZombieWasKilled() {
        handleZombieDrop();
    }

    public static StringBuilder showZombiesInfo() {
        StringBuilder builder = new StringBuilder();
        for (Zombie zombie : currentLevel.getActiveZombies()) {
            builder.append(zombie.getData().getDisplayName()).append(":\n");
            builder.append("position: ").append(zombie.getX()).append(", ").append(zombie.getY()).append('\n');
            builder.append("health: ").append(zombie.getCurrentHp()).append('\n');
            builder.append("effects: ").append(zombie.getEffects()).append('\n');
        }
        return builder;
    }

    public static void cheatSpawnZombies(String input) {
        System.out.println("zombie repository connection is not implemented in this phase code");
    }

    public static void endGame() {
    }

    public static String gameWon(){
        return "Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.";
    }

    public void saveGame() {
    }

    public void loadGame() {
    }

    public void updateProjectiles() {
    }

    private void handleProjectileCollisions() {
    }

    private void cleanUpDestroyedProjectiles() {
    }

    public void spawnProjectile(models.Projectile projectile) {
    }

    private static Plant findPlant(int x, int y) {
        for (Plant plant : currentLevel.getActivePlants()) {
            if (plant.getX() == x && plant.getY() == y) {
                return plant;
            }
        }
        return null;
    }

    private static Tile findTile(int x, int y) {
        if (currentLevel == null || currentLevel.getGameMap() == null || currentLevel.getGameMap().getTiles() == null) {
            return null;
        }
        for (Tile tile : currentLevel.getGameMap().getTiles()) {
            if (tile.getColumn() == x && tile.getRow() == y) {
                return tile;
            }
        }
        return null;
    }

    private static int secondsToTicks(double seconds) {
        return Math.max(1, (int) Math.ceil(seconds * TICKS_PER_SECOND));
    }

    private static boolean isBoostedPlant(Plant plant) {
        return plant.isBoosted();
    }

    private static void handleZombieDrop() {
        if (Math.random() < 0.05 && currentLevel.getPlantFoodCount() < MAX_PLANT_FOOD) {
            currentLevel.setPlantFoodCount(currentLevel.getPlantFoodCount() + 1);
            System.out.println("The glowing zombie dropeed a plant food; you have "
                    + currentLevel.getPlantFoodCount() + " plant foods now.");
        }
    }

    private static void append(String[] message, String line) {
        if (message[0] == null || message[0].isEmpty()) {
            message[0] = line;
        } else {
            message[0] += System.lineSeparator() + line;
        }
    }
}