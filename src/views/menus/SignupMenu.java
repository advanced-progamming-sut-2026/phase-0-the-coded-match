package views.menus;

import controllers.menus.SignupMenuController;
import enums.Commands;

import java.util.Scanner;

public class SignupMenu {
    public static String[] message = new String[1];
    public static boolean registered;
    public static boolean questionPicked;

    public static void check(String input, Scanner scanner) {
        if (input.matches(Commands.REGISTER.getPattern())) {
            registered = false;
            SignupMenuController.register(input, message);
            System.out.println(message[0]);
            if (registered) {
                SignupMenuController.showQuestions("", message);
                System.out.println(message[0]);
            }
        } else if (input.matches(Commands.PICK_QUESTION.getPattern())) {
            SignupMenuController.pickQuestion(input, message);
            System.out.println(message[0]);
        } else System.out.println("invalid command");
    }
}
