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
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(Commands.CHOOSE_ADD_PLANT.getPattern()).matcher(input); m.matches();
            System.out.println(ChoosePlantsMenuController.addPlant(m.group("type")));
        }else if(input.matches(Commands.CHOOSE_REMOVE_PLANT.getPattern())){
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(Commands.CHOOSE_REMOVE_PLANT.getPattern()).matcher(input); m.matches();
            System.out.println(ChoosePlantsMenuController.removePlant(m.group("type")));
        }else if(input.matches(Commands.CHOOSE_BOOST_PLANT.getPattern())){
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(Commands.CHOOSE_BOOST_PLANT.getPattern()).matcher(input); m.matches();
            System.out.println(ChoosePlantsMenuController.boostPlant(m.group("type")));
        }else if(input.matches(Commands.CHOOSE_START_GAME.getPattern())){
            ChoosePlantsMenuController.startGame();
        }else{
            System.out.println("invalid command");
        }

    }
}
