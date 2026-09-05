package PvZ2.APproject.views.screens;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.LeaderBoardController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.LeaderboardEntry;
import PvZ2.APproject.models.User;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import pvz.skin.BorderedTable;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardScreen extends BaseScreen {
    private Main game;
    private LeaderBoardController controller;
    private BorderedTable leaderboardTable;

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
        stage.addActor(backgroundImage);addCurrencyBar();
        Table mainTable = new Table(skin);
        mainTable.setFillParent(true);mainTable.center();mainTable.padTop(20);
        Table titleTable = new Table(skin);titleTable.setBackground(new TextureRegionDrawable(textures.region
            ("IMAGE_UI_JOUST_LEADERBOARD_LEADERBOARD_SCROLL_BOTTOM")));
        Label titleLabel = new Label("LEADERBOARD", skin, "big");
        titleLabel.setAlignment(Align.center);
        titleTable.add(titleLabel).center().expand().fill();
        titleTable.setSize(400, 100);
        mainTable.add(titleTable).width(400).height(100).padBottom(20).row();
        leaderboardTable = new BorderedTable();leaderboardTable.top().left();
        SelectBox<String> sort = new SelectBox<>(skin);
        sort.setItems("last level", "minigames", "daily quests", "non-daily quests", "score");
        SelectBox<String> ascendOrDescend = new SelectBox<>(skin);
        ascendOrDescend.setItems("ascending", "descending");
        buildLeaderBoard(controller.getLeaderboard(sort.getSelected(), ascendOrDescend.getSelected()));
        ScrollPane scrollPane = new ScrollPane(leaderboardTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        mainTable.add(sort).width(250).row();
        mainTable.add(ascendOrDescend).width(250).row();
        mainTable.add(scrollPane).width(500).height(500).expand().fill();
        stage.addActor(mainTable);
        sort.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String sortType = sort.getSelected();
                List<LeaderboardEntry> sorted = controller.getLeaderboard(sortType, ascendOrDescend.getSelected());
                buildLeaderBoard(sorted);
            }
        });
        ascendOrDescend.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = ascendOrDescend.getSelected();
//                List<User> sorted = controller.getSortedUsers(sort.getSelected(), text);
                List<LeaderboardEntry> sorted = controller.getLeaderboard(sort.getSelected(),
                    ascendOrDescend.getSelected());
                buildLeaderBoard(sorted);
            }
        });
        addBackButton(() -> {
            App.setCurrentMenu(Menu.GAME_MENU);
            game.setScreen(new GameMenuScreen(game));
        });
    }

    public void buildLeaderBoard(List<LeaderboardEntry> entries) {
        leaderboardTable.clearChildren();
        for (LeaderboardEntry entry : entries) {
            leaderboardTable.add(new Label(entry.rank() + ". " + entry.username() + ":", skin,
                "secondary")).width(250).row();
            leaderboardTable.add(new Label("Season " + entry.seasonId() + " Level " + entry.levelNumber(),
                skin, "secondary")).width(150);
            leaderboardTable.add(new Label("Mini games won: " + entry.minigamesWon(), skin,
                "secondary")).width(150).row();
            leaderboardTable.add(new Label("Quests done: " + entry.quests() + " - daily: " + entry.dailyQuests(),
                skin, "secondary")).width(250).row();
            leaderboardTable.add(new Label("My Point: " + entry.score(), skin, "secondary")).width(150).row();
            leaderboardTable.row();
        }
        leaderboardTable.pack();
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
