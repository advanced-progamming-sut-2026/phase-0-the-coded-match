package PvZ2.APproject.controllers;

import PvZ2.APproject.controllers.menus.SignupMenuController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.LevelType;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.enums.PlantTag;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.GameMapRelated.GameMap;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.LevelData;
import PvZ2.APproject.models.MiniGameRelated.Beghouled;
import PvZ2.APproject.models.MiniGameRelated.MiniGame;
import PvZ2.APproject.models.MiniGameRelated.VaseBreaker;
import PvZ2.APproject.models.Sun;
import PvZ2.APproject.models.Projectile;
import PvZ2.APproject.models.GameMapRelated.Tile;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.seasons.Season;
import PvZ2.APproject.models.zombies.*;

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
    private static boolean isGameEnded = false;

    public static GameManagerController getInstance() {
        if (instance == null) {
            instance = new GameManagerController();
        }
        return instance;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public boolean isCooldownRemoved() {
        return cooldownRemoved;
    }

    public void setCooldownRemoved(boolean cooldownRemoved) {
        GameManagerController.cooldownRemoved = cooldownRemoved;
    }

    public Map<String, Integer> getPlantCooldowns() {
        return plantCooldowns;
    }

    public void setPlantCooldowns(String plant, int time){
        plantCooldowns.put(plant, time);
    }

    public void setCurrentLevel(Level level) {
        currentLevel = level;
        cooldownRemoved = false;
        plantCooldowns.clear();
    }

//    public String[] advanceTime(String input, String[] message) {
//        message[0] = "";
//        Matcher matcher = Pattern.compile(Commands.ADVANCE_TIME.getPattern()).matcher(input);
//        if (!matcher.matches()) {
//            message[0] = "invalid command";
//            return message;
//        }
//        int count = Integer.parseInt(matcher.group("count"));
//        int dl = App.getCurrentUser().getDifficultyLevel();
//        double step = 1 * (dl / 3.0);
//        for (int i = 0; i < count; i++) {
//            currentLevel.setCurrentTick(currentLevel.getCurrentTick() + step);
//            updateObjects(message);
//        }
//        return message;
//    }

    public String updateObjects(float delta) {
        String message = "";
        if (currentLevel.getLevelType() == LevelType.I_ZOMBIE) {
            decreasePlantCooldowns();
            updatePlants(delta);
            updateZombies(delta);
            updateProjectiles();
        } else {
            decreasePlantCooldowns();
            updateSeason(delta);
            updatePlants(delta);
            if (isIsGameEnded()) {
                return message;
            }
            updateBarrels(delta);
            message += updateZombies(delta);
            if (!(currentLevel instanceof MiniGame)) {
                message += updateWaves(delta);
                updateSkySuns(delta);
            }
            updateSuns(delta);
            updateTiles(delta);
            updateProjectiles();
            if (currentLevel instanceof Beghouled beghouled) {
                beghouled.updateMinigameTick();
            }
        }
        if (currentLevel.getSpecialLevelStrategy() != null) {
            currentLevel.getSpecialLevelStrategy().update(currentLevel);
        }
        if (currentLevel instanceof VaseBreaker) {
            ((VaseBreaker) currentLevel).updateGroundSeeds();
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

    private void updatePlants(float delta) {
        Iterator<Plant> iterator = currentLevel.getActivePlants().iterator();
        while (iterator.hasNext()) {
            Plant plant = iterator.next();
            plant.update(delta);
            if (plant.isProducedSun()) {
//                append(message, "plant " + plant.getData().getDisplayName() + " produced a sun at ("
//                        + plant.getX() + ", " + plant.getY() + ")");
                plant.setProducedSun(false);
            }
            if (plant.isDead()) {
//                append(message, "Plant " + plant.getData().getDisplayName() + " at (" + plant.getX() + ", "
//                        + plant.getY() + ") is destroyed.");
                Tile tile = currentLevel.getGameMap().getTile(plant.getX(), plant.getY());
                if (tile != null && tile.getPlant() == plant) {
                    tile.removePlant();
                }
                iterator.remove();
                if (currentLevel instanceof Beghouled beghouled) {
                    beghouled.onPlantDestroyed(plant);
                }
                currentLevel.setRemovedPlantsCount(currentLevel.getRemovedPlantsCount() + 1);
                if (currentLevel.getSpecialLevelStrategy() != null) {
                    currentLevel.getSpecialLevelStrategy().plantLost(currentLevel, plant);
                }
            }
        }
    }

    private String updateZombies(float delta) {
        int killedThisTick = 0;
        int damageThisTick = 0;
        String dropMessage = "";
        Iterator<Zombie> iterator = currentLevel.getActiveZombies().iterator();
        while (iterator.hasNext()) {
            Zombie zombie = iterator.next();
            zombie.update(delta);
            if (zombie.getX() <= 0) { //todo: 0 -> 250(x from left side of the screen)
                currentLevel.getGameMap().handleLawnMower(zombie);
            }
            if (zombie.isDead()) {
                iterator.remove();
                killedThisTick++;
                damageThisTick += zombie.getLastDamageTaken();
                dropMessage = handleZombieDrop(zombie) + "\n";
                if (!(currentLevel instanceof MiniGame)) {
                    QuestController.notifyZombieKilled(zombie);
                    if (currentLevel.getCurrentSeason() != null) {
                        QuestController.notifyZombieKilled(currentLevel.getCurrentSeason());
                    }
                }
            }
        }
        if (killedThisTick > 0 && BonusGameController.isActive()) {
            String result = BonusGameController.recordKills(killedThisTick, damageThisTick,
                    !BonusGameController.getGame().hasRemainingZombies() && currentLevel.getActiveZombies().isEmpty(),
                    currentLevel.getCurrentTick());
            if (!result.isEmpty()) System.out.println(result);
        }
        return dropMessage;
    }

    private String updateWaves(float delta) {
        if (currentLevel instanceof VaseBreaker) {
            return "";
        }
        String message = "";
        if (currentLevel.getZombieWave() != null) {
            currentLevel.getZombieWave().update(delta);
            if (currentLevel.getZombieWave().isLastWave()) {
                message = "The final wave has come.\n";
                currentLevel.getZombieWave().setLastWave(false);
            } else if (currentLevel.getZombieWave().isNewWaveStarted()){
                message = "Wave " + (currentLevel.getZombieWave().getCurrentWave() + 1) + " started.\n";
                currentLevel.getZombieWave().setNewWaveStarted(false);
            }
        }
        return message;
    }

    private void updateSkySuns(float delta) {
        if (currentLevel instanceof VaseBreaker) {
            return;
        }
        if (currentLevel.getSkySunProducer() == null) {
            return;
        }
        currentLevel.getSkySunProducer().update(delta);
        if (currentLevel.getSkySunProducer().isProducedASun()) {
            Sun sun = currentLevel.getSkySunProducer().getSun();
//            append(message, "New " + sun.getType().getName() + " sun is dropping at position (" + sun.getX()
//                    + ", " + sun.getY() + ")");
            currentLevel.getSkySunProducer().setProducedASun(false);
        }
    }

    private void updateSuns(float delta) {
        for (Sun sun : currentLevel.getActiveSuns()) {
            boolean wasFalling = sun.isFalling();
            sun.update(delta);
            if (wasFalling && !sun.isFalling()) {
//                append(message, "Sun reached the ground at position (" + sun.getX() + ", " + sun.getY() + ")");
            }
        }
    }

    private void updateTiles(float delta) {
        GameMap map = currentLevel.getGameMap();
        if (map == null || map.getGrid() == null) {
            return;
        }
        for (int i = 0; i < map.getRows(); i++) {
            for (int j = 0; j < map.getColumns(); j++) {
                Tile tile = map.getGrid()[i][j];
                tile.update(delta);
            }
        }
    }

    private void updateBarrels(float delta) {
        for (Barrel barrel : currentLevel.getBarrels()) {
            barrel.update(delta);
        }
    }

    public void updateSeason(float delta){
        Season season = currentLevel.getCurrentSeason();
        if (season != null) {
            season.Update(currentLevel, delta);
        }
    }

    public void collectSun(Sun sun) {
        sun.collect();
        Plant plant = findPlant((int) sun.getX(), (int) sun.getY());
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

//    public void plantPlant(String input) {
//        Matcher matcher = Pattern.compile(Commands.PLANT_PLANT.getPattern()).matcher(input);
//        if (!matcher.matches()) {
//            System.out.println("invalid command");
//            return;
//        }
//        String type = matcher.group("type").trim();
//        int x = Integer.parseInt(matcher.group("x"));
//        int y = Integer.parseInt(matcher.group("y"));
//        String error = getPlantingError(type, x, y);
//
//        if (currentLevel instanceof VaseBreaker) {
//            plantVasebreakerSeed((VaseBreaker) currentLevel, type, x, y);
//            return;
//        }
//        if (error != null) {
//            System.out.println(error);
//            return;
//        }
//        PlantData data = PlantRepository.getInstance().findByName(type);
//        Plant plant = new Plant(data, x, y, 1);
//        if (currentLevel.getSpecialLevel() instanceof LockedPlantsLevel &&
//            ((LockedPlantsLevel) currentLevel.getSpecialLevel()).isPlantLocked(plant.getData().getName())) {
//            System.out.println("Plant is locked");
//            return;
//        }
//        currentLevel.getActivePlants().add(plant);
//        if (currentLevel.getCurrentSeason() != null) {
//            currentLevel.getCurrentSeason().PlantPlaced(currentLevel, plant, x, y);
//        }
//        currentLevel.getGameMap().getTile(plant.getX(), plant.getY()).setPlant(plant);
//        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() - data.getSunCost());
//        if (!cooldownRemoved) {
//            plantCooldowns.put(data.getName().toLowerCase(), secondsToTicks(data.getRecharge()));
//        }
//        if (isBoostedPlant(plant)) plant.activatePlantFood();
//        if (App.getCurrentUser().getGreenHouse().storedBoosts.remove(data.getName().toLowerCase()) != null)
//            plant.activatePlantFood();
//        QuestController.onPlantPlaced(plant);
//        System.out.println("Plant " + data.getDisplayName() + " planted at (" + x + ", " + y + ")");
//    }

//    private String getPlantingError(String type, int x, int y) {
//        PlantData data = PlantRepository.getInstance().findByName(type);
//        if (data == null) return "plant type does not exist";
//        if (!currentLevel.getChosenPlants().isEmpty() &&
//            currentLevel.getChosenPlants().stream().noneMatch(name -> name.equalsIgnoreCase(type)))
//            return "plant was not selected";
//        Tile tile = currentLevel.getGameMap().getTile(x, y);
//        if (tile == null) {
//            return "location is out of map";
//        }
//        Plant temp = new Plant(data, x, y, 1);
//        if (!PlantController.canPlaceOnTile(temp, tile)) {
//            return "cannot plant on this tile";
//        }
//        if (currentLevel.getCollectedSunsAmount() < data.getSunCost()) {
//            return "not enough suns";
//        }
//        if (!cooldownRemoved && plantCooldowns.getOrDefault(data.getName().toLowerCase(), 0) > 0) {
//            return "plant is on cooldown";
//        }
//        return null;
//    }
//
//    public void cheatRemoveCooldown() {
//        cooldownRemoved = true;
//        plantCooldowns.clear();
//        System.out.println("all plant cooldowns removed");
//    }
//
//    public void pluckPlant(String input) {
//        Matcher matcher = Pattern.compile(Commands.PLUCK_PLANT.getPattern()).matcher(input);
//        if (!matcher.matches()) {
//            System.out.println("invalid command");
//            return;
//        }
//        int x = Integer.parseInt(matcher.group("x"));
//        int y = Integer.parseInt(matcher.group("y"));
//        Plant plant = findPlant(x, y);
//        if (plant == null) {
//            System.out.println("there is no plant at this location");
//            return;
//        }
//        currentLevel.getActivePlants().remove(plant);
//        Tile tile = currentLevel.getGameMap().getTile(x, y);
//        if (tile != null) {
//            tile.removePlant();
//        }
//        System.out.println("Plant " + plant.getData().getDisplayName() + " at (" + x + ", " + y + ") removed");
//    }
//
//    public void feedPlant(String input) {
//        Matcher matcher = Pattern.compile(Commands.FEED_PLANT.getPattern()).matcher(input);
//        if (!matcher.matches()) {
//            System.out.println("invalid command");
//            return;
//        }
//        int x = Integer.parseInt(matcher.group("x"));
//        int y = Integer.parseInt(matcher.group("y"));
//        Plant plant = findPlant(x, y);
//        if (plant == null) {
//            System.out.println("there is no plant at this location");
//            return;
//        }
//        if (currentLevel.getPlantFoodCount() <= 0) {
//            System.out.println("you do not have plant food");
//            return;
//        }
//        currentLevel.setPlantFoodCount(currentLevel.getPlantFoodCount() - 1);
//        plant.activatePlantFood();
//        System.out.println("Plant " + plant.getData().getDisplayName() + " at (" + x + ", " + y + ") was fed; you have "
//                + currentLevel.getPlantFoodCount() + " plant foods now");
//    }
//
//    public void cheatAddPlantFood() {
//        if (currentLevel.getPlantFoodCount() >= MAX_PLANT_FOOD) {
//            System.out.println("plant food storage is full");
//            return;
//        }
//        currentLevel.setPlantFoodCount(currentLevel.getPlantFoodCount() + 1);
//        System.out.println("you have " + currentLevel.getPlantFoodCount() + " plant foods now");
//    }

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
        for (String plantName : currentLevel.getChosenPlants()) {
            PlantData data = repository.findByName(plantName);
            if (data == null) continue;
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
        isGameEnded = true;
        if (BonusGameController.isActive()) {
            System.out.println(BonusGameController.endGame());
            return;
        }
        App.getCurrentUser().setVictroy(false);
        SignupMenuController.saveToJson();
        App.setCurrentMenu(Menu.GAME_MENU);
//        this.currentLevel = null;
    }

    public String gameWon(){
        isGameEnded = true;
        if (showSunsAmount() == 0) {
            QuestController.notifyNoSunsLeft();
        }
        QuestController.notifyPlantsDestroyed(currentLevel.getRemovedPlantsCount());
        App.getCurrentUser().setVictroy(true);
        App.getCurrentUser().addGamesPlayed();
        App.getCurrentUser().addChapters();
        App.getCurrentUser().recordLevelVictory(currentLevel.getCurrentSeason(), currentLevel.getData(), 0);
        if (currentLevel.getCurrentSeason() != null) {
            LevelData next = App.getLevelByNumber(currentLevel.getLevelNumber() + 1, currentLevel.getCurrentSeason());
            if (next != null) next.setUnlocked(true);
            else {
                int nextSeasonId = currentLevel.getCurrentSeason().getData().getId() + 1;
                for (Season season : App.getAllSeasons()) {
                    if (season.getData().getId() == nextSeasonId) {
                        season.setUnlocked(true);
                        if (!season.getLevels().isEmpty()) season.getLevels().get(0).setUnlocked(true);
                        break;
                    }
                }
            }
        }
        App.setCurrentMenu(Menu.GAME_MENU);
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
                    if(projectile.getCreatorPlantCategory().hasThisTag(PlantTag.ICE)){
                        zombie.setChilled(true);
                    }
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

    int secondsToTicks(double seconds) {
        return Math.max(1, (int) Math.ceil(seconds * TICKS_PER_SECOND));
    }

    boolean isBoostedPlant(Plant plant) {
        return plant.isBoosted();
    }

    private String handleZombieDrop(Zombie zombie) {
        if (zombie.isGlowing() && currentLevel.getPlantFoodCount() < MAX_PLANT_FOOD) {
            currentLevel.setPlantFoodCount(currentLevel.getPlantFoodCount() + 1);
            return "The glowing zombie dropped a plant food; you have "
                + currentLevel.getPlantFoodCount() + " plant foods now.";
        }
        if (Math.random() >= 0.10 || App.getCurrentUser() == null) {
            return "";
        }
        int drop = (int) (Math.random() * 3);
        if (drop == 0) {
            App.getCurrentUser().addCoins(50);
            return "A zombie dropped 50 coins; you have " + App.getCurrentUser().getCoinsCount() + " coins now.";
        } else if (drop == 1) {
            App.getCurrentUser().addGems(1);
            return "A zombie dropped 1 diamond; you have " + App.getCurrentUser().getGemsCount() + " diamonds now.";
        } else {
            int unlocked = App.getCurrentUser().getGreenHouse().unlockPots(1);
            if (unlocked > 0) {
                return "A zombie dropped a pot; you have " +
                    App.getCurrentUser().getGreenHouse().getPotsCount() + " pots now.";
            } else {
                App.getCurrentUser().addCoins(50);
                return "The greenhouse is full; the pot was converted to 50 coins.";
            }
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
        PlantData data = PlantRepository.getInstance().findByName(plantName);
        Tile tile = level.getGameMap().getTile(x, y);
        if (data == null) {
            System.out.println("plant type does not exist");
            return;
        }
        if (tile == null || tile.getPlant() != null || level.hasUnbrokenVaseAt(x, y)) {
            System.out.println("cannot plant on this tile");
            return;
        }
        if (!level.consumeSeedPacket(plantName)) {
            System.out.println("You do not have a " + plantName + " seed packet!");
            return;
        }
        Plant plant = new Plant(data, x, y, 1);
        level.getActivePlants().add(plant);
        tile.setPlant(plant);
        System.out.println("Planted " + plantName + " at (" + x + ", " + y + ")");
    }

    public boolean isIsGameEnded() {
        return isGameEnded;
    }

    public void setIsGameEnded(boolean isGameEnded) {
        GameManagerController.isGameEnded = isGameEnded;
    }
}
