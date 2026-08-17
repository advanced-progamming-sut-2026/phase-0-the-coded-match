package PvZ2.APproject.views;

import PvZ2.APproject.controllers.GreenHouseController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.models.App;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GreenHouseView {
    public static void check(String input) {
//        Matcher matcher;
//        if (input.matches(Commands.SHOW_GREENHOUSE.getPattern())) {
//            App.getCurrentUser().getGreenHouse().showGreenhouse();
//        } else if ((matcher = getMatcher(input, Commands.GREENHOUSE_PLANT_POT.getPattern())) != null) {
//            GreenHouseController.plantSeed(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")));
//        } else if ((matcher = getMatcher(input, Commands.GREENHOUSE_COLLECT.getPattern())) != null) {
//            GreenHouseController.collectPlant(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")));
//        } else if ((matcher = getMatcher(input, Commands.GREENHOUSE_GROW.getPattern())) != null) {
//            GreenHouseController.growPlant(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")));
//        } else {
//            System.out.println("invalid command");
//        }
    }
//
//    private static Matcher getMatcher(String input, String regex) {
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(input);
//        return matcher.matches() ? matcher : null;
//    }
}
