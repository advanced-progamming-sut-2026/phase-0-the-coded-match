package views;

import controllers.SignupMenuController;
import enums.Commands;

import java.util.Scanner;

public class SignupMenu {
    public static String[] message = new String[1];
    public static boolean registered = false;

    public static void check(Scanner scanner) {
        String input = scanner.nextLine();
        if (input.matches(Commands.REGISTER.getPattern())) {
            SignupMenuController.register(input, message);
            System.out.println(message[0]);
            if (registered) {
                input = scanner.nextLine();
                SignupMenuController.showQuestion(input, message);
                System.out.println(message[0]);
                input = scanner.nextLine();
                SignupMenuController.pickQuestion(input); //TODO: doesn't seem clean.
            }
        } else {
            System.out.println("invalid command");
        }
    }
}
