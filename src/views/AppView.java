package views;

import controllers.MenuController;
import enums.Commands;
import enums.Menu;
import models.App;

import java.util.Scanner;

public class AppView {
    public static String[] message = new String[1];
    public static boolean isRunning = true;

    public static void run() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (scanner.hasNextLine() && isRunning) {
            if (input.matches(Commands.SHOW_MENU.getPattern())) {
                MenuController.showCurrentMenu(message);
                System.out.println(message[0]);
            } else if (input.matches(Commands.ENTER_MENU.getPattern())) {
                MenuController.enterMenu(input);
            } else if (input.matches(Commands.EXIT_MENU.getPattern())) {
                MenuController.exitMenu();
            } else if (App.getCurrentMenu() == Menu.SIGNUP_MENU) {
                SignupMenu.check(scanner);
            }
        }
    }
}
