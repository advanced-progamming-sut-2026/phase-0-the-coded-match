package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.Main;
import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.controllers.QuestController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.enums.Phases;
import PvZ2.APproject.enums.SecurityQuestions;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.User;
import PvZ2.APproject.views.screens.SignupScreen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginMenuController{
    private static SecurityQuestions resetQuestion;

    public static String login(String username, String password, boolean stayLoggedIn) {


        if (username == null || username.isBlank() || password == null) return "Username and password are required";
        if (!App.getNetworkClient().isConnected()) return "Error: server is not connected";

        try {
            Request request = new Request(MessageType.LOGIN);
            request.put("username", username.trim());
            request.put("password", password);

            User local = App.getUserByUsername(username);
            if (local != null) {
                request.put("nickname", local.getNickname());
                request.put("email", local.getEmail());
                request.put("gender", local.getGender() == null ? "male" : local.getGender().name());
                request.put("legacyStateJson", App.exportCurrentUserStateFor(local));
            }

            Response response = App.getNetworkClient().sendAndWait(request);
            if (!response.isSuccess()) return response.getMessage();

            boolean remembered = stayLoggedIn;
            App.setCurrentUser(local);
            if (local != null) local.setStayLoggedIn(remembered);
            App.applyServerUserState(response.getData(), password);
            if (App.getCurrentUser() != null) App.getCurrentUser().setStayLoggedIn(remembered);

            if (stayLoggedIn) App.saveLoggedInUser(username);
            else App.clearLoggedInUser();
            App.setCurrentMenu(Menu.MAIN_MENU);
            QuestController.generateAllQuests();
            QuestController.refreshDailyQuests();
            SignupMenuController.saveToJson();
            return response.getMessage();
        } catch (Exception e) {
            return "Error: could not contact server (" + e.getMessage() + ")";
        }
    }

    public static String forgotPassword(String username, String email) {
//        User target = App.getUserByUsername(username);
//        if(target == null){
//            return "User does not exist";
//        }
//        if(!target.getEmail().equals(email)){
//            return "Incorrect email";
//        }
//        App.setUserUndergoingReset(target);
//        resetQuestion = null;
//        for (SecurityQuestions questions : target.getQuestions().keySet()) {
//            resetQuestion = questions;
//            return questions.getText();
//        }
//        return "no question";

        ///  Phase 3 implementation ///
        try {
            Request request = new Request(MessageType.FORGOT_PASSWORD);
            request.put("username", username);
            request.put("email", email);
            Response response = App.getNetworkClient().sendAndWait(request);
            if (!response.isSuccess()) return response.getMessage();
            App.setUserUndergoingReset(App.getUserByUsername(username));
            App.setResetUsername(username);
            return response.get("question");
        } catch (Exception e) {
            return "Error: could not contact server";
        }
    }

    public static String isAnswerCorrect(String input) {
//        if (input == null) return "Wrong answer";
//        String answer = input.trim();
//        Matcher matcher = Pattern.compile(Commands.ANSWER.getPattern()).matcher(input);
//        if (matcher.matches()) answer = matcher.group("answer").trim();
//        User target = App.getUserUndergoingReset();
//        String expected = target == null || resetQuestion == null ? null : target.getQuestions().get(resetQuestion);
//        if (expected == null || !expected.trim().equalsIgnoreCase(answer)){
//            return "Wrong answer";
//        }
//        App.setCurrentPhase(Phases.RESETTING_PASSWORD);
//        return "Please enter your new password";

        /// Phase 3 implementaion ///
//        Pattern pattern = Pattern.compile(Commands.ANSWER.getPattern());
//        Matcher matcher = pattern.matcher(input == null ? "" : input);
//        if (!matcher.matches()) return "invalid command";
        String username = App.getResetUsername();
        if (username == null || username.isBlank()) {
            App.setCurrentMenu(Menu.SIGNUP_MENU);
            return "Wrong answer";
        }
        try {
            Request request = new Request(MessageType.VERIFY_SECURITY_ANSWER);
            request.put("username", username);
            request.put("answer", input);
            Response response = App.getNetworkClient().sendAndWait(request);
            if (!response.isSuccess()) {
                App.setCurrentMenu(Menu.SIGNUP_MENU);
                return response.getMessage();
            }
            App.setCurrentPhase(Phases.RESETTING_PASSWORD);
            App.setResetToken(response.get("resetToken"));
            return response.getMessage();
        } catch (Exception e) {
            return "Error: could not contact server";
        }
    }

    public static String resetPassword(String newPassword) {
//        if (input == null) return "invalid password";
//        String newPassword = input.trim();
//        Matcher matcher = Pattern.compile(Commands.NEW_PASSWORD.getPattern()).matcher(input);
//        if (matcher.matches()) newPassword = matcher.group("password");
//        if (!SignupMenuController.validatePassword(newPassword)) {
//            return "invalid password";
//        }
//        User target = App.getUserUndergoingReset();
//        if (target == null) return "No password reset request";
//        target.setPassword(SignupMenuController.hashPassword(newPassword));
//        App.setCurrentPhase(Phases.NORMAL_GAMEPLAY);
//        App.setUserUndergoingReset(null);
//        resetQuestion = null;
//        SignupMenuController.saveToJson();
//        return "Password reset successfully";

        /// Phase 3 implementation ///
//        Pattern pattern = Pattern.compile(Commands.NEW_PASSWORD.getPattern());
//        Matcher matcher = pattern.matcher(input == null ? "" : input);
//        if (!matcher.matches()) return "invalid command";
//        String newPassword = matcher.group("password");
        if (!SignupMenuController.validatePassword(newPassword)) return "invalid password";
        if (App.getResetToken() == null) return "No password reset request";

        try {
            Request request = new Request(MessageType.RESET_PASSWORD);
            request.put("resetToken", App.getResetToken());
            request.put("password", newPassword);
            Response response = App.getNetworkClient().sendAndWait(request);
            if (response.isSuccess()) {
                App.setResetToken(null);
                App.setResetUsername(null);
                App.setUserUndergoingReset(null);
                App.setCurrentPhase(Phases.NORMAL_GAMEPLAY);
            }
            return response.getMessage();
        } catch (Exception e) {
            return "Error: could not contact server";
        }
    }

    public static void exit(Main game) {
        App.setCurrentMenu(Menu.SIGNUP_MENU);
        game.setScreen(new SignupScreen(game));
    }
}
