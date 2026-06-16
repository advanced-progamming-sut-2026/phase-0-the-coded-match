package views;

import controllers.CollectionMenuController;
import enums.Commands;

public class CollectionMenu {
    public static void check(String input) {
        if (input.matches(Commands.COLLECTION_SHOW_PLANTS.getPattern())) {
            System.out.print(CollectionMenuController.showAchievedPlants());
        } else if (input.matches(Commands.COLLECTION_SHOW_ALL_PLANTS.getPattern())) {
            System.out.print(CollectionMenuController.showAllPlants());
        } else if (input.matches(Commands.COLLECTION_SHOW_ZOMBIES.getPattern())) {
            System.out.print(CollectionMenuController.showSeenZombies());
        } else if (input.matches(Commands.COLLECTION_SHOW_ALL_ZOMBIES.getPattern())) {
            System.out.print(CollectionMenuController.showAllZombies());
        } else if (input.matches(Commands.COLLECTION_SHOW_PLANT.getPattern())) {
            System.out.print(CollectionMenuController.showPlant(input));
        } else if (input.matches(Commands.COLLECTION_SHOW_ZOMBIE.getPattern())) {
            System.out.print(CollectionMenuController.showZombie(input));
        } else if (input.matches(Commands.COLLECTION_UPGRADE.getPattern())) {
            CollectionMenuController.upgradePlant(input);
        } else if (input.matches(Commands.COLLECTION_PURCHASE.getPattern())) {
            CollectionMenuController.purchasePlant(input);
        } else {
            System.out.println("invalid command");
        }
    }
}