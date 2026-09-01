package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.LevelData;
import PvZ2.APproject.models.User;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;

import java.util.ArrayList;
import java.util.List;

public class ChoosePlantsMenuController {

    public static String showAllPlants() {
        StringBuilder message = new StringBuilder();
        for (PlantData p : PlantRepository.getInstance().getAllPlants()) {
            message.append(p.getDisplayName()).append("\n");
        }
        return message.toString();
    }

    public static String showAvailablePlants() {
        StringBuilder message = new StringBuilder();
        for (PlantData plant : getAvailablePlantsForUi()) {
            message.append(plant.getDisplayName()).append("\n");
        }
        return message.toString();
    }

    public static String addPlant(String plantName) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return "Choose a level first";
        }
        if (plantName == null || !isPlantAvailableForUi(plantName)) {
            return "Plant is not available";
        }
        if (!isPlantUnlockedForUi(plantName)) {
            return "Plant is locked";
        }
        if (hasPlantBeenChosen(plantName)) {
            return "Plant is already selected";
        }
        if (level.getChosenPlants().size() >= 8) {
            return "You cannot choose more than 8 plants";
        }
        level.addChosenPlant(plantName);
        return "Plant added successfully";
    }

    public static boolean isPlantAvailableForUi(String plantName) {
        if (plantName == null) {
            return false;
        }
        LevelData level = getRelevantLevelData();
        if (level == null || level.getAvailablePlants() == null || level.getAvailablePlants().isEmpty()) {
            return PlantRepository.getInstance().findByName(plantName) != null;
        }
        for (String available : level.getAvailablePlants()) {
            if (available.equalsIgnoreCase(plantName)) {
                return true;
            }
            PlantData data = PlantRepository.getInstance().findByName(plantName);
            if (data != null && available.equalsIgnoreCase(data.getId())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlantUnlockedForUi(String plantName) {
        PlantData plant = PlantRepository.getInstance().findByName(plantName);
        User current = App.getCurrentUser();
        if (plant == null || current == null) {
            return false;
        }
        for (String plantId : current.getCollection().getAvailablePlantsIds()) {
            if (plantId.equalsIgnoreCase(plant.getId())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasPlantBeenChosen(String plantName) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null || plantName == null) {
            return false;
        }
        for (String plant : level.getChosenPlants()) {
            if (plant.equalsIgnoreCase(plantName)) {
                return true;
            }
        }
        return false;
    }

    public static String removePlant(String plantName) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return "Choose a level first";
        }
        if (plantName == null || !isPlantAvailableForUi(plantName)) {
            return "Plant is not available";
        }
        if (!hasPlantBeenChosen(plantName)) {
            return "Plant has not been chosen";
        }
        for (String plant : new ArrayList<>(level.getChosenPlants())) {
            if (plant.equalsIgnoreCase(plantName)) {
                level.getChosenPlants().remove(plant);
                return "Plant removed successfully";
            }
        }
        return "Plant has not been chosen";
    }

    public static String boostPlant(String plantName) {
        User current = App.getCurrentUser();
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (current == null || level == null) {
            return "Choose a level first";
        }
        if (!hasPlantBeenChosen(plantName)) {
            return "Plant has not been chosen";
        }
        if (current.getGemsCount() < 2) {
            return "Not enough gems";
        }
        PlantData plant = PlantRepository.getInstance().findByName(plantName);
        if (plant == null) return "Plant is not available";
        String key = plant.getId().toLowerCase();
        if (current.getGreenHouse().storedBoosts.getOrDefault(key, false)) return "Plant is already boosted";
        current.setGemsCount(current.getGemsCount() - 2);
        current.getGreenHouse().storedBoosts.put(key, true);
        SignupMenuController.saveToJson();
        return "Plant boosted successfully";
    }

    public static String startGame() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return "Choose a level first";
        }
        if (level.getChosenPlants().isEmpty()) {
            return "Choose at least one plant";
        }
        App.setCurrentMenu(Menu.GAME_MANAGER);
        return "Game started";
    }

    public static List<PlantData> getAvailablePlantsForUi() {
        List<PlantData> result = new ArrayList<>();
        LevelData level = getRelevantLevelData();
        if (level == null || level.getAvailablePlants() == null || level.getAvailablePlants().isEmpty()) {
            result.addAll(PlantRepository.getInstance().getAllPlants());
            return result;
        }
        for (String name : level.getAvailablePlants()) {
            PlantData plant = PlantRepository.getInstance().findByName(name);
            if (plant != null && !result.contains(plant)) {
                result.add(plant);
            }
        }
        return result;
    }

    public static List<String> getChosenPlantsForUi() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(level.getChosenPlants());
    }

    public static boolean hasCurrentLevel() {
        return GameManagerController.getInstance().getCurrentLevel() != null;
    }

    private static LevelData getRelevantLevelData() {
        Level currentLevel = GameManagerController.getInstance().getCurrentLevel();
        if (currentLevel != null) {
            return currentLevel.getData();
        }
        User current = App.getCurrentUser();
        return current == null ? null : current.getLastLevel();
    }
}
