package PvZ2.APproject.views.menus;

import PvZ2.APproject.Main;
import PvZ2.APproject.controllers.menus.NewsMenuController;
import PvZ2.APproject.enums.Commands;
import PvZ2.APproject.enums.Menu;
import PvZ2.APproject.models.App;
import PvZ2.APproject.models.News;
import PvZ2.APproject.views.screens.BaseScreen;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import pvz.skin.BorderedTable;

import java.util.List;

public class NewsMenu extends BaseScreen {
    private final Main game;

    public NewsMenu(Main game) {
        this.game = game;
    }

    public static void check(String input) {
        if (input.matches(Commands.NEWS_SHOW_ALL.getPattern())) {
            System.out.println(NewsMenuController.showAll());
        } else if (input.matches(Commands.NEWS_SHOW_UNREAD.getPattern())) {
            System.out.println(NewsMenuController.showUnreadNews());
        } else {
            System.out.println("invalid command");
        }
    }

    @Override
    public void show() {
        super.show();
        addMainBackground();
        addCurrencyBar();
        addBackButton(() -> {
            App.setCurrentMenu(Menu.MAIN_MENU);
            game.setScreen(new MainMenu(game));
        });

        Table root = new Table();
        root.setFillParent(true);
        root.top().padTop(72).padLeft(80).padRight(80).padBottom(35);

        Label title = new Label("NEWS & UPDATES", skin, "medium_outline");
        root.add(title).padBottom(20).row();

        Table newsList = new Table(skin);
        newsList.top();

        List<News> items = NewsMenuController.getAllNewsForUi();
        if (items.isEmpty()) {
            BorderedTable empty = new BorderedTable();
            empty.add(new Label("No news yet", skin, "medium_outline")).pad(40);
            newsList.add(empty).width(760).pad(8).row();
        } else {
            int index = items.size();
            for (int i = items.size() - 1; i >= 0; i--) {
                News item = items.get(i);
                BorderedTable card = new BorderedTable();
                String text = item.getNewsText() == null ? "" : item.getNewsText().trim();
                String[] parts = text.split("\\R", 2);
                String heading = parts.length > 1 && !parts[0].isBlank() ? parts[0] : "News #" + index;
                String bodyText = parts.length > 1 ? parts[1] : text;
                Label headingLabel = new Label((item.isUnread() ? "NEW   " : "") + heading, skin, "medium_outline");
                Label body = new Label(bodyText, skin, "default");
                body.setWrap(true);
                card.add(headingLabel).left().pad(12).row();
                card.add(body).width(700).left().padLeft(12).padRight(12).padBottom(14);
                newsList.add(card).width(760).padBottom(10).row();
                index--;
            }
        }

        ScrollPane scrollPane = new ScrollPane(newsList, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        root.add(scrollPane).width(800).height(585);
        stage.addActor(root);

        NewsMenuController.markAllAsReadForUi();
    }
}
