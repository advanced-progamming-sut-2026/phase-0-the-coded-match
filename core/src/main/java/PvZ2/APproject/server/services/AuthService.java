package PvZ2.APproject.server.services;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Gender;
import PvZ2.APproject.enums.SecurityQuestions;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.User;
import PvZ2.APproject.server.ClientHandler;
import PvZ2.APproject.server.Server;
import PvZ2.APproject.server.models.ServerUser;
import PvZ2.APproject.server.repositories.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {
    private static final long RESET_TOKEN_LIFETIME_MS = 5 * 60 * 1000L;

    private final UserRepository users;
    private final Map<String, ResetToken> resetTokens = new ConcurrentHashMap<>();
    private final Map<String, RegistrationToken> registrationTokens = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

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
//        if(blank(username) || blank(password) || blank(nickname) || blank(email) || blank(gender)){
//            return Response.error(request.getRequestId(), "Missing registration field");
//        }
//        if (validateUsername(username)) {
//            return Response.error(request.getRequestId(), "Error: username already exists.");
//        } else if (!validatePassword(password)) {
//            return Response.error(request.getRequestId(), "Error: password is not strong enough.");
//        } else if (!passwordIsConfirmed(password, passwordConfirm)) {
//            return Response.error(request.getRequestId(), "Error: password not confirmed.");
//        } else if (!validateNickname(nickname)) {
//            return Response.error(request.getRequestId(), "Error: nickname is too short");
//        } else if (!validateEmail(email)) {
//            return Response.error(request.getRequestId(), "Error: email pattern in not valid");
//        } else if (!(gender.equalsIgnoreCase("male")) &&
//            !(gender.equalsIgnoreCase("female"))) {
//            return Response.error(request.getRequestId(), "Error: gender is not valid");
//        }
//        if (users.exists(username)) {
//            return Response.error(request.getRequestId(), "Username already exists");
//        }
//        users.save(new ServerUser(username, UserRepository.hashPassword(password), nickname, email, gender == null ?
//        "" : gender));
//        return Response.ok(request.getRequestId(), MessageType.REGISTER, "Registration successful");

        /// new and better hopefully ///
        String error = validateRegistration(username, password, passwordConfirm, nickname, email, gender);
        if (error != null) return Response.error(request.getRequestId(), error);
        if (users.exists(username)) return Response.error(request.getRequestId(), "Username already exists");

        ServerUser user = new ServerUser(username.trim(), UserRepository.hashPassword(password), nickname.trim(),
            email.trim(), gender.toLowerCase());
        String state = request.get("stateJson");
        if (state != null && !state.isBlank()) applyState(user, state);
        users.save(user);
        String registrationToken = UUID.randomUUID().toString();
        registrationTokens.put(registrationToken, new RegistrationToken(user.getUsername(),
            System.currentTimeMillis() + RESET_TOKEN_LIFETIME_MS));
        Map<String, String> result = profile(user);
        result.put("registrationToken", registrationToken);
        return new Response(request.getRequestId(), MessageType.REGISTER, true,
            "Registration successful", result);

    }

    public Response login(Request request, ClientHandler handler){
        String username = request.get("username");
        String password = request.get("password");
        ServerUser user = users.find(username);
        if(user == null){
            String legacyState = request.get("legacyStateJson");
            String nickname = request.get("nickname");
            String email = request.get("email");
            String gender = request.get("gender");
            if (legacyState == null || legacyState.isBlank()) {
                return Response.error(request.getRequestId(), "User does not exist");
            }
            if (blank(nickname) || blank(email) || blank(gender)) {
                return Response.error(request.getRequestId(), "Legacy account migration data is incomplete");
            }
            if (!validatePassword(password) || !validateNickname(nickname) || !validateEmail(email)) {
                return Response.error(request.getRequestId(), "Legacy account data is invalid");
            }
            user = new ServerUser(username.trim(), UserRepository.hashPassword(password), nickname.trim(),
                email.trim(), gender.toLowerCase());
            applyState(user, legacyState);
            users.save(user);
        }
        if (!user.getPasswordHash().equals(UserRepository.hashPassword(password == null ? "" : password))){
            return Response.error(request.getRequestId(), "Incorrect password");
        }
        ClientHandler existing = Server.online(user.getUsername());
        if (existing != null && existing != handler) {
            return Response.error(request.getRequestId(), "User is already logged in");
        }
        String legacyState = request.get("legacyStateJson");
        if (user.getGameStateJson().isBlank() && legacyState != null && !legacyState.isBlank()) {
            applyState(user, legacyState);
            users.update(user);
        }
        handler.setUsername(user.getUsername());
        Map<String, String> info = profile(user);
        return new Response(request.getRequestId(), MessageType.LOGIN, true, "Logged in successfully",
            info);

    }

    public Response updateProfile(Request request, ClientHandler handler) {
        ServerUser user = loggedInUser(handler, request);
        if (user == null) return Response.error(request.getRequestId(), "Not logged in");
        String field = request.get("field");
        String value = request.get("value");
        if (blank(field) || value == null) return Response.error(request.getRequestId(),
            "Missing profile field");
        switch (field.toLowerCase()) {
            case "nickname" -> {
                if (!validateNickname(value)) return Response.error(request.getRequestId(), "Invalid nickname");
                if (value.equals(user.getNickname())) return Response.error(request.getRequestId(),
                    "New nickname is the same as current nickname");
                user.setNickname(value.trim());
            }case "email" -> {
                if (!validateEmail(value)) return Response.error(request.getRequestId(), "Invalid email");
                if (value.equalsIgnoreCase(user.getEmail())) return Response.error(request.getRequestId(),
                    "New email is the same as current email");
                user.setEmail(value.trim());
            }case "password" -> {
                String oldPassword = request.get("oldPassword");
                if (!user.getPasswordHash().equals(UserRepository.hashPassword(oldPassword == null ?
                    "" : oldPassword))) {
                    return Response.error(request.getRequestId(), "Current password is incorrect");
                }
                if (!validatePassword(value)) return Response.error(request.getRequestId(),
                    "Password is not strong enough");
                if (user.getPasswordHash().equals(UserRepository.hashPassword(value))) {
                    return Response.error(request.getRequestId(), "New password is the same as current password");
                }
                user.setPasswordHash(UserRepository.hashPassword(value));
            }case "username" -> {
                if (!validateUsername(value)) return Response.error(request.getRequestId(), "Invalid username");
                if (value.equalsIgnoreCase(user.getUsername())) return Response.error(request.getRequestId(),
                    "New username is the same as current username");
                if (users.exists(value)) return Response.error(request.getRequestId(), "Username already exists");
                String old = user.getUsername();
                if (!users.rename(old, value.trim())) return Response.error(request.getRequestId(),
                    "Could not change username");
                handler.setUsername(value.trim());
                user = users.find(value.trim());
            }default -> {
                return Response.error(request.getRequestId(), "Unsupported profile field");
            }
        }

        updateStoredProfileFields(user);
        users.update(user);
        return new Response(request.getRequestId(), MessageType.UPDATE_PROFILE, true, "Profile updated", profile(user));
    }

    public Response profile(Request request, ClientHandler handler) {
//        ServerUser user = users.find(handler.getUsername());
        ServerUser user = loggedInUser(handler, request);
        if (user == null){
            return Response.error(request.getRequestId(), "Not logged in");
        }
        return new Response(request.getRequestId(), MessageType.GET_PROFILE, true, "Profile", profile(user));
    }

    private ServerUser loggedInUser(ClientHandler handler, Request request) {
        if (handler.getUsername() == null) return null;
        ServerUser user = users.find(handler.getUsername());
        if (user == null) return null;
        return user;
    }

    public Response logout(Request request, ClientHandler handler) {
        handler.setUsername(null);
        handler.setSessionId(null);
        return Response.ok(request.getRequestId(), MessageType.LOGOUT, "Logged out");
    }

    public Response setSecurityQuestion(Request request, ClientHandler handler) {
        ServerUser user = loggedInUser(handler, request);
        String registrationToken = request.get("registrationToken");
        if (user == null && registrationToken != null) {
            RegistrationToken token = registrationTokens.remove(registrationToken);
            if (token == null || token.expiresAt < System.currentTimeMillis()) {
                return Response.error(request.getRequestId(), "Registration token is invalid or expired");
            }
            user = users.find(token.username);
        }
        if (user == null) return Response.error(request.getRequestId(), "Not logged in");
        String question = request.get("question");
        String answer = request.get("answer");
        if (blank(question) || blank(answer)) return Response.error(request.getRequestId(),
            "Missing security question or answer");
        user.getSecurityQuestions().put(question, answer);
        users.update(user);
        return Response.ok(request.getRequestId(), MessageType.SET_SECURITY_QUESTION, "Security question saved");
    }

    public Response forgotPassword(Request request) {
        String username = request.get("username");
        String email = request.get("email");
        ServerUser user = users.find(username);
        if (user == null) return Response.error(request.getRequestId(), "User does not exist");
        if (!user.getEmail().equalsIgnoreCase(email == null ? "" : email.trim())) {
            return Response.error(request.getRequestId(), "Incorrect email");
        }
        if (user.getSecurityQuestions().isEmpty()) {
            return Response.error(request.getRequestId(), "No security question is configured");
        }
        String question = user.getSecurityQuestions().keySet().iterator().next();
        for (SecurityQuestions q : SecurityQuestions.values()) {
            if (q.name().equalsIgnoreCase(question)) { question = q.getText(); break; }
        }
        return new Response(request.getRequestId(), MessageType.FORGOT_PASSWORD, true,
            "Security question", Map.of("question", question));
    }

    public Response verifySecurityAnswer(Request request) {
        String username = request.get("username");
        String answer = request.get("answer");
        ServerUser user = users.find(username);
        if (user == null) return Response.error(request.getRequestId(), "User does not exist");

        boolean correct = user.getSecurityQuestions().values().stream().anyMatch(v -> v.equals(answer));
        if (!correct) return Response.error(request.getRequestId(), "Wrong answer");

        String token = UUID.randomUUID().toString();
        resetTokens.put(token, new ResetToken(user.getUsername(), System.currentTimeMillis()
            + RESET_TOKEN_LIFETIME_MS));
        return new Response(request.getRequestId(), MessageType.VERIFY_SECURITY_ANSWER, true,
            "Please enter your new password", Map.of("resetToken", token));
    }

    public Response resetPassword(Request request) {
        String token = request.get("resetToken");
        String newPassword = request.get("password");
        ResetToken reset = token == null ? null : resetTokens.remove(token);
        if (reset == null || reset.expiresAt < System.currentTimeMillis()) {
            return Response.error(request.getRequestId(), "Password reset request is invalid or expired");
        }
        if (!validatePassword(newPassword)) return Response.error(request.getRequestId(), "Invalid password");

        ServerUser user = users.find(reset.username);
        if (user == null) return Response.error(request.getRequestId(), "User does not exist");
        user.setPasswordHash(UserRepository.hashPassword(newPassword));
        users.update(user);
        return Response.ok(request.getRequestId(), MessageType.RESET_PASSWORD, "Password reset successfully");
    }

    public Response syncState(Request request, ClientHandler handler) {
        ServerUser user = loggedInUser(handler, request);
        if (user == null) return Response.error(request.getRequestId(), "Not logged in");
        String state = request.get("stateJson");
        if (state == null || state.isBlank()) return Response.error(request.getRequestId(), "Missing user state");

        applyState(user, state);
        users.update(user);
        return new Response(request.getRequestId(), MessageType.SYNC_USER_STATE, true,
            "User state synchronized", profile(user));
    }

    private void updateStoredProfileFields(ServerUser user) {
        String state = user.getGameStateJson();
        if (state == null || state.isBlank()) return;
        try {
            JsonObject root = JsonParser.parseString(state).getAsJsonObject();
            root.addProperty("username", user.getUsername());
            root.addProperty("nickname", user.getNickname());
            root.addProperty("email", user.getEmail());
            root.addProperty("gender", user.getGender());
            user.setGameStateJson(gson.toJson(root));
        } catch (Exception ignored) { }
    }

    private String validateRegistration(String username, String password, String passwordConfirm,
                                        String nickname, String email, String gender) {
        if (blank(username) || blank(password) || blank(nickname) || blank(email) || blank(gender))
            return "Missing registration field";
        if (!validateUsername(username)) return "Error: username is not valid";
        if (!validatePassword(password)) return "Error: password is not strong enough.";
        if (!passwordIsConfirmed(password, passwordConfirm)) return "Error: password not confirmed.";
        if (!validateNickname(nickname)) return "Error: nickname is too short";
        if (!validateEmail(email)) return "Error: email pattern is not valid";
        if (!(gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female")))
            return "Error: gender is not valid";
        return null;
    }

    public static boolean validateUsername(String username) {
        return username != null && username.trim().length() >= 1 && username.trim().length() <= 64
            && !username.matches(".*[\\s|].*");
    }

    public static boolean validatePassword(String password) {
        return password != null && password.matches(Commands.PASSWORD.getPattern());
    }

    public static boolean passwordIsConfirmed(String password, String passwordConfirm) {
        return password != null && password.equals(passwordConfirm);
    }

    public static boolean validateNickname(String nickname) {
        return nickname != null && nickname.matches(Commands.NICKNAME.getPattern());
    }

    public static boolean validateEmail(String email) {
        return email != null && email.matches(Commands.EMAIL.getPattern());
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
        d.put("levelsCount", String.valueOf(u.getLevelsCount()));
        d.put("meowPoints", String.valueOf(u.getMeowPoints()));
        d.put("lastSeasonId", String.valueOf(u.getLastSeasonId()));
        d.put("lastLevelNumber", String.valueOf(u.getLastLevelNumber()));
        d.put("completedQuests", String.valueOf(u.getCompletedQuests()));
        d.put("completedDailyQuests", String.valueOf(u.getCompletedDailyQuests()));
        d.put("stateJson", u.getGameStateJson());
        return d;
    }

    private void applyState(ServerUser user, String stateJson) {
        try {
            JsonObject root = JsonParser.parseString(stateJson).getAsJsonObject();
            // Keep the complete JSON, but normalize server-authoritative score
            // fields before storing it so a newer server record cannot be
            // accidentally overwritten by an older client cache.
            user.setCoins(readInt(root, "coinsCount", user.getCoins()));
            user.setGems(readInt(root, "gemsCount", user.getGems()));
            user.setMinigamesWon(readInt(root, "minigamesWonCount", user.getMinigamesWon()));
            user.setHighestPoint(readInt(root, "highestPointAchieved", user.getHighestPoint()));
            user.setGamesPlayed(readInt(root, "gamesPlayedCount", user.getGamesPlayed()));
            user.setLevelsCount(readInt(root, "levelsCount", user.getLevelsCount()));
            user.setMeowPoints(readInt(root, "meowPoints", user.getMeowPoints()));
            user.setLastSeasonId(readInt(root, "lastSeasonId", user.getLastSeasonId()));
            user.setLastLevelNumber(readInt(root, "lastLevelNumber", user.getLastLevelNumber()));
            int totalQuests = readCompletedQuestCount(root, user.getCompletedQuests());
            int dailyQuests = readCompletedDailyQuestCount(root, user.getCompletedDailyQuests());
            user.setCompletedQuests(Math.max(0, totalQuests - dailyQuests));
            user.setCompletedDailyQuests(dailyQuests);
            if (root.has("questions") && root.get("questions").isJsonObject()) {
                Map<String, String> questions = new HashMap<>();
                for (var entry : root.getAsJsonObject("questions").entrySet()) {
                    try { questions.put(entry.getKey(), entry.getValue().getAsString()); }
                    catch (Exception ignoredQuestion) { }
                }
                user.setSecurityQuestions(questions);
            }
            root.addProperty("coinsCount", user.getCoins());
            root.addProperty("gemsCount", user.getGems());
            root.addProperty("minigamesWonCount", user.getMinigamesWon());
            root.addProperty("highestPointAchieved", user.getHighestPoint());
            root.addProperty("gamesPlayedCount", user.getGamesPlayed());
            root.addProperty("levelsCount", user.getLevelsCount());
            root.addProperty("meowPoints", user.getMeowPoints());
            user.setGameStateJson(gson.toJson(root));
        } catch (Exception ignored) {
            user.setGameStateJson(stateJson);
            // The full JSON is still stored. If an older client sends a state
            // shape the server does not understand, profile fields simply keep
            // their previous values.
        }
    }

    private int readInt(JsonObject root, String key, int fallback) {
        try { return root.has(key) && !root.get(key).isJsonNull() ? root.get(key).getAsInt() : fallback; }
        catch (Exception e) { return fallback; }
    }

    private int readCompletedQuestCount(JsonObject root, int fallback) {
        try {
            JsonObject q = root.getAsJsonObject("questsModel");
            var list = q.getAsJsonArray("availableQuests");
            int count = 0;
            for (var item : list) if (item.getAsJsonObject().has("isCompleted") &&
                item.getAsJsonObject().get("isCompleted").getAsBoolean()) count++;
            return count;
        } catch (Exception e) { return fallback; }
    }

    private int readCompletedDailyQuestCount(JsonObject root, int fallback) {
        try {
            JsonObject q = root.getAsJsonObject("questsModel");
            var list = q.getAsJsonArray("availableQuests");
            int count = 0;
            for (var item : list) {
                JsonObject quest = item.getAsJsonObject();
                boolean completed = quest.has("isCompleted") && quest.get("isCompleted").getAsBoolean();
                String category = quest.has("category") && !quest.get("category").isJsonNull() ?
                    quest.get("category").getAsString() : "";
                if (!category.isBlank() && "DAILY".equalsIgnoreCase(category) && completed) count++;
            }
            return count;
        } catch (Exception e) { return fallback; }
    }

    private boolean blank(String s) {
        return s == null || s.isBlank();
    }
    private record ResetToken(String username, long expiresAt) {}
    private record RegistrationToken(String username, long expiresAt) {}
}
