package waheed.nanodegree.udacity.android.newsapp;

/**
 * Created by waheed on 3/20/2018.
 */

public class Article {

    private String articleTitle;
    private String articleUrl;
    private String articleSection;
    private String articleType;
    private String articleTime;
    private String articleAuthor;

    public Article(String articleTitle, String articleUrl, String articleSection, String articleType, String articleTime, String articleAuthor) {
        this.articleTitle = articleTitle;
        this.articleUrl = articleUrl;
        this.articleSection = articleSection;
        this.articleType = articleType;
        this.articleTime = articleTime;
        this.articleAuthor = articleAuthor;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public String getArticleSecton() {
        return articleSection;
    }

    public String getArticleType() {
        return articleType;
    }

    public String getArticleTime() {
        return articleTime;
    }

    public String getArticleAuthor() {
        return articleAuthor;
    }
}
