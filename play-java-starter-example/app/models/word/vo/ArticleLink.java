package models.word.vo;

public class ArticleLink {

    public String page;
    public Integer articleIndex;
    public String articleLinkText;
    public String articleLinkHref;
    public Integer articleType;

    @Override
    public String toString() {
        return "ArticleLink{" +
                "articleIndex=" + articleIndex +
                ", articleLinkText='" + articleLinkText + '\'' +
                ", articleType=" + articleType +
                '}';
    }
}
