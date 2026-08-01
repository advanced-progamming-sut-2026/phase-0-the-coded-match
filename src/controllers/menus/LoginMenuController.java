package controllers.menus;

import controllers.QuestController;
import enums.Commands;
import enums.Menu;
import enums.Phases;
import models.App;
import models.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginMenuController{

    public static String login(String input) {
        Pattern pattern = Pattern.compile(Commands.LOGIN.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return "invalid command";
        }

        String username = matcher.group("username");
        String password = matcher.group("password");
        String stayLoggedIn = matcher.group(1); //right??

        User target = App.getUserByUsername(username);
        if(target == null){
            return "User does not exist";
        }
        String hashedPassword = SignupMenuController.hashPassword(password);
        if(!target.getPassword().equals(hashedPassword)){
            return "Incorrect password";
        }
        App.setCurrentUser(target);
        App.saveLoggedInUser(username);
        if(!stayLoggedIn.isEmpty()){
            App.getCurrentUser().setStayLoggedIn(true); //TODO: is this necessary?
        }
        QuestController.generateAllQuests();
        QuestController.refreshDailyQuests();
        return "Logged in successfully";
    }

    public String forgotPassword(String username, String email) {
        User target = App.getUserByUsername(username);
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

    public static String resetPassword(String newPassword) {
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
        target.setPassword(SignupMenuController.hashPassword(newPassword));
        App.setCurrentPhase(Phases.NORMAL_GAMEPLAY);
        return "Password reset successfully";
    }

    public static void saveLoggedInUserData() {

    }

    public static void loadPlayer() {

    }

    public static void clearPlayer() {

    }
}
