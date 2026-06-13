package models;

import java.util.List;

public class News {
    private boolean isUnread;
    private List<News> unreadNews;
    private List<News> allNews;
    private boolean newNewsExist;
    private String newsText;

    public News(String newsText) {
        this.newsText = newsText;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public List<News> getUnreadNews() {
        return unreadNews;
    }

    public void setUnreadNews(List<News> unreadNews) {
        this.unreadNews = unreadNews;
    }

    public List<News> getAllNews() {
        return allNews;
    }

    public void setAllNews(List<News> allNews) {
        this.allNews = allNews;
    }

    public boolean isNewNewsExist() {
        return newNewsExist;
    }

    public void setNewNewsExist(boolean newNewsExist) {
        this.newNewsExist = newNewsExist;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }
}
