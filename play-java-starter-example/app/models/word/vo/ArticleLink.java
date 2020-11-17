package models.word.vo;

public class ArticleLink {

    public ArticlePage articlePage;
    public Integer articleIndex;
    public String articleLinkText;
    public String articleLinkHref;
    public Integer articleType;

    @Override
    public String toString() {
        return "ArticleLink{" +
                "page=" + articlePage.page +
                ", articleIndex=" + articleIndex +
                ", articleLinkText='" + articleLinkText + '\'' +
                ", articleType=" + articleType +
                '}';
    }
}
