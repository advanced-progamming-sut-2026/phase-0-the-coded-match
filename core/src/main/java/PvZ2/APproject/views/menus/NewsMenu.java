package PvZ2.APproject.views.menus;

import PvZ2.APproject.controllers.menus.NewsMenuController;
import PvZ2.APproject.enums.Commands;

public class NewsMenu {
    public static void check(String input) {
        if(input.matches(Commands.NEWS_SHOW_ALL.getPattern())){
            System.out.println(NewsMenuController.showAll());
        }else if(input.matches(Commands.NEWS_SHOW_UNREAD.getPattern())){
            System.out.println(NewsMenuController.showUnreadNews());
        }else{
            System.out.println("invalid command");
        }

    }
}
