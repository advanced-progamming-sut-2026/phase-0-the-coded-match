package enums;

public enum Menu {
    SIGNUP_MENU("signup menu"),
    LOGIN_MENU("login menu"),
    MAIN_MENU("main menu"),
    GAME_MENU("game menu"),
    SETTINGS_MENU("settings menu"),
    NEWS_MENU("news menu"),
    PROFILE_MENU("profile menu"),
    COLLECTION_MENU("collection menu"),
    CHOOSEPLANTS_MENU("choose plants menu");

    private final String menuName;

    Menu(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuName() {
        return menuName;
    }
}
