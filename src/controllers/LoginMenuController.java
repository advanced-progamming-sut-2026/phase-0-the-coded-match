package controllers;

import enums.Menu;
import enums.Phases;
import models.App;
import models.User;

import java.util.Scanner;

public class LoginMenuController extends MenuController {

    @Override
    public void enterMenu(String targetMenu) {

    }

    @Override
    public void exitMenu() {

    }

    public String login(String username, String password, String stayLoggedIn) {
        User target=findUserByUsername(username);
        if(target == null){
            return "User does not exist";
        }
        if(!target.getPassword().equals(password)){
            return "Incorrect password";
        }
        App.setCurrentUser(target);
        if(!stayLoggedIn.isEmpty()){
            App.getCurrentUser().setStayLoggedIn(true);
        }
        return "Logged in successfully";
    }

    public String forgotPassword(String username, String email) {
        User target = findUserByUsername(username);
        if(target == null){
            return "User does not exist";
        }
        if(!target.getEmail().equals(email)){
            return "Incorrect email";
        }
        App.setUserUndergoingReset(target);
        return target.getQuestions().keySet().toString();

    }

    public static String isAnswerCorrect(String answer) {
        User target = App.getUserUndergoingReset();
        if (!target.getQuestions().containsValue(answer)){
            App.setCurrentMenu(Menu.SIGNUP_MENU);
            return "Wrong answer";
        }
        App.setCurrentPhase(Phases.RESETTING_PASSWORD);
        return "Please enter your new password";
    }

    public static String resetPassword(String newPassword){
        if (newPassword == null || newPassword.length() < 8) {
            return "Invalid password: It must be at least 8 characters long.";
        }
        if (!newPassword.matches(".*[A-Z].*")) {
            return "Invalid password: It must contain at least one uppercase letter.";
        }
        if (!newPassword.matches(".*[a-z].*")) {
            return "Invalid password: It must contain at least one lowercase letter.";
        }
        if (!newPassword.matches(".*\\d.*")) {
            return "Invalid password: It must contain at least one number.";
        }
        String specialCharsRegex = ".*[?><,\"';:\\\\/|\\[\\]}{+=\\(\\)*&^%$#!].*";
        if (!newPassword.matches(specialCharsRegex)) {
            return "Invalid password: It must contain at least one special character.";
        }
        User target = App.getUserUndergoingReset();
        target.setPassword(newPassword);
        App.setCurrentPhase(Phases.NORMAL_GAMEPLAY);
        return "Password reset successfully";
    }

    private User findUserByUsername(String username){
        for(User u : App.getUsers()){
            if(u.getUsername().equals(username)){
                return u;
            }
        }
        return null;
    }

    public static void savePlayer() {

    }

    public static void loadPlayer() {

    }

    public static void clearPlayer() {

    }
}
