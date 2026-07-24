package views;

import controllers.menus.TravelLogController;
import enums.Commands;

public class TravelLogView {
    public static void check(String input) {
        if (input.matches(Commands.TRAVEL_LOG_PAGE.getPattern())) {
            System.out.println(TravelLogController.changePage(input));
        } else {
            System.out.println("invalid command");
        }
    }

}
