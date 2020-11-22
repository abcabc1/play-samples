package models.word.vo;

import models.word.ArticleLink;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ArticlePage {
    public Integer page;
    public LinkedList<ArticleLink> articleLinkList = new LinkedList<>();
    public LinkedList<ArticleLink> singleArticleLinkList = new LinkedList<>();
    public LinkedList<ArticleLink> multiArticleLinkList = new LinkedList<>();
    public List<ArticleLink> errorArticleList= new ArrayList<>();
    public List<ArticleLink> todoArticleList= new ArrayList<>();

    @Override
    public String toString() {
        return "ArticlePage{" +
                "page=" + page +
                ", articleCount=" + articleLinkList.size() +
                ", singleArticleCount=" + singleArticleLinkList.size() +
                ", multiArticleCount=" + multiArticleLinkList.size() +
                '}';
    }
}
