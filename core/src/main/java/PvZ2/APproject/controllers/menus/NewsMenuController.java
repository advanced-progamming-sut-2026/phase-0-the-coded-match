package PvZ2.APproject.controllers.menus;

import PvZ2.APproject.models.App;
import PvZ2.APproject.models.News;
import PvZ2.APproject.models.User;

public class NewsMenuController{

    public static String showUnreadNews() {
        User current = App.getCurrentUser();
        StringBuilder message = new StringBuilder();
        for (News n : new java.util.ArrayList<>(current.getPersonalNews().getUnreadNews())) {
            message.append(n.getNewsText()).append("\n");
            n.setUnread(false);
            current.getPersonalNews().removeReadNews(n);
        }

        return message.toString();
    }

    public static String showAll() {
        User current = App.getCurrentUser();
        StringBuilder message = new StringBuilder();
        for (News n : new java.util.ArrayList<>(current.getPersonalNews().getAllNews())) {
            message.append(n.getNewsText()).append("\n");
            if(n.isUnread()) {
                current.getPersonalNews().removeReadNews(n);
                n.setUnread(false);
            }
        }

        return message.toString();
    }
}
