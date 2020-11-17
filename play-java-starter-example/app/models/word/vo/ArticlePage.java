package models.word.vo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ArticlePage {
    public Integer page;
    public List<String> errorArticleList= new ArrayList<>();
    public List<String> todoArticleList= new ArrayList<>();
    public LinkedList<ArticleLink> articleLinkList = new LinkedList<>();
    public LinkedList<ArticleLink> singleArticleLinkList = new LinkedList<>();
    public LinkedList<ArticleLink> multiArticleLinkList = new LinkedList<>();

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
