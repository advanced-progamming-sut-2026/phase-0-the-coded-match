package PvZ2.APproject.controllers;

import PvZ2.APproject.client.MessageType;
import PvZ2.APproject.client.Request;
import PvZ2.APproject.client.Response;
import PvZ2.APproject.controllers.menus.SignupMenuController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Level;
import PvZ2.APproject.models.LevelData;
import PvZ2.APproject.models.BonusGameRelated.BonusGame;
import PvZ2.APproject.models.BonusGameRelated.KillContext;
import PvZ2.APproject.models.seasons.Season;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

public class BonusGameController {
    private static BonusGame game;
    private static double lastKillTick = -1;

    public static String startGame() {
        if (App.getCurrentUser() == null) return "no logged in user";
        LevelData levelData = App.getCurrentUser().getLastLevel();
        Season season = App.getCurrentUser().getLastSeason();
        if (levelData == null || season == null) {
            for (Season candidate : App.getAllSeasons()) {
                if (!candidate.isUnlocked()) continue;
                for (LevelData candidateLevel : candidate.getLevels()) {
                    if (candidateLevel.isUnlocked()) {
                        season = candidate;
                        levelData = candidateLevel;
                        break;
                    }
                }
                if (levelData != null) break;
            }
        }
        if (levelData == null || season == null) return "no unlocked level available";
        App.getCurrentUser().setLastSeason(season);
        App.getCurrentUser().setLastLevel(levelData);
        Level level = new Level(levelData);
        level.setCurrentSeason(season);
        level.setZombieWave(null);
        GameManagerController.getInstance().setCurrentLevel(level);
        game = new BonusGame();
        lastKillTick = -1;
        App.setCurrentMenu(Menu.CHOOSEPLANTS_MENU);
        return "bonus game started with daily seed " + game.getDailyGameZombies();
    }

    public static String spawnNextZombie() {
        if (game == null) return "no bonus game is active";
        Level level = GameManagerController.getInstance().getCurrentLevel();
        if (level == null) return "no active level";
        String zombieName = game.nextZombie();
        if (zombieName == null) return "no more zombies";
        ZombieData data = ZombieRepository.getInstance().findByDisplayName(zombieName);
        if (data == null) return "zombie data not found";
        int lane = game.nextLane(level.getGameMap().getRows());
        Zombie zombie = new Zombie(data, level.getGameMap().getColumns(), lane);
        level.addActiveZombie(zombie);
        App.getCurrentUser().getCollection().unlockZombie(data.getId());
        return "spawned " + zombieName + " in lane " + lane;
    }

    public static String recordKills(int count, int damage, boolean allDead, double currentTick) {
        if (game == null || count <= 0) return "";
        KillContext context = new KillContext();
        context.zombiesKilledByOneShot = count;
        context.timeSinceLastKill = lastKillTick < 0 ? Long.MAX_VALUE : Math.round(currentTick - lastKillTick);
        context.damageDealt = damage;
        context.allZombiesDead = allDead;
        lastKillTick = currentTick;
        return handleZombiesKilled(context);
    }

    public static String handleZombiesKilled(KillContext context) {
        if (game == null) return "no bonus game is active";
        int gained = game.score(context);
        return "gained " + gained + " mio points; total " + game.getTotalMioPoints();
    }

    public static String endGame() {
        if (game == null) return "no bonus game is active";
        int score = game.getTotalMioPoints();
        if (App.getCurrentUser() != null) {
//            App.getCurrentUser().addMeowPoints(score);
//            App.getCurrentUser().setHighestPointAchieved(Math.max(App.getCurrentUser().getHighestPointAchieved(),
//            score));
            /// PHASE 3///
            try {
                Request request = new Request(MessageType.SUBMIT_SCORE);
                request.put("score", String.valueOf(score));
                Response response = App.getNetworkClient().sendAndWait(request);
                if (response.isSuccess()) {
                    App.getCurrentUser().addMeowPoints(score);
                    App.getCurrentUser().setHighestPointAchieved(Math.max
                        (App.getCurrentUser().getHighestPointAchieved(), score));
                } else {
                    return response.getMessage();
                }
            } catch (Exception e) {
                return "Error: could not submit score to server";
            }
            SignupMenuController.saveToJson();
        }
        game = null;
        lastKillTick = -1;
        GameManagerController.getInstance().setCurrentLevel(null);
        App.setCurrentMenu(Menu.MAIN_MENU);
        return "bonus game ended with " + score + " mio points";
    }

    public static void addStickerPoints(String sticker) {
        if (game != null && sticker != null && sticker.toLowerCase().startsWith("sticker")) {
            game.addExternalPoints(100);
        }
    }

    public static boolean isActive() { return game != null; }
    public static BonusGame getGame() { return game; }
}
