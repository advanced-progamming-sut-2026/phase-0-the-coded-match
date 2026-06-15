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
                SignupMenuController.answerQuestions(input);
            }
        } else {
            System.out.println("invalid command");
        }
    }
}
