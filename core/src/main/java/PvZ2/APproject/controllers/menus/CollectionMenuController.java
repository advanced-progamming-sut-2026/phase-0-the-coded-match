package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Collection;
import PvZ2.APproject.models.User;
import PvZ2.APproject.models.plants.Plant;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;
import PvZ2.APproject.models.plants.PlantUpgradeData;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectionMenuController {

    private static final int PLANT_PRICE = 2000;

    public static StringBuilder showAchievedPlants() {
        StringBuilder result = new StringBuilder();
        Collection collection = getCollection();
        if (collection == null || collection.getAvailablePlantsIds().isEmpty()) {
            return result.append("no plants achieved\n");
        }
        for (PlantData plant : PlantRepository.getInstance().getAllPlants()) {
            if (isPlantUnlockedForUi(plant)) {
                result.append(plant.getDisplayName()).append("\n");
            }
        }
        return result;
    }

    public static StringBuilder showAllPlants() {
        StringBuilder result = new StringBuilder();
        List<PlantData> plants = PlantRepository.getInstance().getAllPlants();
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
        if (collection == null || collection.getAvailableZombiesIds().isEmpty()) {
            return result.append("no zombies seen\n");
        }
        for (ZombieData zombie : ZombieRepository.getInstance().getAllZombies()) {
            if (isZombieSeenForUi(zombie)) {
                result.append(zombie.getDisplayName()).append("\n");
            }
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
        PlantData plant = PlantRepository.getInstance().findByName(matcher.group("plantName"));
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
        ZombieData zombie = ZombieRepository.getInstance().findByDisplayName(matcher.group("zombieName"));
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
        System.out.println(upgradePlantByName(matcher.group("plantName")));
    }

    public static void purchasePlant(String input) {
        Matcher matcher = getMatcher(Commands.COLLECTION_PURCHASE, input);
        if (!matcher.matches()) {
            return;
        }
        System.out.println(purchasePlantByName(matcher.group("plantName")));
    }

    public static String upgradePlantByName(String plantName) {
        User user = App.getCurrentUser();
        if (user == null) {
            return "no user is logged in";
        }
        Plant plant = findAvailablePlant(plantName);
        if (plant == null) {
            return "plant is not purchased";
        }
        PlantUpgradeData upgrade = findNextUpgrade(plant);
        if (upgrade == null) {
            return "plant is already at max level";
        }
        if (user.getCoinsCount() < upgrade.getRequiredCoins()) {
            return "not enough coins";
        }
        if (user.getSeedPacketCount(plant.getData().getDisplayName()) < upgrade.getRequiredSeedPackets()) {
            return "not enough seed packets";
        }
        user.setCoinsCount(user.getCoinsCount() - upgrade.getRequiredCoins());
        user.spendSeedPackets(plant.getData().getDisplayName(), upgrade.getRequiredSeedPackets());
        plant.setLevel(upgrade.getLevel());
        return "plant upgraded successfully";
    }

    public static String purchasePlantByName(String plantName) {
        User user = App.getCurrentUser();
        if (user == null) {
            return "no user is logged in";
        }
        PlantData plantData = PlantRepository.getInstance().findByName(plantName);
        if (plantData == null) {
            return "plant not found";
        }
        if (findAvailablePlant(plantName) != null) {
            return "plant already purchased";
        }
        if (user.getCoinsCount() < PLANT_PRICE) {
            return "not enough coins";
        }
        user.setCoinsCount(user.getCoinsCount() - PLANT_PRICE);
        user.getCollection().addPlant(new Plant(plantData, 0, 0, 1));
        return "plant purchased successfully";
    }

    public static boolean isPlantUnlockedForUi(PlantData plant) {
        Collection collection = getCollection();
        if (collection == null || plant == null) {
            return false;
        }
        for (String id : collection.getAvailablePlantsIds()) {
            if (id.equalsIgnoreCase(plant.getId())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isZombieSeenForUi(ZombieData zombie) {
        Collection collection = getCollection();
        if (collection == null || zombie == null) {
            return false;
        }
        for (String id : collection.getAvailableZombiesIds()) {
            if (id.equalsIgnoreCase(zombie.getId())) {
                return true;
            }
        }
        return false;
    }

    public static Plant getAvailablePlantForUi(PlantData plantData) {
        if (plantData == null) {
            return null;
        }
        return findAvailablePlant(plantData.getDisplayName());
    }

    public static int getPlantLevelForUi(PlantData plantData) {
        Plant plant = getAvailablePlantForUi(plantData);
        return plant == null ? 0 : plant.getLevel();
    }

    public static PlantUpgradeData getNextUpgradeForUi(PlantData plantData) {
        Plant plant = getAvailablePlantForUi(plantData);
        return plant == null ? null : findNextUpgrade(plant);
    }

    public static boolean canUpgradeForUi(PlantData plantData) {
        User user = App.getCurrentUser();
        PlantUpgradeData next = getNextUpgradeForUi(plantData);
        if (user == null || next == null || plantData == null) {
            return false;
        }
        return user.getCoinsCount() >= next.getRequiredCoins()
            && user.getSeedPacketCount(plantData.getDisplayName()) >= next.getRequiredSeedPackets();
    }

    public static int getPlantPrice() {
        return PLANT_PRICE;
    }

    private static Collection getCollection() {
        User user = App.getCurrentUser();
        return user == null ? null : user.getCollection();
    }

    private static Plant findAvailablePlant(String name) {
        Collection collection = getCollection();
        if (collection == null || name == null) {
            return null;
        }
        if (collection.getAvailablePlants() != null) {
            for (Plant plant : collection.getAvailablePlants()) {
                if (plant.getData().getDisplayName().equalsIgnoreCase(name)
                    || plant.getData().getId().equalsIgnoreCase(name)) {
                    return plant;
                }
            }
        }
        PlantData data = PlantRepository.getInstance().findByName(name);
        if (data != null && collection.getAvailablePlantsIds().stream().anyMatch(id -> id.equalsIgnoreCase(data.getId()))) {
            Plant plant = new Plant(data, 0, 0, 1);
            collection.getAvailablePlants().add(plant);
            return plant;
        }
        return null;
    }

    private static PlantUpgradeData findNextUpgrade(Plant plant) {
        if (plant == null || plant.getData().getUpgrades() == null) {
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
}
