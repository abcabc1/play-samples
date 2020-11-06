package models.word.vo;

public class Article {

    public ArticleLink articleLink;
    public String title;
    public String titleNote;
    public String content;
    public String contentNote;
    public String titleAndNote;

    public Article(ArticleLink articleLink) {
        this.articleLink = articleLink;
    }
}
