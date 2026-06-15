package enums;

public enum Commands {
    ENTER_MENU("^\\s*menu\\s+enter\\s+(?<menu_name>.*)\\s*$"),
    SHOW_MENU("^\\s*menu\\s+show\\s+current\\s*$"),
    EXIT_MENU("^\\s*menu\\s+exit\\s*$"),
    REGISTER("^\\s*register\\s+-u\\s+(?<username>[A-Za-z0-9_]+)\\s+-p\\s+" +
            "(?<password>[A-Za-z0-9!#$%^&*()=+{}\\[\\]|\\/:;\"',<>?\\\\]+)\\s+" +
            "(?<password_confirm>[A-Za-z0-9!#$%^&*()=+{}\\[\\]|\\/:;\"',<>?\\\\]+)\\s+-n\\s+(?<nickname>.*)\\s+-e\\s+" +
            "(?<email>[A-Za-z0-9](?:[A-Za-z0-9_-]|(?<!\\.)\\.)*[A-Za-z0-9]@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\." +
            "[A-Za-z]{2,})\\s+-g\\s+(?<gender>.*)\\s*$"),
    PASSWORD("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!#$%^&*()=+{}\\[\\]|\\/\\\\:;\"',<>?])" +
            "[A-Za-z0-9!#$%^&*()=+{}\\[\\]|\\/:;\"',<>?\\\\]{8,}$"),
    NICKNAME("^.{3,30}$"),
    LOGIN,
    PICK_QUESTION("^\\s*pick\\s+question\\s+-q\\s+(?<question_number>.*)\\s+-a\\s+(?<answer>.*)\\s+-c" +
            "\\s+(?<answer_confirm>.*)\\s*$"),
    FORGET_PASSWORD,
    ANSWER,
    LOGOUT,
    ENTER_SEASON("^\\s*menu\\s+enter\\s+season\\s+-s\\s+(?<season>.*)\\s*$"),
    ENTER_LEVEL("^\\s*menu\\s+enter\\s+level\\s+-l\\s+(?<level>\\d+)\\s*$"),
    GAME_MENU_MENUS("^\\s*menu\\s+(?<menu>\\S+)\\s*$"),
    CHEAT_ADD_CURRENCY("^\\s*menu\\s+cheat\\s+add\\s+(?<amount>\\d+)\\s+(?<currency>coin|diamond)\\s*$"),
    CHANGE_DIFFICULTY("^\\s*menu\\s+settings\\s+change-difficulty\\s+-l\\s+(?<difficulty_level>\\d+)\\s*$"),
    NEWS_SHOW_UNREAD,
    NEWS_SHOW_ALL,
    PROFILE_CHANGE,
    PROFILE_SHOW_INFO,
    COLLECTION_SHOW,
    COLLECTION_UPGRADE,
    COLLECTION_PURCHASE,
    PLANTS_COMMANDS,
    ADVANCE_TIME,
    COLLECT_SUN,
    PLANT_PLANT,
    SUN_AMOUNT,
    TILE_STATUS;

    private final String pattern;

    Commands(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
