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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IZombie extends MiniGame {

    private final int stageNumber;
    private int sunAmount;
    private Boolean[] brainsEatenInLane;
    private double redLineCoordinateX = 6;
    private List<String> availableZombies;

    public IZombie(int stageNumber) {
        super(createIZombieLevelData(stageNumber));
        this.stageNumber = stageNumber;
        this.sunAmount = 150;
        this.brainsEatenInLane = new Boolean[5];
        java.util.Arrays.fill(this.brainsEatenInLane, false);
        this.availableZombies = new ArrayList<>();
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
        ZombieRepository zombies = ZombieRepository.getInstance();


        ZombieData sunZombieData = zombies.findByDisplayName("Sun Zombie");
        for (int row = 1; row <= 5; row++) {
            addActiveZombie(new Zombie(sunZombieData, 9, row));
        }

        switch (stage) {
            case 1:
                setUpStage1(plants);
                break;
            case 2:
                setUpStage2(plants);
                break;
            case 3:
                setUpStage3(plants);
                break;
        }
        for (Tile[] row : getGameMap().getGrid()) for (Tile tile : row) if (tile.getPlant() != null) addActivePlants(tile.getPlant());
    }

    public void setUpStage1(PlantRepository plants) {
        availableZombies.add("Default");
        availableZombies.add("Gargantuar");
        availableZombies.add("Buckethead Zombie");
        availableZombies.add("Knight Zombie");
        availableZombies.add("Brickhead Zombie");

        for (int row = 1; row <= 5; row++) {
            getGameMap().getTile(1, row).setPlant(new Plant(plants.findByName("Peashooter"),
                    1, row, 1));
            getGameMap().getTile(5, row).setPlant(new Plant(plants.findByName("Wall-nut"),
                    5, row, 1));
        }

        getGameMap().getTile(1, 3).setPlant(new Plant(plants.findByName("Cabbage-pult"),
                3, 1, 1));
        getGameMap().getTile(2, 2).setPlant(new Plant(plants.findByName("Cabbage-pult"),
                2, 2, 1));
        getGameMap().getTile(3, 3).setPlant(new Plant(plants.findByName("Cabbage-pult"),
                3, 3, 1));
        getGameMap().getTile(4, 2).setPlant(new Plant(plants.findByName("Cabbage-pult"),
                2, 4, 1));
        getGameMap().getTile(5, 3).setPlant(new Plant(plants.findByName("Cabbage-pult"),
                3, 5, 1));
    }

    public void setUpStage2(PlantRepository plants) {
        availableZombies.add("Default");
        availableZombies.add("Imp");
        availableZombies.add("AllStar");
        availableZombies.add("Arcade");
        availableZombies.add("Parasol Zombie");

        for (int row = 1; row <= 5; row++) {
            getGameMap().getTile(1, row).setPlant(new Plant(plants.findByName("Peashooter"),
                    1, row, 1));
            getGameMap().getTile(2, row).setPlant(new Plant(plants.findByName("Cabbage-pult"),
                    2, row, 1));
            getGameMap().getTile(5, row).setPlant(new Plant(plants.findByName("Wall-nut"),
                    5, row, 1));
        }

        getGameMap().getTile(1, 3).setPlant(new Plant(plants.findByName("Bonk Choy"),
                3, 1, 1));
        getGameMap().getTile(3, 3).setPlant(new Plant(plants.findByName("Bonk Choy"),
                3, 3, 1));
        getGameMap().getTile(5, 3).setPlant(new Plant(plants.findByName("Bonk Choy"),
                3, 5, 1));
    }

    public void setUpStage3(PlantRepository plants) {
        availableZombies.add("Default");
        availableZombies.add("Conehead Zombie");
        availableZombies.add("AllStar");
        availableZombies.add("Parasol Zombie");
        availableZombies.add("Buckethead Zombie");

        for (int row = 1; row <= 5; row++) {
            getGameMap().getTile(1, row).setPlant(new Plant(plants.findByName("Peashooter"),
                    1, row, 1));
            getGameMap().getTile(2, row).setPlant(new Plant(plants.findByName("Cabbage-pult"),
                    2, row, 1));
            getGameMap().getTile(4, row).setPlant(new Plant(plants.findByName("Cabbage-pult"),
                    4, row, 1));
            getGameMap().getTile(5, row).setPlant(new Plant(plants.findByName("Wall-nut"),
                    5, row, 1));
        }
    }

    public void Update() {
        for (Zombie zombie : getActiveZombies()) {
            if (zombie.getX() <= 1) {
                eatBrainAtRow(zombie.getY());
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
        if (!matcher.matches()) {
            return "invalid command";
        }

        String zombieName = matcher.group("name");
        int col = Integer.parseInt(matcher.group("x"));
        int row = Integer.parseInt(matcher.group("y"));
        if (row < 1 || row > 5 || col < 1 || col > 9) return "invalid location";
        if (col < redLineCoordinateX) {
            return "place zombies on the right side of the red line";
        }

        ZombieData zombieData = getZombieDataFromAvailable(zombieName);
        if (zombieData == null) {
            return "this zombie is not available";
        }

        if (sunAmount < zombieData.getCost()) {
            return "not enough sun";
        }

        sunAmount -= zombieData.getCost();
        Zombie newZombie = new Zombie(zombieData, col, row);
        getActiveZombies().add(newZombie);

        return "Placed " + zombieName + " at (" + col + ", " + row + ")";
    }

    public void eatBrainAtRow(int row) {
        brainsEatenInLane[row - 1] = true;
    }

    private ZombieData getZombieDataFromAvailable(String name) {
        for (String z : availableZombies) {
            if (z.equalsIgnoreCase(name)) {
                return ZombieRepository.getInstance().findByDisplayName(z);
            }
        }
        return null;
    }

    public boolean allBrainsEaten() { for (Boolean eaten : brainsEatenInLane) if (!Boolean.TRUE.equals(eaten)) return false; return true; }
    public int getSunAmount() { return sunAmount; }
    public void addSun() { this.sunAmount += 50; }
}
