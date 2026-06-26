package controllers;

import enums.Commands;
import models.App;
import models.Collection;
import models.User;
import models.plants.Plant;
import models.plants.PlantData;
import models.plants.PlantRepository;
import models.plants.PlantUpgradeData;
import models.zombies.Zombie;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectionMenuController {

    private static final int PLANT_PRICE = 2000;

    public static StringBuilder showAchievedPlants() {
        StringBuilder result = new StringBuilder();
        Collection collection = getCollection();
        if (collection == null || collection.getAvailablePlants().isEmpty()) {
            return result.append("no plants achieved\n");
        }
        for (Plant plant : collection.getAvailablePlants()) {
            result.append(plant.getData().getDisplayName()).append("\n");
        }
        return result;
    }

    public static StringBuilder showAllPlants() {
        StringBuilder result = new StringBuilder();
        List<PlantData> plants = getPlantRepository().getAllPlants();
        if (plants.isEmpty()) {
            return result.append("no plants defined\n");
        }
        for (PlantData plant : plants) {
            result.append(plant.getDisplayName()).append("\n");
        }
        return result;
    }

    public static StringBuilder showSeenZombies() {
        StringBuilder result = new StringBuilder();
        Collection collection = getCollection();
        if (collection == null || collection.getAvailableZombies().isEmpty()) {
            return result.append("no zombies seen\n");
        }
        for (Zombie zombie : collection.getAvailableZombies()) {
            result.append(zombie.getData().getDisplayName()).append("\n");
        }
        return result;
    }

    public static StringBuilder showAllZombies() {
        StringBuilder result = new StringBuilder();
        List<ZombieData> zombies = ZombieRepository.getInstance().getAllZombies();
        if (zombies.isEmpty()) {
            return result.append("no zombies defined\n");
        }
        for (ZombieData zombie : zombies) {
            result.append(zombie.getDisplayName()).append("\n");
        }
        return result;
    }

    public static StringBuilder showPlant(String input) {
        Matcher matcher = getMatcher(Commands.COLLECTION_SHOW_PLANT, input);
        if (!matcher.matches()) {
            return new StringBuilder();
        }
        String plantName = matcher.group("plant_name");
        PlantData plant = getPlantRepository().findByName(plantName);
        if (plant == null) {
            return new StringBuilder("plant not found\n");
        }
        StringBuilder result = new StringBuilder();
        result.append("name: ").append(plant.getDisplayName()).append("\n");
        result.append("id: ").append(plant.getId()).append("\n");
        result.append("category: ").append(plant.getCategory()).append("\n");
        result.append("tags: ").append(plant.getTags()).append("\n");
        result.append("sun cost: ").append(plant.getSunCost()).append("\n");
        result.append("hp: ").append(plant.getBaseHp()).append("\n");
        result.append("damage: ").append(plant.getDamage()).append("\n");
        result.append("recharge: ").append(plant.getRecharge()).append("\n");
        result.append("ability: ").append(plant.getBaseAbility()).append("\n");
        result.append("plant food effect: ").append(plant.getPlantFoodEffect()).append("\n");
        result.append("description: ").append(plant.getDescription()).append("\n");
        return result;
    }

    public static StringBuilder showZombie(String input) {
        Matcher matcher = getMatcher(Commands.COLLECTION_SHOW_ZOMBIE, input);
        if (!matcher.matches()) {
            return new StringBuilder();
        }
        String zombieName = matcher.group("zombie_name");
        ZombieData zombie = ZombieRepository.getInstance().findByDisplayName(zombieName);
        if (zombie == null) {
            return new StringBuilder("zombie not found\n");
        }
        StringBuilder result = new StringBuilder();
        result.append("name: ").append(zombie.getDisplayName()).append("\n");
        result.append("id: ").append(zombie.getId()).append("\n");
        result.append("seasons: ").append(zombie.getSeasons()).append("\n");
        result.append("health: ").append(zombie.getHP()).append("\n");
        result.append("damage: ").append(zombie.getEatDPS()).append("\n");
        result.append("speed: ").append(zombie.getSpeed()).append("\n");
        result.append("wave cost: ").append(zombie.getWaveCost()).append("\n");
        return result;
    }

    public static void upgradePlant(String input) {
        Matcher matcher = getMatcher(Commands.COLLECTION_UPGRADE, input);
        if (!matcher.matches()) {
            return;
        }
        User user = App.getCurrentUser();
        if (user == null) {
            System.out.println("no user is logged in");
            return;
        }
        Plant plant = findAvailablePlant(matcher.group("plant_name"));
        if (plant == null) {
            System.out.println("plant is not purchased");
            return;
        }
        PlantUpgradeData upgrade = findNextUpgrade(plant);
        if (upgrade == null) {
            System.out.println("plant is already at max level");
            return;
        }
        if (user.getCoinsCount() < upgrade.getRequiredCoins()) {
            System.out.println("not enough coins");
            return;
        }
        if (user.getSeedPacketCount(plant.getData().getDisplayName()) < upgrade.getRequiredSeedPackets()) {
            System.out.println("not enough seed packets");
            return;
        }
        user.setCoinsCount(user.getCoinsCount() - upgrade.getRequiredCoins());
        user.spendSeedPackets(plant.getData().getDisplayName(), upgrade.getRequiredSeedPackets());
        plant.setLevel(upgrade.getLevel());
        System.out.println("plant upgraded successfully");
    }

    public static void purchasePlant(String input) {
        Matcher matcher = getMatcher(Commands.COLLECTION_PURCHASE, input);
        if (!matcher.matches()) {
            return;
        }
        User user = App.getCurrentUser();
        if (user == null) {
            System.out.println("no user is logged in");
            return;
        }
        String plantName = matcher.group("plant_name");
        PlantData plantData = getPlantRepository().findByName(plantName);
        if (plantData == null) {
            System.out.println("plant not found");
            return;
        }
        if (findAvailablePlant(plantName) != null) {
            System.out.println("plant already purchased");
            return;
        }
        if (user.getCoinsCount() < PLANT_PRICE) {
            System.out.println("not enough coins");
            return;
        }
        user.setCoinsCount(user.getCoinsCount() - PLANT_PRICE);
        user.getCollection().getAvailablePlants().add(new Plant(plantData, 0, 0, 1));
        System.out.println("plant purchased successfully");
    }

    private static Collection getCollection() {
        User user = App.getCurrentUser();
        return user == null ? null : user.getCollection();
    }

    private static Plant findAvailablePlant(String name) {
        Collection collection = getCollection();
        if (collection == null) {
            return null;
        }
        for (Plant plant : collection.getAvailablePlants()) {
            PlantData data = plant.getData();
            if (data.getDisplayName().equalsIgnoreCase(name) || data.getName().equalsIgnoreCase(name)
                    || data.getId().equalsIgnoreCase(name)) {
                return plant;
            }
        }
        return null;
    }

    private static PlantUpgradeData findNextUpgrade(Plant plant) {
        if (plant.getData().getUpgrades() == null) {
            return null;
        }
        for (PlantUpgradeData upgrade : plant.getData().getUpgrades()) {
            if (upgrade.getLevel() == plant.getLevel() + 1) {
                return upgrade;
            }
        }
        return null;
    }

    private static Matcher getMatcher(Commands command, String input) {
        return Pattern.compile(command.getPattern()).matcher(input);
    }

    private static PlantRepository getPlantRepository() {
        PlantRepository repository = new PlantRepository("src/assets/Plants.json");
        if (!repository.getAllPlants().isEmpty()) {
            return repository;
        }
        return new PlantRepository("assets/Plants.json");
    }

//    private static ZombieRepository getZombieRepository() {
//        ZombieRepository repository = new ZombieRepository("src/assets/Zombies.json");
//        if (!repository.getAllZombies().isEmpty()) {
//            return repository;
//        }
//        return new ZombieRepository("assets/Zombies.json");
//    }
}