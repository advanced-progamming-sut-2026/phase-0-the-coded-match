package controllers.menus;

import controllers.QuestController;
import enums.Commands;
import enums.Menu;
import enums.Phases;
import enums.SecurityQuestions;
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
        boolean stayLoggedIn = matcher.group("stay") != null;

        User target = App.getUserByUsername(username);
        if(target == null){
            return "User does not exist";
        }
        String hashedPassword = SignupMenuController.hashPassword(password);
        if(!target.getPassword().equals(hashedPassword)){
            return "Incorrect password";
        }
        App.setCurrentUser(target);
        target.setStayLoggedIn(stayLoggedIn);
        if (stayLoggedIn) App.saveLoggedInUser(username); else App.clearLoggedInUser();
        App.setCurrentMenu(Menu.MAIN_MENU);
        QuestController.generateAllQuests();
        QuestController.refreshDailyQuests();
        return "Logged in successfully";
    }

    public static String forgotPassword(String input) {
        Matcher matcher = Pattern.compile(Commands.FORGET_PASSWORD.getPattern()).matcher(input);
        if (!matcher.matches()) return "invalid command";
        String username = matcher.group("username"), email = matcher.group("email");
        User target = App.getUserByUsername(username);
        if(target == null){
            return "User does not exist";
        }
        if(!target.getEmail().equals(email)){
            return "Incorrect email";
        }
        App.setUserUndergoingReset(target);
        for (SecurityQuestions questions : target.getQuestions().keySet()) {
            return questions.getText();
        }
        return "no question";
    }

    public static String isAnswerCorrect(String input) {
        Pattern pattern = Pattern.compile(Commands.ANSWER.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return "invalid command";
        }
        String answer = matcher.group("answer");
        User target = App.getUserUndergoingReset();
        if (target == null || !target.getQuestions().containsValue(answer)){
            App.setCurrentMenu(Menu.SIGNUP_MENU);
            return "Wrong answer";
        }
        App.setCurrentPhase(Phases.RESETTING_PASSWORD);
        return "Please enter your new password";
    }

    public static String resetPassword(String input) {
        Pattern pattern = Pattern.compile(Commands.NEW_PASSWORD.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return "invalid command";
        }
        String newPassword = matcher.group("password");
        if (newPassword == null || !SignupMenuController.validatePassword(newPassword)) {
            return "invalid password";
        }
        User target = App.getUserUndergoingReset();
        if (target == null) return "No password reset request";
        target.setPassword(SignupMenuController.hashPassword(newPassword));
        App.setCurrentPhase(Phases.NORMAL_GAMEPLAY);
        SignupMenuController.saveToJson();
        return "Password reset successfully";
    }

    public static void saveLoggedInUserData() {

    }

    public static void loadPlayer() {

    }

    public static void clearPlayer() {

    }
}
