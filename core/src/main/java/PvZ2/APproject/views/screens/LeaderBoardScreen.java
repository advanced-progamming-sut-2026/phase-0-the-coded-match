package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.LeaderBoardController;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.User;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardScreen extends BaseScreen {
    private Main game;
    private LeaderBoardController controller;
    private Table leaderboardTable;

    public LeaderBoardScreen(Main game) {
        this.game = game;
        this.controller = new LeaderBoardController();
    }

    @Override
    public void show() {
        super.show();

        background = textures.region("IMAGE_TITLEBACKGROUNDS_BACKDROP_A");
        backgroundImage = new Image(new TextureRegionDrawable(background));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        addCurrencyBar();

        Table mainTable = new Table(skin);

        leaderboardTable = new Table();

        ArrayList<User> users = App.getUsers();

        buildLeaderBoard(users);

        ScrollPane scrollPane = new ScrollPane(leaderboardTable, skin);

        SelectBox<String> sort = new SelectBox<>(skin);
        sort.setItems("last level", "minigames", "daily quests", "non-daily quests", "score");

        mainTable.add(sort).width(250).row();
        mainTable.add(scrollPane)
            .width(500)
            .height(500);

        stage.addActor(mainTable);

        sort.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String sortType = sort.getSelected();

                List<User> sorted = controller.getSortedUsers(sortType);
                buildLeaderBoard(sorted);
            }
        });
    }

    public void buildLeaderBoard(List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);

            leaderboardTable.add(new Label(user.getUsername() + ":", skin, "default")).width(250);

            leaderboardTable.add(new Label("Season" + (user.getLastSeason() == null ? 0 :
                user.getLastSeason().getData().getId()) + " Level" + (user.getLastLevel() == null ? 0 :
                user.getLastLevel().getLevelNumber()), skin, "default")).width(150);

            leaderboardTable.add(new Label("Mini games won: " + user.getMinigamesWonCount(), skin, "default")).width(150);

            leaderboardTable.add(new Label("Quests done: " + user.getCompletedQuestsCount(), skin, "default")).width(150);
            //todo: separate daily and normal
            leaderboardTable.add(new Label("highest point: " + user.getHighestPointAchieved(), skin, "default")).width(150);

            leaderboardTable.row();
        }
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
