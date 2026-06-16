package views;

import controllers.ProfileMenuController;
import enums.Commands;

public class ProfileMenu {
    public static void check(String input) {
        if (input.matches(Commands.PROFILE_CHANGE_USERNAME.getPattern())) {
            ProfileMenuController.changeUsername(input);
        } else if (input.matches(Commands.PROFILE_CHANGE_NICKNAME.getPattern())) {
            ProfileMenuController.changeNickname(input);
        } else if (input.matches(Commands.PROFILE_CHANGE_EMAIL.getPattern())) {
            ProfileMenuController.changeEmail(input);
        } else if (input.matches(Commands.PROFILE_CHANGE_PASSWORD.getPattern())) {
            ProfileMenuController.changePassword(input);
        } else if (input.matches(Commands.PROFILE_SHOW_INFO.getPattern())) {
            System.out.print(ProfileMenuController.showProfileInfo(input));
        } else {
            System.out.println("invalid command");
        }
    }
}