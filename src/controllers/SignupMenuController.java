package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import enums.Commands;
import enums.Gender;
import enums.SecurityQuestions;
import models.App;
import models.User;
import views.SignupMenu;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
            String hashedPassword = hashPassword(password);
            User user = new User(username, hashedPassword, nickname, email, gender);
            App.addUser(user);
            saveToJson(user);
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

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public static void saveToJson(User newUser) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("assets/Users.json")){
            gson.toJson(newUser, writer);
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    public static void loadFromJson() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("assets/Users.json")){
            Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
            ArrayList<User> loadedUsers = gson.fromJson(reader, userListType);
            App.setUsers(loadedUsers != null ? loadedUsers : new ArrayList<>());
            return;
        } catch (FileNotFoundException e) {
            App.setUsers(new ArrayList<>());
            return;
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
            App.setUsers(new ArrayList<>());
            return;
        }
    }
}
