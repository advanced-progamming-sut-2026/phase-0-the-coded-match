package views.menus;

import controllers.menus.SignupMenuController;
import enums.Commands;

import java.util.Scanner;

public class SignupMenu {
    public static String[] message = new String[1];
    public static boolean registered = false;
    public static boolean questionPicked = false;

    public static void check(String input, Scanner scanner) {
        if (input.matches(Commands.REGISTER.getPattern())) {
            registered = false;
            questionPicked = false;
            SignupMenuController.register(input, message);
            System.out.println(message[0]);

            if (registered) {
                SignupMenuController.showQuestions(input, message);
                System.out.println(message[0]);

                while (!questionPicked) {
                    input = scanner.nextLine();
                    if (!input.matches(Commands.PICK_QUESTION.getPattern())) {
                        System.out.println("pick a question");
                    } else {
                        SignupMenuController.pickQuestion(input, message);
                        System.out.println(message[0]);
                    }
                }
            }
        } else {
            System.out.println("invalid command");
        }
    }
}