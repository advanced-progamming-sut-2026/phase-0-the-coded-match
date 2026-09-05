package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
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
    private static User pendingUser;
    private static String pendingRegistrationUsername;
    private static String pendingRegistrationToken;

    public String register(String username, String password, String passwordConfirm, String nickname, String email,
                           String genderSt) {
        if (!validateUsername(username)) {
            return "Error: username is invalid or already exists.";
        } else if (!validatePassword(password)) {
            return "Error: password is not strong enough.";
        } else if (!passwordIsConfirmed(password, passwordConfirm)) {
            return "Error: password not confirmed.";
        } else if (!validateNickname(nickname)) {
            return "Error: nickname is too short";
        } else if (!validateEmail(email)) {
            return "Error: email pattern in not valid";
        } else if (genderSt == null || (!(genderSt.equalsIgnoreCase("male")) &&
            !(genderSt.equalsIgnoreCase("female")))) {
            return "Error: gender is not valid";
        }
//        else {
//            SignupScreen.registered = true;
//            Gender gender = whichGender(genderSt);
//            String hashedPassword = hashPassword(password);
//            pendingUser = new User(username, hashedPassword, nickname, email, gender);
//            return "Registered successfully";
//        }
        /// Phase 3 implementation ///
        if (username == null || username.isBlank()) return "Error: username is required";
        if (!App.getNetworkClient().isConnected()) return "Error: server is not connected";

        try {
            Request request = new Request(MessageType.REGISTER);
            request.put("username", username.trim());
            request.put("password", password);
            request.put("passwordConfirm", passwordConfirm);
            request.put("nickname", nickname.trim());
            request.put("email", email.trim());
            request.put("gender", genderSt.toLowerCase());

            Response response = App.getNetworkClient().sendAndWait(request);
            if (!response.isSuccess()) return response.getMessage();

            Gender gender = whichGender(genderSt);
            User user = new User(username.trim(), hashPassword(password), nickname.trim(), email.trim(), gender);
            App.addUser(user);
            pendingRegistrationUsername = username.trim();
            pendingRegistrationToken = response.get("registrationToken");
            SignupScreen.registered = true;
            saveToJson();
            return response.getMessage();
        } catch (Exception e) {
            return "Error: could not contact server (" + e.getMessage() + ")";
        }
    }

    public static boolean validateUsername(String username) {
        return username != null && username.matches("[A-Za-z0-9-]+") && !App.doesUsernameExists(username);
    }

    public static boolean validatePassword(String password) {
        return password != null && password.matches(Commands.PASSWORD.getPattern());
    }

    public static boolean passwordIsConfirmed(String password, String passwordConfirm) {
        return password != null && passwordConfirm != null && password.equals(passwordConfirm);
    }

    public static boolean validateNickname(String nickname) {
        return nickname != null && nickname.matches(Commands.NICKNAME.getPattern());
    }

    public static boolean validateEmail(String email) {
        return email != null && email.matches(Commands.EMAIL.getPattern());
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
        }
//        else if (pendingUser == null) {
//            return "registration expired";
//        } else if (answer != null && answer.equals(answerConfirm)) {
//            pendingUser.addQuestion(question, answer);
//            App.addUser(pendingUser);
//            pendingUser = null;
//            SignupScreen.questionPicked = true;
//            saveToJson();
//            App.setCurrentMenu(LOGIN_MENU);
//             return "question picked successfully";
//        } else {
//            return "not confirmed";
//        }

        /// Phase 3 implementation ///

        if (answer == null || !answer.equals(answerConfirm)) return "not confirmed";
        if (pendingRegistrationUsername == null) return "no pending registration";

        try {
            Request request = new Request(MessageType.SET_SECURITY_QUESTION);
            request.put("question", question.getText());
            request.put("answer", answer);
            request.put("registrationToken", pendingRegistrationToken);
            Response response = App.getNetworkClient().sendAndWait(request);
            if (!response.isSuccess()) return response.getMessage();

            User user = App.getUserByUsername(pendingRegistrationUsername);
            if (user != null) {
                user.addQuestion(question, answer);
                saveToJson();
            }
            SignupScreen.questionPicked = true;
            pendingRegistrationUsername = null;
            pendingRegistrationToken = null;
            App.setCurrentMenu(LOGIN_MENU);
            return "question picked successfully";
        } catch (Exception e) {
            return "Error: could not save security question";
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
        /// Phase 3 ///
        App.syncCurrentUserToServer();
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
