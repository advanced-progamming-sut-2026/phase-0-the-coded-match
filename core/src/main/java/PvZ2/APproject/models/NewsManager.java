package PvZ2.APproject.models;

import java.util.ArrayList;
import java.util.List;

public class NewsManager {

    private List<News> unreadNews = new ArrayList<>();
    private List<News> allNews = new ArrayList<>();

    public void addNews(News news) {
        unreadNews.add(news);
        allNews.add(news);
    }

    public void removeReadNews(News news) {
        unreadNews.remove(news);
    }

    public List<News> getUnreadNews(){
        return unreadNews;
    }

    public List<News> getAllNews() {
        return allNews;
    }
}
