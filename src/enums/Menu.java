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
    CHOOSEPLANTS_MENU("choose plants menu"),
    GAME_MANAGER("game manager"),
    GREEN_HOUSE("greenhouse"),
    TRAVEL_LOG("travel-log"),
    LEADERBOARD("leaderboard"),
    BONUS_GAME("bonus game"),
    COIN_WALLET("coin-wallet"),
    GEM_WALLET("gem-wallet"),
    SHOP("shop"),
    QUESTS("quests"),
    MINIGAMES("minigames");

    private final String menuName;

    Menu(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuName() {
        return menuName;
    }
}
