package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.Main;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.User;
import PvZ2.APproject.views.menus.MainMenu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileMenuController {

    public String changeUsername(String username) {
        User user = App.getCurrentUser();

        if (user.getUsername().equals(username)) {
            return "Error: New username is the same as current username.";
        } else if (App.doesUsernameExists(username)) {
            return "Error: Username already exists.";
        } else {
            user.setUsername(username);
            return "Username changed successfully!";
        }
    }

    public String changeNickname(String nickname) {
        User user = App.getCurrentUser();

        if (user.getNickname().equals(nickname)) {
            return "Error: New nickname is the same as current nickname.";
        } else if (!SignupMenuController.validateNickname(nickname)) {
            return "Error: Invalid nickname.";
        } else {
            user.setNickname(nickname);
            return "Nickname changed successfully!";
        }
    }

    public String changeEmail(String email) {
        User user = App.getCurrentUser();

        if (user.getEmail().equals(email)) {
            return "Error: New email is the same as current email.";
        } else if (!SignupMenuController.validateEmail(email)) {
            return "Error: Invalid email.";
        } else {
            user.setEmail(email);
            return "Email changed successfully!";
        }
    }

    public String changePassword(String oldPassword, String newPassword) {
        User user = App.getCurrentUser();

        String hashedNewPassword = SignupMenuController.hashPassword(newPassword);
        String hashedOldPassword = SignupMenuController.hashPassword(oldPassword);

        if (!user.getPassword().equals(hashedOldPassword)) {
            return "Error: Current password is incorrect.";
        } else if (user.getPassword().equals(hashedNewPassword)) {
            return "Error: New password is the same as current password.";
        } else if (!SignupMenuController.validatePassword(newPassword)) {
            return "Error: Password is not strong enough.";
        } else {
            user.setPassword(hashedNewPassword);
            return "Password changed successfully!";
        }
    }

    public void exit(Main game) {
        App.setCurrentMenu(Menu.MAIN_MENU);
        game.setScreen(new MainMenu(game));
    }

//    public static StringBuilder showProfileInfo(String input) {
//        StringBuilder result = new StringBuilder();
//        User user = App.getCurrentUser();
//        if (user == null) {
//            return result.append("no user is logged in\n");
//        }
//        result.append("username: ").append(user.getUsername()).append("\n");
//        result.append("nickname: ").append(user.getNickname()).append("\n");
//        result.append("games played: ").append(user.getGamesPlayedCount()).append("\n");
//        result.append("coins: ").append(user.getCoinsCount()).append("\n");
//        result.append("gems: ").append(user.getGemsCount()).append("\n");
//        result.append("passed chapters: ").append(user.getLevelsCount()).append("\n");
//        result.append("highest meow points: ").append(user.getMeowPoints()).append("\n");
//        return result;
//    }
}
