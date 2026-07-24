package views.menus;

import controllers.menus.ChoosePlantsMenuController;
import enums.Commands;

public class ChoosePlantsMenu {
    public static void check(String input) {
        if(input.matches(Commands.CHOOSE_SHOW_ALL.getPattern())){
            System.out.println(ChoosePlantsMenuController.showAllPlants());
        }else if(input.matches(Commands.CHOOSE_SHOW_AVAILABLE.getPattern())){
            System.out.println(ChoosePlantsMenuController.showAvailablePlants());
        }else if(input.matches(Commands.CHOOSE_ADD_PLANT.getPattern())){
            System.out.println(ChoosePlantsMenuController.addPlant(input));
        }else if(input.matches(Commands.CHOOSE_REMOVE_PLANT.getPattern())){
            System.out.println(ChoosePlantsMenuController.removePlant(input));
        }else if(input.matches(Commands.CHOOSE_BOOST_PLANT.getPattern())){
            System.out.println(ChoosePlantsMenuController.boostPlant(input));
        }else if(input.matches(Commands.CHOOSE_START_GAME.getPattern())){
            ChoosePlantsMenuController.startGame();
        }else{
            System.out.println("invalid command");
        }

    }
}
