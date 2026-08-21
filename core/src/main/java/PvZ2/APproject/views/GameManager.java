package PvZ2.APproject.views;

import PvZ2.APproject.controllers.BonusGameController;
import PvZ2.APproject.controllers.GameManagerController;
import PvZ2.APproject.controllers.ZombieController;
import PvZ2.APproject.enums.Commands;

public class GameManager {
    public static String[] message = new String[1];

    public static void check(String input) {
//        GameManagerController instance = GameManagerController.getInstance();
//        if (BonusGameController.isActive() && input.matches("^\\s*next\\s+zombie\\s*$")) {
//            System.out.println(BonusGameController.spawnNextZombie());
//        } else if (BonusGameController.isActive() && input.matches("^\\s*show\\s+score\\s*$")) {
//            System.out.println("score: " + BonusGameController.getGame().getTotalMioPoints());
//        } else if (BonusGameController.isActive() && input.matches("^\\s*end\\s+game\\s*$")) {
//            System.out.println(BonusGameController.endGame());
//        } else if (input.matches(Commands.ADVANCE_TIME.getPattern())) {
//            instance.advanceTime(input, message);
//            System.out.println(message[0]);
//        } else if (input.matches(Commands.COLLECT_SUN.getPattern())) {
//            instance.collectSun(input);
//        } else if (input.matches(Commands.SUN_AMOUNT.getPattern())) {
//            System.out.println("sun amount = " + instance.showSunsAmount());
//        } else if (input.matches(Commands.CHEAT_ADD_SUNS.getPattern())) {
//            System.out.println(instance.cheatAddSuns(input));
//        } else if (input.matches(Commands.PLANT_PLANT.getPattern())) {
//            instance.plantPlant(input);
//        } else if (input.matches(Commands.CHEAT_REMOVE_COOLDOWN.getPattern())) {
//            instance.cheatRemoveCooldown();
//        } else if (input.matches(Commands.PLUCK_PLANT.getPattern())) {
//            instance.pluckPlant(input);
//        } else if (input.matches(Commands.FEED_PLANT.getPattern())) {
//            instance.feedPlant(input);
//        } else if (input.matches(Commands.CHEAT_ADD_PLANT_FOOD.getPattern())) {
//            instance.cheatAddPlantFood();
//        } else if (input.matches(Commands.SHOW_MAP.getPattern())) {
//            System.out.println(instance.showMap());
//        } else if (input.matches(Commands.SHOW_PLANTS_STATUS.getPattern())) {
//            System.out.println(instance.showPlantsStatus());
//        } else if (input.matches(Commands.TILE_STATUS.getPattern())) {
//            System.out.println(instance.showTileStatus(input));
//        } else if (input.matches(Commands.SHOW_ZOMBIES_INFO.getPattern())) {
//            System.out.println(ZombieController.showZombiesInfo().toString());
//        } else if (input.matches(Commands.CHEAT_SPAWN_ZOMBIE.getPattern())) {
//            ZombieController.cheatSpawnZombies(input);
////        } else if (input.matches(Commands.START_ZOMBIE_WAVES.getPattern())) {
////            System.out.println(instance.startWave()[0]);
//        } else if(input.matches(Commands.RELEASE_THE_NUKE.getPattern())){
//            instance.cheatReleaseTheNuke();
//        } else if (input.matches("^\\s*start\\s+zombie\\s+waves\\s*$")) {
//            if (instance.getCurrentLevel() != null && instance.getCurrentLevel().getZombieWave() != null) {
//                if (instance.getCurrentLevel().getZombieWave().getCurrentWave() >= 0) System.out.println("zombie waves already started");
//                else { instance.getCurrentLevel().getZombieWave().update(); System.out.println("zombie waves started"); }
//            } else System.out.println("no active level");
//        } else {
//            System.out.println("invalid command");
//        }
    }
}
