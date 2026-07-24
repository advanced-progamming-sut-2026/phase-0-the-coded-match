package views;

import controllers.GameManagerController;
import controllers.ZombieController;
import enums.Commands;

public class GameManager {
    public static String[] message = new String[1];

    public static void check(String input) {
        GameManagerController instance = GameManagerController.getInstance();
        if (input.matches(Commands.ADVANCE_TIME.getPattern())) {
            instance.advanceTime(input, message);
            System.out.println(message[0]);
        } else if (input.matches(Commands.COLLECT_SUN.getPattern())) {
            instance.collectSun(input);
        } else if (input.matches(Commands.SUN_AMOUNT.getPattern())) {
            System.out.println(instance.showSunsAmount());
        } else if (input.matches(Commands.CHEAT_ADD_SUNS.getPattern())) {
            instance.cheatAddSuns(input);
        } else if (input.matches(Commands.PLANT_PLANT.getPattern())) {
            instance.plantPlant(input);
        } else if (input.matches(Commands.CHEAT_REMOVE_COOLDOWN.getPattern())) {
            instance.cheatRemoveCooldown();
        } else if (input.matches(Commands.PLUCK_PLANT.getPattern())) {
            instance.pluckPlant(input);
        } else if (input.matches(Commands.FEED_PLANT.getPattern())) {
            instance.feedPlant(input);
        } else if (input.matches(Commands.CHEAT_ADD_PLANT_FOOD.getPattern())) {
            instance.cheatAddPlantFood();
        } else if (input.matches(Commands.SHOW_MAP.getPattern())) {
            System.out.println(instance.showMap());
        } else if (input.matches(Commands.SHOW_PLANTS_STATUS.getPattern())) {
            System.out.println(instance.showPlantsStatus());
        } else if (input.matches(Commands.TILE_STATUS.getPattern())) {
            System.out.println(instance.showTileStatus(input));
        } else if (input.matches(Commands.SHOW_ZOMBIES_INFO.getPattern())) {
            System.out.println(ZombieController.showZombiesInfo().toString());
        } else if (input.matches(Commands.CHEAT_SPAWN_ZOMBIE.getPattern())) {
            ZombieController.cheatSpawnZombies(input);
        } else {
            System.out.println("invalid command");
        }
    }
}