package controllers;

import enums.Commands;
import models.App;
import models.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileMenuController {

    public static void changeUsername(String input) {
        Matcher matcher = getMatcher(Commands.PROFILE_CHANGE_USERNAME, input);
        if (!matcher.matches()) {
            return;
        }
        User user = App.getCurrentUser();
        String username = matcher.group("username");
        if (user == null) {
            System.out.println("no user is logged in");
        } else if (user.getUsername().equals(username)) {
            System.out.println("new username is the same as current username");
        } else if (App.doesUsernameExists(username)) {
            System.out.println("username already exists");
        } else {
            user.setUsername(username);
            System.out.println("username changed successfully");
        }
    }

    public static void changeNickname(String input) {
        Matcher matcher = getMatcher(Commands.PROFILE_CHANGE_NICKNAME, input);
        if (!matcher.matches()) {
            return;
        }
        User user = App.getCurrentUser();
        String nickname = matcher.group("nickname");
        if (user == null) {
            System.out.println("no user is logged in");
        } else if (user.getNickname().equals(nickname)) {
            System.out.println("new nickname is the same as current nickname");
        } else if (!SignupMenuController.validateNickname(nickname)) {
            System.out.println("invalid nickname");
        } else {
            user.setNickname(nickname);
            System.out.println("nickname changed successfully");
        }
    }

    public static void changeEmail(String input) {
        Matcher matcher = getMatcher(Commands.PROFILE_CHANGE_EMAIL, input);
        if (!matcher.matches()) {
            return;
        }
        User user = App.getCurrentUser();
        String email = matcher.group("email");
        if (user == null) {
            System.out.println("no user is logged in");
        } else if (user.getEmail().equals(email)) {
            System.out.println("new email is the same as current email");
        } else if (!isEmailValid(email)) {
            System.out.println("invalid email");
        } else {
            user.setEmail(email);
            System.out.println("email changed successfully");
        }
    }

    public static void changePassword(String input) {
        Matcher matcher = getMatcher(Commands.PROFILE_CHANGE_PASSWORD, input);
        if (!matcher.matches()) {
            return;
        }
        User user = App.getCurrentUser();
        String newPassword = matcher.group("new_password");
        String oldPassword = matcher.group("old_password");
        if (user == null) {
            System.out.println("no user is logged in");
        } else if (!user.getPassword().equals(oldPassword)) {
            System.out.println("old password is incorrect");
        } else if (user.getPassword().equals(newPassword)) {
            System.out.println("new password is the same as current password");
        } else if (!SignupMenuController.validatePassword(newPassword)) {
            System.out.println("password is not strong enough");
        } else {
            user.setPassword(newPassword);
            System.out.println("password changed successfully");
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
        result.append("passed chapters: ").append(user.getChaptersCount()).append("\n");
        result.append("highest meow points: ").append(user.getMeowPoints()).append("\n");
        return result;
    }

    private static Matcher getMatcher(Commands command, String input) {
        return Pattern.compile(command.getPattern()).matcher(input);
    }

    private static boolean isEmailValid(String email) {
        return email.matches("^[A-Za-z0-9](?:[A-Za-z0-9_-]|(?<!\\.)\\.)*[A-Za-z0-9]@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.[A-Za-z]{2,}$");
    }
}