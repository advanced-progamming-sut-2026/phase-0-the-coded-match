package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.menus.LoginMenuController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.views.menus.MainMenu;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pvz.skin.BorderedTable;

public class LoginScreen extends BaseScreen {
    private final Main game;
    private BorderedTable wrapper;
    private Label messageNotif;

    public LoginScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        super.show();

        background = textures.region("IMAGE_TITLEBACKGROUNDS_BACKDROP_E");
        backgroundImage = new Image(new TextureRegionDrawable(background));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        wrapper = new BorderedTable();
        TextField usernameField = new TextField("", skin, "default");
        usernameField.setMessageText("Username");
        TextField passwordField = new TextField("", skin, "default");
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        CheckBox stayLoggedInCheckBox = new CheckBox(" Remember Me", skin, "default");

        TextButton loginBtn = new TextButton("Login", skin, "default");
        TextButton forgotPassBtn = new TextButton("Forgot Password?", skin, "default");
        wrapper.add(usernameField).width(200).height(50).row();
        wrapper.add(passwordField).width(200).height(50).row();
        wrapper.add(loginBtn).width(150).height(40).padTop(10).row();
        wrapper.add(forgotPassBtn).width(200).height(30).padTop(5).row();
        wrapper.add(stayLoggedInCheckBox).padBottom(10).row();
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

        loginBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                boolean stay = stayLoggedInCheckBox.isChecked();
                String response = LoginMenuController.login(username, password, stay);
                showMessage(response);

                if (response.equals("Logged in successfully")) {
                    // Navigate to Main Menu after a short delay
                     game.setScreen(new MainMenu(game));
                    App.setCurrentMenu(Menu.MAIN_MENU);
                }
            }
        });

        forgotPassBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openForgotPasswordModal();
            }
        });

        TextButton exitButton = new TextButton("Back", skin, "default");
        exitButton.setBounds(18, 652, 110, 48);

        stage.addActor(exitButton);

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new AuthScreen(game));
            }
        });
    }

    private void openForgotPasswordModal() {
        wrapper.setVisible(false);
        Dialog dialog = new Dialog("Forgot Password", skin);

        Label instructionLabel = new Label("Enter your credentials:", skin);
        TextField usernameField = new TextField("", skin, "default");
        usernameField.setMessageText("Username");
        TextField emailField = new TextField("", skin, "default");
        emailField.setMessageText("Email");

        Table content = dialog.getContentTable();
        content.add(usernameField).width(200).height(40).row();
        content.add(emailField).width(200).height(40).row();

        TextButton submitBtn = new TextButton("Verify", skin, "default");
        dialog.getButtonTable().add(submitBtn);
        final int[] step = {1};

        submitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (step[0] == 1) {
                    String response = LoginMenuController.forgotPassword(usernameField.getText(), emailField.getText());

                    if (response.equals("User does not exist") || response.equals("Incorrect email")) {
                        dialog.hide();
                        showMessage(response);
                    } else {
                        step[0] = 2;
                        content.clearChildren();

                        instructionLabel.setText("Question: " + response);
                        usernameField.setText("");
                        usernameField.setMessageText("Your Security Answer...");

                        content.add(instructionLabel).padBottom(10).row();
                        content.add(usernameField).width(200).height(40).padBottom(5).row();

                        submitBtn.setText("Submit Answer");
                    }
                }
                else if (step[0] == 2) {
                    String answer = usernameField.getText();
                    String response = LoginMenuController.isAnswerCorrect(answer);

                    if (response.equals("Please enter your new password")) {
                        step[0] = 3;

                        content.clearChildren();
                        instructionLabel.setText("Enter your new password:");
                        usernameField.setText("");
                        usernameField.setMessageText("New Password");
                        usernameField.setPasswordMode(true);
                        usernameField.setPasswordCharacter('*');

                        content.add(instructionLabel).padBottom(10).row();
                        content.add(usernameField).width(200).height(40).padBottom(5).row();

                        submitBtn.setText("Set New Password");
                    } else {
                        showMessage(response);
                        dialog.hide();
                    }
                }
                else if (step[0] == 3) {
                    String newPassword = usernameField.getText();
                    String response = LoginMenuController.resetPassword(newPassword);

                    if (response.equals("Password reset successfully")) {
                        dialog.hide();
                        showMessage("Password reset successfully! Please log in.");
                    } else {
                        showMessage(response);
                    }
                }
            }
        });

        dialog.show(stage);
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
        wrapper.setVisible(true);
    }
}

