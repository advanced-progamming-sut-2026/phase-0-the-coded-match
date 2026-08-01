package controllers.menus;

import controllers.GameManagerController;
import controllers.QuestController;
import controllers.SeasonController;
import controllers.ZombieWaveManager;
import enums.Menu;
import models.App;
import models.Level;
import models.LevelData;
import models.User;
import models.plants.PlantData;
import models.plants.PlantRepository;
import models.specialLevels.ConveyorBeltStrategy;

public class ChoosePlantsMenuController {
    public static String showAllPlants() {
        StringBuilder message = new StringBuilder();
        for (PlantData plant : PlantRepository.getInstance().getAllPlants()) {
            message.append(plant.getDisplayName()).append('\n');
        }
        return message.toString();
    }

    public static String showAvailablePlants() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return "no active level";
        }
        StringBuilder message = new StringBuilder();
        for (String plantName : level.getData().getAvailablePlants()) {
            if (isPlantAvailable(plantName) && isPlantUnlocked(plantName)) {
                PlantData data = PlantRepository.getInstance().findByName(plantName);
                message.append(data == null ? plantName : data.getDisplayName()).append('\n');
            }
        }
        return message.length() == 0 ? "no available plants" : message.toString();
    }

    public static String addPlant(String plantName) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return "no active level";
        }
        plantName = extractPlantName(plantName);
        if (plantName == null || !isPlantAvailable(plantName)) {
            return "Plant is not available";
        }
        if (!isPlantUnlocked(plantName)) {
            return "Plant is locked";
        }
        if (hasPlantBeenChosen(plantName)) {
            return "Plant is already selected";
        }
        int limit = getSelectionLimit(level);
        if (limit > 0 && level.getChosenPlants().size() >= limit) {
            return "Plant selection is full";
        }
        PlantData data = PlantRepository.getInstance().findByName(plantName);
        level.addChosenPlant(data == null ? plantName : data.getName());
        return "Plant added successfully";
    }

    private static int getSelectionLimit(Level level) {
        int limit = level.getData().getPlantSelectionLimit();
        if (limit > 0) {
            return limit;
        }
        if (level.getSpecialLevel() instanceof ConveyorBeltStrategy) {
            return 0;
        }
        return 8;
    }

    private static String extractPlantName(String input) {
        if (input == null) {
            return null;
        }
        int index = input.indexOf("-t");
        return index >= 0 ? input.substring(index + 2).trim() : input.trim();
    }

    private static boolean isPlantAvailable(String name) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return false;
        }
        LevelData data = level.getData();
        PlantData plantData = PlantRepository.getInstance().findByName(name);
        if (plantData == null) {
            return false;
        }
        boolean available = data.getAvailablePlants().stream().anyMatch(plant -> samePlant(plant, plantData));
        boolean locked = data.getLockedPlants().stream().anyMatch(plant -> samePlant(plant, plantData));
        return available && !locked;
    }

    private static boolean isPlantUnlocked(String name) {
        PlantData plant = PlantRepository.getInstance().findByName(name);
        if (plant == null || App.getCurrentUser() == null) {
            return false;
        }
        User user = App.getCurrentUser();
        return user.getCollection().getAvailablePlantsIds().stream().anyMatch(id -> id.equalsIgnoreCase(plant.getId()));
    }

    private static boolean samePlant(String value, PlantData data) {
        return value != null && (value.equalsIgnoreCase(data.getId()) || value.equalsIgnoreCase(data.getName())
                || value.equalsIgnoreCase(data.getDisplayName()));
    }

    public static boolean hasPlantBeenChosen(String name) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        PlantData data = PlantRepository.getInstance().findByName(name);
        if (level == null || data == null) {
            return false;
        }
        return level.getChosenPlants().stream().anyMatch(plant -> samePlant(plant, data));
    }

    public static String removePlant(String plantName) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return "no active level";
        }
        plantName = extractPlantName(plantName);
        PlantData data = PlantRepository.getInstance().findByName(plantName);
        if (data == null) {
            return "Plant does not exist";
        }
        for (String chosen : level.getChosenPlants().toArray(new String[0])) {
            if (samePlant(chosen, data)) {
                level.getChosenPlants().remove(chosen);
                level.getBoostedPlantNames().remove(normalize(data.getName()));
                return "Plant removed successfully";
            }
        }
        return "Plant has not been chosen";
    }

    public static String boostPlant(String plantName) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        plantName = extractPlantName(plantName);
        PlantData data = PlantRepository.getInstance().findByName(plantName);
        if (level == null || data == null || !hasPlantBeenChosen(plantName)) {
            return "Plant has not been chosen";
        }
        if (level.isPlantBoostedForLevel(data.getName())) {
            return "Plant is already boosted";
        }
        if (App.getCurrentUser().getGemsCount() < 2) {
            return "not enough gems";
        }
        App.getCurrentUser().setGemsCount(App.getCurrentUser().getGemsCount() - 2);
        level.boostPlantForLevel(data.getName());
        return "Plant boosted successfully";
    }

    private static String normalize(String name) {
        return name.trim().toLowerCase().replace('_', ' ').replace('-', ' ').replaceAll("\\s+", " ");
    }

    public static void startGame() {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) {
            return;
        }
        if (level.getZombieWave() == null) {
            level.setZombieWave(new ZombieWaveManager(level));
        }
        SeasonController.startLevel(level);
        QuestController.initializeForCurrentUser();
        QuestController.onLevelStarted();
        App.getCurrentUser().addGamesPlayed();
        App.setCurrentMenu(Menu.GAME_MANAGER);
    }
}
