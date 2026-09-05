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
import com.badlogic.gdx.utils.Align;

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
        addAssetBackground("OUR_ASSETS/menus/news_menu.png");
        addCurrencyBar();
        addBackButton(() -> {
            App.setCurrentMenu(Menu.MAIN_MENU);
            game.setScreen(new MainMenu(game));
        });
        Table newsList = new Table();
        newsList.top().left();
        List<News> items = NewsMenuController.getAllNewsForUi();
        if (items.isEmpty()) {
            Label empty = new Label("No news yet", skin, "medium_outline");
            empty.setAlignment(Align.center);
            newsList.add(empty).width(640).height(100).center();
        } else {
            int index = items.size();
            for (int i = items.size() - 1; i >= 0; i--) {
                News item = items.get(i);
                String text = item.getNewsText() == null ? "" : item.getNewsText().trim();
                String[] parts = text.split("\\R", 2);
                String heading = parts.length > 1 && !parts[0].isBlank() ? parts[0] : "News #" + index;
                String bodyText = parts.length > 1 ? parts[1] : text;
                Label headingLabel = new Label((item.isUnread() ? "NEW   " : "") + heading, skin, "medium_outline");
                Label body = new Label(bodyText, skin, "default");
                headingLabel.setAlignment(Align.left);
                body.setWrap(true);
                body.setAlignment(Align.topLeft);
                newsList.add(headingLabel).width(620).left().padTop(10).padBottom(5).row();
                newsList.add(body).width(620).left().padBottom(10).row();
                if (i > 0) {
                    Label separator = new Label("------------------------------------------------------------",
                        skin, "default");
                    newsList.add(separator).width(620).center().padBottom(4).row();
                }
                index--;
            }
        }

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle(
            skin.get("default", ScrollPane.ScrollPaneStyle.class)
        );
        scrollStyle.background = null;
        ScrollPane scrollPane = new ScrollPane(newsList, scrollStyle);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setBounds(330, 145, 650, 425);
        stage.addActor(scrollPane);
        NewsMenuController.markAllAsReadForUi();
    }
}
