package controllers.menus;

import enums.Commands;
import models.App;
import models.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileMenuController {

    public static String changeUsername(String input) {
        Matcher matcher = getMatcher(Commands.PROFILE_CHANGE_USERNAME, input);
        if (!matcher.matches()) {
            return "invalid command";
        }
        User user = App.getCurrentUser();
        String username = matcher.group("username");
        if (user == null) {
            return "no user is logged in";
        } else if (user.getUsername().equals(username)) {
            return "new username is the same as current username";
        } else if (App.doesUsernameExists(username)) {
            return "username already exists";
        } else {
            user.setUsername(username);
            return "username changed successfully";
        }
    }

    public static String changeNickname(String input) {
        Matcher matcher = getMatcher(Commands.PROFILE_CHANGE_NICKNAME, input);
        if (!matcher.matches()) {
            return "invalid command";
        }
        User user = App.getCurrentUser();
        String nickname = matcher.group("nickname");
        if (user == null) {
            return "no user is logged in";
        } else if (user.getNickname().equals(nickname)) {
            return "new nickname is the same as current nickname";
        } else if (!SignupMenuController.validateNickname(nickname)) {
            return "invalid nickname";
        } else {
            user.setNickname(nickname);
            return "nickname changed successfully";
        }
    }

    public static String changeEmail(String input) {
        Matcher matcher = getMatcher(Commands.PROFILE_CHANGE_EMAIL, input);
        if (!matcher.matches()) {
            return "invalid command";
        }
        User user = App.getCurrentUser();
        String email = matcher.group("email");
        if (user == null) {
            return "no user is logged in";
        } else if (user.getEmail().equals(email)) {
            return "new email is the same as current email";
        } else if (!SignupMenuController.validateEmail(email)) {
            return "invalid email";
        } else {
            user.setEmail(email);
            return "email changed successfully";
        }
    }

    public static String changePassword(String input) {
        Matcher matcher = getMatcher(Commands.PROFILE_CHANGE_PASSWORD, input);
        if (!matcher.matches()) {
            return "invalid command";
        }
        User user = App.getCurrentUser();
        String newPassword = matcher.group("newPassword");
        String oldPassword = matcher.group("oldPassword");
        String hashedNewPassword = SignupMenuController.hashPassword(newPassword);
        String hashedOldPassword = SignupMenuController.hashPassword(oldPassword);
        if (user == null) {
            return "no user is logged in";
        } else if (!user.getPassword().equals(hashedOldPassword)) {
            return "old password is incorrect";
        } else if (user.getPassword().equals(hashedNewPassword)) {
            return "new password is the same as current password";
        } else if (!SignupMenuController.validatePassword(newPassword)) {
            return "password is not strong enough";
        } else {
            user.setPassword(hashedNewPassword);
            return "password changed successfully";
        }
    }

    public static StringBuilder showProfileInfo(String input) {
        StringBuilder result = new StringBuilder();
        User user = App.getCurrentUser();
        if (user == null) {
            return result.append("no user is logged in\n");
        }
        result.append("username: ").append(user.getUsername()).append("\n");
        result.append("nickname: ").append(user.getNickname()).append("\n");
        result.append("games played: ").append(user.getGamesPlayedCount()).append("\n");
        result.append("coins: ").append(user.getCoinsCount()).append("\n");
        result.append("gems: ").append(user.getGemsCount()).append("\n");
        result.append("passed chapters: ").append(user.getLevelsCount()).append("\n");
        result.append("highest meow points: ").append(user.getMeowPoints()).append("\n");
        return result;
    }

    private static Matcher getMatcher(Commands command, String input) {
        return Pattern.compile(command.getPattern()).matcher(input);
    }
}