package controllers;

import models.App;
import models.News;
import models.User;

import java.util.List;

public class NewsMenuController{

    public static String showUnreadNews() {
        User current = App.getCurrentUser();
        StringBuilder message = new StringBuilder();
        for(News n : current.getPersonalNews().getUnreadNews()){
            message.append(n.getNewsText()).append("\n");
            n.setUnread(false);
            current.getPersonalNews().removeReadNews(n);
        }

        return message.toString();
    }

    public static String showAll() {
        User current = App.getCurrentUser();
        StringBuilder message = new StringBuilder();
        for(News n : current.getPersonalNews().getAllNews()){
            message.append(n.getNewsText()).append("\n");
            if(n.isUnread()) {
                current.getPersonalNews().removeReadNews(n);
                n.setUnread(false);
            }
        }

        return message.toString();
    }
}
