package views;

import controllers.GameManagerController;
import enums.Commands;

public class GameManager {
    public static String[] message = new String[1];

    public static void check(String input) {
        if (input.matches(Commands.ADVANCE_TIME.getPattern())) {
            GameManagerController.advanceTime(input, message);
            System.out.println(message[0]);
        } else if (input.matches(Commands.COLLECT_SUN.getPattern())) {
            GameManagerController.collectSun(input);
        } else if (input.matches(Commands.SUN_AMOUNT.getPattern())) {
            System.out.println(GameManagerController.showSunsAmount());
        } else if (input.matches(Commands.CHEAT_ADD_SUNS.getPattern())) {
            GameManagerController.cheatAddSuns(input);
        } else if (input.matches(Commands.PLANT_PLANT.getPattern())) {
            GameManagerController.plantPlant(input);
        } else if (input.matches(Commands.CHEAT_REMOVE_COOLDOWN.getPattern())) {
            GameManagerController.cheatRemoveCooldown();
        } else if (input.matches(Commands.PLUCK_PLANT.getPattern())) {
            GameManagerController.pluckPlant(input);
        } else if (input.matches(Commands.FEED_PLANT.getPattern())) {
            GameManagerController.feedPlant(input);
        } else if (input.matches(Commands.CHEAT_ADD_PLANT_FOOD.getPattern())) {
            GameManagerController.cheatAddPlantFood();
        } else if (input.matches(Commands.SHOW_MAP.getPattern())) {
            System.out.println(GameManagerController.showMap());
        } else if (input.matches(Commands.SHOW_PLANTS_STATUS.getPattern())) {
            System.out.println(GameManagerController.showPlantsStatus());
        } else if (input.matches(Commands.TILE_STATUS.getPattern())) {
            System.out.println(GameManagerController.showTileStatus(input));
        } else if (input.matches(Commands.SHOW_ZOMBIES_INFO.getPattern())) {
            System.out.println(GameManagerController.showZombiesInfo());
        } else if (input.matches(Commands.CHEAT_SPAWN_ZOMBIE.getPattern())) {
            GameManagerController.cheatSpawnZombies(input);
        } else {
            System.out.println("invalid command");
        }
    }
}