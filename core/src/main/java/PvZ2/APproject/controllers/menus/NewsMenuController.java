package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.models.App;
import PvZ2.APproject.models.News;
import PvZ2.APproject.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsMenuController {

    public static String showUnreadNews() {
        User current = App.getCurrentUser();
        if (current == null) {
            return "";
        }
        StringBuilder message = new StringBuilder();
        for (News n : new ArrayList<>(current.getPersonalNews().getUnreadNews())) {
            message.append(n.getNewsText()).append("\n");
            n.setUnread(false);
            current.getPersonalNews().removeReadNews(n);
        }
        SignupMenuController.saveToJson();
        return message.toString();
    }

    public static String showAll() {
        User current = App.getCurrentUser();
        if (current == null) {
            return "";
        }
        StringBuilder message = new StringBuilder();
        for (News n : new ArrayList<>(current.getPersonalNews().getAllNews())) {
            message.append(n.getNewsText()).append("\n");
            if (n.isUnread()) {
                current.getPersonalNews().removeReadNews(n);
                n.setUnread(false);
            }
        }
        SignupMenuController.saveToJson();
        return message.toString();
    }

    public static int getUnreadCount() {
        User current = App.getCurrentUser();
        if (current == null) {
            return 0;
        }
        return current.getPersonalNews().getUnreadNews().size();
    }

    public static List<News> getAllNewsForUi() {
        User current = App.getCurrentUser();
        if (current == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(current.getPersonalNews().getAllNews());
    }

    public static void markAllAsReadForUi() {
        User current = App.getCurrentUser();
        if (current == null) {
            return;
        }
        for (News news : new ArrayList<>(current.getPersonalNews().getUnreadNews())) {
            news.setUnread(false);
            current.getPersonalNews().removeReadNews(news);
        }
        SignupMenuController.saveToJson();
    }
}
