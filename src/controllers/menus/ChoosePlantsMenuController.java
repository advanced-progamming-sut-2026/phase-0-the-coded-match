package controllers.menus;

import controllers.GameManagerController;
import enums.Menu;
import models.*;
import models.plants.*;

public class ChoosePlantsMenuController {

    public static String showAllPlants() {
        StringBuilder message = new StringBuilder();
        for(PlantData p : App.getAllPlants()){
            message.append(p.getData().getDisplayName()).append("\n");
        }
        return message.toString();
    }

    public static String showAvailablePlants() {
        User current = App.getCurrentUser();
        LevelData level = current.getLastLevel();
        StringBuilder message = new StringBuilder();
        for (Plant p: level.getAvailablePlants()){
            message.append(p.getData().getDisplayName()).append("\n");
        }
        return message.toString();
    }

    public static String addPlant(String plantName) {
        Plant p = App.getPlantByName(plantName);
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if(p == null || !isPlantAvailable(p)){
            return "Plant is not available";
        }
        if(!isPlantUnlocked(p)){
            return "Plant is locked";
        }
        if(!hasPlantBeenChosen(p)){
            return "Plant is already selected";
        }
        level.addActivePlants(p);
        return "Plant added successfully";

    }

    private static boolean isPlantAvailable(Plant p){
        User current = App.getCurrentUser();
        LevelData level = current.getLastLevel();
        for(Plant plant: level.getAvailablePlants()){
            if(plant.getData().getName().equalsIgnoreCase(p.getData().getName())){
                return true;
            }
        }
        return false;
    }

    private static boolean isPlantUnlocked(Plant p){
        User current = App.getCurrentUser();
        for(Plant plant : current.getCollection().getAvailablePlants()){
            if(plant.getData().getName().equalsIgnoreCase(p.getData().getName())){
                return true;
            }
        }
        return false;
    }

    public static boolean hasPlantBeenChosen(Plant p) {
        User current = App.getCurrentUser();
        Level level = GameManagerController.getInstance().getCurrentLevel();
        for(Plant plant : level.getActivePlants()){
            if(plant.getData().getName().equalsIgnoreCase(p.getData().getName())){
                return true;
            }
        }
        return false;

    }

    public static String removePlant(String plantName) {
        Plant p = App.getPlantByName(plantName);
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if(p == null || !isPlantAvailable(p)){
            return "Plant is not available";
        }
        if(!hasPlantBeenChosen(p)){
            return "Plant has not been chosen";
        }
        for(Plant plant : level.getActivePlants()){
            if(plant.getData().getName().equalsIgnoreCase(p.getData().getName())){
                level.getActivePlants().remove(plant);
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
        App.setCurrentMenu(Menu.GAME_MENU);
        return;
    }
}
