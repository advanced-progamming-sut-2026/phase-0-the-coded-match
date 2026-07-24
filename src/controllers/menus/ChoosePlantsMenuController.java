package controllers.menus;

import controllers.GameManagerController;
import enums.Menu;
import models.*;
import models.plants.*;

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
        for (String plantName: level.getAvailablePlants()){
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
        if(!hasPlantBeenChosen(plantName)){
            return "Plant is already selected";
        }
        level.addChosenPlant(plantName);
        return "Plant added successfully";

    }

    private static boolean isPlantAvailable(String p){
        User current = App.getCurrentUser();
        LevelData level = current.getLastLevel();
        for (String plantName: level.getAvailablePlants()){
            if (plantName.equalsIgnoreCase(p)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPlantUnlocked(String p){
        PlantData plant = PlantRepository.getInstance().findByName(p);
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
        for(Plant plant : level.getActivePlants()){
            if(plant.getData().getName().equalsIgnoreCase(plantName)){
                level.getChosenPlants().remove(plant);
                return "Plant removed successfully";
            }
        }
        return null;
    }



    public static String boostPlant(String plantName) {
        User current = App.getCurrentUser();
        Level level = GameManagerController.getInstance().getCurrentLevel();
        for(Plant plant : level.getActivePlants()){
            if(plant.getData().getName().equalsIgnoreCase(plantName)){
                plant.setBoosted(true);
                current.setGemsCount(current.getGemsCount()-2);
                return "Plant boosted successfully";
            }
        }
        return "Plant type does not exist";
    }

    public static void startGame() {
        App.setCurrentMenu(Menu.GAME_MANAGER);
        return;
    }
}
