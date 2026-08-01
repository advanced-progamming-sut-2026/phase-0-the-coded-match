package controllers;

import models.Level;
import models.plants.Plant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class QuestMapEvaluator {
    private QuestMapEvaluator() {
    }

    static boolean isGardenSymmetric(Level level) {
        int rows = level.getGameMap().getRows();
        int columns = level.getGameMap().getColumns();
        for (int x = 1; x <= columns; x++) {
            for (int y = 1; y <= rows / 2; y++) {
                String first = plantSignature(level, x, y);
                String second = plantSignature(level, x, rows + 1 - y);
                if (!Objects.equals(first, second)) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean hasNoMirroredPlantPairs(Level level) {
        int rows = level.getGameMap().getRows();
        int columns = level.getGameMap().getColumns();
        boolean hasPlant = false;
        for (int x = 1; x <= columns; x++) {
            for (int y = 1; y <= rows / 2; y++) {
                String first = plantSignature(level, x, y);
                String second = plantSignature(level, x, rows + 1 - y);
                if (!first.isEmpty() || !second.isEmpty()) {
                    hasPlant = true;
                }
                if (!first.isEmpty() && Objects.equals(first, second)) {
                    return false;
                }
            }
        }
        if (rows % 2 == 1) {
            int middleRow = rows / 2 + 1;
            for (int x = 1; x <= columns; x++) {
                if (!plantSignature(level, x, middleRow).isEmpty()) {
                    hasPlant = true;
                    break;
                }
            }
        }
        return hasPlant;
    }

    private static String plantSignature(Level level, int x, int y) {
        List<String> names = new ArrayList<>();
        for (Plant plant : level.getActivePlants()) {
            if (plant.getX() == x && plant.getY() == y && plant.getData() != null) {
                names.add(plant.getData().getName());
            }
        }
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        return String.join("+", names);
    }
}
