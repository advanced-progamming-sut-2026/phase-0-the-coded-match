package views.menus;

import controllers.menus.SignupMenuController;
import enums.Commands;

import java.util.Scanner;

public class SignupMenu {
    public static String[] message = new String[1];
    public static boolean registered = false;

    public static void check(String input, Scanner scanner) {
        if (input.matches(Commands.REGISTER.getPattern())) {
            registered = false;
            SignupMenuController.register(input, message);
            System.out.println(message[0]);

            if (registered) {
                input = scanner.nextLine();
                SignupMenuController.showQuestion(input, message);
                System.out.println(message[0]);

                input = scanner.nextLine();
                SignupMenuController.pickQuestion(input);
            }
        } else {
            System.out.println("invalid command");
        }
    }
}