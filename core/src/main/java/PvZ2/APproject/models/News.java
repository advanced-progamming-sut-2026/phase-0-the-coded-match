package PvZ2.APproject.models;

public class News {
    private boolean isUnread;
    private String newsText;

    public News(String newsText) {
        this.newsText = newsText;
        this.isUnread = true;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }
}
