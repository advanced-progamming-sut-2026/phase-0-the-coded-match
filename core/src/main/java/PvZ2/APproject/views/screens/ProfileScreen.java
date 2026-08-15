package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.menus.ProfileMenuController;
import PvZ2.APproject.models.App;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pvz.skin.BorderedTable;

public class ProfileScreen extends BaseScreen {
    private final Main game;
    private ProfileMenuController controller;
    private Label messageNotif;

    public ProfileScreen(Main game) {
        this.game = game;
        this.controller = new ProfileMenuController();
    }

    @Override
    public void show() {
        super.show();

        background = textures.region("IMAGE_MAINMENU_BACKGROUND");
        backgroundImage = new Image(new TextureRegionDrawable(background));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        BorderedTable wrapper = new BorderedTable();

        Table usernameTable = new Table(skin);
        Label username = new Label("Username: " +
            App.getCurrentUser().getUsername(), skin, "medium_outline");
        TextButton changeUN = new TextButton("Change Username", skin, "purple");
        usernameTable.add(username);
        usernameTable.add(changeUN).padLeft(20);

        Table nicknameTable = new Table(skin);
        Label nickname = new Label("Nickname: " +
            App.getCurrentUser().getNickname(), skin, "medium_outline");
        TextButton changeNN = new TextButton("Change Nickname", skin, "purple");
        nicknameTable.add(nickname);
        nicknameTable.add(changeNN).padLeft(20);

        Label gamesPlayedCount = new Label("Games Played Count: " +
            App.getCurrentUser().getGamesPlayedCount(), skin, "medium_outline");
        Label levelsCount = new Label("Levels Count: " +
            App.getCurrentUser().getLevelsCount(), skin, "medium_outline");
        Label meowPoints = new Label("Meow Points: " +
            App.getCurrentUser().getMeowPoints(), skin, "medium_outline");

        //for testing, don't delete:
//        Label username = new Label("Username: SOHA!#*8w", skin, "medium_outline");
//        TextButton changeUN = new TextButton("Change Username", skin, "purple");
//
//        Table usernameTable = new Table(skin);
//        usernameTable.add(username);
//        usernameTable.add(changeUN).padLeft(20);
//
//        Table nicknameTable = new Table(skin);
//        Label nickname = new Label("Nickname: Soha", skin, "medium_outline");
//        TextButton changeNN = new TextButton("Change Nickname", skin, "purple");
//        nicknameTable.add(nickname);
//        nicknameTable.add(changeNN).padLeft(20);
//
//        Label gamesPlayedCount = new Label("Games Played Count: 3618", skin, "medium_outline");
//        Label levelsCount = new Label("Levels Count: 2372", skin, "medium_outline");
//        Label meowPoints = new Label("Meow Points: 387817", skin, "medium_outline");
        //test till here

        Table emailTable = new Table(skin);
        TextButton changeE = new TextButton("Change Email", skin, "purple");
        emailTable.add(changeE);

        Table passwordTable = new Table(skin);
        TextButton changeP = new TextButton("Change Password", skin, "purple");
        passwordTable.add(changeP);

        wrapper.add(usernameTable).colspan(2).row();

        wrapper.add(nicknameTable).colspan(2).row();

        wrapper.add(gamesPlayedCount).spaceBottom(20).row();
        wrapper.add(levelsCount).spaceBottom(20).row();
        wrapper.add(meowPoints).spaceBottom(20).row();

        wrapper.add(emailTable).padRight(60);
        wrapper.add(passwordTable).padRight(30);

        wrapper.pack();
        wrapper.setPosition(
            (VIRTUAL_WIDTH - wrapper.getWidth()) / 2f,
            (VIRTUAL_HEIGHT - wrapper.getHeight()) / 2f
        );
        stage.addActor(wrapper);

        messageNotif = new Label("", skin, "promo_ribbon");
        messageNotif.setVisible(false);
        messageNotif.setPosition(265, 50);
        stage.addActor(messageNotif);

        ImageButton exitButton = new ImageButton(skin, "generic_close_circle");
        exitButton.setPosition(10, 700);

        stage.addActor(exitButton);

        Table currencyTable = new Table(skin);

        TextureRegion gemRegion = textures.region("IMAGE_UI_HUD_INGAME_GEM");
        Image gemImage = new Image(gemRegion);

        int gemsCount = App.getCurrentUser().getGemsCount();

        Label gemLabel = new Label(Integer.toString(gemsCount), skin, "default");//Integer.toString(gemsCount)

        TextureRegion coinRegion = textures.region("IMAGE_UI_HUD_INGAME_COIN");
        Image coinImage = new Image(coinRegion);

        int coinCount = App.getCurrentUser().getCoinsCount();

        Label coinLabel = new Label(Integer.toString(coinCount), skin, "default");//Integer.toString(coinCount)

        currencyTable.add(coinImage).size(40, 40).padRight(5);
        currencyTable.add(coinLabel);
        currencyTable.add(gemImage).size(40, 40).padRight(10);
        currencyTable.add(gemLabel);

        currencyTable.pack();

        currencyTable.setPosition(
            VIRTUAL_WIDTH - currencyTable.getWidth() - 20,
            VIRTUAL_HEIGHT - currencyTable.getHeight() - 20
        );

        stage.addActor(currencyTable);

        changeUN.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editInformation(usernameTable, username, changeUN, "New Username");
            }
        });

        changeNN.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editInformation(nicknameTable, nickname, changeNN, "New Nickname");
            }
        });

        changeE.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editInformation(emailTable, null, changeE, "New Email");
            }
        });

        changeP.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                passwordTable.clearChildren();

                TextField currentPass = new TextField("", skin, "default");
                currentPass.setMessageText("Current Password");

                TextField newPass = new TextField("", skin, "default");
                newPass.setMessageText("New Password");

                TextButton confirm = new TextButton("Confirm", skin, "default");

                passwordTable.add(currentPass).width(200).height(50).row();
                passwordTable.add(newPass).width(200).height(50).row();
                passwordTable.add(confirm);
                passwordTable.pack();

                confirm.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String oldPassword = currentPass.getText();
                        String newPassword = newPass.getText();
                        String message = controller.changePassword(oldPassword, newPassword);

                        showMessage(message);

                        passwordTable.clearChildren();

                        passwordTable.add(changeP);
                    }
                });
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.exit(game);
            }
        });
    }

    public void editInformation(Table table, Label label, TextButton textButton, String message) {
        table.clearChildren();

        TextField newInfo = new TextField("", skin, "default");
        newInfo.setMessageText(message);

        TextButton confirm = new TextButton("Confirm", skin, "default");

        table.add(newInfo).width(200).height(50).padRight(20);
        table.add(confirm);
        table.pack();

        confirm.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String newInformation = newInfo.getText();
                String messageReturned = "";

                if (message.equalsIgnoreCase("new username")) {
                    messageReturned = controller.changeUsername(newInformation);
                } else if (message.equalsIgnoreCase("new nickname")) {
                    messageReturned = controller.changeNickname(newInformation);
                } else if (message.equalsIgnoreCase("new email")) {
                    messageReturned = controller.changeEmail(newInformation);
                }

                showMessage(messageReturned);

                table.clearChildren();

                if (label == null) {
                    table.add(textButton);
                } else {
                    table.add(label);
                    table.add(textButton).padLeft(20);
                }

                table.pack();
            }
        });
    }

    public void showMessage(String message) {
        messageNotif.clearActions();

        messageNotif.setText(message);
        messageNotif.setVisible(true);
        messageNotif.pack();
        messageNotif.getColor().a = 1f;

        messageNotif.addAction(
            Actions.sequence(
                Actions.delay(2f),
                Actions.fadeOut(0.5f),
                Actions.hide()
            )
        );
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
