package views.menus;

import controllers.menus.LoginMenuController;
import enums.Commands;

public class LoginMenu {
    public static void check(String input) {
        if (input.matches(Commands.LOGIN.getPattern())) {
            System.out.println(LoginMenuController.login(input));
        }else if(input.matches(Commands.FORGET_PASSWORD.getPattern())){
            System.out.println(LoginMenuController.resetPassword(input));
        }else if(input.matches(Commands.ANSWER.getPattern())){
            System.out.println(LoginMenuController.isAnswerCorrect(input));
        }else{
            System.out.println("invalid command");
        }
    }
}
