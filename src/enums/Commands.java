package enums;

public enum Commands {
    ENTER_MENU("^\\s*menu\\s+enter\\s+(?<menuName>.*)\\s*$"),
    SHOW_MENU("^\\s*menu\\s+show\\s+current\\s*$"),
    EXIT_MENU,
    REGISTER,
    LOGIN,
    PICK_QUESTION,
    FORGET_PASSWORD,
    ANSWER,
    LOGOUT,
    ENTER_CHAPTER,
    GAME_MANU_MENUS,
    CHEAT_ADD,
    SETTINGS_CHANGE,
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
