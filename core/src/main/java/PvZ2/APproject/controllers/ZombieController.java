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

        ZombieData newZombie = ZombieRepository.getInstance().findByDisplayName(type);
        Zombie zombie = new Zombie(newZombie, x, y);
        GameManagerController.getInstance().getCurrentLevel().getActiveZombies().add(zombie);
    }

    public void removeDeadZombies() { //in GameManagerController
       //TODO
    }

}
