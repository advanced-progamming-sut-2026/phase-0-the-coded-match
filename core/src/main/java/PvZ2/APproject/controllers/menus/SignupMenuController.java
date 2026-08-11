package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Gender;
import PvZ2.APproject.enums.SecurityQuestions;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.User;
import PvZ2.APproject.utils.AssetPaths;
import PvZ2.APproject.views.menus.SignupMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static PvZ2.APproject.enums.Menu.LOGIN_MENU;

public class SignupMenuController{

    public static String[] register(String input, String[] message) {
        Pattern pattern = Pattern.compile(Commands.REGISTER.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return null;
        }

        String username = matcher.group("username");
        String password = matcher.group("password");
        String passwordConfirm = matcher.group("passwordConfirm");
        String nickname = matcher.group("nickname");
        String email = matcher.group("email");
        String genderSt = matcher.group("gender");

        if (validateUsername(username)) {
            message[0] = "Error: username already exists.";
        } else if (!validatePassword(password)) {
            message[0] = "Error: password is not strong enough.";
        } else if (!passwordIsConfirmed(password, passwordConfirm)) {
            message[0] = "Error: password not confirmed.";
        } else if (!validateNickname(nickname)) {
            message[0] = "Error: nickname is too short";
        } else if (!validateEmail(email)) {
            message[0] = "Error: email pattern in not valid";
        } else if (!(genderSt.equalsIgnoreCase("male")) && !(genderSt.equalsIgnoreCase("female"))) {
            message[0] = "Error: gender is not valid " + genderSt;
        } else {
            SignupMenu.registered = true;
            Gender gender = whichGender(genderSt);
            String hashedPassword = hashPassword(password);
            User user = new User(username, hashedPassword, nickname, email, gender);
            App.addUser(user);
            saveToJson();
            message[0] = "Registered successfully";
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

    public static boolean validateEmail(String email) {
        return email.matches(Commands.EMAIL.getPattern());
    }

    public static Gender whichGender(String gender) {
        if (gender.equalsIgnoreCase("female")) {
            return Gender.female;
        } else {
            return Gender.male;
        }
    }

    public static String[] showQuestions(String input, String[] message) {
        message[0] = "";
        for (SecurityQuestions securityQuestion : SecurityQuestions.values()) {
            message[0] += securityQuestion.getNum() + ": " + securityQuestion.getText() + "\n";
        }
        return message;
    }

    public static String[] pickQuestion(String input, String[] message) {
        Pattern pattern = Pattern.compile(Commands.PICK_QUESTION.getPattern());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            message[0] = "pick a question!";
            return message;
        }
        int questionNum;
        try { questionNum = Integer.parseInt(matcher.group("questionNumber").trim()); } catch (NumberFormatException e) { message[0] = "invalid question"; return message; }
        String answer = matcher.group("answer");
        String confirmAnswer = matcher.group("answerConfirm");
        SecurityQuestions question = getQuestionByNumber(questionNum);
        if (question == null) {
            message[0] = "invalid question";
        } else if (answer.equals(confirmAnswer)) {
            App.getUsers().get(App.getUsers().size() - 1).addQuestion(question, answer);
            SignupMenu.questionPicked = true;
            saveToJson();
            App.setCurrentMenu(LOGIN_MENU);
            message[0] = "question picked successfully";
        } else {
            message[0] = "not confirmed";
        }
        return message;
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

    public static void saveToJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ArrayList<User> users = App.getUsers();
        try (Writer writer = AssetPaths.writer("Users.json")) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public static void loadFromJson() {
        Gson gson = new Gson();
        try (Reader reader = AssetPaths.reader("Users.json")) {
            Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
            ArrayList<User> loadedUsers = gson.fromJson(reader, userListType);
            App.setUsers(loadedUsers != null ? loadedUsers : new ArrayList<>());
        } catch (FileNotFoundException e) {
            App.setUsers(new ArrayList<>());
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
            App.setUsers(new ArrayList<>());
        }
    }
}
