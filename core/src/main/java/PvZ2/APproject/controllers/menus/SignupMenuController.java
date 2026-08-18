package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Gender;
import PvZ2.APproject.enums.SecurityQuestions;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.User;
import PvZ2.APproject.views.screens.SignupScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static PvZ2.APproject.enums.Menu.LOGIN_MENU;

public class SignupMenuController{

    public String register(String username, String password, String passwordConfirm, String nickname, String email,
                           String genderSt) {

        if (validateUsername(username)) {
            return "Error: username already exists.";
        } else if (!validatePassword(password)) {
            return "Error: password is not strong enough.";
        } else if (!passwordIsConfirmed(password, passwordConfirm)) {
            return "Error: password not confirmed.";
        } else if (!validateNickname(nickname)) {
            return "Error: nickname is too short";
        } else if (!validateEmail(email)) {
            return "Error: email pattern in not valid";
        } else if (!(genderSt.equalsIgnoreCase("male")) &&
            !(genderSt.equalsIgnoreCase("female"))) {
            return "Error: gender is not valid";
        } else {
            SignupScreen.registered = true;
            Gender gender = whichGender(genderSt);
            String hashedPassword = hashPassword(password);
            User user = new User(username, hashedPassword, nickname, email, gender);
            App.addUser(user);
            saveToJson();
            return "Registered successfully";
        }
    }

    public static boolean validateUsername(String username) {
        return username == null || App.doesUsernameExists(username);
    }

    public static boolean validatePassword(String password) {
        return password == null || password.matches(Commands.PASSWORD.getPattern());
    }

    public static boolean passwordIsConfirmed(String password, String passwordConfirm) {
        return passwordConfirm == null || password.equals(passwordConfirm);
    }

    public static boolean validateNickname(String nickname) {
        return nickname == null || nickname.matches(Commands.NICKNAME.getPattern());
    }

    public static boolean validateEmail(String email) {
        return email == null || email.matches(Commands.EMAIL.getPattern());
    }

    public static Gender whichGender(String gender) {
        if (gender.equalsIgnoreCase("female")) {
            return Gender.female;
        } else {
            return Gender.male;
        }
    }

    public String showQuestions() {
        String message = "";
        for (SecurityQuestions securityQuestion : SecurityQuestions.values()) {
            message += securityQuestion.getNum() + ": " + securityQuestion.getText() + "\n";
        }
        return message;
    }

    public String pickQuestion(int questionNum ,String answer, String answerConfirm) {
        SecurityQuestions question = getQuestionByNumber(questionNum);
        if (question == null) {
            return "invalid question";
        } else if (answer.equals(answerConfirm)) {
            App.getUsers().get(App.getUsers().size() - 1).addQuestion(question, answer);
            SignupScreen.questionPicked = true;
            saveToJson();
            App.setCurrentMenu(LOGIN_MENU);
             return "question picked successfully";
        } else {
            return "not confirmed";
        }
    }

    public SecurityQuestions getQuestionByNumber(int num) {
        for (SecurityQuestions question : SecurityQuestions.values()) {
            if (question.getNum() == num) {
                return question;
            }
        }
        return null;
    }

    public void exit() {
        Gdx.app.exit();
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

    public static void saveToJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ArrayList<User> users = App.getUsers();

        try (Writer writer = Gdx.files.local("Users.json").writer(false)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public static void loadFromJson() {
        Gson gson = new Gson();
        FileHandle file = Gdx.files.local("Users.json");

        if (!file.exists()) {
            App.setUsers(new ArrayList<>());
            return;
        }

        try (Reader reader = file.reader()) {
            Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
            ArrayList<User> loadedUsers = gson.fromJson(reader, userListType);
            App.setUsers(loadedUsers != null ? loadedUsers : new ArrayList<>());
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
            App.setUsers(new ArrayList<>());
        }
    }
}
