package PvZ2.APproject.views;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.QuestController;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.Quest;
import PvZ2.APproject.models.User;
import PvZ2.APproject.views.screens.BaseScreen;
import PvZ2.APproject.views.screens.GameMenuScreen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pvz.skin.BorderedTable;

import java.util.Collections;
import java.util.List;

public class QuestScreen extends BaseScreen {
//    public static void check(String input) {
//        if (input.matches("^\\s*show\\s+quests\\s*$")) {
//            System.out.println(TravelLogController.showQuests());
//        } else if (input.matches("^\\s*claim\\s+quest\\s+-q\\s+.+$")) {
//            String name = input.replaceFirst("^\\s*claim\\s+quest\\s+-q\\s+", "").trim();
//            System.out.println(TravelLogController.claimQuestReward(name));
//        } else if (input.matches("^\\s*refresh\\s+daily\\s+quests\\s*$")) {
//            System.out.println(TravelLogController.refreshDailyQuests());
//        } else {
//            System.out.println("invalid command");
//        }
//    }\


    private Main game;
    private Table questListTable;

    public QuestScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        super.show();
        QuestController.refreshDailyQuests(false);

        background = textures.region("IMAGE_UI_QUESTS_TRAVEL_LOG_FINAL");
        backgroundImage = new Image(new TextureRegionDrawable(background));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        addCurrencyBar();

        buildUI();

        addBackButton(() -> {
            App.setCurrentMenu(Menu.GAME_MENU);
            game.setScreen(new GameMenuScreen(game));
        });
    }

    private void buildUI() {

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Table dialogTable = new Table(skin);

        questListTable = new Table(skin);
        questListTable.top();
        populateQuests();

        ScrollPane scrollPane = new ScrollPane(questListTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        dialogTable.add(scrollPane).width(750).height(650).padTop(15).row();

        root.add(dialogTable).size(800, 500).center();
    }

    private void populateQuests() {
        questListTable.clear();
        User user = App.getCurrentUser();
        if (user == null || user.getQuestsModel() == null) return;

        List<Quest> quests = user.getQuestsModel().getAvailableQuests();
        Collections.sort(quests);

        for (Quest q : quests) {
            Table card = createQuestCard(q);
            questListTable.add(card).growX().padBottom(12).row();
        }
    }

    private Table createQuestCard(Quest quest) {
        BorderedTable card = new BorderedTable();

        Label title = new Label(quest.getQuestName(), skin, "big_outline");
//        title.setFontScale(1.1f);
        Label desc = new Label(quest.getQuestData().getConditionText(), skin, "medium_outline");
        desc.setWrap(true);
        Label currentValue = new Label("Progress: " + quest.getCurrentValue() + " from " +
            quest.getTargetValue()[0], skin, "bundle_reward_multiplier");
        Label reward = new Label("Reward: " + quest.getRewardAmount() + " " + quest.getReward(), skin,
            "bundle_reward_multiplier");

        Table infoTable = new Table();
        infoTable.left();
        infoTable.add(title).left().row();
        infoTable.add(desc).left().width(450).padTop(4).row();
        infoTable.add(currentValue).left().row();
        infoTable.add(reward).left().padTop(4).row();

        card.add(infoTable).expandX().fillX().pad(10);

        if (quest.isCompleted() && !quest.isRewardClaimed()) {
            TextButton claimBtn = new TextButton("CLAIM", skin, "purple");
            claimBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    QuestController.claimReward(quest.getQuestName());
                    populateQuests();
                }
            });
            card.add(claimBtn).size(100, 45).padRight(10);
        } else if (quest.isRewardClaimed()) {
            Label doneLabel = new Label("COMPLETED", skin, "secondary");
            card.add(doneLabel).padRight(15);
        }

        return card;
    }
}
