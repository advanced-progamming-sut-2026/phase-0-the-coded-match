package models.plants;

import enums.PlantCategory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantRepository {
    private final List<PlantData> plants;

    public PlantRepository(String jsonPath) {
        this.plants = loadPlants(jsonPath);
    }

    private List<PlantData> loadPlants(String jsonPath) {
        List<PlantData> result = new ArrayList<>();
        String json = readJsonFile(jsonPath);
        if (json.isEmpty()) {
            return result;
        }

        List<String> objects = extractTopLevelObjects(json);
        for (String object : objects) {
            PlantData plantData = createPlantData(object);
            if (plantData != null) {
                result.add(plantData);
            }
        }
        return result;
    }

    private String readJsonFile(String jsonPath) {
        List<String> possiblePaths = new ArrayList<>();
        possiblePaths.add(normalizePath(jsonPath));
        possiblePaths.add("src/assets/Data/Plants.json");
        possiblePaths.add("assets/Plants.json");

        for (String path : possiblePaths) {
            try {
                Path filePath = Path.of(path);
                if (Files.exists(filePath)) {
                    return Files.readString(filePath);
                }
            } catch (IOException ignored) {
            }
        }
        return "";
    }

    private String normalizePath(String jsonPath) {
        if (jsonPath == null || jsonPath.trim().isEmpty()) {
            return "src/assets/Data/Plants.json";
        }
        return jsonPath.replace("plants.json", "Plants.json");
    }

    private List<String> extractTopLevelObjects(String json) {
        List<String> objects = new ArrayList<>();
        boolean inString = false;
        int depth = 0;
        int start = -1;

        for (int i = 0; i < json.length(); i++) {
            char current = json.charAt(i);
            if (current == '"' && !isEscaped(json, i)) {
                inString = !inString;
            }
            if (inString) {
                continue;
            }
            if (current == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (current == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    objects.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return objects;
    }

    private boolean isEscaped(String text, int index) {
        int slashCount = 0;
        for (int i = index - 1; i >= 0 && text.charAt(i) == '\\'; i--) {
            slashCount++;
        }
        return slashCount % 2 == 1;
    }

    private PlantData createPlantData(String object) {
        PlantData plantData = new PlantData();

        setField(plantData, "id", readString(object, "id"));
        setField(plantData, "name", readString(object, "name"));
        setField(plantData, "displayName", readString(object, "displayName"));
        setField(plantData, "category", readCategory(object));
        setField(plantData, "tags", readStringArray(object, "tags"));
        setField(plantData, "sunCost", readInt(object, "sunCost", readInt(object, "sun_cost", 0)));
        setField(plantData, "baseHp", readInt(object, "baseHp", 0));
        setField(plantData, "damage", readInt(object, "damage", 0));
        setField(plantData, "actionInterval", readDouble(object, "actionInterval", 0));
        setField(plantData, "recharge", readDouble(object, "recharge", 0));
        setField(plantData, "behaviorType", readString(object, "behaviorType"));
        setField(plantData, "abilities", readStringArray(object, "abilities"));
        setField(plantData, "baseAbility", readString(object, "baseAbility"));
        setField(plantData, "plantFoodEffect", readString(object, "plantFoodEffect"));
        setField(plantData, "upgrades", new ArrayList<>());
        setField(plantData, "description", readString(object, "description"));

        if (plantData.getName() == null || plantData.getName().trim().isEmpty()) {
            return null;
        }
        return plantData;
    }

    private PlantCategory readCategory(String object) {
        String category = readString(object, "category");
        if (category == null) {
            return null;
        }

        for (PlantCategory plantCategory : PlantCategory.values()) {
            if (plantCategory.name().equalsIgnoreCase(category)
                    || plantCategory.getName().equalsIgnoreCase(category)) {
                return plantCategory;
            }
        }
        return null;
    }

    private String readString(String object, String key) {
        Pattern pattern = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"");
        Matcher matcher = pattern.matcher(object);
        if (matcher.find()) {
            return unescape(matcher.group(1));
        }
        return null;
    }

    private int readInt(String object, String key, int defaultValue) {
        Pattern pattern = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*(-?\\d+)");
        Matcher matcher = pattern.matcher(object);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return defaultValue;
    }

    private double readDouble(String object, String key, double defaultValue) {
        Pattern pattern = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");
        Matcher matcher = pattern.matcher(object);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return defaultValue;
    }

    private List<String> readStringArray(String object, String key) {
        List<String> values = new ArrayList<>();
        int keyIndex = object.indexOf("\"" + key + "\"");
        if (keyIndex == -1) {
            return values;
        }

        int arrayStart = object.indexOf('[', keyIndex);
        if (arrayStart == -1) {
            return values;
        }

        int arrayEnd = findMatchingBracket(object, arrayStart);
        if (arrayEnd == -1) {
            return values;
        }

        String arrayText = object.substring(arrayStart + 1, arrayEnd);
        Pattern pattern = Pattern.compile("\\\"((?:\\\\.|[^\\\"])*)\\\"");
        Matcher matcher = pattern.matcher(arrayText);
        while (matcher.find()) {
            values.add(unescape(matcher.group(1)));
        }
        return values;
    }

    private int findMatchingBracket(String text, int start) {
        boolean inString = false;
        int depth = 0;

        for (int i = start; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == '"' && !isEscaped(text, i)) {
                inString = !inString;
            }
            if (inString) {
                continue;
            }
            if (current == '[') {
                depth++;
            } else if (current == ']') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private String unescape(String value) {
        return value.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\t", "\t");
    }

    private void setField(PlantData plantData, String fieldName, Object value) {
        try {
            Field field = PlantData.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(plantData, value);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }

    public List<PlantData> getAllPlants() {
        return plants;
    }

    public PlantData findByName(String name) {
        if (name == null) {
            return null;
        }

        for (PlantData plant : plants) {
            if (plant.getName() != null && plant.getName().equalsIgnoreCase(name)) {
                return plant;
            }
            if (plant.getDisplayName() != null && plant.getDisplayName().equalsIgnoreCase(name)) {
                return plant;
            }
        }
        return null;
    }

    public PlantData findById(String id) {
        if (id == null) {
            return null;
        }

        for (PlantData plant : plants) {
            if (plant.getId() != null && plant.getId().equalsIgnoreCase(id)) {
                return plant;
            }
        }
        return null;
    }

    public List<PlantData> findByCategory(String category) {
        List<PlantData> result = new ArrayList<>();
        if (category == null) {
            return result;
        }

        for (PlantData plant : plants) {
            if (plant.getCategory() == null) {
                continue;
            }
            if (plant.getCategory().name().equalsIgnoreCase(category)
                    || plant.getCategory().getName().equalsIgnoreCase(category)) {
                result.add(plant);
            }
        }
        return result;
    }

    public List<PlantData> findByTag(String tag) {
        List<PlantData> result = new ArrayList<>();
        if (tag == null) {
            return result;
        }

        for (PlantData plant : plants) {
            if (plant.getTags() != null && plant.getTags().contains(tag)) {
                result.add(plant);
            }
        }
        return result;
    }
}