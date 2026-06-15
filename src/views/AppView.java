package views;

import controllers.MenuController;
import enums.Commands;
import enums.Menu;
import models.App;

import java.util.Scanner;

public class AppView {
    public static String[] message = new String[1];

    public static void run() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.matches(Commands.SHOW_MENU.getPattern())) {
            MenuController.showCurrentMenu(message);
            System.out.println(message[0]);
        } else if (App.getCurrentMenu() == Menu.SIGNUP_MENU) {
            SignupMenu.check(input);
        }
    }
}
