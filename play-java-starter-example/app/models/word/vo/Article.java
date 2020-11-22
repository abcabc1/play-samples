package models.word.vo;

import models.word.ArticleLink;

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

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", titleNote='" + titleNote + '\'' +
                '}';
    }
}
