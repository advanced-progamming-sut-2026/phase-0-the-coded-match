package views;

import enums.Commands;
import models.App;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GreenHouseView {
    public static void check(String input) {
        Matcher matcher;
        if (input.matches(Commands.SHOW_GREENHOUSE.getPattern())) {
            App.getCurrentUser().getGreenHouse().showGreenhouse();
        } else if ((matcher = getMatcher(input, Commands.GREENHOUSE_PLANT_POT.getPattern())) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            List<String> plants = App.getCurrentUser().getCollection().getAvailablePlantsIds();
            App.getCurrentUser().getGreenHouse().plantPot(x, y, plants);
        } else if ((matcher = getMatcher(input, Commands.GREENHOUSE_COLLECT.getPattern())) != null) {
            App.getCurrentUser().getGreenHouse().collect(Integer.parseInt(matcher.group("x")),
                    Integer.parseInt(matcher.group("y")));
        } else if ((matcher = getMatcher(input, Commands.GREENHOUSE_GROW.getPattern())) != null) {
            App.getCurrentUser().getGreenHouse().grow(Integer.parseInt(matcher.group("x")),
                    Integer.parseInt(matcher.group("y")));
        } else {
            System.out.println("invalid command");
        }
    }

    private static Matcher getMatcher(String input, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(input);
        return matcher.matches() ? matcher : null;
    }
}
