package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.menus.SignupMenuController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pvz.skin.BorderedTable;


public class SignupScreen extends BaseScreen {
    private final Main game;
    private SignupMenuController controller;
    private BorderedTable wrapper;
    private Label messageNotif;
    public static boolean registered;
    public static boolean questionPicked;

    public SignupScreen(Main game) {
        this.game = game;
        this.controller = new SignupMenuController();
    }

    @Override
    public void show() {
        super.show();

        background = textures.region("IMAGE_TITLEBACKGROUNDS_BACKDROP_D");
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
        TextField passwordConfirmField = new TextField("", skin, "default");
        passwordConfirmField.setMessageText("Confirm Password");
        passwordConfirmField.setPasswordMode(true);
        passwordConfirmField.setPasswordCharacter('*');
        TextField nicknameField = new TextField("", skin, "default");
        nicknameField.setMessageText("Nickname");
        TextField emailField = new TextField("", skin, "default");
        emailField.setMessageText("Email");
        TextField genderField = new TextField("", skin, "default");
        genderField.setMessageText("Gender");

        TextButton register = new TextButton("Register", skin, "default");
        TextButton login = new TextButton("Already Have an Account?  Log In", skin, "default");

        wrapper.add(usernameField).width(200).height(50).row();
        wrapper.add(passwordField).width(200).height(50).row();
        wrapper.add(passwordConfirmField).width(200).height(50).row();
        wrapper.add(nicknameField).width(200).height(50).row();
        wrapper.add(emailField).width(200).height(50).row();
        wrapper.add(genderField).width(200).height(50).row();

        wrapper.add(register).width(160).height(40).padTop(8).row();
        wrapper.add(login).width(260).height(36).padTop(6);

        wrapper.pack();
        wrapper.setPosition(
            (VIRTUAL_WIDTH - wrapper.getWidth()) / 2f,
            (VIRTUAL_HEIGHT - wrapper.getHeight()) / 2f
        );
        stage.addActor(wrapper);

        ImageButton exitButton = new ImageButton(skin, "generic_close_circle");
        exitButton.setPosition(10, 700);

        stage.addActor(exitButton);

        //for testing, don't delete
//        TextButton enterProfile = new TextButton("profile", skin, "default");
//        enterProfile.setPosition(30, 700);
//
//        stage.addActor(enterProfile);
//
//        enterProfile.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(new ProfileScreen(game));
//            }
//        });
//        TextButton greenhouse = new TextButton("greenhouse", skin, "default");
//        greenhouse.setPosition(200, 700);
//
//        stage.addActor(greenhouse);
//
//        greenhouse.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(new GreenHouseScreen(game));
//            }
//        });
//        TextButton showMap = new TextButton("gameMap", skin, "purple");
//        showMap.setPosition(300, 700);
//        stage.addActor(showMap);
//        showMap.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(new PlayScreen(game));
//            }
//        });

        messageNotif = new Label("", skin, "promo_ribbon");
        messageNotif.setVisible(false);
        messageNotif.setPosition(265, 50);
        stage.addActor(messageNotif);

        registered = false;
        questionPicked = false;

        login.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                App.setCurrentMenu(Menu.LOGIN_MENU);
                game.setScreen(new LoginScreen(game));
            }
        });

        register.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String passwordConfirm = passwordConfirmField.getText();
                String nickname = nicknameField.getText();
                String email = emailField.getText();
                String gender = genderField.getText();
                String message = controller.register(username, password, passwordConfirm, nickname, email, gender);
                showMessage(message);

                if (registered) {
                    wrapper.clearChildren();
                    String securityQuestions = controller.showQuestions();

                    Label.LabelStyle style = new Label.LabelStyle(
                        skin.get("default", Label.LabelStyle.class)
                    );

                    style.fontColor = Color.GREEN;

                    Label questions = new Label(securityQuestions, style);

                    TextField questionNumField = new TextField("", skin, "default");
                    questionNumField.setMessageText("Choose a security question");
                    TextField answerField = new TextField("", skin, "default");
                    answerField.setMessageText("Answer the security question");
                    TextField answerConfirmField = new TextField("", skin, "default");
                    answerConfirmField.setMessageText("Confirm answer");

                    TextButton confirm = new TextButton("Confirm", skin, "default");

                    wrapper.add(questions).width(200).height(50).row();
                    wrapper.add(questionNumField).width(200).height(50).row();
                    wrapper.add(answerField).width(200).height(50).row();
                    wrapper.add(answerConfirmField).width(200).height(50).row();

                    wrapper.add(confirm);

                    confirm.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            try {
                                int questionNum = Integer.parseInt(questionNumField.getText());
                                String message = controller.pickQuestion(questionNum, answerField.getText(),
                                    answerConfirmField.getText());
                                showMessage(message);
                            } catch (NumberFormatException e) {
                                showMessage("Invalid question number");
                            }

                            if (questionPicked) {
                                game.setScreen(new LoginScreen(game));
                                App.setCurrentMenu(Menu.LOGIN_MENU);
                            }
                        }
                    });


                }
            }


        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.exit();
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
