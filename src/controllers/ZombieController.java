package controllers;

import enums.Commands;
import enums.ZombieEffect;
import models.zombies.Zombie;
import models.zombies.ZombieArmor;
import models.zombies.ZombieData;
import models.zombies.ZombieRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZombieController {
    public static StringBuilder showZombiesInfo() {
        StringBuilder builder = new StringBuilder();
        if (GameManagerController.getInstance().getCurrentLevel() == null) {
            return builder.append("no active level");
        }
        for (Zombie zombie : GameManagerController.getInstance().getCurrentLevel().getActiveZombies()) {
            builder.append(zombie.getData().getDisplayName()).append(":\n");
            builder.append("position: ").append(zombie.getX()).append(", ").append(zombie.getY()).append('\n');
            builder.append("health: ").append(zombie.getCurrentHp()).append('\n');
            builder.append("glowing: ").append(zombie.isGlowing()).append('\n');
            builder.append("armor:\n");
            for (ZombieArmor armor : zombie.getArmors()) {
                builder.append(armor.getData().getType().getName()).append(": ")
                        .append(armor.getCurrentHp()).append('\n');
            }
            builder.append("effects:\n");
            for (ZombieEffect effect : zombie.getEffects()) {
                double remaining = zombie.getEffectRemainingSeconds(effect);
                builder.append(effect.name().toLowerCase()).append(": ");
                if (remaining > 0) {
                    builder.append(remaining).append("s");
                } else {
                    builder.append("active");
                }
                builder.append('\n');
            }
        }
        return builder;
    }

    public static void cheatSpawnZombies(String input) {
        Matcher matcher = Pattern.compile(Commands.CHEAT_SPAWN_ZOMBIE.getPattern()).matcher(input);
        if (!matcher.matches()) {
            System.out.println("invalid command");
            return;
        }
        if (GameManagerController.getInstance().getCurrentLevel() == null) {
            System.out.println("no active level");
            return;
        }
        String type = matcher.group("zombieType").trim();
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        if (GameManagerController.getInstance().getCurrentLevel().getGameMap().getTile(x, y) == null) {
            System.out.println("location is out of map");
            return;
        }
        ZombieData data = ZombieRepository.getInstance().findByDisplayName(type);
        if (data == null) {
            System.out.println("zombie type does not exist");
            return;
        }
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(new Zombie(data, x, y));
        System.out.println("Zombie " + data.getDisplayName() + " spawned at (" + x + ", " + y + ")");
    }
}
