package enums;

public enum Commands {
    ENTER_MENU("^\\s*menu\\s+enter\\s+(?<menu_name>.*)\\s*$"),
    SHOW_MENU("^\\s*menu\\s+show\\s+current\\s*$"),
    EXIT_MENU("^\\s*menu\\s+exit\\s*$"),
    REGISTER("^\\s*register\\s+-u\\s+(?<username>[A-Za-z0-9_]+)\\s+-p\\s+" +
            "(?<password>[A-Za-z0-9!#$%^&*()=+{}\\[\\]|\\/:;\\\"',<>?\\\\]+)\\s+" +
            "(?<password_confirm>[A-Za-z0-9!#$%^&*()=+{}\\[\\]|\\/:;\\\"',<>?\\\\]+)\\s+-n\\s+(?<nickname>.*)\\s+-e\\s+" +
            "(?<email>[A-Za-z0-9](?:[A-Za-z0-9_-]|(?<!\\.)\\.)*[A-Za-z0-9]@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\." +
            "[A-Za-z]{2,})\\s+-g\\s+(?<gender>.*)\\s*$"),
    PASSWORD("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!#$%^&*()=+{}\\[\\]|\\/\\\\:;\\\"',<>?])" +
            "[A-Za-z0-9!#$%^&*()=+{}\\[\\]|\\/:;\\\"',<>?\\\\]{8,}$"),
    NICKNAME("^.{3,30}$"),
    EMAIL("^[a-zA-Z0-9]+([._-][a-zA-Z0-9]+)*@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$"),
    LOGIN("^\\s*login\\s+-u\\s+(?<username>\\S+)\\s+-p\\s+(?<password>\\S+)(?:\\s+-stay-logged-in)?\\s*$"),
    PICK_QUESTION("^\\s*pick\\s+question\\s+-q\\s+(?<question_number>.*)\\s+-a\\s+(?<answer>.*)\\s+-c" +
            "\\s+(?<answer_confirm>.*)\\s*$"),
    FORGET_PASSWORD("^\\s*forget\\s+password\\s+-u\\s+(?<username>\\S+)\\s+-e\\s+(?<email>\\S+)\\s*$"),
    ANSWER("^\\s*answer\\s+-a\\s+(?<answer>.*)\\s*$"),
    LOGOUT("^\\s*menu\\s+logout\\s*$"),
    ENTER_SEASON("^\\s*menu\\s+enter\\s+season\\s+-s\\s+(?<season>.*)\\s*$"),
    ENTER_LEVEL("^\\s*menu\\s+enter\\s+level\\s+-l\\s+(?<level>\\d+)\\s*$"),
    GAME_MENU_MENUS("^\\s*menu\\s+(?<menu>\\S+)\\s*$"),
    CHEAT_ADD_CURRENCY("^\\s*menu\\s+cheat\\s+add\\s+(?<amount>\\d+)\\s+(?<currency>coin|diamond)\\s*$"),
    CHANGE_DIFFICULTY("^\\s*menu\\s+settings\\s+change-difficulty\\s+-l\\s+(?<difficulty_level>\\d+)\\s*$"),
    NEWS_SHOW_UNREAD("^\\s*menu\\s+news\\s+show-unread\\s*$"),
    NEWS_SHOW_ALL("^\\s*menu\\s+news\\s+show-all\\s*$"),
    PROFILE_CHANGE_USERNAME("^\\s*menu\\s+profile\\s+change-username\\s+-u\\s+(?<username>\\S+)\\s*$"),
    PROFILE_CHANGE_NICKNAME("^\\s*menu\\s+profile\\s+change-nickname\\s+-u\\s+(?<nickname>.*)\\s*$"),
    PROFILE_CHANGE_EMAIL("^\\s*menu\\s+profile\\s+change-email\\s+-e\\s+(?<email>\\S+)\\s*$"),
    PROFILE_CHANGE_PASSWORD("^\\s*menu\\s+profile\\s+change-password\\s+-p\\s+(?<new_password>\\S+)\\s+-o\\s+" +
            "(?<old_password>\\S+)\\s*$"),
    PROFILE_SHOW_INFO("^\\s*menu\\s+profile\\s+show-info\\s*$"),
    COLLECTION_SHOW_PLANTS("^\\s*menu\\s+collection\\s+show-plants\\s*$"),
    COLLECTION_SHOW_ALL_PLANTS("^\\s*menu\\s+collection\\s+show-all-plants\\s*$"),
    COLLECTION_SHOW_ZOMBIES("^\\s*menu\\s+collection\\s+show-zombies\\s*$"),
    COLLECTION_SHOW_ALL_ZOMBIES("^\\s*menu\\s+collection\\s+show-all-zombies\\s*$"),
    COLLECTION_SHOW_PLANT("^\\s*menu\\s+collection\\s+show-plant\\s+-p\\s+(?<plant_name>.*)\\s*$"),
    COLLECTION_SHOW_ZOMBIE("^\\s*menu\\s+collection\\s+show-zombie\\s+-z\\s+(?<zombie_name>.*)\\s*$"),
    COLLECTION_UPGRADE("^\\s*menu\\s+collection\\s+upgrade-plant\\s+-p\\s+(?<plant_name>.*)\\s*$"),
    COLLECTION_PURCHASE("^\\s*menu\\s+collection\\s+purchase-plant\\s+-p\\s+(?<plant_name>.*)\\s*$"),
    PLANTS_COMMANDS("^\\s*(show\\s+all\\s+plants|show\\s+available\\s+plants|add\\s+plant.*|remove\\s+" +
            "plant.*|boost\\s+plant.*|start\\s+game)\\s*$"),
    ADVANCE_TIME("^\\s*advance\\s+time\\s+-t\\s+(?<count>\\d+)\\s+ticks\\s*$"),
    COLLECT_SUN("^\\s*collect\\s+sun\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"),
    SUN_AMOUNT("^\\s*show\\s+sun\\s+amount\\s*$"),
    CHEAT_ADD_SUNS("^\\s*cheat\\s+add\\s+-n\\s+(?<count>\\d+)\\s+suns\\s*$"),
    PLANT_PLANT("^\\s*plant\\s+plant\\s+-t\\s+(?<type>.+?)\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"),
    CHEAT_REMOVE_COOLDOWN("^\\s*cheat\\s+remove-cooldown\\s*$"),
    PLUCK_PLANT("^\\s*pluck\\s+plant\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"),
    FEED_PLANT("^\\s*feed\\s+plant\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"),
    CHEAT_ADD_PLANT_FOOD("^\\s*cheat\\s+add-plant-food\\s*$"),
    SHOW_MAP("^\\s*show\\s+map\\s*$"),
    SHOW_PLANTS_STATUS("^\\s*show\\s+plants\\s+status\\s*$"),
    TILE_STATUS("^\\s*show\\s+tile\\s+status\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"),
    SHOW_ZOMBIES_INFO("^\\s*zombies\\s+info\\s*$"),
    CHEAT_SPAWN_ZOMBIE("^\\s*cheat\\s+spawn-zombie\\s+-t\\s+(?<zombie_type>.+?)\\s+-l\\s*<?\\s*(?<x>\\d+)\\s*," +
            "\\s*(?<y>\\d+)\\s*>?\\s*$"),
    SORT_LEADERBOARD("^\\s*sort\\s+by\\s+(.+)\\s+ascending:\\s+(.+)\\s*$"),
    SHOP_LIST("^\\s*shop\\s+list\\s*$"),
    SHOP_DAILY("^\\s*shop\\s+daily\\s*$"),
    SHOP_BUY("^\\s*shop\\s+buy\\s+-i\\s+(?<item_id>\\d+)\\s+-n\\s+(?<count>\\d+)(?:\\s+-t\\s+(\\S+))?$");

    private final String pattern;

    Commands(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}