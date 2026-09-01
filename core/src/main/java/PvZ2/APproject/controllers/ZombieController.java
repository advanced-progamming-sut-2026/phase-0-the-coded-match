package PvZ2.APproject.controllers;

import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.models.zombies.Zombie;
import PvZ2.APproject.models.zombies.ZombieArmor;
import PvZ2.APproject.models.zombies.ZombieData;
import PvZ2.APproject.models.zombies.ZombieRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZombieController {


    public static StringBuilder showZombiesInfo() {
        StringBuilder builder = new StringBuilder();
        if (GameManagerController.getInstance().getCurrentLevel() == null) return builder;
        for (Zombie zombie : GameManagerController.getInstance().getCurrentLevel().getActiveZombies()) {
            builder.append(zombie.getData().getDisplayName()).append(":\n");
            builder.append("position: ").append(zombie.getX()).append(", ").append(zombie.getY()).append('\n');
            builder.append("health: ").append(zombie.getCurrentHp()).append('\n');
            if (!zombie.getArmors().isEmpty()) {
                builder.append("armor:").append("\n");
                for (ZombieArmor armor : zombie.getArmors()) {
                    builder.append(armor.getData().getType().getName()).append(": ")
                            .append(armor.getCurrentHp()).append("\n");
                }
            }
            builder.append("effects: ").append(zombie.getEffects()).append('\n');
        }
        return builder;
    }

    public static void cheatSpawnZombies(String input) {
        Matcher matcher = Pattern.compile(Commands.CHEAT_SPAWN_ZOMBIE.getPattern()).matcher(input);
        if (!matcher.matches()) {
            System.out.println("invalid command");
            return;
        }

        String type = matcher.group("zombieType");
        float x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));

        if (GameManagerController.getInstance().getCurrentLevel() == null) {
            System.out.println("no active level");
            return;
        }
        if (x < 0 || x > GameManagerController.getInstance().getCurrentLevel().getGameMap().getColumns() + 1 ||
            y < 1 || y > GameManagerController.getInstance().getCurrentLevel().getGameMap().getRows()) {
            System.out.println("invalid location");
            return;
        }
        ZombieData newZombie = ZombieRepository.getInstance().findByDisplayName(type);
        if (newZombie == null) newZombie = ZombieRepository.getInstance().findById(type);
        if (newZombie == null) {
            System.out.println("zombie type does not exist");
            return;
        }
        Zombie zombie = new Zombie(newZombie, x, y);
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(zombie);
    }

    public void removeDeadZombies() {
        if (GameManagerController.getInstance().getCurrentLevel() == null) return;
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().removeIf(Zombie::isDead);
    }

}
