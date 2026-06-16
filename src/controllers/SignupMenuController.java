package controllers;

import enums.Commands;
import enums.Gender;
import enums.SecurityQuestions;
import models.App;
import models.User;
import views.SignupMenu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupMenuController{

    public static String[] register(String input, String[] message) {
        Pattern pattern = Pattern.compile(Commands.REGISTER.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return null;
        }

        String username = matcher.group("username");
        String password = matcher.group("password");
        String passwordConfirm = matcher.group("password_confirm");
        String nickname = matcher.group("nickname");
        String email = matcher.group("email");
        String genderSt = matcher.group("gender");

        if (validateUsername(username)) {
            message[0] = "Error: username already exists.";
        } else if (!validatePassword(password)) {
            message[0] = "Error: password is not strong enough.";
        } else if (!passwordIsConfirmed(password, passwordConfirm)) {
            message[0] = "Error: password not confirmed."; //TODO: enter password again or go back to signup menu
        } else if (!validateNickname(nickname)) {
            message[0] = "Error: nickname is too short";
        } else {
            SignupMenu.registered = true;
            Gender gender = whichGender(genderSt);
            User user = new User(username, password, nickname, email, gender);
            App.addUser(user);
            message[0] = "";
        }
        return message;
    }

    public static boolean validateUsername(String username) {
        return App.doesUsernameExist(username);
    }

    public static boolean validatePassword(String password) {
        return password.matches(Commands.PASSWORD.getPattern());
    }

    public static boolean passwordIsConfirmed(String password, String passwordConfirm) {
        return password.equals(passwordConfirm);
    }

    public static boolean validateNickname(String nickname) {
        return nickname.matches(Commands.NICKNAME.getPattern());
    }

//    public static boolean validateEmail(String email) {
//
//    }

    public static Gender whichGender(String gender) {
        if (gender.equalsIgnoreCase("female")) {
            return Gender.female;
        } else {
            return Gender.male;
        }
    }

    public static String[] showQuestion(String input, String[] message) {
        Pattern pattern = Pattern.compile(Commands.PICK_QUESTION.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            message[0] = "invalid command";
            return message;
        }
        int questionNum = Integer.parseInt(matcher.group("question_number"));
        SecurityQuestions question = getQuestionByNumber(questionNum);
        if (question == null) {
            return message;
        } else {
            message[0] = question.getText();
        }
        return message;
    }

    public static void pickQuestion(String input) {
        Pattern pattern = Pattern.compile(Commands.PICK_QUESTION.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return;
        }
        int questionNum = Integer.parseInt(matcher.group("question_number"));
        String answer = matcher.group("answer");
        String confirmAnswer = matcher.group("answer_confirm");
        SecurityQuestions question = getQuestionByNumber(questionNum);
        if (answer.equals(confirmAnswer)) {
            App.getUsers().get(App.getUsers().size() - 1).addQuestion(question, answer);
        }
    }

    public static SecurityQuestions getQuestionByNumber(int num) {
        for (SecurityQuestions question : SecurityQuestions.values()) {
            if (question.getNum() == num) {
                return question;
            }
        }
        return null;
    }

    public static void hashPassword(String password) {

    }

    public static void saveToFile() {

    }

    public static void loadFromFile() {

    }
}
