package PvZ2.APproject.controllers;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.controllers.menus.SignupMenuController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.MiniGameRelated.*;
import PvZ2.APproject.views.MiniGameView;
import com.badlogic.gdx.Gdx;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiniGameController {
    private static MiniGame miniGame;
    private static boolean resultRecorded;
    private static Level currentLevel;
    private static boolean networkedIZombie;
    private static String networkRole;
    private static String networkSessionId;
    private static String networkOpponent;
    private static final Set<String> appliedNetworkActions = new HashSet<>();
    private static Consumer<Response> matchFoundListener;

    public static String enterMinigame(String input) {
//        Pattern pattern = Pattern.compile(Commands.ENTER_MINIGAME.getPattern());
//        Matcher matcher = pattern.matcher(input);
//
//        if (!matcher.matches()) {
//            return "invalid command";
//        }
//
//        String name = matcher.group("name");
//        name = name.toLowerCase();
//        int stage = Integer.parseInt(matcher.group("level"));
//        if (stage <= 0 || stage > 3) {
//            return "invalid level";
//        }
//
//        switch (name.replace("-", "").replace(" ", "")) {
//            case "izombie" -> miniGame = new IZombie(stage);
//            case "vasebreaker" -> miniGame = new VaseBreaker(stage);
//            case "wallnutbowling" -> miniGame = new WallNutBowling(stage);
//            case "beghouled" -> miniGame = new Beghouled(stage);
//            case "zombotany" -> miniGame = new Zombotany(stage);
//            default -> { return "minigame does not exist"; }
//        }
//        GameManagerController.getInstance().setCurrentLevel(miniGame);
//        resultRecorded = false;
//        return "entered minigame " + name + " stage " + stage;
        /// Phase 3 implementation ///
        Matcher matcher = Pattern.compile(Commands.ENTER_MINIGAME.getPattern()).matcher(input);
        if (!matcher.matches()) return "invalid command";

        String name = matcher.group("name").toLowerCase();
        int stage = Integer.parseInt(matcher.group("level"));
        if (stage <= 0 || stage > 3) return "invalid level";

        if (name.replace("-", "").replace(" ", "").equals("izombie")) {
            networkedIZombie = true;
            resultRecorded = false;
            return MatchmakingController.findRandom(stage);
        }

        networkedIZombie = false;
        switch (name.replace("-", "").replace(" ", "")) {
            case "vasebreaker" -> miniGame = new VaseBreaker(stage);
            case "wallnutbowling" -> miniGame = new WallNutBowling(stage);
            case "beghouled" -> miniGame = new Beghouled(stage);
            case "zombotany" -> miniGame = new Zombotany(stage);
            default -> { return "minigame does not exist"; }
        }
        GameManagerController.getInstance().setCurrentLevel(miniGame);
        resultRecorded = false;
        return "entered minigame " + name + " stage " + stage;
    }

    public void loadMiniGame(){ currentLevel = miniGame; }

    public void StartGame(){
        loadMiniGame();
        if (currentLevel != null) GameManagerController.getInstance().setCurrentLevel(currentLevel);
    }

    public static boolean isNetworkedIZombie() { return networkedIZombie && miniGame instanceof IZombie; }
    public static String getNetworkRole() { return networkRole; }
    public static String getNetworkSessionId() { return networkSessionId; }
    public static String getNetworkOpponent() { return networkOpponent; }
    public static void setMatchFoundListener(Consumer<Response> listener) { matchFoundListener = listener; }
    public static void clearMatchFoundListener() { matchFoundListener = null; }

    public static String placeZombie(String input) {
        if (!(miniGame instanceof IZombie game)) return "not an I, Zombie game";
        if (!isNetworkedIZombie()) return game.placeZombie(input);

        Matcher matcher = Pattern.compile(Commands.PLACE_ZOMBIE.getPattern()).matcher(input);
        if (!matcher.matches()) return "invalid command";
        try {
            Request request = new Request(MessageType.GAME_ACTION);
            request.put("action", "PLACE_ZOMBIE");
            request.put("role", "ZOMBIES");
            request.put("entityType", matcher.group("name"));
            request.put("x", matcher.group("x"));
            request.put("y", matcher.group("y"));
            Response response = App.getNetworkClient().sendAndWait(request);
            return response.getMessage();
        } catch (Exception e) {
            return "Error: could not send game action";
        }
    }

    public static void pollNetworkState() {
        if (!isNetworkedIZombie() || !App.getNetworkClient().isConnected()) return;
        try {
            Request request = new Request(MessageType.GAME_STATE);
            Response response = App.getNetworkClient().sendAndWait(request);
            if (response.isSuccess()) handleGameState(response);
        } catch (Exception e) {
            System.err.println("Could not poll game state: " + e.getMessage());
        }
    }

    public static String finishNetworkGame(boolean zombieWon) {
        if (!isNetworkedIZombie()) return "not a networked I-Zombie game";
        try {
            Request request = new Request(MessageType.GAME_OVER);
            request.put("winner", zombieWon ? getZombiePlayerName() : getPlantPlayerName());
            request.put("reason", zombieWon ? "brains_eaten" : "defended");
            Response response = App.getNetworkClient().sendAndWait(request);
            return response.getMessage();
        } catch (Exception e) {
            return "Error: could not finish game";
        }
    }

    private static String getPlantPlayerName() {
        return "PLANTS".equalsIgnoreCase(networkRole) ? App.getCurrentUser().getUsername() : networkOpponent;
    }

    private static String getZombiePlayerName() {
        return "ZOMBIES".equalsIgnoreCase(networkRole) ? App.getCurrentUser().getUsername() : networkOpponent;
    }

    /** Called by the shared NetworkClient reader for unsolicited server messages. */
    public static void handleNetworkResponse(Response response) {
        if (response == null) return;
        Runnable action = switch (response.getType()) {
            case MATCH_FOUND -> () -> handleMatchFound(response);
            case MATCH_INVITATION -> () -> handleInvitation(response);
            case GAME_STATE -> () -> handleGameState(response);
            case GAME_OVER -> () -> handleGameOver(response);
            case REACTION_RECEIVED -> () -> ReactionController.handleIncoming(response);
            default -> null;
        };
        if (action != null) {
            if (Gdx.app != null) Gdx.app.postRunnable(action);
            else action.run();
        }
    }

    private static void handleMatchFound(Response response) {
        int stage = parseInt(response.get("stage"), 1);
        miniGame = new IZombie(stage);
        currentLevel = miniGame;
        networkedIZombie = true;
        networkSessionId = response.get("sessionId");
        networkRole = response.get("role");
        networkOpponent = response.get("opponent");
        appliedNetworkActions.clear();
        resultRecorded = false;
        GameManagerController.getInstance().setCurrentLevel(miniGame);
        App.setCurrentMenu(Menu.CHOOSEPLANTS_MENU);
        System.out.println("I-Zombie match found. Role: " + networkRole + ", opponent: " + networkOpponent);
        if (matchFoundListener != null) matchFoundListener.accept(response);
    }

    private static void handleInvitation(Response response) {
        MatchmakingController.handleIncoming(response);
    }

    private static void handleGameState(Response response) {
        if (!isNetworkedIZombie()) return;
        String actions = response.get("actions");
        if (actions == null || actions.isBlank()) return;
        for (String line : actions.split("\\n")) applyActionLine(line);
    }

    private static void applyActionLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 8) return;
        String key = p[7];
        if (!appliedNetworkActions.add(key)) return;
        String action = unescape(p[2]);
        if ("PLACE_ZOMBIE".equalsIgnoreCase(action) && miniGame instanceof IZombie game) {
            String name = unescape(p[4]);
            int x = parseInt(p[5], 6);
            int y = parseInt(p[6], 1);
            game.placeZombie("place zombie " + name + " at (" + x + ", " + y + ")");
        }
    }

    private static void handleGameOver(Response response) {
        if (!isNetworkedIZombie() || resultRecorded) return;
        resultRecorded = true;
        String winner = response.get("winner");
        boolean won = App.getCurrentUser() != null && App.getCurrentUser().getUsername().equalsIgnoreCase(winner);
        if (won) {
            App.getCurrentUser().addMinigamesWon();
            SignupMenuController.saveToJson();
            MiniGameView.miniGameWon();
        } else {
            MiniGameView.miniGameLost();
        }
        networkedIZombie = false;
        networkSessionId = null;
        networkRole = null;
        networkOpponent = null;
    }

    public static void verifyWinLossConditions() {
        if (miniGame instanceof IZombie) {
            IZombie game = (IZombie) miniGame;
            boolean allBrainsEaten = game.allBrainsEaten();
            int cheapest = game.getCheapestAvailableZombieCost();
            boolean outOfSunAndZombies = cheapest > 0 && game.getSunAmount() < cheapest && miniGame.getActiveZombies().isEmpty();
            if (isNetworkedIZombie()) {
                if (allBrainsEaten && "ZOMBIES".equalsIgnoreCase(networkRole)) finishNetworkGame(true);
                else if (outOfSunAndZombies && "PLANTS".equalsIgnoreCase(networkRole)) finishNetworkGame(false);
                return;
            }

            if (allBrainsEaten) {
                EndGame(true);
            } else if (outOfSunAndZombies) {
                EndGame(false);
            }
        }
        if (miniGame instanceof VaseBreaker && ((VaseBreaker) miniGame).winConditionsChecked()) EndGame(true);
        if (miniGame instanceof WallNutBowling game && game.isGameOver) EndGame(game.hasWon());
        if (miniGame instanceof Zombotany game && game.isGameOver) EndGame(game.hasWon());
        if (miniGame instanceof Beghouled game) {
            game.checkRules();
            if (game.isGameOver) EndGame(game.hasWon());
        }
    }

    public static void EndGame(Boolean won){
        if (resultRecorded) return;
        resultRecorded = true;
        if (miniGame != null) miniGame.isGameOver = true;
        if (won) {
            if (App.getCurrentUser() != null) {
                App.getCurrentUser().addMinigamesWon();
                SignupMenuController.saveToJson();
            }
            MiniGameView.miniGameWon();
        } else {
            MiniGameView.miniGameLost();
        }
    }

    public static MiniGame getMiniGame() {
        return miniGame;
    }

    private static int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); } catch (Exception e) { return fallback; }
    }

    private static String unescape(String value) {
        return value.replace("\\|", "|").replace("\\n", "\n").replace("\\=", "=").replace("\\\\", "\\");
    }
}
