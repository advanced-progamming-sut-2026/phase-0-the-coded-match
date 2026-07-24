package views;

import enums.Commands;
import models.App;

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
            App.getCurrentUser().getGreenHouse().plantPot(x, y, App.getCurrentUser().getLastLevel().getAvailablePlants());

        } else if ((matcher = getMatcher(input, Commands.GREENHOUSE_COLLECT.getPattern())) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            App.getCurrentUser().getGreenHouse().collect(x, y);

        } else if ((matcher = getMatcher(input, Commands.GREENHOUSE_GROW.getPattern())) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            App.getCurrentUser().getGreenHouse().grow(x, y);

        } else {
            System.out.println("invalid command");
        }

    }

    private static Matcher getMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches() ? matcher : null;
    }
}
