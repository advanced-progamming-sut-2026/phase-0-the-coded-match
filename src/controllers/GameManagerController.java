package controllers;

import controllers.menus.SignupMenuController;
import enums.Commands;
import enums.LevelType;
import models.App;
import models.GameMapRelated.GameMap;
import models.Level;
import models.MiniGameRelated.VaseBreaker;
import models.Sun;
import models.Projectile;
import models.GameMapRelated.Tile;
import models.plants.Plant;
import models.plants.PlantData;
import models.plants.PlantRepository;
import models.seasons.Season;
import models.zombies.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameManagerController {
    private static GameManagerController instance;
    private static final int MAX_PLANT_FOOD = 3;
    private static final int TICKS_PER_SECOND = 10;
    private Level currentLevel;
    private static boolean cooldownRemoved;
    private static final Map<String, Integer> plantCooldowns = new HashMap<>();

    public static GameManagerController getInstance() {
        if (instance == null) {
            instance = new GameManagerController();
        }
        return instance;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level level) {
        currentLevel = level;
        cooldownRemoved = false;
        plantCooldowns.clear();
    }

    public String[] advanceTime(String input, String[] message) {
        message[0] = "";
        Matcher matcher = Pattern.compile(Commands.ADVANCE_TIME.getPattern()).matcher(input);
        if (!matcher.matches()) {
            message[0] = "invalid command";
            return message;
        }
        int count = Integer.parseInt(matcher.group("count"));
        int dl = App.getCurrentUser().getDifficultyLevel();
        double step = 1 * (dl / 3.0);
        for (int i = 0; i < count; i++) {
            currentLevel.setCurrentTick(currentLevel.getCurrentTick() + step);
            updateObjects(message);
        }
        return message;
    }

    public String[] updateObjects(String[] message) {
        if (currentLevel.getLevelType() == LevelType.I_ZOMBIE) {
            decreasePlantCooldowns();
            updatePlants(message);
            updateZombies();
            updateProjectiles();
        } else {
            decreasePlantCooldowns();
            updatePlants(message);
            updateBarrels();
            updateZombies();
            updateWaves(message);
            updateSkySuns(message);
            updateSuns(message);
            updateTiles();
            updateProjectiles();
            updateSeason();
        }
        if (currentLevel.getSpecialLevel() != null) {
            currentLevel.getSpecialLevel().update(currentLevel);
        }
        if (currentLevel instanceof VaseBreaker) {
            ((VaseBreaker) currentLevel).updateGroundSeeds(message);
        }
        return message;
    }

    private void decreasePlantCooldowns() {
        if (cooldownRemoved) {
            return;
        }
        for (String plantName : plantCooldowns.keySet()) {
            int remaining = Math.max(0, plantCooldowns.get(plantName) - 1);
            plantCooldowns.put(plantName, remaining);
        }
    }

    private void updatePlants(String[] message) {
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
                Tile tile = currentLevel.getGameMap().getTile(plant.getX(), plant.getY());
                if (tile != null && tile.getPlant() == plant) {
                    tile.removePlant();
                }
                iterator.remove();
                currentLevel.setRemovedPlantsCount(currentLevel.getRemovedPlantsCount() + 1);
                if (currentLevel.getSpecialLevel() != null) {
                    currentLevel.getSpecialLevel().plantLost(currentLevel, plant);
                }
            }
        }
    }

    private void updateZombies() {
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
                QuestController.notifyZombieKilled(zombie);
                QuestController.notifyZombieKilled(currentLevel.getCurrentSeason());
            }
        }
    }

    private void updateWaves(String[] message) {
        if (currentLevel instanceof VaseBreaker) {
            return;
        }
        if (currentLevel.getZombieWave() != null) {
            currentLevel.getZombieWave().update();
            if (currentLevel.getZombieWave().isLastWave()) {
                append(message, "The final wave has come.");
            } else if (currentLevel.getZombieWave().isNewWaveStarted()){
                append(message, "Wave " + (currentLevel.getZombieWave().getCurrentWave() + 1) + " started.");
            }
        }
    }

    private void updateSkySuns(String[] message) {
        if (currentLevel instanceof VaseBreaker) {
            return;
        }
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

    private void updateSuns(String[] message) {
        for (Sun sun : currentLevel.getActiveSuns()) {
            boolean wasFalling = sun.isFalling();
            sun.update();
            if (wasFalling && !sun.isFalling()) {
                append(message, "Sun reached the ground at position (" + sun.getX() + ", " + sun.getY() + ")");
            }
        }
    }

    private void updateTiles() {
        GameMap map = currentLevel.getGameMap();
        if (map == null || map.getGrid() == null) {
            return;
        }
        for (int i = 0; i < map.getRows(); i++) {
            for (int j = 0; j < map.getColumns(); j++) {
                Tile tile = map.getGrid()[i][j];
                tile.update();
            }
        }
    }

    private void updateBarrels() {
        for (Barrel barrel : currentLevel.getBarrels()) {
            barrel.update();
        }
    }

    public void updateSeason(){
        Season season = currentLevel.getCurrentSeason();
        if (season != null) {
            season.Update(currentLevel);
        }
    }

    public void collectSun(String input) {
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

    public Sun findSun(int x, int y) {
        for (Sun sun : currentLevel.getActiveSuns()) {
            if (sun.getX() == x && sun.getY() == y) {
                return sun;
            }
        }
        return null;
    }

    public int showSunsAmount() {
        return currentLevel.getCollectedSunsAmount();
    }

    public String cheatAddSuns(String input) {
        Matcher matcher = Pattern.compile(Commands.CHEAT_ADD_SUNS.getPattern()).matcher(input);
        if (!matcher.matches()) {
            return "invalid command";
        }
        int count = Integer.parseInt(matcher.group("count"));
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() + count);
        return "you have " + currentLevel.getCollectedSunsAmount() + " suns now";
    }

    public void cheatReleaseTheNuke() {
        ZombieWaveManager.releaseTheNuke();
        currentLevel.getActiveZombies().clear();
        System.out.println("all zombies are dead");
    }

    public void plantPlant(String input) {
        Matcher matcher = Pattern.compile(Commands.PLANT_PLANT.getPattern()).matcher(input);
        if (!matcher.matches()) {
            System.out.println("invalid command");
            return;
        }
        String type = matcher.group("type").trim();
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        String error = getPlantingError(type, x, y);

        if (currentLevel instanceof VaseBreaker) {
            plantVasebreakerSeed((VaseBreaker) currentLevel, type, x, y);
            return;
        }
        if (error != null) {
            System.out.println(error);
            return;
        }
        PlantData data = PlantRepository.getInstance().findByName(type);
        Plant plant = new Plant(data, x, y, 1);
        currentLevel.getActivePlants().add(plant);
        currentLevel.getGameMap().getTile(plant.getX(), plant.getY()).setPlant(plant);
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() - data.getSunCost());
        if (!cooldownRemoved) {
            plantCooldowns.put(data.getName().toLowerCase(), secondsToTicks(data.getRecharge()));
        }
        if (isBoostedPlant(plant)) {
            plant.activatePlantFood();
        }
        QuestController.onPlantPlaced(plant);
        System.out.println("Plant " + data.getDisplayName() + " planted at (" + x + ", " + y + ")");
    }

    private String getPlantingError(String type, int x, int y) {
        PlantData data = PlantRepository.getInstance().findByName(type);
        if (data == null) {
            return "plant type does not exist";
        }
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
        if (!cooldownRemoved && plantCooldowns.getOrDefault(data.getName().toLowerCase(), 0) > 0) {
            return "plant is on cooldown";
        }
        return null;
    }

    public void cheatRemoveCooldown() {
        cooldownRemoved = true;
        plantCooldowns.clear();
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

    public StringBuilder showMap() {
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

    private String tileSymbol(int x, int y) {
        Tile tile = currentLevel.getGameMap().getTile(x, y);
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

    public StringBuilder showPlantsStatus() {
        StringBuilder builder = new StringBuilder();
        PlantRepository repository = PlantRepository.getInstance();
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

    public StringBuilder showTileStatus(String input) {
        Matcher matcher = Pattern.compile(Commands.TILE_STATUS.getPattern()).matcher(input);
        StringBuilder builder = new StringBuilder();
        if (!matcher.matches()) {
            return builder.append("invalid command");
        }
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        Tile tile = currentLevel.getGameMap().getTile(x, y);
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

    public void gameOver() {
        this.currentLevel = null;
        App.getCurrentUser().setVictroy(false);
        SignupMenuController.saveToJson();
    }

    public String gameWon(){
        if (showSunsAmount() == 0) {
            QuestController.notifyNoSunsLeft();
        }
        QuestController.notifyPlantsDestroyed(currentLevel.getRemovedPlantsCount());
        App.getCurrentUser().setVictroy(true);
        SignupMenuController.saveToJson();
        QuestController.onLevelCompleted(true);
        return "Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.";
    }

    public void updateProjectiles() {
        if (currentLevel == null || currentLevel.getActiveProjectiles() == null) {
            return;
        }
        Iterator<Projectile> iterator = currentLevel.getActiveProjectiles().iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            if (projectile.isDestroyed()) {
                iterator.remove();
                continue;
            }
            projectile.move();
            for (Zombie zombie : currentLevel.getActiveZombies().toArray(new Zombie[0])) {
                if (projectile.checkZombieCollision(zombie)) {
                    zombie.getBehavior().onProjectileHit(zombie, projectile);
                    iterator.remove();
                    break;
                }
            }
            int col = (int) projectile.getxCoordinate();
            int row = (int) projectile.getyCoordinate();

            Tile tile = currentLevel.getGameMap().getTile(col, row);
            if (tile != null && tile.isGrave()) {
                tile.takeDamage(projectile.getDamage());
                projectile.destroy();
            }
            List<Barrel> barrels = currentLevel.getBarrels();
            for (Barrel barrel : barrels) {
                if (projectile.checkBarrelCollision(barrel)) {
                    barrel.onProjectileHit(projectile);
                }
            }
        }
    }

    private Plant findPlant(int x, int y) {
        for (Plant plant : currentLevel.getActivePlants()) {
            if (plant.getX() == x && plant.getY() == y) {
                return plant;
            }
        }
        return null;
    }

    private int secondsToTicks(double seconds) {
        return Math.max(1, (int) Math.ceil(seconds * TICKS_PER_SECOND));
    }

    private boolean isBoostedPlant(Plant plant) {
        return plant.isBoosted();
    }

    private void handleZombieDrop() {
        if (Math.random() < 0.05 && currentLevel.getPlantFoodCount() < MAX_PLANT_FOOD) {
            currentLevel.setPlantFoodCount(currentLevel.getPlantFoodCount() + 1);
            System.out.println("The glowing zombie dropeed a plant food; you have "
                    + currentLevel.getPlantFoodCount() + " plant foods now.");
        }
    }

    private void append(String[] message, String line) {
        if (message[0] == null || message[0].isEmpty()) {
            message[0] = line;
        } else {
            message[0] += System.lineSeparator() + line;
        }
    }

    public void breakVaseCommand(int x, int y) {
        if (!(currentLevel instanceof VaseBreaker)) {
            System.out.println("You are not in a Vasebreaker minigame!");
            return;
        }

        VaseBreaker level = (VaseBreaker) currentLevel;
        String result = level.breakVaseAt(x, y);
        System.out.println(result);
    }

    public void pickUPPacket(int x, int y){
        if (!(currentLevel instanceof VaseBreaker)) {
            System.out.println("You are not in a Vasebreaker minigame!");
            return;
        }
        VaseBreaker level = (VaseBreaker) currentLevel;
        String result = level.pickUpSeedAt(x, y);
        System.out.println(result);

    }

    public void plantVasebreakerSeed(VaseBreaker level, String plantName, int x, int y) {

        if (!level.consumeSeedPacket(plantName)) {
            System.out.println("You do not have a " + plantName + " seed packet!");
            return;
        }

        level.consumeSeedPacket(plantName);
        PlantData data = PlantRepository.getInstance().findByName(plantName);
        Plant plant = new Plant(data, x, y, 1);
        level.getActivePlants().add(plant);
        level.getGameMap().getTile(x, y).setPlant(plant);
        System.out.println("Planted " + plantName + " at (" + x + ", " + y + ")");
    }
}