package PvZ2.APproject.server.services;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Gender;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.User;
import PvZ2.APproject.server.ClientHandler;
import PvZ2.APproject.server.models.ServerUser;
import PvZ2.APproject.server.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private final UserRepository users;

    public AuthService(UserRepository users) {
        this.users = users;
    }

    public Response register(Request request){
        String username = request.get("username");
        String password = request.get("password");
        String passwordConfirm = request.get("passwordConfirm");
        String nickname = request.get("nickname");
        String email = request.get("email");
        String gender = request.get("gender");
        if(blank(username) || blank(password) || blank(nickname) || blank(email) || blank(gender)){
            return Response.error(request.getRequestId(), "Missing registration field");
        }
        if (validateUsername(username)) {
            return Response.error(request.getRequestId(), "Error: username already exists.");
        } else if (!validatePassword(password)) {
            return Response.error(request.getRequestId(), "Error: password is not strong enough.");
        } else if (!passwordIsConfirmed(password, passwordConfirm)) {
            return Response.error(request.getRequestId(), "Error: password not confirmed.");
        } else if (!validateNickname(nickname)) {
            return Response.error(request.getRequestId(), "Error: nickname is too short");
        } else if (!validateEmail(email)) {
            return Response.error(request.getRequestId(), "Error: email pattern in not valid");
        } else if (!(gender.equalsIgnoreCase("male")) &&
            !(gender.equalsIgnoreCase("female"))) {
            return Response.error(request.getRequestId(), "Error: gender is not valid");
        }
        if (users.exists(username)) {
            return Response.error(request.getRequestId(), "Username already exists");
        }
        users.save(new ServerUser(username, UserRepository.hashPassword(password), nickname, email, gender == null ? "" : gender));
        return Response.ok(request.getRequestId(), MessageType.REGISTER, "Registration successful");

    }

    public Response login(Request request, ClientHandler handler){
        String username = request.get("username");
        String password = request.get("password");
        ServerUser user = users.find(username);
        if(user == null){
            return Response.error(request.getRequestId(), "User does not exist");
        }
        if (!user.getPasswordHash().equals(UserRepository.hashPassword(password == null ? "" : password))){
            return Response.error(request.getRequestId(), "Incorrect password");
        }
        handler.setUsername(user.getUsername());
        Map<String, String> info = profile(user);
        return new Response(request.getRequestId(), MessageType.LOGIN, true, "Logged in successfully", info);
    }

    public Response profile(Request request, ClientHandler handler) {
        ServerUser user = users.find(handler.getUsername());
        if (user == null){
            return Response.error(request.getRequestId(), "Not logged in");
        }
        return new Response(request.getRequestId(), MessageType.GET_PROFILE, true, "Profile", profile(user));
    }

    public Response logout(Request request, ClientHandler handler) {
        handler.setUsername(null);
        return Response.ok(request.getRequestId(), MessageType.LOGOUT, "Logged out");
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

    public Map<String, String> profile(ServerUser u) {
        Map<String, String> d = new HashMap<>();
        d.put("username", u.getUsername());
        d.put("nickname", u.getNickname());
        d.put("email", u.getEmail());
        d.put("gender", u.getGender());
        d.put("coins", String.valueOf(u.getCoins()));
        d.put("gems", String.valueOf(u.getGems()));
        d.put("minigamesWon", String.valueOf(u.getMinigamesWon()));
        d.put("highestPoint", String.valueOf(u.getHighestPoint()));
        d.put("gamesPlayed", String.valueOf(u.getGamesPlayed()));
        return d;
    }

    private boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
