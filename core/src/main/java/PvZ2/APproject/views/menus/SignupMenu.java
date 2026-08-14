package PvZ2.APproject.views.menus;

import PvZ2.APproject.controllers.menus.SignupMenuController;
import PvZ2.APproject.enums.Commands;

import java.util.Scanner;

public class SignupMenu {
    public static String[] message = new String[1];
    public static boolean registered;
    public static boolean questionPicked;

    public static void check(String input, Scanner scanner) {
//        if (input.matches(Commands.REGISTER.getPattern())) {
//            registered = false;
//            SignupMenuController.register(input, message);
//            System.out.println(message[0]);
//
//            if (registered) {
//                input = scanner.nextLine();
//                SignupMenuController.showQuestions(input, message);
//                System.out.println(message[0]);
//
//                while (!questionPicked) {
//                    input = scanner.nextLine();
//                    if (!input.matches(Commands.PICK_QUESTION.getPattern())) {
//                        System.out.println("pick a question");
//                    } else {
//                        SignupMenuController.pickQuestion(input, message);
//                        System.out.println(message[0]);
//                    }
//                }
//            }
//        } else {
//            System.out.println("invalid command");
//        }
    }
}
