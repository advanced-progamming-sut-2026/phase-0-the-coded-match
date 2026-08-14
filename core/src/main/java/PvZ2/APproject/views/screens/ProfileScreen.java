package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.menus.ProfileMenuController;

public class ProfileScreen extends BaseScreen {
    private final Main game;
    private ProfileMenuController controller;

    public ProfileScreen(Main game) {
        this.game = game;
        this.controller = new ProfileMenuController();
    }

    @Override
    public void show() {
        super.show();

    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
