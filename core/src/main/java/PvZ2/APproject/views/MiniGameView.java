package PvZ2.APproject.views;

import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.MiniGameController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.models.MiniGameRelated.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiniGameView {
    public static String[] message = new String[1];

    public static void check(String input) {
        Matcher matcher;
        MiniGame miniGame = MiniGameController.getMiniGame();
        GameManagerController instance = GameManagerController.getInstance();
        if (input.matches(Commands.ENTER_MINIGAME.getPattern())) {
            System.out.println(MiniGameController.enterMinigame(input));
            miniGame = MiniGameController.getMiniGame();
        } else if (input.matches(Commands.ADVANCE_TIME.getPattern())) {
//            instance.advanceTime(input, message);
            if (miniGame instanceof IZombie game) game.Update();
            else if (miniGame instanceof WallNutBowling game) game.tick();
            else if (miniGame instanceof Zombotany game) game.tick();
            MiniGameController.verifyWinLossConditions();
            System.out.println(message[0]);
        } else if (input.matches(Commands.PLACE_ZOMBIE.getPattern())) {
            if (miniGame instanceof IZombie game) System.out.println(game.placeZombie(input)); else System.out.println("not an I, Zombie game");
        } else if ((matcher = getMatcher(input, Commands.VASEBREAKER_BREAK_VASE.getPattern())) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            instance.breakVaseCommand(x, y);
        } else if ((matcher = getMatcher(input, Commands.WALLNUT_PLACE.getPattern())) != null && miniGame instanceof WallNutBowling game) {
            System.out.println(game.executePlaceNutFromBelt(Integer.parseInt(matcher.group("row"))));
        } else if ((matcher = getMatcher(input, Commands.BEGHOULDED_SWAP.getPattern())) != null && miniGame instanceof Beghouled game) {
            System.out.println(game.swapPlants(Integer.parseInt(matcher.group("r1")) - 1, Integer.parseInt(matcher.group("c1")) - 1, Integer.parseInt(matcher.group("r2")) - 1, Integer.parseInt(matcher.group("c2")) - 1) ? "swap successful" : "invalid swap");
            MiniGameController.verifyWinLossConditions();
        } else if ((matcher = getMatcher(input, Commands.BEGHOULDED_UPGRADE.getPattern())) != null && miniGame instanceof Beghouled game) {
            System.out.println(game.upgradePlants(matcher.group("from").trim(), matcher.group("to").trim()));
        } else if ((matcher = getMatcher(input, Commands.VASEBREAKER_PICKUP_SEED.getPattern())) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            instance.pickUPPacket(x, y);
        } else if (miniGame instanceof Zombotany && isStandardGameCommand(input)) {
            GameManager.check(input);
            MiniGameController.verifyWinLossConditions();
        } else {
            System.out.println("invalid command");
        }
    }

    public static void miniGameWon() {
        System.out.println("You won! minigame completed successfully");
    }

    public static void miniGameLost() {
        System.out.println("Game over! couldn't complete minigame");
    }

    private static boolean isStandardGameCommand(String input) {
        Commands[] commands = {
                Commands.COLLECT_SUN, Commands.SUN_AMOUNT, Commands.CHEAT_ADD_SUNS,
                Commands.PLANT_PLANT, Commands.CHEAT_REMOVE_COOLDOWN, Commands.PLUCK_PLANT,
                Commands.FEED_PLANT, Commands.CHEAT_ADD_PLANT_FOOD, Commands.SHOW_MAP,
                Commands.SHOW_PLANTS_STATUS, Commands.TILE_STATUS, Commands.SHOW_ZOMBIES_INFO,
                Commands.CHEAT_SPAWN_ZOMBIE, Commands.RELEASE_THE_NUKE
        };
        for (Commands command : commands) {
            if (input.matches(command.getPattern())) return true;
        }
        return false;
    }

    private static Matcher getMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches() ? matcher : null;
    }
}
