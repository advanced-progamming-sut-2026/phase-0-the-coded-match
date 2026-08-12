package PvZ2.APproject.views.menus;

import PvZ2.APproject.controllers.menus.ProfileMenuController;
import PvZ2.APproject.enums.Commands;

public class ProfileMenu {
    public static String[] message = new String[1];

    public static void check(String input) {
        if (input.matches(Commands.PROFILE_CHANGE_USERNAME.getPattern())) {
            System.out.println(ProfileMenuController.changeUsername(input));
        } else if (input.matches(Commands.PROFILE_CHANGE_NICKNAME.getPattern())) {
            System.out.println(ProfileMenuController.changeNickname(input));
        } else if (input.matches(Commands.PROFILE_CHANGE_EMAIL.getPattern())) {
            System.out.println(ProfileMenuController.changeEmail(input));
        } else if (input.matches(Commands.PROFILE_CHANGE_PASSWORD.getPattern())) {
            System.out.println(ProfileMenuController.changePassword(input));
        } else if (input.matches(Commands.PROFILE_SHOW_INFO.getPattern())) {
            System.out.println(ProfileMenuController.showProfileInfo(input));
        } else {
            System.out.println("invalid command");
        }
    }
}
