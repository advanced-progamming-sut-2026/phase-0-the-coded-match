package controllers;

import controllers.menus.SignupMenuController;
import enums.Commands;
import enums.LevelType;
import enums.Menu;
import enums.PlantTag;
import enums.SunType;
import models.App;
import models.GameMapRelated.GameMap;
import models.GameMapRelated.Lawnmower;
import models.Level;
import models.MiniGameRelated.VaseBreaker;
import models.Sun;
import models.Projectile;
import models.GameMapRelated.Tile;
import models.plants.Plant;
import models.plants.PlantData;
import models.plants.PlantRepository;
import models.seasons.Season;
import models.specialLevels.ConveyorBeltStrategy;
import models.zombies.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameManagerController {
    private static GameManagerController instance;
    private static final int MAX_PLANT_FOOD = 3;
    private static final int TICKS_PER_SECOND = 10;
    private Level currentLevel;
    private static boolean cooldownRemoved;
    private static final Map<String, Integer> plantCooldowns = new HashMap<>();
    private boolean gameEnded;
    private double simulationTickAccumulator;

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
        gameEnded = false;
        plantCooldowns.clear();
        simulationTickAccumulator = 0;
    }

    public String[] advanceTime(String input, String[] message) {
        message[0] = "";
        Matcher matcher = Pattern.compile(Commands.ADVANCE_TIME.getPattern()).matcher(input);
        if (!matcher.matches()) {
            message[0] = "invalid command";
            return message;
        }
        int count = Integer.parseInt(matcher.group("count"));
        if (currentLevel == null || gameEnded) {
            message[0] = "no active game";
            return message;
        }
        int difficulty = App.getCurrentUser() == null ? 3 : App.getCurrentUser().getDifficultyLevel();
        double speedFactor = difficulty / 3.0;
        for (int i = 0; i < count; i++) {
            simulationTickAccumulator += speedFactor;
            while (simulationTickAccumulator >= 1.0 && !gameEnded) {
                currentLevel.setCurrentTick(currentLevel.getCurrentTick() + 1);
                updateObjects(message);
                simulationTickAccumulator -= 1.0;
            }
        }
        return message;
    }

    public String[] updateObjects(String[] message) {
        if (currentLevel == null || gameEnded) {
            return message;
        }
        if (currentLevel.getLevelType() == LevelType.I_ZOMBIE) {
            decreasePlantCooldowns();
            updatePlants(message);
            updateZombies(message);
            updateProjectiles();
        } else {
            decreasePlantCooldowns();
            updatePlants(message);
            updateBarrels();
            updateZombies(message);
            updateWaves();
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
                if (tile != null) {
                    if (tile.getPlant() == plant) {
                        tile.removePlant();
                    } else if (tile.getLilyPadPlant() == plant) {
                        tile.setLilyPadPlant(null);
                        if (tile.getPlant() != null) {
                            tile.getPlant().setCurrentHp(0);
                        }
                    }
                }
                iterator.remove();
                currentLevel.setRemovedPlantsCount(currentLevel.getRemovedPlantsCount() + 1);
                if (currentLevel.getSpecialLevel() != null) {
                    currentLevel.getSpecialLevel().plantLost(currentLevel, plant);
                }
            }
        }
    }

    private void updateZombies(String[] message) {
        for (Zombie zombie : new java.util.ArrayList<>(currentLevel.getActiveZombies())) {
            if (!currentLevel.getActiveZombies().contains(zombie)) {
                continue;
            }
            zombie.update();
            if (!zombie.isDead() && zombie.getX() <= 0) {
                App.handleLawnMower(zombie);
                if (gameEnded) {
                    return;
                }
            }
            if (zombie.isDead() && currentLevel.getActiveZombies().remove(zombie)) {
                currentLevel.incrementZombiesKilledCount();
                append(message, "Zombie of type " + zombie.getData().getDisplayName() + " is dead at ("
                        + String.format("%.2f", zombie.getX()) + ", " + zombie.getY() + ")");
                handleZombieDrop(zombie, message);
                if (QuestController.isReady()) {
                    QuestController.notifyZombieKilled(zombie);
                    if (currentLevel.getCurrentSeason() != null) {
                        QuestController.notifyZombieKilled(currentLevel.getCurrentSeason());
                    }
                }
                boolean allZombiesDead = currentLevel.getActiveZombies().stream().allMatch(Zombie::isDead);
                BonusGameController.handleZombieKilled(zombie, allZombiesDead);
            }
        }
    }

    private void updateWaves() {
        if (currentLevel instanceof VaseBreaker) {
            return;
        }
        if (currentLevel.getZombieWave() != null) {
            currentLevel.getZombieWave().update();
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
        Iterator<Barrel> iterator = currentLevel.getBarrels().iterator();
        while (iterator.hasNext()) {
            Barrel barrel = iterator.next();
            barrel.update();
            if (barrel.isDestroyed()) {
                iterator.remove();
            }
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
        boolean radioactiveExplosion = sun.isFalling() && sun.getType() == SunType.RADIOACTIVE;
        sun.collect();
        if (radioactiveExplosion) {
            System.out.println("radioactive sun exploded");
        } else {
            System.out.println("sun collected; you have " + currentLevel.getCollectedSunsAmount() + " suns now");
        }
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

    public void cheatAddSuns(String input) {
        Matcher matcher = Pattern.compile(Commands.CHEAT_ADD_SUNS.getPattern()).matcher(input);
        if (!matcher.matches()) {
            System.out.println("invalid command");
            return;
        }
        int count = Integer.parseInt(matcher.group("count"));
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() + count);
        System.out.println("you have " + currentLevel.getCollectedSunsAmount() + " suns now");
    }

    public String[] startWave() {
        if (currentLevel == null) {
            return new String[] {"no active level"};
        }
        currentLevel.setZombieWavesEnabled(true);
        return new String[] {"zombie waves started"};
    }

    public void cheatReleaseTheNuke() {
        ZombieWaveManager.releaseTheNuke();
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
        if (currentLevel instanceof VaseBreaker) {
            plantVasebreakerSeed((VaseBreaker) currentLevel, type, x, y);
            return;
        }
        PlantData data = PlantRepository.getInstance().findByName(type);
        String error = getPlantingError(type, x, y);
        if (error != null) {
            System.out.println(error);
            return;
        }
        if (currentLevel.getSpecialLevel() instanceof ConveyorBeltStrategy) {
            String packet = findChosenPlant(data);
            if (packet == null) {
                System.out.println("plant is not available on conveyor");
                return;
            }
            currentLevel.getChosenPlants().remove(packet);
        }
        Tile tile = currentLevel.getGameMap().getTile(x, y);
        Plant existing = tile.getPlant();
        Plant placedPlant = new Plant(data, x, y, 1);
        if (existing != null && isPumpkin(placedPlant)) {
            existing.setCoverHp(Math.max(existing.getCoverHp(), data.getBaseHp()));
            finishPlanting(data, existing, placedPlant, x, y);
            System.out.println("Plant " + data.getDisplayName() + " planted at (" + x + ", " + y + ")");
            return;
        }
        if (existing != null && isSameStackablePlant(existing, placedPlant)) {
            existing.incrementStackCount();
            finishPlanting(data, existing, placedPlant, x, y);
            System.out.println("Plant " + data.getDisplayName() + " stacked at (" + x + ", " + y + ")");
            return;
        }
        currentLevel.getActivePlants().add(placedPlant);
        if (isLilyPad(placedPlant)) {
            tile.setLilyPadPlant(placedPlant);
        } else {
            tile.setPlant(placedPlant);
        }
        finishPlanting(data, placedPlant, placedPlant, x, y);
        System.out.println("Plant " + data.getDisplayName() + " planted at (" + x + ", " + y + ")");
    }

    private void finishPlanting(PlantData data, Plant activePlant, Plant questPlant, int x, int y) {
        currentLevel.setCollectedSunsAmount(currentLevel.getCollectedSunsAmount() - data.getSunCost());
        if (!cooldownRemoved) {
            plantCooldowns.put(data.getName().toLowerCase(), secondsToTicks(data.getRecharge()));
        }
        if (isBoostedPlant(activePlant)) {
            activePlant.activatePlantFood();
        }
        if (currentLevel.getCurrentSeason() != null) {
            currentLevel.getCurrentSeason().PlantPlaced(currentLevel, questPlant, x, y);
        }
        if (QuestController.isReady()) {
            QuestController.onPlantPlaced(questPlant);
        }
    }

    private String getPlantingError(String type, int x, int y) {
        PlantData data = PlantRepository.getInstance().findByName(type);
        if (data == null) {
            return "plant type does not exist";
        }
        if (!(currentLevel.getSpecialLevel() instanceof ConveyorBeltStrategy) && findChosenPlant(data) == null) {
            return "plant was not selected";
        }
        Tile tile = currentLevel.getGameMap().getTile(x, y);
        if (tile == null) {
            return "location is out of map";
        }
        Plant temp = new Plant(data, x, y, 1);
        if (!PlantController.canPlaceOnTile(temp, tile)) {
            return "cannot plant on this tile";
        }
        if (tile.getPlant() != null && !isPumpkin(temp) && !isSameStackablePlant(tile.getPlant(), temp)) {
            return "cannot stack these plants";
        }
        if (currentLevel.getCollectedSunsAmount() < data.getSunCost()) {
            return "not enough suns";
        }
        if (!cooldownRemoved && plantCooldowns.getOrDefault(data.getName().toLowerCase(), 0) > 0) {
            return "plant is on cooldown";
        }
        return null;
    }

    private String findChosenPlant(PlantData data) {
        if (data == null) {
            return null;
        }
        for (String chosen : currentLevel.getChosenPlants()) {
            if (chosen.equalsIgnoreCase(data.getId()) || chosen.equalsIgnoreCase(data.getName())
                    || chosen.equalsIgnoreCase(data.getDisplayName())) {
                return chosen;
            }
        }
        return null;
    }

    private boolean isPumpkin(Plant plant) {
        return plant != null && plant.getData().getName() != null
                && plant.getData().getName().replace("_", " ").replace("-", " ").trim().equalsIgnoreCase("Pumpkin");
    }

    private boolean isSameStackablePlant(Plant first, Plant second) {
        return first != null && second != null && first.hasThisTag(PlantTag.STACK) && second.hasThisTag(PlantTag.STACK)
                && first.getData().getId().equalsIgnoreCase(second.getData().getId())
                && !isPumpkin(second) && !isLilyPad(second);
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
        Tile tile = currentLevel.getGameMap().getTile(x, y);
        Plant plant = tile == null ? null : tile.getPlant();
        if (plant == null && tile != null) {
            plant = tile.getLilyPadPlant();
        }
        if (plant == null) {
            System.out.println("there is no plant at this location");
            return;
        }
        if (plant.getCoverHp() > 0) {
            plant.setCoverHp(0);
            System.out.println("Plant cover at (" + x + ", " + y + ") removed");
            return;
        }
        if (plant.getStackCount() > 1) {
            plant.decrementStackCount();
            System.out.println("One " + plant.getData().getDisplayName() + " layer at (" + x + ", " + y + ") removed");
            return;
        }
        currentLevel.getActivePlants().remove(plant);
        currentLevel.setRemovedPlantsCount(currentLevel.getRemovedPlantsCount() + 1);
        if (tile.getLilyPadPlant() == plant) {
            if (tile.getPlant() != null) {
                tile.getPlant().setCurrentHp(0);
            }
            tile.setLilyPadPlant(null);
        } else {
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
        ZombieWaveManager wave = currentLevel.getZombieWave();
        builder.append("wave: ").append(wave == null ? 0 : wave.getCurrentWave()).append('/')
                .append(wave == null ? currentLevel.getData().getWaveCount() : wave.getTotalWaves()).append('\n');
        builder.append("tick: ").append((long) currentLevel.getCurrentTick()).append('\n');
        builder.append("suns: ").append(currentLevel.getCollectedSunsAmount()).append('\n');
        builder.append("plant foods: ").append(currentLevel.getPlantFoodCount()).append('\n');
        builder.append("lawnmowers: ");
        for (Lawnmower mower : currentLevel.getGameMap().getLawnmowers()) {
            builder.append("row ").append(mower.getRow()).append('=')
                    .append(mower.HasBeenUsed() ? "used" : "ready").append(' ');
        }
        builder.append('\n');
        builder.append("suns on map:");
        if (currentLevel.getActiveSuns().isEmpty()) {
            builder.append(" none");
        } else {
            for (Sun sun : currentLevel.getActiveSuns()) {
                builder.append(' ').append(sun.getType().getName()).append("@(").append(sun.getX()).append(',')
                        .append(sun.getY()).append(')').append(sun.isFalling() ? "[falling]" : "[ground]");
            }
        }
        builder.append('\n');
        int rows = currentLevel.getGameMap().getRows();
        int columns = currentLevel.getGameMap().getColumns();
        for (int y = 1; y <= rows; y++) {
            for (int x = 1; x <= columns; x++) {
                builder.append(tileSymbol(x, y));
            }
            builder.append('\n');
        }
        builder.append("zombies:");
        if (currentLevel.getActiveZombies().isEmpty()) {
            builder.append(" none");
        } else {
            for (Zombie zombie : currentLevel.getActiveZombies()) {
                builder.append(' ').append(zombie.getData().getDisplayName()).append("@(")
                        .append(String.format("%.2f", zombie.getX())).append(',').append(zombie.getY()).append(')');
            }
        }
        return builder;
    }

    private String tileSymbol(int x, int y) {
        Tile tile = currentLevel.getGameMap().getTile(x, y);
        if (tile == null) {
            return "[?]";
        }
        StringBuilder symbol = new StringBuilder("[").append(tileCode(tile));
        if (tile.getLilyPadPlant() != null) {
            symbol.append("+L");
        }
        if (tile.getPlant() != null) {
            symbol.append("+P");
            if (tile.getPlant().getStackCount() > 1) {
                symbol.append(tile.getPlant().getStackCount());
            }
            if (tile.getPlant().getCoverHp() > 0) {
                symbol.append("+C");
            }
        }
        int zombies = 0;
        for (Zombie zombie : currentLevel.getActiveZombies()) {
            if ((int) Math.round(zombie.getX()) == x && zombie.getY() == y && !zombie.isDead()) {
                zombies++;
            }
        }
        if (zombies > 0) {
            symbol.append("+Z").append(zombies);
        }
        int suns = 0;
        for (Sun sun : currentLevel.getActiveSuns()) {
            if (sun.getX() == x && sun.getY() == y) {
                suns++;
            }
        }
        if (suns > 0) {
            symbol.append("+S").append(suns);
        }
        if (tile.isGrave() && tile.getGraveReward() != Tile.GraveReward.NONE) {
            symbol.append(':').append(tile.getGraveReward().name());
        }
        return symbol.append(']').toString();
    }

    private String tileCode(Tile tile) {
        return switch (tile.getType()) {
            case NORMAL -> "N";
            case GRAVE -> "G";
            case ICE -> "I";
            case SLIDE_UP -> "SU";
            case SLIDE_DOWN -> "SD";
            case WATER -> "W";
            case LOW_TIDE -> "LT";
            case NECROMANCY -> "NC";
        };
    }

    public StringBuilder showPlantsStatus() {
        StringBuilder builder = new StringBuilder();
        PlantRepository repository = PlantRepository.getInstance();
        for (PlantData data : repository.getAllPlants()) {
            boolean selected = findChosenPlant(data) != null;
            int cooldown = plantCooldowns.getOrDefault(data.getName().toLowerCase(), 0);
            builder.append(data.getDisplayName()).append(" | cost: ").append(data.getSunCost())
                    .append(" | selected: ").append(selected ? "yes" : "no");
            if (!selected) {
                builder.append(" | unavailable: not selected");
            } else if (currentLevel.getCollectedSunsAmount() < data.getSunCost()) {
                builder.append(" | unavailable: not enough suns");
            } else if (!cooldownRemoved && cooldown > 0) {
                builder.append(" | cooldown: ")
                        .append(String.format("%.1f", cooldown / (double) TICKS_PER_SECOND)).append("s");
            } else {
                builder.append(" | ready");
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
        builder.append("tile: ").append(tile.getType()).append(" hp: ").append(tile.getCurrentHp())
                .append(" plantable: ").append(tile.getType().isCanPlant() && !tile.isGrave()).append('\n');
        if (tile.isGrave()) {
            builder.append("grave reward: ").append(tile.getGraveReward()).append('\n');
        }
        appendPlantDetails(builder, "plant", tile.getPlant());
        appendPlantDetails(builder, "lily pad", tile.getLilyPadPlant());
        builder.append("suns:\n");
        boolean hasSun = false;
        for (Sun sun : currentLevel.getActiveSuns()) {
            if (sun.getX() == x && sun.getY() == y) {
                hasSun = true;
                builder.append(sun.getType()).append(" value: ").append(sun.getValue())
                        .append(" state: ").append(sun.isFalling() ? "falling" : "ground").append('\n');
            }
        }
        if (!hasSun) {
            builder.append("none\n");
        }
        builder.append("zombies:\n");
        boolean hasZombie = false;
        for (Zombie zombie : currentLevel.getActiveZombies()) {
            if ((int) Math.round(zombie.getX()) == x && zombie.getY() == y) {
                hasZombie = true;
                builder.append(zombie.getData().getDisplayName()).append(" hp: ").append(zombie.getCurrentHp())
                        .append('/').append(zombie.getMaxHp()).append(" position: ")
                        .append(String.format("%.2f", zombie.getX())).append(',').append(zombie.getY())
                        .append(" glowing: ").append(zombie.isGlowing()).append('\n');
                builder.append("armor:");
                if (zombie.getArmors().isEmpty()) {
                    builder.append(" none");
                } else {
                    for (ZombieArmor armor : zombie.getArmors()) {
                        builder.append(' ').append(armor.getData().getType().getName()).append('=')
                                .append(armor.getCurrentHp());
                    }
                }
                builder.append("\neffects:");
                if (zombie.getEffects().isEmpty()) {
                    builder.append(" none");
                } else {
                    for (enums.ZombieEffect effect : zombie.getEffects()) {
                        builder.append(' ').append(effect.name()).append('=')
                                .append(String.format("%.1fs", zombie.getEffectRemainingSeconds(effect)));
                    }
                }
                builder.append('\n');
            }
        }
        if (!hasZombie) {
            builder.append("none\n");
        }
        return builder;
    }

    private void appendPlantDetails(StringBuilder builder, String label, Plant plant) {
        if (plant == null) {
            builder.append(label).append(": none\n");
            return;
        }
        PlantData data = plant.getData();
        builder.append(label).append(": ").append(data.getDisplayName()).append(" hp: ")
                .append(plant.getCurrentHp()).append('/').append(data.getBaseHp())
                .append(" damage: ").append(data.getDamage()).append(" action interval: ")
                .append(data.getActionInterval()).append(" recharge: ").append(data.getRecharge())
                .append(" stack: ").append(plant.getStackCount()).append(" cover hp: ").append(plant.getCoverHp())
                .append(" disabled: ").append(plant.isDisabled()).append('\n');
        builder.append("tags: ").append(plant.getTags()).append('\n');
        builder.append("abilities: ").append(data.getAbilities()).append('\n');
    }

    public void ifAZombieWasKilled() {
    }

    public void gameOver() {
        gameEnded = true;
        App.getCurrentUser().setVictroy(false);
        if (QuestController.isReady()) {
            QuestController.onLevelCompleted(false);
        }
        SeasonController.endLevel(currentLevel, false);
        SignupMenuController.saveToJson();
        App.setCurrentMenu(Menu.GAME_MENU);
    }

    public String gameWon() {
        if (gameEnded) {
            return "Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.";
        }
        gameEnded = true;
        App.getCurrentUser().setVictroy(true);
        if (QuestController.isReady()) {
            if (showSunsAmount() == 0) {
                QuestController.notifyNoSunsLeft();
            }
            QuestController.notifyPlantsDestroyed(currentLevel.getRemovedPlantsCount());
            QuestController.onLevelCompleted(true);
        }
        SeasonController.endLevel(currentLevel, true);
        SignupMenuController.saveToJson();
        App.setCurrentMenu(Menu.GAME_MENU);
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
            boolean collided = false;
            for (Zombie zombie : currentLevel.getActiveZombies().toArray(new Zombie[0])) {
                if (projectile.checkZombieCollision(zombie)) {
                    zombie.getBehavior().onProjectileHit(zombie, projectile);
                    collided = true;
                    break;
                }
            }
            if (!collided) {
                int col = (int) Math.round(projectile.getxCoordinate());
                int row = (int) Math.round(projectile.getyCoordinate());
                Tile tile = currentLevel.getGameMap().getTile(col, row);
                if (tile != null && tile.isGrave()) {
                    tile.takeDamage(projectile.getDamage());
                    collided = true;
                }
            }
            if (!collided) {
                for (Barrel barrel : new java.util.ArrayList<>(currentLevel.getBarrels())) {
                    if (projectile.checkBarrelCollision(barrel)) {
                        barrel.onProjectileHit(projectile);
                        collided = true;
                        break;
                    }
                }
            }
            if (collided || projectile.isDestroyed()) {
                iterator.remove();
            }
        }
    }

    private void handleProjectileCollisions() {
        updateProjectiles();
    }

    private void cleanUpDestroyedProjectiles() {
        if (currentLevel == null || currentLevel.getActiveProjectiles() == null) {
            return;
        }
        currentLevel.getActiveProjectiles().removeIf(Projectile::isDestroyed);
    }

    public void spawnProjectile(models.Projectile projectile) {
        if (currentLevel != null && projectile != null) {
            currentLevel.getActiveProjectiles().add(projectile);
        }
    }

    private Plant findPlant(int x, int y) {
        Tile tile = currentLevel == null || currentLevel.getGameMap() == null ? null : currentLevel.getGameMap().getTile(x, y);
        if (tile != null) {
            if (tile.getPlant() != null) {
                return tile.getPlant();
            }
            if (tile.getLilyPadPlant() != null) {
                return tile.getLilyPadPlant();
            }
        }
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
        if (plant.isBoosted()) {
            return true;
        }
        if (currentLevel != null && currentLevel.isPlantBoostedForLevel(plant.getData().getName())) {
            return true;
        }
        return App.getCurrentUser() != null
                && App.getCurrentUser().getGreenHouse().consumeStoredBoost(plant.getData().getName());
    }

    private boolean isLilyPad(Plant plant) {
        String name = plant.getData().getName() == null ? "" : plant.getData().getName();
        return name.replace("_", " ").replace("-", " ").trim().equalsIgnoreCase("Lily Pad");
    }

    private void handleZombieDrop(Zombie zombie, String[] message) {
        if (zombie != null && zombie.isGlowing() && currentLevel.getPlantFoodCount() < MAX_PLANT_FOOD) {
            currentLevel.setPlantFoodCount(currentLevel.getPlantFoodCount() + 1);
            append(message, "The glowing zombie dropeed a plant food; you have "
                    + currentLevel.getPlantFoodCount() + " plant foods now.");
        }
        if (ThreadLocalRandom.current().nextInt(100) >= 10 || App.getCurrentUser() == null) {
            return;
        }
        int reward = ThreadLocalRandom.current().nextInt(3);
        if (reward == 0) {
            App.getCurrentUser().addCoins(50);
            append(message, "A zombie dropeed a coin; you have " + App.getCurrentUser().getCoinsCount() + " coins now.");
        } else if (reward == 1) {
            App.getCurrentUser().addGems(1);
            append(message, "A zombie dropeed a diamond; you have " + App.getCurrentUser().getGemsCount() + " diamonds now.");
        } else {
            boolean unlocked = App.getCurrentUser().getGreenHouse().unlockNextPot();
            int pots = App.getCurrentUser().getGreenHouse().getPotsCount();
            if (unlocked) {
                append(message, "A zombie dropeed a pot; you have " + pots + " pots now.");
            } else {
                App.getCurrentUser().addCoins(50);
                append(message, "All pots are unlocked; the pot was converted to 50 coins. You have "
                        + App.getCurrentUser().getCoinsCount() + " coins now.");
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

        if (!level.consumeSeedPacket(plantName)) {
            System.out.println("You do not have a " + plantName + " seed packet!");
            return;
        }

        PlantData data = PlantRepository.getInstance().findByName(plantName);
        Plant plant = new Plant(data, x, y, 1);
        level.getActivePlants().add(plant);
        level.getGameMap().getTile(x, y).setPlant(plant);
        System.out.println("Planted " + plantName + " at (" + x + ", " + y + ")");
    }
}