package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.LevelData;
import PvZ2.APproject.models.User;
import PvZ2.APproject.models.plants.PlantData;
import PvZ2.APproject.models.plants.PlantRepository;

public class ChoosePlantsMenuController {

    public static String showAllPlants() {
        StringBuilder message = new StringBuilder();
        for(PlantData p : PlantRepository.getInstance().getAllPlants()){
            message.append(p.getDisplayName()).append("\n");
        }
        return message.toString();
    }

    public static String showAvailablePlants() {
        User current = App.getCurrentUser();
        LevelData level = current.getLastLevel();
        StringBuilder message = new StringBuilder();
        if (level.getAvailablePlants() == null) return showAllPlants();
        for (String plantName: level.getAvailablePlants()) {
            message.append(plantName+"\n");
        }
        return message.toString();
    }

    public static String addPlant(String plantName) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if(plantName == null || !isPlantAvailable(plantName)){
            return "Plant is not available";
        }
        if(!isPlantUnlocked(plantName)){
            return "Plant is locked";
        }
        if(hasPlantBeenChosen(plantName)) return "Plant is already selected";
        if (level.getChosenPlants().size() >= 8) return "You cannot choose more than 8 plants";
        level.addChosenPlant(plantName);
        return "Plant added successfully";

    }

    private static boolean isPlantAvailable(String p){
        User current = App.getCurrentUser();
        LevelData level = current.getLastLevel();
        if (level.getAvailablePlants() == null || level.getAvailablePlants().isEmpty()) return PlantRepository.getInstance().findByName(p) != null;
        for (String plantName: level.getAvailablePlants()){
            if (plantName.equalsIgnoreCase(p)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPlantUnlocked(String p){
        PlantData plant = PlantRepository.getInstance().findByName(p);
        if (plant == null) return false;
        User current = App.getCurrentUser();
        for(String plantId : current.getCollection().getAvailablePlantsIds()){
            if(plantId.equalsIgnoreCase(plant.getId())){
                return true;
            }
        }
        return false;
    }

    public static boolean hasPlantBeenChosen(String p) {
        User current = App.getCurrentUser();
        Level level = GameManagerController.getInstance().getCurrentLevel();
        for(String plant : level.getChosenPlants()){
            if(plant.equalsIgnoreCase(p)){
                return true;
            }
        }
        return false;

    }

    public static String removePlant(String plantName) {
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if(plantName == null || !isPlantAvailable(plantName)){
            return "Plant is not available";
        }
        if(!hasPlantBeenChosen(plantName)){
            return "Plant has not been chosen";
        }
        for (String plant : new java.util.ArrayList<>(level.getChosenPlants())) if (plant.equalsIgnoreCase(plantName)) {
            level.getChosenPlants().remove(plant);
            return "Plant removed successfully";
        }
        return "Plant has not been chosen";
    }



    public static String boostPlant(String plantName) {
        User current = App.getCurrentUser();
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (!hasPlantBeenChosen(plantName)) return "Plant has not been chosen";
        if (current.getGemsCount() < 2) return "Not enough gems";
        current.setGemsCount(current.getGemsCount() - 2);
        current.getGreenHouse().storedBoosts.put(plantName.toLowerCase(), true);
        return "Plant boosted successfully";
    }

    public static void startGame() {
        App.setCurrentMenu(Menu.GAME_MANAGER);
        return;
    }
}
