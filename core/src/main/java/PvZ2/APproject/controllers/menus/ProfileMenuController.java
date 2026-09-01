package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.Main;
import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.User;
import PvZ2.APproject.views.menus.MainMenu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileMenuController {

    public String changeUsername(String username) {
//        User user = App.getCurrentUser();
//
//        if (user == null) return "Error: No logged in user.";
//        if (!SignupMenuController.validateUsername(username) && (username == null || !user.getUsername().equalsIgnoreCase(username))) {
//            return "Error: Invalid username or username already exists.";
//        } else if (user.getUsername().equals(username)) {
//            return "Error: New username is the same as current username.";
//        } else if (App.doesUsernameExists(username)) {
//            return "Error: Username already exists.";
//        } else {
//            user.setUsername(username);
//            if (user.isStayLoggedIn()) App.saveLoggedInUser(username);
//            SignupMenuController.saveToJson();
//            return "Username changed successfully!";
//        }
        User user = App.getCurrentUser();
        if (user == null) return "Error: no user is logged in";
        return update("username", username, null);
    }

    public String changeNickname(String nickname) {
//        User user = App.getCurrentUser();
//
//        if (user.getNickname().equals(nickname)) {
//            return "Error: New nickname is the same as current nickname.";
//        } else if (!SignupMenuController.validateNickname(nickname)) {
//            return "Error: Invalid nickname.";
//        } else {
//            user.setNickname(nickname);
//            SignupMenuController.saveToJson();
//            return "Nickname changed successfully!";
//        }
        User user = App.getCurrentUser();
        if (user == null) return "Error: no user is logged in";
        if (!SignupMenuController.validateNickname(nickname)) return "Error: Invalid nickname.";
        return update("nickname", nickname, null);
    }

    public String changeEmail(String email) {
//        User user = App.getCurrentUser();
//
//        if (user.getEmail().equals(email)) {
//            return "Error: New email is the same as current email.";
//        } else if (!SignupMenuController.validateEmail(email)) {
//            return "Error: Invalid email.";
//        } else {
//            user.setEmail(email);
//            SignupMenuController.saveToJson();
//            return "Email changed successfully!";
//        }
        User user = App.getCurrentUser();
        if (user == null) return "Error: no user is logged in";
        if (!SignupMenuController.validateEmail(email)) return "Error: Invalid email.";
        return update("email", email, null);
    }

    public String changePassword(String oldPassword, String newPassword) {
//        User user = App.getCurrentUser();
//
//        String hashedNewPassword = SignupMenuController.hashPassword(newPassword);
//        String hashedOldPassword = SignupMenuController.hashPassword(oldPassword);
//
//        if (!user.getPassword().equals(hashedOldPassword)) {
//            return "Error: Current password is incorrect.";
//        } else if (user.getPassword().equals(hashedNewPassword)) {
//            return "Error: New password is the same as current password.";
//        } else if (!SignupMenuController.validatePassword(newPassword)) {
//            return "Error: Password is not strong enough.";
//        } else {
//            user.setPassword(hashedNewPassword);
//            SignupMenuController.saveToJson();
//            return "Password changed successfully!";
//        }
        if (!SignupMenuController.validatePassword(newPassword)) return "Error: Password is not strong enough.";
        return update("password", newPassword, oldPassword);
    }

    /// Phase 3 ///

    private String update(String field, String value, String oldPassword) {
        if (!App.getNetworkClient().isConnected()) return "Error: server is not connected";
        try {
            Request request = new Request(MessageType.UPDATE_PROFILE);
            request.put("field", field);
            request.put("value", value == null ? "" : value);
            if (oldPassword != null) request.put("oldPassword", oldPassword);

            Response response = App.getNetworkClient().sendAndWait(request);
            if (!response.isSuccess()) return response.getMessage();

            User user = App.getCurrentUser();
            if (user != null) {
                switch (field.toLowerCase()) {
                    case "username" -> user.setUsername(response.get("username"));
                    case "nickname" -> user.setNickname(response.get("nickname"));
                    case "email" -> user.setEmail(response.get("email"));
                    case "password" -> user.setPassword(SignupMenuController.hashPassword(value));
                }
                SignupMenuController.saveToJson();
            }
            return response.getMessage();
        } catch (Exception e) {
            return "Error: could not contact server";
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
